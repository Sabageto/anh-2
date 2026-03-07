package com.thunga.web.service;

import com.thunga.web.entity.Category;
import com.thunga.web.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).get();
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Validate category name format
     */
    public void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        if (!name.matches("^[A-Za-z]+( [A-Za-z]+)*$")) {
            throw new IllegalArgumentException("Category name must contain only letters");
        }
    }

    /**
     * Validate new category (name must be unique)
     */
    public void validateNewCategory(String name) {
        validateCategoryName(name);

        Category existingCategory = categoryRepository.findByName(name);
        if (existingCategory != null) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
    }

    /**
     * Validate edit category (name must be unique except for current category)
     */
    public void validateEditCategory(Integer id, String newName) {
        validateCategoryName(newName);

        Category existingCategory = categoryRepository.findByName(newName);
        if (existingCategory != null && !existingCategory.getId().equals(id)) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
    }

    /**
     * Validate category level (0-2 only)
     */
    public void validateCategoryLevel(Integer level) {
        if (level == null) {
            throw new IllegalArgumentException("Please select a category level");
        }

        if (level < 0 || level > 2) {
            throw new IllegalArgumentException("Invalid category level. Must be 0, 1, or 2");
        }
    }

    /**
     * Validate parent-child relationship
     */
    public void validateParentChildRelationship(Integer level, Integer parentId) {
        if (level > 0) {
            if (parentId == null) {
                throw new IllegalArgumentException("Parent category is required for level " + level);
            }

            Category parentCategory = categoryRepository.findById(parentId).orElse(null);
            if (parentCategory == null) {
                throw new IllegalArgumentException("Parent category not found");
            }

            // Level 1 categories must have Level 0 parent
            if (level == 1 && parentCategory.getLevel() != 0) {
                throw new IllegalArgumentException("Level 1 categories must have a Level 0 parent");
            }

            // Level 2 categories must have Level 1 parent
            if (level == 2 && parentCategory.getLevel() != 1) {
                throw new IllegalArgumentException("Level 2 categories must have a Level 1 parent");
            }
        }
    }

    /**
     * Validate category can be deleted (no books, no children)
     */
    public void validateCategoryDeletion(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Check if category has books
        if (category.getBookList() != null && !category.getBookList().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete category with books. Please move or delete all books first.");
        }

        // Check if category has children
        List<Category> children = categoryRepository.findByParentId(categoryId);
        if (children != null && !children.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete category with child categories. Please delete child categories first.");
        }
    }

    // =====================================================
    // BUSINESS LOGIC METHODS
    // =====================================================

    /**
     * Create new category with full validation
     */
    @Transactional
    public Category createCategory(String name, Integer level, Integer parentId) {
        // Validate inputs
        validateCategoryName(name);
        validateCategoryLevel(level);
        validateNewCategory(name);
        validateParentChildRelationship(level, parentId);

        // Create category
        Category category = new Category();
        category.setName(name.trim());
        category.setLevel(level);
        category.setParentId(parentId);
        category.setCreated_at(new Date());
        category.setUpdated_at(new Date());

        return categoryRepository.save(category);
    }

    /**
     * Update category with full validation
     */
    @Transactional
    public Category updateCategory(Integer id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        // Validate
        validateEditCategory(id, newName);

        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Update
        category.setName(newName.trim());
        category.setUpdated_at(new Date());

        return categoryRepository.save(category);
    }

    /**
     * Delete category with full validation
     */
    @Transactional
    public void deleteCategory(Integer id) {
        validateCategoryDeletion(id);

        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            categoryRepository.delete(category);
        }
    }

    // =====================================================
    // BASIC CRUD OPERATIONS
    // =====================================================

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Category category) {
        categoryRepository.delete(category);
    }

    public Page<Category> findByLimit(Integer page, Integer limit, String sortBy) {
        Pageable paging = PageRequest.of(page, limit);
        if (sortBy != null) paging = PageRequest.of(page, limit, Sort.by(sortBy));
        Page<Category> categories = categoryRepository.findAll(paging);
        return categories;
    }

    // =====================================================
    // MENU DROPDOWN (Header)
    // =====================================================

    /**
     * Lấy cấu trúc phân cấp categories cho menu dropdown (header)
     */
    public Map<Category, List<Category>> getCategoryHierarchyForMenu() {
        List<Category> rootCategories = buildCategoryTree();
        Map<Category, List<Category>> hierarchy = new LinkedHashMap<>();

        for (Category root : rootCategories) {
            hierarchy.put(root, root.getChildren());
        }

        return hierarchy;
    }

    // =====================================================
    // SIDEBAR (Products page)
    // =====================================================

    /**
     * Lấy categories cho sidebar
     * - Nếu selectedCategoryId = null: chỉ trả về level 0
     * - Nếu có selectedCategoryId: trả về cây đầy đủ từ root category
     */
    public List<Category> getCategoriesForSidebar(Integer selectedCategoryId) {
        if (selectedCategoryId == null) {
            return getRootCategoriesWithCount();
        }

        Category selectedCategory = categoryRepository.findById(selectedCategoryId).orElse(null);
        Category displayCategory;

        // Xác định category hiển thị dựa trên level
        if (selectedCategory.getLevel() == 0) {
            displayCategory = selectedCategory;  // Level 0 → chính nó
        } else if (selectedCategory.getLevel() == 1) {
            displayCategory = selectedCategory;  // Level 1 → chính nó
        } else {
            // Level 2+ → tìm parent level 1
            displayCategory = findParentAtLevel(selectedCategory, 1);
        }

        buildCategoryTree(displayCategory);
        return Collections.singletonList(displayCategory);
    }

    private Category findParentAtLevel(Category category, int targetLevel) {
        while (category != null &&
                category.getLevel() > targetLevel &&
                category.getParentId() != null) {
            category = categoryRepository.findById(category.getParentId()).orElse(null);
        }
        return (category != null && category.getLevel() == targetLevel) ? category : null;
    }

    /**
     * Lấy tất cả root categories với book count
     */
    private List<Category> getRootCategoriesWithCount() {
        List<Category> roots = categoryRepository.findByLevel(0);
        for (Category root : roots) {
            root.setTotalBooks(countTotalBooksInCategory(root.getId()));
        }
        return roots;
    }

    /**
     * Build cây category đệ quy từ một node
     */
    private void buildCategoryTree(Category node) {
        // Lấy children trực tiếp
        List<Category> children = categoryRepository.findByParentId(node.getId());
        node.setChildren(children);
        node.setTotalBooks(countTotalBooksInCategory(node.getId()));

        // Đệ quy build cho mỗi child
        for (Category child : children) {
            buildCategoryTree(child);
        }
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Đếm tổng số sách trong một category và tất cả children của nó
     */
    public Integer countTotalBooksInCategory(Integer categoryId) {
        int total = 0;

        // Đếm sách trực tiếp trong category này
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null && category.getBookList() != null) {
            total += category.getBookList().size();
        }

        // Đệ quy đếm sách trong các children
        List<Category> children = categoryRepository.findByParentId(categoryId);
        for (Category child : children) {
            total += countTotalBooksInCategory(child.getId());
        }

        return total;
    }

    /**
     * Lấy tất cả categories ở level cụ thể
     */
    public List<Category> getCategoriesByLevel(Integer level) {
        return categoryRepository.findByLevel(level);
    }

    /**
     * Lấy children của một category
     */
    public List<Category> getChildCategories(Integer parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    /**
     * Build cây category đầy đủ (cho admin hoặc menu dropdown)
     */
    public List<Category> buildCategoryTree() {
        List<Category> allCategories = categoryRepository.findAll();
        Map<Integer, Category> categoryMap = new HashMap<>();
        List<Category> rootCategories = new ArrayList<>();

        // Tạo map để tra cứu nhanh
        for (Category category : allCategories) {
            category.setChildren(new ArrayList<>());
            category.setTotalBooks(countTotalBooksInCategory(category.getId()));
            categoryMap.put(category.getId(), category);
        }

        // Build tree
        for (Category category : allCategories) {
            if (category.getParentId() == null || category.getLevel() == 0) {
                rootCategories.add(category);
            } else {
                Category parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(category);
                }
            }
        }

        // Sắp xếp tất cả các level theo tên
        sortCategoryTree(rootCategories);

        return rootCategories;
    }

    /**
     * Lấy tất cả category IDs trong cây phân cấp (bao gồm chính nó và children)
     */
    public List<Integer> getAllCategoryIdsInHierarchy(Integer categoryId) {
        List<Integer> ids = new ArrayList<>();
        collectCategoryIds(categoryId, ids);
        return ids;
    }

    /**
     * Đệ quy thu thập tất cả category IDs
     */
    private void collectCategoryIds(Integer categoryId, List<Integer> ids) {
        // Thêm ID hiện tại
        ids.add(categoryId);

        // Lấy tất cả children
        List<Category> children = categoryRepository.findByParentId(categoryId);

        // Đệ quy cho mỗi child
        for (Category child : children) {
            collectCategoryIds(child.getId(), ids);
        }
    }

    /**
     * Sắp xếp cây category theo tên ở tất cả các level
     */
    private void sortCategoryTree(List<Category> categories) {
        if (categories == null) return;

        // Sắp xếp level hiện tại
        categories.sort(Comparator.comparing(Category::getName));

        // Sắp xếp đệ quy các children
        for (Category category : categories) {
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                sortCategoryTree(category.getChildren());
            }
        }
    }

    /**
     * Helper method to flatten category tree for display
     */
    public void flattenCategoryTree(List<Category> categories, List<Category> result) {
        for (Category category : categories) {
            result.add(category);
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                flattenCategoryTree(category.getChildren(), result);
            }
        }
    }
}