package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookService implements IBookService {

    @Autowired
    private IBookRepository bookRepository;

    @Override
    public void createBook(Book book) throws Exception {
        bookRepository.save(book);
    }

    @Override
    public List<Book> getUnpublishedBooks() throws Exception {
        return bookRepository.findByPublishedFalse();
    }

    @Override
    public Book getBookById(Long id) throws Exception {
        Optional<Book> optional = bookRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public List<Book> getAllBooks() throws Exception {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getPublishedBooks() throws Exception {
        return bookRepository.findByPublishedTrue();
    }

    @Override
    public Book getBookByTitle(String title) throws Exception {
        Optional<Book> optional = bookRepository.findByTitle(title);
        return optional.orElse(null);
    }

    @Override
    public List<Book> getBooksByTitleContaining(String keyword) throws Exception {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public Book getBookByIsbn(String isbn) throws Exception {
        Optional<Book> optional = bookRepository.findByIsbn(isbn);
        return optional.orElse(null);
    }

    @Override
    public List<Book> getBooksByTitleOrDescriptionContaining(String searchText) throws Exception {
        return bookRepository.findByTitleOrDescriptionContaining(searchText);
    }

    @Override
    public List<Book> getBooksByPublicationDateBetween(Date startDate, Date endDate) throws Exception {
        return bookRepository.findByPublicationDateBetween(startDate, endDate);
    }

    @Override
    public void updateBook(Long id, Book book) throws Exception {
        Optional<Book> optional = bookRepository.findById(id);
        if (optional.isPresent()) {
            Book existingBook = optional.get();
            existingBook.setTitle(book.getTitle());
            existingBook.setDescription(book.getDescription());
            existingBook.setPublished(book.isPublished());
            existingBook.setEditor(book.getEditor());
            existingBook.setPublicationDate(book.getPublicationDate());
            existingBook.setIsbn(book.getIsbn());
            existingBook.setNbPages(book.getNbPages());
            existingBook.setCategory(book.getCategory());
            existingBook.setLanguage(book.getLanguage());
            existingBook.setAuthor(book.getAuthor());
            bookRepository.save(existingBook);
        } else {
            throw new Exception("Book not found with id: " + id);
        }
    }

    @Override
    public void deleteBook(Long id) throws Exception {
        bookRepository.deleteById(id);
    }
}