package com.thunga.web.service;

import com.thunga.web.entity.Translator;
import com.thunga.web.repository.TranslatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslatorService {

    @Autowired
    private TranslatorRepository translatorRepository;

    public List<Translator> findAll() {
        return translatorRepository.findAll();
    }

    public Translator findById(Integer id) {
        return translatorRepository.findById(id).orElse(null);
    }

    public Translator findByName(String name) {
        return translatorRepository.findByName(name);
    }

    public Translator save(Translator translator) {
        return translatorRepository.save(translator);
    }

    public void delete(Translator translator) {
        translatorRepository.delete(translator);
    }

    public Page<Translator> findByLimit(Integer page, Integer limit) {
        Pageable paging = PageRequest.of(page, limit);
        return translatorRepository.findAll(paging);
    }

    public long countBooksByTranslatorId(Integer translatorId) {
        return translatorRepository.countBooksByTranslatorId(translatorId);
    }

    /**
     * Validate new translator.
     * Returns Map<fieldName, errorMessage> — empty = valid.
     * Duplicate names ARE allowed — translators are distinguished by ID.
     */
    public Map<String, String> validateNewTranslator(String name) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Translator name cannot be empty");
        } else if (!name.trim().matches("[\\p{L} ]+")) {
            errors.put("name", "Translator name must contain letters only");
        }
        return errors;
    }

    /**
     * Validate edit translator.
     * Returns Map<fieldName, errorMessage> — empty = valid.
     * No uniqueness check — translators with same name can coexist.
     */
    public Map<String, String> validateEditTranslator(Integer id, String newName) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (newName == null || newName.trim().isEmpty()) {
            errors.put("name", "Translator name cannot be empty");
        } else if (!newName.trim().matches("[\\p{L} ]+")) {
            errors.put("name", "Translator name must contain letters only");
        }
        return errors;
    }

    /**
     * Delete translator - cannot delete if it has books
     */
    public void deleteTranslator(Integer id) {
        Translator translator = findById(id);
        if (translator == null) {
            throw new IllegalArgumentException("Translator not found");
        }
        long bookCount = countBooksByTranslatorId(id);
        if (bookCount > 0) {
            throw new IllegalArgumentException(
                    "Cannot delete translator with " + bookCount + " book(s). Please reassign or remove books first.");
        }
        delete(translator);
    }

    /**
     * Create new translator
     */
    public Translator createTranslator(String name, String bio) {
        Map<String, String> errors = validateNewTranslator(name);
        if (!errors.isEmpty()) throw new IllegalArgumentException(errors.values().iterator().next());

        Translator translator = new Translator();
        translator.setName(name.trim());
        translator.setBio(bio != null ? bio.trim() : null);
        translator.setCreated_at(new Date());
        translator.setUpdated_at(new Date());

        return save(translator);
    }

    /**
     * Update translator
     */
    public Translator updateTranslator(Integer id, String newName, String bio) {
        Map<String, String> errors = validateEditTranslator(id, newName);
        if (!errors.isEmpty()) throw new IllegalArgumentException(errors.values().iterator().next());

        Translator translator = findById(id);
        if (translator == null) {
            throw new IllegalArgumentException("Translator not found");
        }

        translator.setName(newName.trim());
        translator.setBio(bio != null ? bio.trim() : null);
        translator.setUpdated_at(new Date());

        return save(translator);
    }
}