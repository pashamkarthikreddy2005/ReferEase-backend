package com.example.ReferEase.Service;

import com.example.ReferEase.Dto.ReqRes;
import com.example.ReferEase.Model.Referrals;
import com.example.ReferEase.Model.Users;
import com.example.ReferEase.Repo.ReferralsRepo;
import com.example.ReferEase.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersManagementService {
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private ReferralsService referralsService;
    @Autowired
    private ReferralsRepo referralsRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (usersRepo.findByUsername(registrationRequest.getUsername()).isPresent()) {
                resp.setStatusCode(409);
                resp.setError("Username already exists. Please choose a different username.");
                return resp;
            }

            if (usersRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
                resp.setStatusCode(409);
                resp.setError("An account with this email already exists. Please use a different email.");
                return resp;
            }
            String unique_code = UUID.randomUUID().toString().substring(0, 8);
            String entered_referral_code = registrationRequest.getReferrer();
            if (!(entered_referral_code == null || entered_referral_code.isEmpty()) && !usersRepo.existsByReferralCode(entered_referral_code)) {
                resp.setStatusCode(409);
                resp.setError("Invalid referral code");
                return resp;
            }
            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            user.setUsername(registrationRequest.getUsername());
            user.setRole(registrationRequest.getRole());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setReferralCode(unique_code);
            if (!(entered_referral_code == null || entered_referral_code.isEmpty())) {
                Optional<Users> referral_user = usersRepo.findByReferralCode(entered_referral_code);
                user.setReferrer(referral_user.get());
            }

            Users currentUser = usersRepo.save(user);
            if (!(entered_referral_code == null || entered_referral_code.isEmpty())) {
                Optional<Users> referral_user = usersRepo.findByReferralCode(entered_referral_code);
                referralsService.addReferral(entered_referral_code, referral_user.get(), currentUser);
            }
            if (currentUser.getId() > 0) {
                resp.setUser(currentUser);
                resp.setMessage("User saved successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
            var user = usersRepo.findByUsername(loginRequest.getUsername()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully logged in");
            response.setReferralCode(user.getReferralCode());
            response.setUsername(user.getUsername());
        } catch (UsernameNotFoundException e) {
            response.setStatusCode(404);
            response.setError("Invalid credentials. Please check your username and password.");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("An error occurred: " + e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes req) {
        ReqRes resp = new ReqRes();
        try {
            String userName = jwtUtils.extractUsername(req.getToken());
            Users users = usersRepo.findByUsername(userName).orElseThrow();
            if (jwtUtils.isTokenValid(req.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                resp.setStatusCode(200);
                resp.setToken(jwt);
                resp.setRefreshToken(req.getToken());
                resp.setExpirationTime("24Hr");
                resp.setMessage("Successfully refreshed token");
            }
            resp.setStatusCode(200);
            return resp;
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }
    }

    public Users updateProfile(Users user, Principal principal) {
        Optional<Users> optional = usersRepo.findByUsername(principal.getName());
        if (optional.isPresent()) {
            Users ans = optional.get();

            ans.setFirstName(user.getFirstName());
            ans.setLastName(user.getLastName());
            ans.setAddress(user.getAddress());
            ans.setPhoneNumber(user.getPhoneNumber());
            ans.setSkills(user.getSkills());

            if (!ans.getIsProfileCompleted()) {
                ans.setIsProfileCompleted(true);
            }
            usersRepo.save(ans);

            Optional<Referrals> referralOptional = referralsRepo.findByReferredUser(ans);
            if (referralOptional.isPresent()) {
                Referrals referral = referralOptional.get();
                referral.setStatus("COMPLETED");
                referralsRepo.save(referral);
            }

            return ans;
        } else {
            throw new RuntimeException("User not found");
        }
    }
}