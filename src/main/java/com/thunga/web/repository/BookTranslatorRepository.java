package com.thunga.web.repository;

import com.thunga.web.entity.BookTranslator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTranslatorRepository extends JpaRepository<BookTranslator, Integer> {

    /**
     * Find all translators for a specific book
     */
    List<BookTranslator> findByBookId(Integer bookId);

    /**
     * Delete all translator associations for a book
     */
    void deleteByBookId(Integer bookId);

    /**
     * Check if a book-translator association exists
     */
    boolean existsByBookIdAndTranslatorId(Integer bookId, Integer translatorId);
}