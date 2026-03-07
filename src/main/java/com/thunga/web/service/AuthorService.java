package com.thunga.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.thunga.web.entity.Author;
import com.thunga.web.repository.AuthorRepository;

@Service
public class AuthorService {
    @Autowired AuthorRepository authorRepository;

    public List<Author> findAll(){
        return authorRepository.findAll();
    }

    public Author findById(Integer id) {
        return authorRepository.findById(id).get();
    }

    public Author findByName(String name) {
        return authorRepository.findByName(name);
    }

    public void validateAuthorName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }

        if (!name.matches("^[A-Za-z]+( [A-Za-z]+)*$")) {
            throw new IllegalArgumentException("Author name must contain only letters");
        }
    }

    public void validateNewAuthor(String name) {
        validateAuthorName(name);

        Author existingAuthor = authorRepository.findByName(name);
        if (existingAuthor != null) {
            throw new IllegalArgumentException("An author with this name already exists");
        }
    }

    public void validateEditAuthor(Integer id, String newName) {
        validateAuthorName(newName);

        Author existingAuthor = authorRepository.findByName(newName);
        if (existingAuthor != null && !existingAuthor.getId().equals(id)) {
            throw new IllegalArgumentException("An author with this name already exists");
        }
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public void delete(Author author) {
        authorRepository.delete(author);
    }

    public Page<Author> findByLimit(Integer page, Integer limit){
        Page<Author> authors = authorRepository.findAll(PageRequest.of(page, limit));
        return authors;
    }
}