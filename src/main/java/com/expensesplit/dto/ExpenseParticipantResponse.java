package com.expensesplit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseParticipantResponse {
    
    private Long participantId;
    private UserResponse user;
    private BigDecimal shareAmount;
}

