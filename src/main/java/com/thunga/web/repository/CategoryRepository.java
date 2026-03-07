package com.thunga.web.repository;

import com.thunga.web.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);

    // Tìm categories theo level
    List<Category> findByLevel(Integer level);

    // Tìm children của một parent
    List<Category> findByParentId(Integer parentId);
}