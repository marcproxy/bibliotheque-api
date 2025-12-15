package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.entity.User;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.repository.IReservationRepository;
import com.example.demo.repository.IUserRepository;
import com.example.demo.repository.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Reservation")
class ReservationServiceTest {

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IBookRepository bookRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Book book;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        book = new Book();
        book.setId(1L);
        book.setTitle("1984");
        book.setStock(5);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.EN_COURS);
    }

    @Test
    @DisplayName("Devrait créer une réservation si livre disponible")
    void testReserveBook_Success() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reservationRepository.findByUserIdAndBookIdAndStatus(1L, 1L, ReservationStatus.EN_COURS))
                .thenReturn(Optional.empty());
        when(reservationRepository.findByUserIdAndStatus(1L, ReservationStatus.EN_COURS))
                .thenReturn(Arrays.asList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        reservationService.reserveBook(1L, "test@example.com");

        // Assert
        verify(bookRepository, times(1)).save(book);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        assertEquals(4, book.getStock()); // Stock décrementé
    }

    @Test
    @DisplayName("Devrait rejeter si utilisateur a déjà réservé ce livre")
    void testReserveBook_AlreadyReserved() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reservationRepository.findByUserIdAndBookIdAndStatus(1L, 1L, ReservationStatus.EN_COURS))
                .thenReturn(Optional.of(reservation));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            reservationService.reserveBook(1L, "test@example.com");
        });

        assertTrue(exception.getMessage().contains("déjà réservé"));
    }

    @Test
    @DisplayName("Devrait rejeter si stock insuffisant")
    void testReserveBook_OutOfStock() {
        // Arrange
        book.setStock(0);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            reservationService.reserveBook(1L, "test@example.com");
        });

        assertTrue(exception.getMessage().contains("stock"));
    }

    @Test
    @DisplayName("Devrait retourner un livre")
    void testReturnBook() throws Exception {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(bookRepository.save(book)).thenReturn(book);

        // Act
        reservationService.returnBook(1L);

        // Assert
        assertEquals(ReservationStatus.RETOURNE, reservation.getStatus());
        assertEquals(6, book.getStock()); // Stock incrémenté
        verify(reservationRepository, times(1)).save(reservation);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Devrait annuler une réservation")
    void testCancelReservation() throws Exception {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        reservationService.cancelReservation(1L);

        // Assert
        assertEquals(ReservationStatus.ANNULEE, reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    @DisplayName("Devrait vérifier si un livre est disponible")
    void testIsBookAvailable() throws Exception {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reservationRepository.findByBookIdAndStatus(1L, ReservationStatus.EN_COURS))
                .thenReturn(Arrays.asList());

        // Act
        boolean result = reservationService.isBookAvailable(1L);

        // Assert
        assertTrue(result);
    }
}