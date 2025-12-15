package com.example.demo.controller;

import com.example.demo.entity.Reservation;
import com.example.demo.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private IReservationService reservationService;

    /**
     * POST /api/reservations
     * Créer une nouvelle réservation
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody Map<String, Object> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String userEmail = (String) request.get("userEmail");
            Long bookId = Long.valueOf(request.get("bookId").toString());

            if (userEmail == null || userEmail.isEmpty()) {
                response.put("error", "L'email de l'utilisateur est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (bookId == null) {
                response.put("error", "L'ID du livre est requis");
                return ResponseEntity.badRequest().body(response);
            }

            reservationService.createReservation(userEmail, bookId);
            response.put("message", "Réservation créée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/reservations/{id}/return
     * Retourner un livre
     */
    @PutMapping("/{id}/return")
    public ResponseEntity<Map<String, String>> returnBook(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            reservationService.returnBook(id);
            response.put("message", "Livre retourné avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/reservations/{id}/cancel
     * Annuler une réservation
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelReservation(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            reservationService.cancelReservation(id);
            response.put("message", "Réservation annulée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/reservations/user/{email}
     * Récupérer toutes les réservations d'un utilisateur
     */
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserReservations(@PathVariable String email) {
        try {
            List<Reservation> reservations = reservationService.getUserReservations(email);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/reservations/user/{email}/active
     * Récupérer les réservations en cours d'un utilisateur
     */
    @GetMapping("/user/{email}/active")
    public ResponseEntity<?> getUserActiveReservations(@PathVariable String email) {
        try {
            List<Reservation> reservations = reservationService.getUserActiveReservations(email);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/reservations/book/{bookId}
     * Récupérer toutes les réservations d'un livre
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookReservations(@PathVariable Long bookId) {
        try {
            List<Reservation> reservations = reservationService.getBookReservations(bookId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/reservations
     * Récupérer toutes les réservations
     */
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/reservations/book/{bookId}/available
     * Vérifier si un livre est disponible
     */
    @GetMapping("/book/{bookId}/available")
    public ResponseEntity<Map<String, Object>> checkBookAvailability(@PathVariable Long bookId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean available = reservationService.isBookAvailable(bookId);
            response.put("available", available);
            response.put("message", available ? "Le livre est disponible" : "Le livre est déjà réservé");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}