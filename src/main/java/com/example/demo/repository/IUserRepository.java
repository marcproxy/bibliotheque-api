package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Trouver un utilisateur par email et mot de passe (pour l'authentification)
    Optional<User> findByEmailAndPassword(String email, String password);

    // Trouver un utilisateur par email et statut
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}