package com.expensesplit.service;

import com.expensesplit.dto.GroupRequest;
import com.expensesplit.dto.GroupResponse;
import com.expensesplit.dto.UserResponse;
import com.expensesplit.model.Group;
import com.expensesplit.model.GroupMember;
import com.expensesplit.model.User;
import com.expensesplit.repository.GroupMemberRepository;
import com.expensesplit.repository.GroupRepository;
import com.expensesplit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    
    public GroupResponse createGroup(GroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        
        Group savedGroup = groupRepository.save(group);
        return convertToResponse(savedGroup);
    }
    
    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        return convertToResponse(group);
    }
    
    public GroupResponse addMemberToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupGroupIdAndUserUserId(groupId, userId)) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        GroupMember groupMember = new GroupMember(group, user);
        groupMemberRepository.save(groupMember);
        
        return convertToResponse(group);
    }
    
    public void removeMemberFromGroup(Long groupId, Long userId) {
        GroupMember groupMember = groupMemberRepository.findByGroupGroupIdAndUserUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        groupMemberRepository.delete(groupMember);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getGroupMembers(Long groupId) {
        List<GroupMember> members = groupMemberRepository.findMembersByGroupId(groupId);
        return members.stream()
                .map(member -> convertUserToResponse(member.getUser()))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsByUserId(Long userId) {
        return groupRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public void deleteGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new RuntimeException("Group not found with id: " + groupId);
        }
        groupRepository.deleteById(groupId);
    }
    
    private GroupResponse convertToResponse(Group group) {
        List<UserResponse> members = group.getMembers().stream()
                .map(member -> convertUserToResponse(member.getUser()))
                .collect(Collectors.toList());
        
        return new GroupResponse(
                group.getGroupId(),
                group.getName(),
                group.getCreatedAt(),
                members
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


