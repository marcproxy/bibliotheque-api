package com.example.demo.repository;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    // Trouver toutes les réservations d'un utilisateur
    List<Reservation> findByUserId(Long userId);

    // Trouver toutes les réservations d'un livre
    List<Reservation> findByBookId(Long bookId);

    // Trouver les réservations en cours d'un utilisateur
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    // Trouver les réservations en cours d'un livre
    List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status);

    // Vérifier si un utilisateur a déjà réservé un livre (réservation en cours)
    Optional<Reservation> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    // Trouver toutes les réservations par statut
    List<Reservation> findByStatus(ReservationStatus status);
}