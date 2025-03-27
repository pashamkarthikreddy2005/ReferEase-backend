package com.example.ReferEase.Service;

import com.example.ReferEase.Dto.ReqRes;
import com.example.ReferEase.Model.Referrals;
import com.example.ReferEase.Model.Users;
import com.example.ReferEase.Repo.ReferralsRepo;
import com.example.ReferEase.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ReferralsService {
    @Autowired
    private ReferralsRepo referralsRepo;
    @Autowired
    private UsersRepo usersRepo;

    public void addReferral(String enteredReferralCode, Users referrerUser, Users referredUser) {
        Referrals referrals = new Referrals();
        referrals.setReferrer(referrerUser);
        referrals.setReferredUser(referredUser);
        referrals.setReferralCode(enteredReferralCode);
        referrals.setStatus("PENDING");
        referralsRepo.save(referrals);
    }

    public List<Users> getReferrals(Principal principal) {
        Optional<Users> curr = usersRepo.findByUsername(principal.getName());
        List<Users> ans = null;
        if (curr.isPresent()) {
            Users currUser=curr.get();
            ans = usersRepo.findByReferrer(curr);
        }
        return ans;
    }

    public List<Users> getAllReferrals() {
        List<Users> referrals = usersRepo.findAll();
        return referrals;
    }

}

