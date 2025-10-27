package com.expensesplit.service;

import com.expensesplit.dto.ExpenseRequest;
import com.expensesplit.dto.ExpenseResponse;
import com.expensesplit.dto.ExpenseParticipantResponse;
import com.expensesplit.dto.UserResponse;
import com.expensesplit.model.Expense;
import com.expensesplit.model.ExpenseParticipant;
import com.expensesplit.model.Group;
import com.expensesplit.model.User;
import com.expensesplit.repository.ExpenseParticipantRepository;
import com.expensesplit.repository.ExpenseRepository;
import com.expensesplit.repository.GroupMemberRepository;
import com.expensesplit.repository.GroupRepository;
import com.expensesplit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + request.getGroupId()));

        User paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getPaidByUserId()));

        // Verify that participants are selected
        if (request.getParticipantUserIds() == null || request.getParticipantUserIds().isEmpty()) {
            throw new RuntimeException("At least one participant must be selected");
        }

        // Verify that the payer is a member of the group
        if (!groupMemberRepository.existsByGroupGroupIdAndUserUserId(request.getGroupId(), request.getPaidByUserId())) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Verify that all participants are members of the group
        for (Long participantId : request.getParticipantUserIds()) {
            if (!groupMemberRepository.existsByGroupGroupIdAndUserUserId(request.getGroupId(), participantId)) {
                throw new RuntimeException("User with id " + participantId + " is not a member of this group");
            }
        }
        
        Expense expense = new Expense(group, paidBy, request.getAmount(), request.getDescription());
        Expense savedExpense = expenseRepository.save(expense);
        
        // Calculate share amounts
        Map<Long, BigDecimal> shareAmounts;
        if (request.getParticipantShareAmounts() != null && !request.getParticipantShareAmounts().isEmpty()) {
            // Use custom share amounts
            shareAmounts = request.getParticipantShareAmounts();
            
            // Validate that the sum of custom shares equals the total amount
            BigDecimal totalShareAmount = shareAmounts.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Allow small rounding differences (within 0.01)
            if (totalShareAmount.subtract(request.getAmount()).abs().compareTo(new BigDecimal("0.01")) > 0) {
                throw new RuntimeException("Total of custom share amounts (" + totalShareAmount + ") does not match expense amount (" + request.getAmount() + ")");
            }
        } else {
            // Calculate equal share amount
            BigDecimal shareAmount = request.getAmount().divide(
                    BigDecimal.valueOf(request.getParticipantUserIds().size()), 
                    2, 
                    RoundingMode.HALF_UP
            );
            
            // Create map for equal shares
            shareAmounts = new HashMap<>();
            for (Long participantId : request.getParticipantUserIds()) {
                shareAmounts.put(participantId, shareAmount);
            }
        }
        
        // Create expense participants
        for (Long participantId : request.getParticipantUserIds()) {
            User participant = userRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + participantId));
            
            BigDecimal shareAmount = shareAmounts.getOrDefault(participantId, BigDecimal.ZERO);
            ExpenseParticipant expenseParticipant = new ExpenseParticipant(savedExpense, participant, shareAmount);
            expenseParticipantRepository.save(expenseParticipant);
        }
        
        return convertToResponse(savedExpense);
    }
    
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByGroupId(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupIdOrderByDateDesc(groupId);
        return expenses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long expenseId) {
        Expense expense = expenseRepository.findByIdWithParticipants(expenseId);
        if (expense == null) {
            throw new RuntimeException("Expense not found with id: " + expenseId);
        }
        return convertToResponse(expense);
    }
    
    @Transactional(readOnly = true)
    public List<ExpenseParticipantResponse> getExpenseParticipants(Long expenseId) {
        List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseExpenseId(expenseId);
        return participants.stream()
                .map(this::convertParticipantToResponse)
                .collect(Collectors.toList());
    }
    
    public void deleteExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Expense not found with id: " + expenseId);
        }
        expenseRepository.deleteById(expenseId);
    }
    
    private ExpenseResponse convertToResponse(Expense expense) {
        UserResponse paidByResponse = convertUserToResponse(expense.getPaidBy());
        
        List<ExpenseParticipantResponse> participants = expense.getParticipants().stream()
                .map(this::convertParticipantToResponse)
                .collect(Collectors.toList());
        
        return new ExpenseResponse(
                expense.getExpenseId(),
                expense.getGroup().getGroupId(),
                paidByResponse,
                expense.getAmount(),
                expense.getDescription(),
                expense.getExpenseDate(),
                participants
        );
    }
    
    private ExpenseParticipantResponse convertParticipantToResponse(ExpenseParticipant participant) {
        return new ExpenseParticipantResponse(
                participant.getParticipantId(),
                convertUserToResponse(participant.getUser()),
                participant.getShareAmount()
        );
    }
    
    private UserResponse convertUserToResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getContactNo(),
                user.getJoinDate()
        );
    }
}

