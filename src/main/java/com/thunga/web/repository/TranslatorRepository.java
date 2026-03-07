package com.thunga.web.repository;

import com.thunga.web.entity.Translator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslatorRepository extends JpaRepository<Translator, Integer> {

    Translator findByName(String name);

    @Query("SELECT COUNT(bt) FROM BookTranslator bt WHERE bt.translator.id = :translatorId")
    int countBooksByTranslatorId(@Param("translatorId") Integer translatorId);
}