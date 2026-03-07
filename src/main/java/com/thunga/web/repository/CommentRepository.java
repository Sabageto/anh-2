package com.thunga.web.repository;

import com.thunga.web.entity.Book;
import com.thunga.web.entity.Comment;
import com.thunga.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    /**
     * Kiểm tra xem user đã comment cho book này chưa
     */
    boolean existsByUserAndBook(User user, Book book);

    /**
     * Tìm comment của user cho book cụ thể
     */
    Comment findByUserAndBook(User user, Book book);

}

