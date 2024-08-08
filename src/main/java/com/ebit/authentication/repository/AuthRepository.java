package com.ebit.authentication.repository;

import com.ebit.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
