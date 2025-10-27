package com.expensesplit.service;

import com.expensesplit.dto.BalanceResponse;
import com.expensesplit.model.User;
import com.expensesplit.repository.ExpenseParticipantRepository;
import com.expensesplit.repository.GroupMemberRepository;
import com.expensesplit.repository.SettlementRepository;
import com.expensesplit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BalanceService {

    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final SettlementRepository settlementRepository;
    
    public List<BalanceResponse> getGroupBalances(Long groupId) {
        // Get all members of the group
        List<User> groupMembers = groupMemberRepository.findMembersByGroupId(groupId).stream()
                .map(member -> member.getUser())
                .collect(Collectors.toList());
        
        return groupMembers.stream()
                .map(member -> calculateUserBalance(member, groupId))
                .collect(Collectors.toList());
    }
    
    private BalanceResponse calculateUserBalance(User user, Long groupId) {
        // Calculate total amount paid by the user in this group
        BigDecimal totalPaid = expenseParticipantRepository.getTotalPaidAmountByUserAndGroup(user.getUserId(), groupId);
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }

        // Calculate total amount owed by the user in this group
        BigDecimal totalOwed = expenseParticipantRepository.getTotalShareAmountByUserAndGroup(user.getUserId(), groupId);
        if (totalOwed == null) {
            totalOwed = BigDecimal.ZERO;
        }

        // Calculate total amount settled from this user
        BigDecimal totalSettledFrom = settlementRepository.getTotalSettledFromUser(user.getUserId(), groupId);
        if (totalSettledFrom == null) {
            totalSettledFrom = BigDecimal.ZERO;
        }

        // Calculate total amount settled to this user
        BigDecimal totalSettledTo = settlementRepository.getTotalSettledToUser(user.getUserId(), groupId);
        if (totalSettledTo == null) {
            totalSettledTo = BigDecimal.ZERO;
        }

        // Calculate net balance (positive means they are owed money, negative means they owe money)
        // Simple logic: payments you make reduce your debt, payments you receive reduce what you're owed
        BigDecimal baseBalance = totalPaid.subtract(totalOwed);
        BigDecimal netBalance = baseBalance.add(totalSettledFrom).subtract(totalSettledTo);

        return new BalanceResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                totalPaid,
                totalOwed,
                netBalance
        );
    }
}

