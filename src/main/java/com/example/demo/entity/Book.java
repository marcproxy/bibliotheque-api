package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "books", catalog = "biblio_database")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "editor", length = 100)
    private String editor;

    @Column(name = "publication_date")
    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    @Column(name = "isbn", unique = true, length = 20)
    private String isbn;

    @Column(name = "nb_pages")
    private Integer nbPages;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "language", length = 50)
    private String language;

    // NOUVEAU : Stock disponible pour les réservations
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    // Relation : Un livre a un auteur
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}