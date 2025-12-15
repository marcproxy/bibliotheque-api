package com.example.demo.service;

import com.example.demo.entity.Book;

import java.util.Date;
import java.util.List;

public interface IBookService {

    void createBook(Book book) throws Exception;
    List<Book> getUnpublishedBooks() throws Exception;
    Book getBookById(Long id) throws Exception;
    List<Book> getAllBooks() throws Exception;
    List<Book> getPublishedBooks() throws Exception;
    Book getBookByTitle(String title) throws Exception;
    List<Book> getBooksByTitleContaining(String keyword) throws Exception;
    Book getBookByIsbn(String isbn) throws Exception;
    List<Book> getBooksByTitleOrDescriptionContaining(String searchText) throws Exception;
    List<Book> getBooksByPublicationDateBetween(Date startDate, Date endDate) throws Exception;
    void updateBook(Long id, Book book) throws Exception;
    void deleteBook(Long id) throws Exception;
}