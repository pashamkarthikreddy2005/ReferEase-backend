package com.example.ReferEase.Repo;

import com.example.ReferEase.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {
    Optional<Users> findByReferralCode(String referralCode);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);

    boolean existsByReferralCode(String enteredReferralCode);

    List<Users> findByReferrer(Optional<Users> curr);
}
