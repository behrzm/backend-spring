package com.codequest.repository;

import com.codequest.domain.entity.XpHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface XpHistoryRepository extends JpaRepository<XpHistory, Long> {
    
    List<XpHistory> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}

