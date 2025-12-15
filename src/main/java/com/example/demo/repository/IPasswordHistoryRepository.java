package com.example.demo.repository;

import com.example.demo.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    /**
     * Récupérer les N derniers mots de passe d'un utilisateur
     * Triés du plus récent au plus ancien
     */
    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.user.id = :userId ORDER BY ph.changedAt DESC")
    List<PasswordHistory> findTopNByUserIdOrderByChangedAtDesc(@Param("userId") Long userId);

    /**
     * Compter le nombre d'entrées pour un utilisateur
     */
    long countByUserId(Long userId);

    /**
     * Supprimer les anciens mots de passe au-delà de la limite
     */
    void deleteByUserId(Long userId);
}