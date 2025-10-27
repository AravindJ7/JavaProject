package com.expensesplit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    
    private Long groupId;
    private String name;
    private LocalDateTime createdAt;
    private List<UserResponse> members;
}

