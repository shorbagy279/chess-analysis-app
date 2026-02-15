package com.chessanalysis.repository;

import com.chessanalysis.model.AIInsight;
import com.chessanalysis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AIInsightRepository extends JpaRepository<AIInsight, Long> {
    
    List<AIInsight> findByUserOrderByGeneratedAtDesc(User user);
    
    Optional<AIInsight> findFirstByUserOrderByGeneratedAtDesc(User user);
}
