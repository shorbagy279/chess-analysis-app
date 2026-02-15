package com.chessanalysis.repository;

import com.chessanalysis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByLichessUsername(String lichessUsername);
    
    Optional<User> findByChesscomUsername(String chesscomUsername);
    
    boolean existsByLichessUsername(String lichessUsername);
    
    boolean existsByChesscomUsername(String chesscomUsername);
}
