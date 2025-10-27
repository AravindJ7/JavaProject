package com.expensesplit.repository;

import com.expensesplit.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:name%")
    List<Group> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT g FROM Group g JOIN g.members gm WHERE gm.user.userId = :userId")
    List<Group> findByUserId(@Param("userId") Long userId);
}


