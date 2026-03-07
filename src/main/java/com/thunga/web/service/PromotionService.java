package com.thunga.web.service;

import com.thunga.web.entity.Promotion;
import com.thunga.web.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    // =====================================================
    // BUSINESS RULES - DISCOUNT LIMITS
    // =====================================================

    /**
     * Giới hạn discount cho ORDER_FIXED:
     * - discount_value ≤ min_order × 20%
     * <p>
     * Giới hạn discount cho PER_PRODUCT:
     * - discount_value ≤ min_order × 15%
     * <p>
     * Lưu ý: min_order được dùng như giá trị tham chiếu cho tính toán % tối đa
     */
    private static final double ORDER_FIXED_MAX_PERCENT = 0.20; // 20%
    private static final double PER_PRODUCT_MAX_PERCENT = 0.15; // 15%

    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    public Promotion findById(Integer id) {
        return promotionRepository.findById(id).orElse(null);
    }

    public Promotion findByCode(String code) {
        return promotionRepository.findByCode(code).orElse(null);
    }

    public Page<Promotion> findByLimit(Integer page, Integer limit, String sortBy) {
        Pageable paging;
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            paging = PageRequest.of(page, limit, Sort.by(sortBy));
        } else {
            paging = PageRequest.of(page, limit);
        }
        return promotionRepository.findAll(paging);
    }

    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public void delete(Promotion promotion) {
        promotionRepository.delete(promotion);
    }

    // =====================================================
    // VALIDATION CHO TẠO MỚI PROMOTION
    // =====================================================

    public void validateNewPromotion(String code, String name, Double discountValue,
                                     LocalDate startDate, LocalDate endDate,
                                     Integer maxUsage, Double minOrder, String discountType) {

        // Validate code
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion code cannot be empty");
        }

        code = code.trim();

        if (!code.matches("^SALE\\d{2}$")) {
            throw new IllegalArgumentException("Promotion code must be in format SALEXX (e.g., SALE10, SALE35)");
        }

        List<Promotion> existing = promotionRepository.findByCodeIgnoreCase(code);
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Promotion code already exists");
        }

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name cannot be empty");
        }

        // Validate discount type
        if (discountType == null || discountType.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount type cannot be empty");
        }

        if (!discountType.equals("PER_PRODUCT") && !discountType.equals("ORDER_FIXED")) {
            throw new IllegalArgumentException("Discount type must be PER_PRODUCT or ORDER_FIXED");
        }

        // Validate min order TRƯỚC discount value
        if (minOrder == null || minOrder <= 0) {
            throw new IllegalArgumentException("Min order value must be greater than 0");
        }

        // Validate discount value
        if (discountValue == null || discountValue <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than 0");
        }

        // ===== BUSINESS RULE: Discount limits based on type =====
        if (discountType.equals("ORDER_FIXED")) {
            // ORDER_FIXED: discount_value ≤ min_order × 20%
            double maxAllowed = minOrder * ORDER_FIXED_MAX_PERCENT;
            if (discountValue > maxAllowed) {
                throw new IllegalArgumentException(
                        String.format("For ORDER_FIXED: Discount value cannot exceed 20%% of min order (max: %,.0f VND)",
                                maxAllowed)
                );
            }
        } else if (discountType.equals("PER_PRODUCT")) {
            // PER_PRODUCT: discount_value ≤ min_order × 15%
            double maxAllowed = minOrder * PER_PRODUCT_MAX_PERCENT;
            if (discountValue > maxAllowed) {
                throw new IllegalArgumentException(
                        String.format("For PER_PRODUCT: Discount value cannot exceed 15%% of min order (max: %,.0f VND)",
                                maxAllowed)
                );
            }
        }

        // Validate dates
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be empty");
        }

        LocalDate currentDate = LocalDate.now();
        if (startDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after or equal to start date");
        }

        // Validate max usage
        if (maxUsage == null || maxUsage <= 0) {
            throw new IllegalArgumentException("Max usage must be greater than 0");
        }
    }

    // =====================================================
    // VALIDATION CHO EDIT PROMOTION
    // =====================================================

    public void validateEditPromotion(Integer id, String name, Double discountValue,
                                      LocalDate startDate, LocalDate endDate,
                                      Integer maxUsage, Double minOrder) {

        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion not found");
        }

        LocalDate currentDate = LocalDate.now();
        Integer usedCount = promotion.getUsedCount() != null ? promotion.getUsedCount() : 0;
        String discountType = promotion.getDiscountType();

        // Validate name (luôn được edit)
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name cannot be empty");
        }

        // Validate end_date (luôn được edit nhưng phải >= current_date)
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be empty");
        }

        if (endDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }

        // Validate max_usage (luôn được edit nhưng phải >= used_count)
        if (maxUsage == null || maxUsage <= 0) {
            throw new IllegalArgumentException("Max usage must be greater than 0");
        }

        if (maxUsage < usedCount) {
            throw new IllegalArgumentException("Max usage cannot be less than used count (" + usedCount + ")");
        }

        // ===== Validate discount_value và min_order (chỉ khi used_count = 0) =====
        if (usedCount > 0) {
            // Nếu đã được sử dụng, KHÔNG cho phép thay đổi discount_value và min_order
            if (!discountValue.equals(promotion.getDiscountValue())) {
                throw new IllegalArgumentException("Cannot change discount value when promotion has been used");
            }

            if (!minOrder.equals(promotion.getMinOrder())) {
                throw new IllegalArgumentException("Cannot change min order when promotion has been used");
            }
        } else {
            // Nếu chưa được sử dụng, validate discount_value và min_order

            // Validate min_order
            if (minOrder == null || minOrder <= 0) {
                throw new IllegalArgumentException("Min order value must be greater than 0");
            }

            // Validate discount_value
            if (discountValue == null || discountValue <= 0) {
                throw new IllegalArgumentException("Discount value must be greater than 0");
            }

            // ===== BUSINESS RULE: Discount limits based on type =====
            if (discountType.equals("ORDER_FIXED")) {
                // ORDER_FIXED: discount_value ≤ min_order × 20%
                double maxAllowed = minOrder * ORDER_FIXED_MAX_PERCENT;
                if (discountValue > maxAllowed) {
                    throw new IllegalArgumentException(
                            String.format("For ORDER_FIXED: Discount value cannot exceed 20%% of min order (max: %,.0f VND)",
                                    maxAllowed)
                    );
                }
            } else if (discountType.equals("PER_PRODUCT")) {
                // PER_PRODUCT: discount_value ≤ min_order × 15%
                double maxAllowed = minOrder * PER_PRODUCT_MAX_PERCENT;
                if (discountValue > maxAllowed) {
                    throw new IllegalArgumentException(
                            String.format("For PER_PRODUCT: Discount value cannot exceed 15%% of min order (max: %,.0f VND)",
                                    maxAllowed)
                    );
                }
            }
        }

        // Validate start_date (chỉ được edit khi current_date < start_date)
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be empty");
        }

        if (!startDate.equals(promotion.getStartDate())) {
            if (!currentDate.isBefore(promotion.getStartDate())) {
                throw new IllegalArgumentException("Cannot change start date when promotion has already started");
            }

            if (startDate.isBefore(currentDate)) {
                throw new IllegalArgumentException("Start date cannot be in the past");
            }

            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after or equal to start date");
            }
        }
    }

    // =====================================================
    // DELETE PROMOTION
    // =====================================================

    /**
     * Xóa promotion khỏi hệ thống
     * Điều kiện:
     * - used_count = 0
     * - promotion_id chưa từng xuất hiện trong order_detail
     */
    public void deletePromotion(Integer id) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion not found");
        }

        // Kiểm tra used_count
        if (promotion.getUsedCount() != null && promotion.getUsedCount() > 0) {
            throw new IllegalArgumentException("Cannot delete promotion that has been used (used count: " + promotion.getUsedCount() + ")");
        }

        // Kiểm tra promotion_id có trong order_detail không
        if (promotionRepository.existsInOrderDetail(id)) {
            throw new IllegalArgumentException("Cannot delete promotion that appears in order history");
        }

        // Xóa promotion
        promotionRepository.delete(promotion);
    }

    // =====================================================
    // HELPER METHODS - Tính toán max discount allowed
    // =====================================================

    /**
     * Tính discount value tối đa cho phép dựa trên discount type và min order
     */
    public Double calculateMaxDiscountAllowed(String discountType, Double minOrder) {
        if (minOrder == null || minOrder <= 0) {
            return 0.0;
        }

        if ("ORDER_FIXED".equals(discountType)) {
            return minOrder * ORDER_FIXED_MAX_PERCENT;
        } else if ("PER_PRODUCT".equals(discountType)) {
            return minOrder * PER_PRODUCT_MAX_PERCENT;
        }

        return 0.0;
    }

    /**
     * Lấy % tối đa cho discount type
     */
    public Double getMaxPercentForType(String discountType) {
        if ("ORDER_FIXED".equals(discountType)) {
            return ORDER_FIXED_MAX_PERCENT * 100; // Convert to percentage
        } else if ("PER_PRODUCT".equals(discountType)) {
            return PER_PRODUCT_MAX_PERCENT * 100;
        }
        return 0.0;
    }

    // =====================================================
    // CÁC PHƯƠNG THỨC KHÁC (GIỮ NGUYÊN LOGIC CŨ)
    // =====================================================

    /**
     * Kiểm tra promotion có hợp lệ hay không (cho toàn đơn hàng)
     */
    public boolean isPromotionValid(Promotion promotion, Double orderTotal) {
        if (promotion == null) {
            return false;
        }

        LocalDate currentDate = LocalDate.now();

        if (!"ACTIVE".equals(promotion.getStatus())) {
            return false;
        }

        if (currentDate.isBefore(promotion.getStartDate()) ||
                currentDate.isAfter(promotion.getEndDate())) {
            return false;
        }

        if (promotion.getUsedCount() >= promotion.getMaxUsage()) {
            return false;
        }

        if (orderTotal < promotion.getMinOrder()) {
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra sản phẩm có thuộc promotion không
     */
    public boolean isBookInPromotion(Integer promotionId, Integer bookId) {
        if (promotionId == null || bookId == null) {
            return false;
        }

        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);

        if (promotion == null || promotion.getPromotionBooks() == null) {
            return false;
        }

        return promotion.getPromotionBooks().stream()
                .anyMatch(pb -> pb.getBook() != null && pb.getBook().getId().equals(bookId));
    }

    /**
     * Lấy danh sách book IDs thuộc promotion
     */
    public List<Integer> getBookIdsInPromotion(Integer promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);

        if (promotion == null || promotion.getPromotionBooks() == null) {
            return List.of();
        }

        return promotion.getPromotionBooks().stream()
                .filter(pb -> pb.getBook() != null)
                .map(pb -> pb.getBook().getId())
                .collect(Collectors.toList());
    }

    /**
     * Tìm promotion hợp lệ theo code
     */
    public Promotion findValidPromotionByCode(String code, Double orderTotal) {
        Promotion promotion = promotionRepository.findByCode(code).orElse(null);

        if (promotion != null && isPromotionValid(promotion, orderTotal)) {
            return promotion;
        }

        return null;
    }

    /**
     * Áp dụng promotion cho đơn hàng (tăng used_count)
     */
    public void applyPromotion(Promotion promotion) {
        if (promotion != null) {
            promotion.setUsedCount(promotion.getUsedCount() + 1);
            promotionRepository.save(promotion);
        }
    }

    /**
     * Kiểm tra promotion hợp lệ cho đơn hàng (dùng cho ORDER_FIXED)
     */
    public boolean isPromotionValidForOrder(Promotion promotion, Double orderTotal) {
        if (promotion == null) return false;

        LocalDate currentDate = LocalDate.now();

        if (!"ACTIVE".equals(promotion.getStatus())) return false;
        if (currentDate.isBefore(promotion.getStartDate()) ||
                currentDate.isAfter(promotion.getEndDate())) return false;
        if (promotion.getUsedCount() >= promotion.getMaxUsage()) return false;

        if (orderTotal < promotion.getMinOrder()) return false;

        return true;
    }

    /**
     * Kiểm tra promotion hợp lệ cho sản phẩm (dùng cho PER_PRODUCT)
     */
    public boolean isPromotionValidForBook(Promotion promotion, Integer bookId) {
        if (promotion == null || bookId == null) return false;

        LocalDate currentDate = LocalDate.now();

        if (!"ACTIVE".equals(promotion.getStatus())) return false;
        if (currentDate.isBefore(promotion.getStartDate()) ||
                currentDate.isAfter(promotion.getEndDate())) return false;
        if (promotion.getUsedCount() >= promotion.getMaxUsage()) return false;

        if (!isBookInPromotion(promotion.getPromotionId(), bookId)) return false;

        return true;
    }

    /**
     * Tính discount cho sản phẩm (PER_PRODUCT)
     */
    public Double calculateDiscountForBook(Promotion promotion, Integer bookId) {
        if (!isPromotionValidForBook(promotion, bookId)) {
            return 0.0;
        }

        return promotion.getDiscountValue();
    }

    /**
     * Tính discount cho toàn đơn (ORDER_FIXED)
     */
    public Double calculateOrderDiscount(Promotion promotion, Double orderTotal) {
        if (!isPromotionValidForOrder(promotion, orderTotal)) {
            return 0.0;
        }

        return promotion.getDiscountValue();
    }

    /**
     * Tính tổng giảm giá (dùng cho toàn đơn - deprecated khi dùng logic mới)
     */
    public Double calculateDiscount(Promotion promotion, Double orderTotal) {
        if (promotion == null || !isPromotionValid(promotion, orderTotal)) {
            return 0.0;
        }
        return promotion.getDiscountValue();
    }
}