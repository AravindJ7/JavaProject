package com.expensesplit.controller;

import com.expensesplit.dto.*;
import com.expensesplit.model.Settlement;
import com.expensesplit.service.BalanceService;
import com.expensesplit.service.ExpenseService;
import com.expensesplit.service.GroupService;
import com.expensesplit.service.UserService;
import com.expensesplit.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    private final UserService userService;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final BalanceService balanceService;
    private final SettlementRepository settlementRepository;
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<UserResponse> users = userService.getAllUsers();
            List<GroupResponse> groups = groupService.getAllGroups();
            model.addAttribute("users", users);
            model.addAttribute("groups", groups);
            model.addAttribute("userCount", users.size());
            model.addAttribute("groupCount", groups.size());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading data: " + e.getMessage());
        }
        return "index";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        try {
            List<UserResponse> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("userRequest", new UserRequest());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
        }
        return "users";
    }
    
    @PostMapping("/users")
    public String createUser(@ModelAttribute UserRequest userRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(userRequest);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
        }
        return "redirect:/users";
    }
    
    @GetMapping("/groups")
    public String groups(Model model) {
        try {
            List<GroupResponse> groups = groupService.getAllGroups();
            List<UserResponse> users = userService.getAllUsers();
            model.addAttribute("groups", groups);
            model.addAttribute("users", users);
            model.addAttribute("groupRequest", new GroupRequest());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading groups: " + e.getMessage());
        }
        return "groups";
    }
    
    @PostMapping("/groups")
    public String createGroup(@ModelAttribute GroupRequest groupRequest, RedirectAttributes redirectAttributes) {
        try {
            groupService.createGroup(groupRequest);
            redirectAttributes.addFlashAttribute("success", "Group created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating group: " + e.getMessage());
        }
        return "redirect:/groups";
    }
    
    @PostMapping("/groups/{groupId}/members")
    public String addMemberToGroup(@PathVariable Long groupId, @RequestParam Long userId, RedirectAttributes redirectAttributes) {
        try {
            groupService.addMemberToGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("success", "Member added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding member: " + e.getMessage());
        }
        return "redirect:/groups";
    }
    
    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public String removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            groupService.removeMemberFromGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("success", "Member removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing member: " + e.getMessage());
        }
        return "redirect:/groups/" + groupId;
    }
    
    @GetMapping("/groups/{groupId}")
    public String groupDetails(@PathVariable Long groupId, Model model) {
        try {
            GroupResponse group = groupService.getGroupById(groupId);
            if (group == null) {
                model.addAttribute("error", "Group not found");
                return "redirect:/groups";
            }
            List<ExpenseResponse> expenses = expenseService.getExpensesByGroupId(groupId);
            List<BalanceResponse> balances = balanceService.getGroupBalances(groupId);
            List<UserResponse> allUsers = userService.getAllUsers();

            model.addAttribute("group", group);
            model.addAttribute("expenses", expenses);
            model.addAttribute("balances", balances);
            model.addAttribute("allUsers", allUsers);
            model.addAttribute("expenseRequest", new ExpenseRequest());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading group details: " + e.getMessage());
            return "redirect:/groups";
        }
        return "group-details";
    }
    
    @PostMapping("/expenses")
    public String createExpense(@ModelAttribute ExpenseRequest expenseRequest, 
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Check if custom amounts are being used
            String enableCustom = request.getParameter("enableCustomAmounts");
            if ("on".equals(enableCustom) || "true".equals(enableCustom)) {
                // Extract custom amounts for each participant
                Map<Long, BigDecimal> customAmounts = new java.util.HashMap<>();
                String[] participantIds = request.getParameterValues("participantUserIds");
                
                if (participantIds != null) {
                    for (String participantIdStr : participantIds) {
                        Long participantId = Long.parseLong(participantIdStr);
                        String amountParam = request.getParameter("amount_" + participantId);
                        if (amountParam != null && !amountParam.isEmpty()) {
                            BigDecimal amount = new BigDecimal(amountParam);
                            customAmounts.put(participantId, amount);
                        }
                    }
                }
                expenseRequest.setParticipantShareAmounts(customAmounts);
            }
            
            expenseService.createExpense(expenseRequest);
            redirectAttributes.addFlashAttribute("success", "Expense created successfully!");
            return "redirect:/groups/" + expenseRequest.getGroupId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating expense: " + e.getMessage());
            // On error, redirect back to the group page if groupId is available, otherwise to groups list
            Long groupId = expenseRequest.getGroupId();
            if (groupId != null) {
                return "redirect:/groups/" + groupId;
            } else {
                return "redirect:/groups";
            }
        }
    }
    
    @GetMapping("/balances/{groupId}")
    public String balances(@PathVariable Long groupId, Model model) {
        try {
            GroupResponse group = groupService.getGroupById(groupId);
            if (group == null) {
                model.addAttribute("error", "Group not found");
                return "redirect:/groups";
            }
            List<BalanceResponse> balances = balanceService.getGroupBalances(groupId);
            model.addAttribute("group", group);
            model.addAttribute("balances", balances);
        } catch (Exception e) {
            model.addAttribute("error", "Error loading balances: " + e.getMessage());
            return "redirect:/groups";
        }
        return "balances";
    }

    @GetMapping("/expenses/{expenseId}")
    public String expenseDetails(@PathVariable Long expenseId, Model model) {
        try {
            ExpenseResponse expense = expenseService.getExpenseById(expenseId);
            if (expense == null) {
                model.addAttribute("error", "Expense not found");
                return "redirect:/groups";
            }
            model.addAttribute("expense", expense);
        } catch (Exception e) {
            model.addAttribute("error", "Error loading expense details: " + e.getMessage());
            return "redirect:/groups";
        }
        return "expense-details";
    }

    @GetMapping("/welcome")
    @ResponseBody
    public Map<String, String> welcome(HttpServletRequest request) {
        logger.info("Request received: {} {}", request.getMethod(), request.getRequestURI());
        return Map.of("message", "Welcome to the Expense Split Service!");
    }

    @PostMapping("/settle/{groupId}")
    @ResponseBody
    public Map<String, String> settleUp(@PathVariable Long groupId, @RequestBody List<Map<String, Object>> settlements,
                          RedirectAttributes redirectAttributes) {
        try {
            for (Map<String, Object> settlementData : settlements) {
                Settlement settlement = new Settlement();
                settlement.setFromUserId(Long.valueOf(settlementData.get("fromUserId").toString()));
                settlement.setToUserId(Long.valueOf(settlementData.get("toUserId").toString()));
                settlement.setAmount(new BigDecimal(settlementData.get("amount").toString()));
                settlement.setGroupId(groupId);
                settlement.setSettledDate(LocalDateTime.now());
                settlementRepository.save(settlement);
            }

            return Map.of("status", "success", "message", "Settlements recorded successfully!");
        } catch (Exception e) {
            logger.error("Error recording settlements", e);
            return Map.of("status", "error", "message", "Error recording settlements: " + e.getMessage());
        }
    }
}

