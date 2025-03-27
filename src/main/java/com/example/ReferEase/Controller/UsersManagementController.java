package com.example.ReferEase.Controller;


import com.example.ReferEase.Dto.ReqRes;
import com.example.ReferEase.Model.Users;
import com.example.ReferEase.Service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UsersManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> registerUser(@RequestBody ReqRes data){
        data.setRole("USER");
        return ResponseEntity.ok(usersManagementService.register(data));
    }
    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.login(req));
    }
    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }
    @PostMapping("/user/profile")
    public ResponseEntity<Users> updateProfile(@RequestBody Users users, Principal principal) {
        Users updatedUser = usersManagementService.updateProfile(users, principal);
        return ResponseEntity.ok(updatedUser);
    }

}

