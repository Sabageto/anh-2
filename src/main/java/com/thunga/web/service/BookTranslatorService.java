package com.thunga.web.service;

import com.thunga.web.entity.BookTranslator;
import com.thunga.web.repository.BookTranslatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookTranslatorService {

    @Autowired
    private BookTranslatorRepository bookTranslatorRepository;

    /**
     * Save a new BookTranslator record
     */
    public BookTranslator save(BookTranslator bookTranslator) {
        return bookTranslatorRepository.save(bookTranslator);
    }

    /**
     * Find all translators for a specific book
     */
    public List<BookTranslator> findByBookId(Integer bookId) {
        return bookTranslatorRepository.findByBookId(bookId);
    }

    /**
     * Delete all translator associations for a book
     */
    @Transactional
    public void deleteByBookId(Integer bookId) {
        bookTranslatorRepository.deleteByBookId(bookId);
    }

    /**
     * Delete a specific BookTranslator record
     */
    public void delete(BookTranslator bookTranslator) {
        bookTranslatorRepository.delete(bookTranslator);
    }

    /**
     * Find a BookTranslator by ID
     */
    public BookTranslator findById(Integer id) {
        return bookTranslatorRepository.findById(id).orElse(null);
    }

    /**
     * Check if a book-translator association exists
     */
    public boolean exists(Integer bookId, Integer translatorId) {
        return bookTranslatorRepository.existsByBookIdAndTranslatorId(bookId, translatorId);
    }
}