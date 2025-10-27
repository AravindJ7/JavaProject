package com.expensesplit.controller;

import com.expensesplit.dto.BalanceResponse;
import com.expensesplit.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BalanceController {
    
    private final BalanceService balanceService;
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<BalanceResponse>> getGroupBalances(@PathVariable Long groupId) {
        try {
            List<BalanceResponse> balances = balanceService.getGroupBalances(groupId);
            return ResponseEntity.ok(balances);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}


