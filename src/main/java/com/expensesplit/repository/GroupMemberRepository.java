package com.expensesplit.repository;

import com.expensesplit.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    List<GroupMember> findByGroupGroupId(Long groupId);
    
    List<GroupMember> findByUserUserId(Long userId);
    
    Optional<GroupMember> findByGroupGroupIdAndUserUserId(Long groupId, Long userId);
    
    boolean existsByGroupGroupIdAndUserUserId(Long groupId, Long userId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.groupId = :groupId")
    List<GroupMember> findMembersByGroupId(@Param("groupId") Long groupId);
}


