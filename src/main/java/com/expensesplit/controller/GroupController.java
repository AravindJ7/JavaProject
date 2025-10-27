package com.expensesplit.controller;

import com.expensesplit.dto.GroupRequest;
import com.expensesplit.dto.GroupResponse;
import com.expensesplit.dto.UserResponse;
import com.expensesplit.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupController {
    
    private final GroupService groupService;
    
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest request) {
        try {
            GroupResponse response = groupService.createGroup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        List<GroupResponse> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long id) {
        try {
            GroupResponse group = groupService.getGroupById(id);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/members")
    public ResponseEntity<GroupResponse> addMemberToGroup(@PathVariable Long id, @RequestBody Long userId) {
        try {
            GroupResponse response = groupService.addMemberToGroup(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserResponse>> getGroupMembers(@PathVariable Long id) {
        try {
            List<UserResponse> members = groupService.getGroupMembers(id);
            return ResponseEntity.ok(members);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromGroup(@PathVariable Long id, @PathVariable Long userId) {
        try {
            groupService.removeMemberFromGroup(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupResponse>> getGroupsByUserId(@PathVariable Long userId) {
        try {
            List<GroupResponse> groups = groupService.getGroupsByUserId(userId);
            return ResponseEntity.ok(groups);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}


