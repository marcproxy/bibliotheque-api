package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.IBookRepository;
import com.example.demo.repository.IReservationRepository;
import com.example.demo.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService implements IReservationService {

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IBookRepository bookRepository;

    @Override
    public void createReservation(String userEmail, Long bookId) throws Exception {
        // Vérifier que l'utilisateur existe
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new Exception("Utilisateur non trouvé");
        }

        // Vérifier que le livre existe
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new Exception("Livre non trouvé");
        }

        User user = optionalUser.get();
        Book book = optionalBook.get();

        // Vérifier si l'utilisateur a déjà une réservation en cours pour ce livre
        Optional<Reservation> existingReservation = reservationRepository
                .findByUserIdAndBookIdAndStatus(user.getId(), bookId, ReservationStatus.EN_COURS);

        if (existingReservation.isPresent()) {
            throw new Exception("Vous avez déjà réservé ce livre");
        }

        // Vérifier si le livre est disponible (pas de réservation en cours)
        List<Reservation> activeReservations = reservationRepository
                .findByBookIdAndStatus(bookId, ReservationStatus.EN_COURS);

        if (!activeReservations.isEmpty()) {
            throw new Exception("Ce livre est déjà réservé par un autre utilisateur");
        }

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.EN_COURS);
        reservation.setReservationDate(LocalDateTime.now());

        reservationRepository.save(reservation);
    }

    @Override
    public void reserveBook(Long bookId, String userEmail) throws Exception {
        // Vérifier que l'utilisateur existe
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new Exception("Utilisateur non trouvé");
        }

        // Vérifier que le livre existe
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new Exception("Livre non trouvé");
        }

        User user = optionalUser.get();
        Book book = optionalBook.get();

        // Règle 1 : Vérifier le stock disponible
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new Exception("Ce livre n'est plus disponible en stock");
        }

        // Règle 2 : Vérifier si l'utilisateur a déjà réservé ce livre (pas de doublons)
        Optional<Reservation> existingReservation = reservationRepository
                .findByUserIdAndBookIdAndStatus(user.getId(), bookId, ReservationStatus.EN_COURS);

        if (existingReservation.isPresent()) {
            throw new Exception("Vous avez déjà réservé ce livre");
        }

        // Règle 3 : Vérifier que l'utilisateur n'a pas déjà 3 livres réservés
        List<Reservation> userActiveReservations = reservationRepository
                .findByUserIdAndStatus(user.getId(), ReservationStatus.EN_COURS);

        if (userActiveReservations.size() >= 3) {
            throw new Exception("Vous avez atteint la limite de 3 livres réservés simultanément");
        }

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.EN_COURS);
        reservation.setReservationDate(LocalDateTime.now());

        // Décrémenter le stock
        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        reservationRepository.save(reservation);
    }

    @Override
    public void returnBook(Long reservationId) throws Exception {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isEmpty()) {
            throw new Exception("Réservation non trouvée");
        }

        Reservation reservation = optionalReservation.get();

        // Vérifier que la réservation est en cours
        if (reservation.getStatus() != ReservationStatus.EN_COURS) {
            throw new Exception("Cette réservation n'est pas en cours");
        }

        // Marquer le livre comme retourné
        reservation.setStatus(ReservationStatus.RETOURNE);

        // Remettre le livre en stock
        Book book = reservation.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        reservationRepository.save(reservation);
    }

    @Override
    public void cancelReservation(Long reservationId) throws Exception {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isEmpty()) {
            throw new Exception("Réservation non trouvée");
        }

        Reservation reservation = optionalReservation.get();

        // Vérifier que la réservation est en cours
        if (reservation.getStatus() != ReservationStatus.EN_COURS) {
            throw new Exception("Cette réservation ne peut pas être annulée");
        }

        // Annuler la réservation
        reservation.setStatus(ReservationStatus.ANNULEE);

        // Remettre le livre en stock
        Book book = reservation.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getUserReservations(String userEmail) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            throw new Exception("Utilisateur non trouvé");
        }

        return reservationRepository.findByUserId(optionalUser.get().getId());
    }

    @Override
    public List<Reservation> getUserActiveReservations(String userEmail) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            throw new Exception("Utilisateur non trouvé");
        }

        return reservationRepository.findByUserIdAndStatus(
                optionalUser.get().getId(),
                ReservationStatus.EN_COURS
        );
    }

    @Override
    public List<Reservation> getBookReservations(Long bookId) throws Exception {
        // Vérifier que le livre existe
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new Exception("Livre non trouvé");
        }

        return reservationRepository.findByBookId(bookId);
    }

    @Override
    public List<Reservation> getAllReservations() throws Exception {
        return reservationRepository.findAll();
    }

    @Override
    public boolean isBookAvailable(Long bookId) throws Exception {
        // Vérifier que le livre existe
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new Exception("Livre non trouvé");
        }

        // Vérifier s'il y a des réservations en cours
        List<Reservation> activeReservations = reservationRepository
                .findByBookIdAndStatus(bookId, ReservationStatus.EN_COURS);

        return activeReservations.isEmpty();
    }
}