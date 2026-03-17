package com.thunga.web.repository;

import com.thunga.web.entity.Book;
import com.thunga.web.entity.Comment;
import com.thunga.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    /**
     * Kiểm tra xem user đã comment cho book này chưa
     */
    boolean existsByUserAndBook(User user, Book book);

    /**
     * Tìm comment của user cho book cụ thể
     */
    List<Comment> findByBook(Book book);

    /**
     * Tìm comment cụ thể của user cho book (dùng để check ownership)
     */
    Optional<Comment> findByIdAndUser(Integer commentId, User user);

    /**
     * Tìm comment của user cho book (để check nếu đã edit)
     */
    Optional<Comment> findByUserAndBook(User user, Book book);

}