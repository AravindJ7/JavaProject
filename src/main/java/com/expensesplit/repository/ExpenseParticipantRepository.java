package com.expensesplit.repository;

import com.expensesplit.model.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {
    
    List<ExpenseParticipant> findByExpenseExpenseId(Long expenseId);
    
    List<ExpenseParticipant> findByUserUserId(Long userId);
    
    @Query("SELECT ep FROM ExpenseParticipant ep WHERE ep.expense.group.groupId = :groupId")
    List<ExpenseParticipant> findByGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT ep FROM ExpenseParticipant ep WHERE ep.user.userId = :userId AND ep.expense.group.groupId = :groupId")
    List<ExpenseParticipant> findByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    @Query("SELECT SUM(ep.shareAmount) FROM ExpenseParticipant ep WHERE ep.user.userId = :userId AND ep.expense.group.groupId = :groupId")
    BigDecimal getTotalShareAmountByUserAndGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.userId = :userId AND e.group.groupId = :groupId")
    BigDecimal getTotalPaidAmountByUserAndGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
}


