package com.example.ReferEase.Repo;

import com.example.ReferEase.Model.Referrals;
import com.example.ReferEase.Model.Users;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ReferralsRepo extends JpaRepository<Referrals, Integer> {

    Optional<Referrals> findByReferralCode(String referralCode);

    Optional<Referrals> findByReferredUser(Users referredUser);

}

