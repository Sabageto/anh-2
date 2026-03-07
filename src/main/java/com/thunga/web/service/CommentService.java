package com.thunga.web.service;

import com.thunga.web.entity.*;
import com.thunga.web.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    /**
     * Lưu comment với validation:
     * - User phải đã mua sách (order Completed)
     * - User chỉ được comment 1 lần cho mỗi sách
     */
    public Comment save(Comment comment, HttpServletRequest request) {
        Integer book_id = Integer.valueOf(request.getParameter("book_id"));
        String username = request.getParameter("username");

        Account account = accountService.findByUsername(username);
        User user = userService.findByAccount(account);
        Book book = bookService.findById(book_id);

        // Kiểm tra đã mua sách chưa
        OrderDetail completedOrderDetail = findCompletedOrderDetailForBook(user, book);
        if (completedOrderDetail == null) {
            throw new IllegalStateException(
                    "You can only review books that you have purchased and received (Completed orders only)!"
            );
        }

        // Kiểm tra đã comment chưa
        if (commentRepository.existsByUserAndBook(user, book)) {
            throw new IllegalStateException(
                    "You have already reviewed this book!"
            );
        }

        comment.setBook(book);
        comment.setUser(user);
        comment.setOrderDetail(completedOrderDetail); // ← SET ORDER DETAIL
        comment.setCreated_at(new Date());
        comment.setUpdated_at(new Date());

        return commentRepository.save(comment);
    }

    // ← THÊM METHOD MỚI
    private OrderDetail findCompletedOrderDetailForBook(User user, Book book) {
        if (user == null || user.getOrderList() == null) {
            return null;
        }

        for (Order order : user.getOrderList()) {
            if ("Completed".equals(order.getStatus())) {
                if (order.getOrderDetailList() != null) {
                    for (OrderDetail orderDetail : order.getOrderDetailList()) {
                        if (orderDetail.getBook().getId().equals(book.getId())) {
                            return orderDetail;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Kiểm tra user đã mua và nhận sách chưa
     */
    private boolean hasCompletedOrderWithBook(User user, Book book) {
        if (user == null || user.getOrderList() == null) {
            return false;
        }

        for (Order order : user.getOrderList()) {
            if ("Completed".equals(order.getStatus())) {
                if (order.getOrderDetailList() != null) {
                    for (OrderDetail orderDetail : order.getOrderDetailList()) {
                        if (orderDetail.getBook().getId().equals(book.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Kiểm tra user đã comment cho sách này chưa
     */
    public boolean hasUserCommented(User user, Book book) {
        if (user == null || book == null) {
            return false;
        }
        return commentRepository.existsByUserAndBook(user, book);
    }
}