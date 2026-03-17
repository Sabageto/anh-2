package com.thunga.web.service;

import com.thunga.web.entity.*;
import com.thunga.web.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
     * Lưu comment mới với validation:
     * - User phải đã mua sách (order Completed)
     * - User chỉ được comment 1 lần cho mỗi sách
     * - Star là bắt buộc (1-5)
     * - Content là tuỳ chọn (có thể chỉ đánh sao)
     * - Nếu có content thì không được vượt 5000 ký tự
     */
    public Comment save(Comment comment, HttpServletRequest request) {
        Integer book_id = Integer.valueOf(request.getParameter("bookId"));
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

        // Validate star (bắt buộc)
        if (comment.getStar() == null || comment.getStar() < 1 || comment.getStar() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Validate content: tuỳ chọn, nhưng nếu có thì không được vượt 5000 ký tự
        String content = comment.getContent();
        if (content != null && content.trim().length() > 5000) {
            throw new IllegalArgumentException("Comment content cannot exceed 5000 characters");
        }

        comment.setBook(book);
        comment.setUser(user);
        comment.setOrderDetail(completedOrderDetail);
        comment.setContent(content != null ? content.trim() : "");
        comment.setCreated_at(new Date());
        comment.setUpdated_at(new Date());

        return commentRepository.save(comment);
    }

    /**
     * Cập nhật comment (chỉ owner mới được edit)
     * - Star là bắt buộc (1-5)
     * - Content là tuỳ chọn (có thể chỉ đánh sao)
     * - Nếu có content thì không được vượt 5000 ký tự
     *
     * @param commentId - ID của comment
     * @param content   - Nội dung mới (có thể null hoặc rỗng)
     * @param star      - Rating mới (bắt buộc 1-5)
     * @param request   - HttpServletRequest để lấy username hiện tại
     * @return Comment đã cập nhật
     * @throws IllegalStateException nếu user không phải owner
     */
    public Comment updateComment(Integer commentId, String content, Integer star, HttpServletRequest request) {
        // Lấy user hiện tại từ request
        String username = request.getRemoteUser();
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("User not authenticated");
        }

        Account account = accountService.findByUsername(username);
        if (account == null) {
            throw new IllegalStateException("Account not found");
        }

        User currentUser = userService.findByAccount(account);
        if (currentUser == null) {
            throw new IllegalStateException("User not found");
        }

        // Tìm comment
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            throw new IllegalStateException("Comment not found");
        }

        Comment comment = commentOpt.get();

        // CHECK OWNERSHIP - Chỉ owner mới được edit
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only edit your own reviews!");
        }

        // Validate star (bắt buộc)
        if (star == null || star < 1 || star > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Validate content: tuỳ chọn, nhưng nếu có thì không được vượt 5000 ký tự
        if (content != null && content.trim().length() > 5000) {
            throw new IllegalArgumentException("Comment content cannot exceed 5000 characters");
        }

        // Update comment
        comment.setContent(content != null ? content.trim() : "");
        comment.setStar(star);
        comment.setUpdated_at(new Date());

        return commentRepository.save(comment);
    }

    /**
     * Kiểm tra xem comment có phải của user hiện tại không
     */
    public boolean isCommentOwner(Integer commentId, User user) {
        if (user == null || commentId == null) {
            return false;
        }

        Optional<Comment> comment = commentRepository.findByIdAndUser(commentId, user);
        return comment.isPresent();
    }

    /**
     * Lấy comment by ID với ownership check
     */
    public Comment getCommentIfOwner(Integer commentId, User user) {
        Optional<Comment> comment = commentRepository.findByIdAndUser(commentId, user);
        if (!comment.isPresent()) {
            throw new IllegalStateException("Comment not found or you don't have permission to edit it");
        }
        return comment.get();
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

    /**
     * Tìm OrderDetail cho sách từ order đã hoàn thành
     */
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

    // ==================== HOME PAGE METHODS ====================

    /**
     * Lấy top rated comments/reviews cho home page
     */
    public List<Comment> getTopRatedComments(int limit) {
        try {
            List<Comment> allComments = commentRepository.findAll();

            if (allComments == null || allComments.isEmpty()) {
                return new ArrayList<>();
            }

            List<Comment> ratedComments = new ArrayList<>();
            for (Comment comment : allComments) {
                if (comment.getStar() != null && comment.getStar() >= 4 && comment.getStar() <= 5) {
                    if (comment.getBook() != null && comment.getUser() != null) {
                        ratedComments.add(comment);
                    }
                }
            }

            if (ratedComments.size() < limit) {
                for (Comment comment : allComments) {
                    if (comment.getStar() != null && comment.getStar() == 3) {
                        if (comment.getBook() != null && comment.getUser() != null) {
                            if (!ratedComments.contains(comment)) {
                                ratedComments.add(comment);
                            }
                        }
                    }
                }
            }

            ratedComments.sort((c1, c2) -> {
                Integer rating1 = c1.getStar() != null ? c1.getStar() : 0;
                Integer rating2 = c2.getStar() != null ? c2.getStar() : 0;
                int ratingCompare = rating2.compareTo(rating1);

                if (ratingCompare != 0) {
                    return ratingCompare;
                }

                if (c1.getCreated_at() == null && c2.getCreated_at() == null) return 0;
                if (c1.getCreated_at() == null) return 1;
                if (c2.getCreated_at() == null) return -1;
                return c2.getCreated_at().compareTo(c1.getCreated_at());
            });

            return ratedComments.size() > limit ?
                    ratedComments.subList(0, limit) : ratedComments;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy comments cho một sách cụ thể
     */
    public List<Comment> getContentsByBook(Book book) {
        try {
            if (book == null || book.getId() == null) {
                return new ArrayList<>();
            }

            return commentRepository.findByBook(book);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tính điểm đánh giá trung bình cho một sách
     */
    public Double getAverageRating(Book book) {
        try {
            if (book == null || book.getId() == null) {
                return 0.0;
            }

            List<Comment> comments = getContentsByBook(book);
            if (comments == null || comments.isEmpty()) {
                return 0.0;
            }

            int totalRating = 0;
            int count = 0;

            for (Comment comment : comments) {
                if (comment.getStar() != null) {
                    totalRating += comment.getStar();
                    count++;
                }
            }

            return count > 0 ? (double) totalRating / count : 0.0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Lấy số lượng comments cho một sách
     */
    public Integer getContentCount(Book book) {
        try {
            if (book == null || book.getId() == null) {
                return 0;
            }

            List<Comment> comments = getContentsByBook(book);
            return comments != null ? comments.size() : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy comments theo rating (1-5 sao)
     */
    public List<Comment> getContentsByRating(Integer rating) {
        try {
            if (rating == null || rating < 1 || rating > 5) {
                return new ArrayList<>();
            }

            List<Comment> allComments = commentRepository.findAll();
            List<Comment> ratedComments = new ArrayList<>();

            for (Comment comment : allComments) {
                if (comment.getStar() != null && comment.getStar().equals(rating)) {
                    ratedComments.add(comment);
                }
            }

            ratedComments.sort((c1, c2) -> {
                if (c1.getCreated_at() == null && c2.getCreated_at() == null) return 0;
                if (c1.getCreated_at() == null) return 1;
                if (c2.getCreated_at() == null) return -1;
                return c2.getCreated_at().compareTo(c1.getCreated_at());
            });

            return ratedComments;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}