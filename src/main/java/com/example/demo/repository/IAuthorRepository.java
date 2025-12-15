package com.example.demo.repository;

import com.example.demo.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAuthorRepository extends JpaRepository<Author, Long> {

    // Trouver un auteur par prénom
    Author findByFirstname(String firstname);

    // Trouver un auteur par nom
    Author findByLastname(String lastname);

    // Trouver des auteurs par prénom et nom
    List<Author> findByFirstnameAndLastname(String firstname, String lastname);
}