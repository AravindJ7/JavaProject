package com.expensesplit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    
    private Long expenseId;
    private Long groupId;
    private UserResponse paidBy;
    private BigDecimal amount;
    private String description;
    private LocalDateTime expenseDate;
    private List<ExpenseParticipantResponse> participants;
}


