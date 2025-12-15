package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {

    // 1 - Chercher tous les livres non publiés
    List<Book> findByPublishedFalse();

    // 2 - Chercher tous les livres publiés
    List<Book> findByPublishedTrue();

    // 3 - Rechercher un livre à partir de son titre
    Optional<Book> findByTitle(String title);

    // 4 - Rechercher tous les livres dont le titre contient une chaîne de caractères précise
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    // 5 - Rechercher un livre à partir de son ISBN
    Optional<Book> findByIsbn(String isbn);

    // 6 - Rechercher un livre dont le titre OU la description contient un texte précis
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Book> findByTitleOrDescriptionContaining(@Param("searchText") String searchText);

    // 7 - Rechercher tous les livres publiés entre deux dates précises
    List<Book> findByPublicationDateBetween(Date startDate, Date endDate);

    // Méthodes supplémentaires utiles
    List<Book> findByCategory(String category);
    List<Book> findByLanguage(String language);

    // Rechercher tous les livres d'un auteur spécifique
    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
}