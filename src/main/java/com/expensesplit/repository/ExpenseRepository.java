package com.expensesplit.repository;

import com.expensesplit.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByGroupGroupId(Long groupId);
    
    List<Expense> findByPaidByUserId(Long userId);
    
    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.participants p LEFT JOIN FETCH p.user WHERE e.group.groupId = :groupId ORDER BY e.expenseDate DESC")
    List<Expense> findByGroupIdOrderByDateDesc(@Param("groupId") Long groupId);
    
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.participants p LEFT JOIN FETCH p.user LEFT JOIN FETCH e.paidBy LEFT JOIN FETCH e.group WHERE e.expenseId = :expenseId")
    Expense findByIdWithParticipants(@Param("expenseId") Long expenseId);
    
    @Query("SELECT e FROM Expense e WHERE e.group.groupId = :groupId AND e.expenseDate BETWEEN :startDate AND :endDate")
    List<Expense> findByGroupIdAndDateRange(@Param("groupId") Long groupId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
}

