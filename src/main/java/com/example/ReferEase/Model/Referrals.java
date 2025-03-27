package com.example.ReferEase.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Data
public class Referrals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "referrer_id", referencedColumnName = "user_id", nullable = false)
    private Users referrer;

    @OneToOne
    @JoinColumn(name = "referred_user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private Users referredUser;

    @Column(name = "referral_code", nullable = false, unique = true)
    private String referralCode;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
}
