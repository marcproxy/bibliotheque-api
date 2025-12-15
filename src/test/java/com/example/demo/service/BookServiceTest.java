package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.entity.Author;
import com.example.demo.repository.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Book")
class BookServiceTest {

    @Mock
    private IBookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private Author author;

    @BeforeEach
    void setUp() {
        // Création d'un auteur de test
        author = new Author();
        author.setId(1L);
        author.setFirstname("George");
        author.setLastname("Orwell");

        // Création de livres de test
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("1984");
        book1.setDescription("Roman dystopique");
        book1.setPublished(true);
        book1.setIsbn("978-2-07-036822-8");
        book1.setStock(5);
        book1.setAuthor(author);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("La Ferme des animaux");
        book2.setDescription("Fable politique");
        book2.setPublished(true);
        book2.setIsbn("978-2-07-037516-5");
        book2.setStock(3);
        book2.setAuthor(author);
    }

    @Test
    @DisplayName("Devrait créer un livre")
    void testCreateBook() throws Exception {
        // Arrange
        when(bookRepository.save(book1)).thenReturn(book1);

        // Act
        bookService.createBook(book1);

        // Assert
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    @DisplayName("Devrait récupérer un livre par ID")
    void testGetBookById() throws Exception {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        // Act
        Book result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("1984", result.getTitle());
        assertEquals("George", result.getAuthor().getFirstname());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait retourner null si livre non trouvé")
    void testGetBookById_NotFound() throws Exception {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Book result = bookService.getBookById(999L);

        // Assert
        assertNull(result);
        verify(bookRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Devrait récupérer tous les livres")
    void testGetAllBooks() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(2, result.size());
        assertEquals("1984", result.get(0).getTitle());
        assertEquals("La Ferme des animaux", result.get(1).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait récupérer tous les livres publiés")
    void testGetPublishedBooks() throws Exception {
        // Arrange
        List<Book> publishedBooks = Arrays.asList(book1, book2);
        when(bookRepository.findByPublishedTrue()).thenReturn(publishedBooks);

        // Act
        List<Book> result = bookService.getPublishedBooks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0).isPublished());
        verify(bookRepository, times(1)).findByPublishedTrue();
    }

    @Test
    @DisplayName("Devrait récupérer un livre par titre")
    void testGetBookByTitle() throws Exception {
        // Arrange
        when(bookRepository.findByTitle("1984")).thenReturn(Optional.of(book1));

        // Act
        Book result = bookService.getBookByTitle("1984");

        // Assert
        assertNotNull(result);
        assertEquals("1984", result.getTitle());
        verify(bookRepository, times(1)).findByTitle("1984");
    }

    @Test
    @DisplayName("Devrait rechercher des livres par mot-clé dans le titre")
    void testGetBooksByTitleContaining() throws Exception {
        // Arrange
        when(bookRepository.findByTitleContainingIgnoreCase("ferme"))
                .thenReturn(Arrays.asList(book2));

        // Act
        List<Book> result = bookService.getBooksByTitleContaining("ferme");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().toLowerCase().contains("ferme"));
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("ferme");
    }

    @Test
    @DisplayName("Devrait récupérer un livre par ISBN")
    void testGetBookByIsbn() throws Exception {
        // Arrange
        String isbn = "978-2-07-036822-8";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book1));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(isbn, result.getIsbn());
        verify(bookRepository, times(1)).findByIsbn(isbn);
    }

    @Test
    @DisplayName("Devrait mettre à jour un livre")
    void testUpdateBook() throws Exception {
        // Arrange
        Book updatedBook = new Book();
        updatedBook.setTitle("1984 - Édition révisée");
        updatedBook.setDescription("Nouvelle description");
        updatedBook.setPublished(true);
        updatedBook.setStock(10);
        updatedBook.setAuthor(author);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        // Act
        bookService.updateBook(1L, updatedBook);

        // Assert
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Devrait lever une exception si livre à mettre à jour n'existe pas")
    void testUpdateBook_NotFound() {
        // Arrange
        Book updatedBook = new Book();
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            bookService.updateBook(999L, updatedBook);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(bookRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Devrait supprimer un livre")
    void testDeleteBook() throws Exception {
        // Arrange
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        bookService.deleteBook(1L);

        // Assert
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait rechercher des livres entre deux dates")
    void testGetBooksByPublicationDateBetween() throws Exception {
        // Arrange
        Date startDate = new Date(0); // 1970
        Date endDate = new Date(); // Aujourd'hui

        when(bookRepository.findByPublicationDateBetween(startDate, endDate))
                .thenReturn(Arrays.asList(book1, book2));

        // Act
        List<Book> result = bookService.getBooksByPublicationDateBetween(startDate, endDate);

        // Assert
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findByPublicationDateBetween(startDate, endDate);
    }
}