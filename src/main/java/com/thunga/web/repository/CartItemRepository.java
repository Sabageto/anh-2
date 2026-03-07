package com.thunga.web.repository;

import com.thunga.web.entity.Book;
import com.thunga.web.entity.CartItem;
import com.thunga.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Tìm tất cả CartItem của một user
    List<CartItem> findByUser(User user);

    // Tìm CartItem cụ thể của user và book
    Optional<CartItem> findByUserAndBook(User user, Book book);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_item WHERE user_id = :userId AND book_id = :bookId", nativeQuery = true)
    void deleteByUserAndBook(Integer userId, Integer bookId);

    // Kiểm tra user có CartItem nào không
    boolean existsByUser(User user);

    // Xóa tất cả CartItem của user
    @Modifying
    void deleteByUser(User user);

    // Tính tổng số sách trong giỏ của user
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.user = :user")
    Integer sumQuantityByUser(User user);

    // Tính tổng giá trị giỏ hàng của user
    @Query("SELECT COALESCE(SUM(ci.book.price * ci.quantity), 0) FROM CartItem ci WHERE ci.user = :user")
    Double sumTotalAmountByUser(User user);
}