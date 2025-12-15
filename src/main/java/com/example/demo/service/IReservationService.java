package com.example.demo.service;

import com.example.demo.entity.Reservation;

import java.util.List;

public interface IReservationService {

    /**
     * Créer une nouvelle réservation
     * @param userEmail l'email de l'utilisateur
     * @param bookId l'ID du livre à réserver
     * @throws Exception si l'utilisateur ou le livre n'existe pas, ou si le livre est déjà réservé
     */
    void createReservation(String userEmail, Long bookId) throws Exception;

    /**
     * Créer une nouvelle réservation avec gestion de stock (Devoir 3)
     * @param bookId l'ID du livre à réserver
     * @param userEmail l'email de l'utilisateur
     * @throws Exception selon les règles métier
     */
    void reserveBook(Long bookId, String userEmail) throws Exception;

    /**
     * Retourner un livre (marquer la réservation comme RETOURNE)
     * @param reservationId l'ID de la réservation
     * @throws Exception si la réservation n'existe pas
     */
    void returnBook(Long reservationId) throws Exception;

    /**
     * Annuler une réservation
     * @param reservationId l'ID de la réservation
     * @throws Exception si la réservation n'existe pas
     */
    void cancelReservation(Long reservationId) throws Exception;

    /**
     * Récupérer toutes les réservations d'un utilisateur
     * @param userEmail l'email de l'utilisateur
     * @return la liste des réservations
     * @throws Exception en cas d'erreur
     */
    List<Reservation> getUserReservations(String userEmail) throws Exception;

    /**
     * Récupérer toutes les réservations en cours d'un utilisateur
     * @param userEmail l'email de l'utilisateur
     * @return la liste des réservations en cours
     * @throws Exception en cas d'erreur
     */
    List<Reservation> getUserActiveReservations(String userEmail) throws Exception;

    /**
     * Récupérer toutes les réservations d'un livre
     * @param bookId l'ID du livre
     * @return la liste des réservations
     * @throws Exception en cas d'erreur
     */
    List<Reservation> getBookReservations(Long bookId) throws Exception;

    /**
     * Récupérer toutes les réservations
     * @return la liste de toutes les réservations
     * @throws Exception en cas d'erreur
     */
    List<Reservation> getAllReservations() throws Exception;

    /**
     * Vérifier si un livre est disponible (pas de réservation en cours)
     * @param bookId l'ID du livre
     * @return true si le livre est disponible, false sinon
     * @throws Exception en cas d'erreur
     */
    boolean isBookAvailable(Long bookId) throws Exception;
}