package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.entity.Author;
import com.example.demo.service.IBookService;
import com.example.demo.service.IReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookService bookService;

    @MockBean
    private IReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setFirstname("George");
        author.setLastname("Orwell");

        book = new Book();
        book.setId(1L);
        book.setTitle("1984");
        book.setDescription("Roman dystopique");
        book.setPublished(true);
        book.setIsbn("978-2-07-036822-8");
        book.setStock(5);
        book.setAuthor(author);
    }

    @Test
    void testGetAllBooks() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(book);
        when(bookService.getAllBooks()).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("1984"))
                .andExpect(jsonPath("$[0].author.firstname").value("George"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() throws Exception {
        // Arrange
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act & Assert
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.isbn").value("978-2-07-036822-8"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        // Arrange
        when(bookService.getBookById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    void testCreateBook() throws Exception {
        // Arrange
        doNothing().when(bookService).createBook(any(Book.class));

        // Act & Assert
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book created successfully"));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void testGetPublishedBooks() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(book);
        when(bookService.getPublishedBooks()).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/api/books/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].published").value(true));

        verify(bookService, times(1)).getPublishedBooks();
    }

    @Test
    void testSearchBooksByTitle() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(book);
        when(bookService.getBooksByTitleContaining("1984")).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/api/books/search/title")
                        .param("keyword", "1984"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("1984"));

        verify(bookService, times(1)).getBooksByTitleContaining("1984");
    }

    @Test
    void testUpdateBook() throws Exception {
        // Arrange
        doNothing().when(bookService).updateBook(eq(1L), any(Book.class));

        // Act & Assert
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book updated successfully"));

        verify(bookService, times(1)).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void testDeleteBook() throws Exception {
        // Arrange
        doNothing().when(bookService).deleteBook(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"));

        verify(bookService, times(1)).deleteBook(1L);
    }
}