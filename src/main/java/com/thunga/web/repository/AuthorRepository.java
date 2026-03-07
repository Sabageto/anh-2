package com.thunga.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunga.web.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
	Author findByName(String name);
}

