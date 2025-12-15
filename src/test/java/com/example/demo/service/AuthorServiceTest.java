package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.repository.IAuthorRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Author")
class AuthorServiceTest {

    @Mock
    private IAuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author1;
    private Author author2;

    @BeforeEach
    void setUp() {
        author1 = new Author();
        author1.setId(1L);
        author1.setFirstname("George");
        author1.setLastname("Orwell");

        author2 = new Author();
        author2.setId(2L);
        author2.setFirstname("Victor");
        author2.setLastname("Hugo");
    }

    @Test
    @DisplayName("Devrait créer un auteur")
    void testCreateAuthor() throws Exception {
        // Arrange
        when(authorRepository.save(author1)).thenReturn(author1);

        // Act
        authorService.createAuthor(author1);

        // Assert
        verify(authorRepository, times(1)).save(author1);
    }

    @Test
    @DisplayName("Devrait récupérer un auteur par ID")
    void testGetAuthorById() throws Exception {
        // Arrange
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));

        // Act
        Author result = authorService.getAuthorById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("George", result.getFirstname());
        assertEquals("Orwell", result.getLastname());
    }

    @Test
    @DisplayName("Devrait récupérer tous les auteurs")
    void testGetAllAuthors() throws Exception {
        // Arrange
        when(authorRepository.findAll()).thenReturn(Arrays.asList(author1, author2));

        // Act
        List<Author> result = authorService.getAllAuthors();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Devrait récupérer un auteur par prénom")
    void testGetAuthorByFirstname() throws Exception {
        // Arrange
        when(authorRepository.findByFirstname("George")).thenReturn(author1);

        // Act
        Author result = authorService.getAuthorByFirstname("George");

        // Assert
        assertNotNull(result);
        assertEquals("Orwell", result.getLastname());
    }

    @Test
    @DisplayName("Devrait supprimer un auteur")
    void testDeleteAuthor() throws Exception {
        // Arrange
        doNothing().when(authorRepository).deleteById(1L);

        // Act
        authorService.deleteAuthor(1L);

        // Assert
        verify(authorRepository, times(1)).deleteById(1L);
    }
}