package com.digitalbanking.repositories;

import com.digitalbanking.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    // Using Optional helps avoid NullPointerExceptions in your Service
    Optional<UserEntity> findByUsername(String username);
    
    // Quick check if a user exists (useful for Registration)
    boolean existsByUsername(String username);
}
