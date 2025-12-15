package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.repository.IAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService implements IAuthorService {

    @Autowired
    private IAuthorRepository authorRepository;

    @Override
    public void createAuthor(Author author) throws Exception {
        authorRepository.save(author);
    }

    @Override
    public Author getAuthorById(Long id) throws Exception {
        Optional<Author> optional = authorRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public List<Author> getAllAuthors() throws Exception {
        return authorRepository.findAll();
    }

    @Override
    public Author getAuthorByFirstname(String firstname) throws Exception {
        return authorRepository.findByFirstname(firstname);
    }

    @Override
    public Author getAuthorByLastname(String lastname) throws Exception {
        return authorRepository.findByLastname(lastname);
    }

    @Override
    public void updateAuthor(Long id, Author author) throws Exception {
        Optional<Author> optional = authorRepository.findById(id);
        if (optional.isPresent()) {
            Author existingAuthor = optional.get();
            existingAuthor.setFirstname(author.getFirstname());
            existingAuthor.setLastname(author.getLastname());
            authorRepository.save(existingAuthor);
        } else {
            throw new Exception("Author not found with id: " + id);
        }
    }

    @Override
    public void deleteAuthor(Long id) throws Exception {
        authorRepository.deleteById(id);
    }
}