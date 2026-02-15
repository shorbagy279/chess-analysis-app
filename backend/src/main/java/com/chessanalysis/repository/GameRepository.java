package com.chessanalysis.repository;

import com.chessanalysis.model.Game;
import com.chessanalysis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    List<Game> findByUserOrderByPlayedAtDesc(User user);
    
    List<Game> findByUserAndPlatform(User user, String platform);
    
    Optional<Game> findByUserAndPlatformAndGameId(User user, String platform, String gameId);
    
    boolean existsByUserAndPlatformAndGameId(User user, String platform, String gameId);
    
    long countByUser(User user);
}
