package com.expensesplit.repository;

import com.expensesplit.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findByGroupId(Long groupId);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Settlement s WHERE s.fromUserId = :userId AND s.groupId = :groupId")
    BigDecimal getTotalSettledFromUser(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Settlement s WHERE s.toUserId = :userId AND s.groupId = :groupId")
    BigDecimal getTotalSettledToUser(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
