package com.thunga.web.repository;

import com.thunga.web.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Language findByName(String name);

    Language findByCode(String code);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.language.id = :languageId")
    int countBooksByLanguageId(@Param("languageId") Integer languageId);
}