package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", catalog = "biblio_database")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.INACTIF;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Question de sécurité pour la double authentification
    @Column(name = "security_question", length = 100)
    private String securityQuestion;

    @Column(name = "security_answer", length = 255)
    private String securityAnswer;

    // Date de dernière mise à jour du mot de passe (pour expiration)
    @Column(name = "password_last_updated")
    private LocalDateTime passwordLastUpdated;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        passwordLastUpdated = LocalDateTime.now(); // Initialiser à la création
    }
}