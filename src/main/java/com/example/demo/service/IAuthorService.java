package com.example.demo.service;

import com.example.demo.entity.Author;

import java.util.List;

public interface IAuthorService {

    void createAuthor(Author author) throws Exception;

    Author getAuthorById(Long id) throws Exception;

    List<Author> getAllAuthors() throws Exception;

    Author getAuthorByFirstname(String firstname) throws Exception;

    Author getAuthorByLastname(String lastname) throws Exception;

    void updateAuthor(Long id, Author author) throws Exception;

    void deleteAuthor(Long id) throws Exception;
}