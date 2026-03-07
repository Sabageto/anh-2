package com.thunga.web.service;

import com.thunga.web.entity.*;
import com.thunga.web.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private SeriesService seriesService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Integer id) {
        return orderRepository.findById(id).get();
    }

    /**
     * NEW: Detect complete series and count how many sets
     * Returns Map<SeriesId, NumberOfCompleteSets>
     * <p>
     * Logic:
     * - Tìm minimum quantity của các cuốn trong series
     * - Nếu min = 1 → 1 bộ → giảm 5%
     * - Nếu min >= 2 → ≥2 bộ → giảm 10%
     */
    public Map<Integer, Integer> detectCompleteSeriesWithSets(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return new HashMap<>();
        }

        Map<Integer, Integer> seriesSetsMap = new HashMap<>();

        // Group cart items by series
        Map<Integer, List<CartItem>> itemsBySeries = new HashMap<>();
        for (CartItem item : cartItems) {
            if (item.getBook().getSeries() != null) {
                Integer seriesId = item.getBook().getSeries().getId();
                itemsBySeries.computeIfAbsent(seriesId, k -> new ArrayList<>()).add(item);
            }
        }

        // Check each series
        for (Map.Entry<Integer, List<CartItem>> entry : itemsBySeries.entrySet()) {
            Integer seriesId = entry.getKey();
            List<CartItem> seriesItems = entry.getValue();

            try {
                // Check if this series can be purchased as bundle
                if (!seriesService.canPurchaseFullSeries(seriesId)) {
                    continue;
                }

                // Get required books
                List<Book> requiredBooks = seriesService.getAvailableBooksInSeries(seriesId);
                Set<Integer> requiredBookIds = requiredBooks.stream()
                        .map(Book::getId)
                        .collect(Collectors.toSet());

                // Get book IDs in cart
                Set<Integer> cartBookIds = seriesItems.stream()
                        .map(item -> item.getBook().getId())
                        .collect(Collectors.toSet());

                // Check if user has all volumes
                if (cartBookIds.containsAll(requiredBookIds)) {
                    // Find minimum quantity across all volumes
                    int minQuantity = Integer.MAX_VALUE;
                    for (CartItem item : seriesItems) {
                        if (requiredBookIds.contains(item.getBook().getId())) {
                            minQuantity = Math.min(minQuantity, item.getQuantity());
                        }
                    }

                    if (minQuantity > 0) {
                        seriesSetsMap.put(seriesId, minQuantity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return seriesSetsMap;
    }

    /**
     * Calculate discount percentage based on number of sets
     * 1 set → 5%
     * ≥2 sets → 10% (maximum)
     */
    private double calculateSeriesDiscountPercentage(int numberOfSets) {
        if (numberOfSets >= 2) {
            return 0.10; // 10% for 2+ sets
        } else if (numberOfSets == 1) {
            return 0.05; // 5% for 1 set
        }
        return 0.0;
    }

    /**
     * Calculate series bundle discount for selected items
     */
    public Double calculateSeriesBundleDiscount(
            List<CartItem> selectedCartItems,
            Map<Integer, Integer> seriesSetsMap
    ) {
        if (seriesSetsMap == null || seriesSetsMap.isEmpty()) {
            return 0.0;
        }

        double totalDiscount = 0.0;

        // Group items by series
        Map<Integer, List<CartItem>> itemsBySeries = new HashMap<>();
        for (CartItem item : selectedCartItems) {
            if (item.getBook().getSeries() != null) {
                Integer seriesId = item.getBook().getSeries().getId();
                if (seriesSetsMap.containsKey(seriesId)) {
                    itemsBySeries.computeIfAbsent(seriesId, k -> new ArrayList<>()).add(item);
                }
            }
        }

        // Calculate discount for each series
        for (Map.Entry<Integer, Integer> entry : seriesSetsMap.entrySet()) {
            Integer seriesId = entry.getKey();
            Integer numberOfSets = entry.getValue();

            // Determine discount percentage
            double discountPercentage = numberOfSets >= 2 ? 0.10 : 0.05;

            List<CartItem> seriesItems = itemsBySeries.get(seriesId);
            if (seriesItems == null) continue;

            // Calculate subtotal for complete sets only
            double seriesSubtotal = 0.0;
            for (CartItem item : seriesItems) {
                int discountedQuantity = Math.min(item.getQuantity(), numberOfSets);
                seriesSubtotal += item.getBook().getPrice() * discountedQuantity;
            }

            totalDiscount += seriesSubtotal * discountPercentage;
        }

        return Math.round(totalDiscount * 10.0) / 10.0;
    }

    /**
     * Tạo Order từ CartItem với promotion, payment method và shipping fee support
     * <p>
     * PRIORITY LOGIC:
     * 1. Series Bundle (5%-10% discount) - CANNOT combine with promotion
     * - 1 bộ → 5%
     * - ≥2 bộ → 10%
     * 2. Promotion codes - ONLY for orders WITHOUT series bundle
     * <p>
     * IMPORTANT: Only complete sets get discount. Extra copies are charged full price.
     */
    @Transactional
    public Order createOrderFromSelectedItems(
            List<CartItem> selectedItems,
            String fullname,
            String phone,
            String address,
            String promotionCode,
            String paymentMethod,
            Double shippingFee) {

        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new IllegalStateException("No items selected!");
        }

        validateCustomerInfo(fullname, phone, address);

        Order order = new Order();
        order.setUser(selectedItems.get(0).getUser());
        order.setCustomer_name(fullname);
        order.setPhone(phone);
        order.setAddress(address);
        order.setStatus("Pending");
        order.setCreatedAt(LocalDate.now());
        // ========== SET PAYMENT METHOD AND SHIPPING FEE ==========
        order.setPayment_method(paymentMethod != null ? paymentMethod : "COD");
        order.setPayment_status("UNPAID");
        order.setShipping_fee(shippingFee != null ? shippingFee : 20000.0);

        double totalCost = 0.0;

        // ========== STEP 1: DETECT SERIES BUNDLE WITH SETS ==========
        Map<Integer, Integer> seriesSetsMap = detectCompleteSeriesWithSets(selectedItems);
        boolean isSeriesBundle = !seriesSetsMap.isEmpty();

        // ========== STEP 2: HANDLE PROMOTION ==========
        Promotion promotion = null;

        // ONLY allow promotion when NO series bundle
        if (!isSeriesBundle && promotionCode != null && !promotionCode.trim().isEmpty()) {
            promotion = promotionService.findByCode(promotionCode);

            if (promotion == null) {
                throw new IllegalStateException("Promotion code not found!");
            }

            if (!"ACTIVE".equals(promotion.getStatus())) {
                throw new IllegalStateException("Promotion is not active!");
            }

            if (promotion.getUsedCount() >= promotion.getMaxUsage()) {
                throw new IllegalStateException("Promotion has reached its usage limit!");
            }
        } else if (isSeriesBundle && promotionCode != null && !promotionCode.trim().isEmpty()) {
            // BLOCK promotion if series bundle detected
            int maxSets = Collections.max(seriesSetsMap.values());
            String discountMsg = maxSets >= 2 ? "10%" : "5%";
            throw new IllegalStateException(
                    "Promotion codes cannot be used with series bundles. " +
                            "You are already receiving a " + discountMsg + " series discount!");
        }

        boolean promotionApplied = false;

        // ========== STEP 3: PROCESS BASED ON PRIORITY ==========

        if (isSeriesBundle) {
            // ========== CASE 1: SERIES BUNDLE (5%-10% DISCOUNT) ==========

            // Group selected items by series
            Map<Integer, List<CartItem>> itemsBySeries = new HashMap<>();
            List<CartItem> nonSeriesItems = new ArrayList<>();

            for (CartItem item : selectedItems) {
                if (item.getBook().getSeries() != null &&
                        seriesSetsMap.containsKey(item.getBook().getSeries().getId())) {
                    Integer seriesId = item.getBook().getSeries().getId();
                    itemsBySeries.computeIfAbsent(seriesId, k -> new ArrayList<>()).add(item);
                } else {
                    nonSeriesItems.add(item);
                }
            }

            // Process each series bundle
            for (Map.Entry<Integer, Integer> seriesEntry : seriesSetsMap.entrySet()) {
                Integer seriesId = seriesEntry.getKey();
                Integer numberOfSets = seriesEntry.getValue();

                Series series = seriesService.findById(seriesId);
                double discountPercentage = calculateSeriesDiscountPercentage(numberOfSets);

                List<CartItem> seriesCartItems = itemsBySeries.get(seriesId);
                if (seriesCartItems == null) continue;

                // Calculate total for complete sets only
                double seriesSubtotalForDiscount = 0.0;
                for (CartItem item : seriesCartItems) {
                    // Only count quantity up to numberOfSets (complete sets)
                    int discountedQuantity = Math.min(item.getQuantity(), numberOfSets);
                    seriesSubtotalForDiscount += item.getBook().getPrice() * discountedQuantity;
                }

                double totalSeriesDiscount = seriesSubtotalForDiscount * discountPercentage;
                double remainingDiscount = totalSeriesDiscount;

                // Process each book in series
                for (int i = 0; i < seriesCartItems.size(); i++) {
                    CartItem cartItem = seriesCartItems.get(i);
                    Book book = bookService.findById(cartItem.getBook().getId());
                    Integer totalQuantity = cartItem.getQuantity();

                    // Validate stock
                    if (book.getNumber_in_stock() < totalQuantity) {
                        throw new IllegalStateException(
                                "Book '" + book.getTitle() + "' only has " +
                                        book.getNumber_in_stock() + " copies left!");
                    }

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setBook(book);
                    orderDetail.setNumber(totalQuantity);

                    double itemTotal = book.getPrice() * totalQuantity;
                    orderDetail.setTotal_cost(itemTotal);

                    // Calculate discount
                    // Only first numberOfSets get discount
                    int discountedQuantity = Math.min(totalQuantity, numberOfSets);
                    double discountableAmount = book.getPrice() * discountedQuantity;

                    double itemDiscount;
                    if (i == seriesCartItems.size() - 1) {
                        // Last item gets remaining discount to avoid rounding errors
                        itemDiscount = remainingDiscount;
                    } else {
                        // Proportional discount based on discountable amount
                        itemDiscount = (discountableAmount / seriesSubtotalForDiscount) * totalSeriesDiscount;
                        itemDiscount = Math.round(itemDiscount * 10.0) / 10.0;
                        remainingDiscount -= itemDiscount;
                    }

                    // Mark as series bundle
                    orderDetail.setSeries(series);
                    orderDetail.setIsFullSeries(true);
                    orderDetail.setPromotion(null);
                    orderDetail.setDiscountAmount(itemDiscount);

                    orderDetail.setCreated_at(new Date());
                    orderDetail.setUpdated_at(new Date());

                    order.addOrderDetail(orderDetail);
                    totalCost += (itemTotal - itemDiscount);
                }
            }

            // Process non-series items (no discount)
            for (CartItem cartItem : nonSeriesItems) {
                Book book = bookService.findById(cartItem.getBook().getId());
                Integer quantity = cartItem.getQuantity();

                if (book.getNumber_in_stock() < quantity) {
                    throw new IllegalStateException(
                            "Book '" + book.getTitle() + "' only has " +
                                    book.getNumber_in_stock() + " copies left!");
                }

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setBook(book);
                orderDetail.setNumber(quantity);

                double itemTotal = book.getPrice() * quantity;
                orderDetail.setTotal_cost(itemTotal);
                orderDetail.setSeries(null);
                orderDetail.setIsFullSeries(false);
                orderDetail.setPromotion(null);
                orderDetail.setDiscountAmount(0.0);

                orderDetail.setCreated_at(new Date());
                orderDetail.setUpdated_at(new Date());

                order.addOrderDetail(orderDetail);
                totalCost += itemTotal;
            }

        } else if (promotion != null) {
            // ========== CASE 2: PROMOTION (NO SERIES BUNDLE) ==========

            // Calculate order total before discount
            double orderTotalBeforeDiscount = 0.0;
            for (CartItem cartItem : selectedItems) {
                Book book = bookService.findById(cartItem.getBook().getId());
                orderTotalBeforeDiscount += book.getPrice() * cartItem.getQuantity();
            }

            // Check min order
            if (orderTotalBeforeDiscount < promotion.getMinOrder()) {
                throw new IllegalStateException(
                        "Your order total (" + String.format("%.1f", orderTotalBeforeDiscount) +
                                " VND) must be at least " + String.format("%.1f", promotion.getMinOrder()) +
                                " VND to use this promotion!");
            }

            String discountType = promotion.getDiscountType();

            if ("ORDER_FIXED".equals(discountType)) {
                // ========== TYPE 1: FIXED DISCOUNT FOR ENTIRE ORDER ==========
                double orderDiscount = promotionService.calculateOrderDiscount(
                        promotion,
                        orderTotalBeforeDiscount
                );

                if (orderDiscount > 0) {
                    double remainingDiscount = orderDiscount;

                    for (int i = 0; i < selectedItems.size(); i++) {
                        CartItem cartItem = selectedItems.get(i);
                        Book book = bookService.findById(cartItem.getBook().getId());
                        Integer quantity = cartItem.getQuantity();

                        if (book.getNumber_in_stock() < quantity) {
                            throw new IllegalStateException(
                                    "Book '" + book.getTitle() + "' only has " +
                                            book.getNumber_in_stock() + " copies left!");
                        }

                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setBook(book);
                        orderDetail.setNumber(quantity);

                        double itemTotal = book.getPrice() * quantity;
                        orderDetail.setTotal_cost(itemTotal);

                        double itemDiscount;
                        if (i == selectedItems.size() - 1) {
                            itemDiscount = remainingDiscount;
                        } else {
                            double itemDiscountRatio = itemTotal / orderTotalBeforeDiscount;
                            itemDiscount = itemDiscountRatio * orderDiscount;
                            itemDiscount = Math.round(itemDiscount * 10.0) / 10.0;
                            remainingDiscount -= itemDiscount;
                        }

                        orderDetail.setSeries(null);
                        orderDetail.setIsFullSeries(false);
                        orderDetail.setPromotion(promotion);
                        orderDetail.setDiscountAmount(itemDiscount);

                        orderDetail.setCreated_at(new Date());
                        orderDetail.setUpdated_at(new Date());

                        order.addOrderDetail(orderDetail);
                        totalCost += (itemTotal - itemDiscount);
                    }

                    promotionApplied = true;
                }

            } else if ("PER_PRODUCT".equals(discountType)) {
                // ========== TYPE 2: DISCOUNT PER SPECIFIC PRODUCT ==========
                for (CartItem cartItem : selectedItems) {
                    Book book = bookService.findById(cartItem.getBook().getId());
                    Integer quantity = cartItem.getQuantity();

                    if (book.getNumber_in_stock() < quantity) {
                        throw new IllegalStateException(
                                "Book '" + book.getTitle() + "' only has " +
                                        book.getNumber_in_stock() + " copies left!");
                    }

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setBook(book);
                    orderDetail.setNumber(quantity);

                    double itemTotal = book.getPrice() * quantity;
                    orderDetail.setTotal_cost(itemTotal);

                    double discountPerUnit = promotionService.calculateDiscountForBook(
                            promotion,
                            book.getId()
                    );

                    double itemDiscount = 0.0;
                    if (discountPerUnit > 0) {
                        itemDiscount = discountPerUnit * quantity;
                        itemDiscount = Math.round(itemDiscount * 10.0) / 10.0;
                        orderDetail.setPromotion(promotion);
                        orderDetail.setDiscountAmount(itemDiscount);
                        promotionApplied = true;
                    } else {
                        orderDetail.setPromotion(null);
                        orderDetail.setDiscountAmount(0.0);
                    }

                    orderDetail.setSeries(null);
                    orderDetail.setIsFullSeries(false);
                    orderDetail.setCreated_at(new Date());
                    orderDetail.setUpdated_at(new Date());

                    order.addOrderDetail(orderDetail);
                    totalCost += (itemTotal - itemDiscount);
                }
            }

            // Check if promotion was supposed to apply but didn't
            if (!promotionApplied) {
                if ("PER_PRODUCT".equals(promotion.getDiscountType())) {
                    throw new IllegalStateException(
                            "None of your selected products are eligible for this promotion!");
                } else {
                    throw new IllegalStateException(
                            "Cannot apply this promotion to your order!");
                }
            }

        } else {
            // ========== CASE 3: NO DISCOUNT ==========
            for (CartItem cartItem : selectedItems) {
                Book book = bookService.findById(cartItem.getBook().getId());
                Integer quantity = cartItem.getQuantity();

                if (book.getNumber_in_stock() < quantity) {
                    throw new IllegalStateException(
                            "Book '" + book.getTitle() + "' only has " +
                                    book.getNumber_in_stock() + " copies left!");
                }

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setBook(book);
                orderDetail.setNumber(quantity);

                double itemTotal = book.getPrice() * quantity;
                orderDetail.setTotal_cost(itemTotal);
                orderDetail.setSeries(null);
                orderDetail.setIsFullSeries(false);
                orderDetail.setPromotion(null);
                orderDetail.setDiscountAmount(0.0);

                orderDetail.setCreated_at(new Date());
                orderDetail.setUpdated_at(new Date());

                order.addOrderDetail(orderDetail);
                totalCost += itemTotal;
            }
        }

        // Round total cost to 1 decimal place (subtotal only, NOT including shipping)
        order.setTotal_cost(Math.round(totalCost * 10.0) / 10.0);
        return order;
    }

    @Transactional
    public Order save(Order order) {
        boolean isNewOrder = (order.getId() == null);

        if (isNewOrder) {
            // Validate stock
            if (order.getOrderDetailList() != null && !order.getOrderDetailList().isEmpty()) {
                for (OrderDetail orderDetail : order.getOrderDetailList()) {
                    Book book = bookService.findById(orderDetail.getBook().getId());
                    Integer quantity = orderDetail.getNumber();

                    if (quantity == null || quantity <= 0) {
                        throw new IllegalStateException(
                                "Invalid quantity for book: " + book.getTitle()
                        );
                    }

                    if (book.getNumber_in_stock() < quantity) {
                        throw new IllegalStateException(
                                "Book '" + book.getTitle() + "' only has " +
                                        book.getNumber_in_stock() + " copies left!");
                    }
                }
            }

            Order savedOrder = orderRepository.save(order);

            // Update stock and increment used_count for promotion
            if (savedOrder.getOrderDetailList() != null) {
                boolean promotionUsed = false;
                Promotion usedPromotion = null;

                for (OrderDetail orderDetail : savedOrder.getOrderDetailList()) {
                    Book book = orderDetail.getBook();
                    Integer quantity = orderDetail.getNumber();

                    // Update stock
                    book.setNumber_in_stock(book.getNumber_in_stock() - quantity);
                    book.setNumber_sold(book.getNumber_sold() + quantity);
                    book.setUpdated_at(new Date());
                    bookService.save(book);

                    // Save promotion to increment used_count (only once per order)
                    if (orderDetail.getPromotion() != null && !promotionUsed) {
                        usedPromotion = orderDetail.getPromotion();
                        promotionUsed = true;
                    }
                }

                // Increment used_count once for entire order
                if (usedPromotion != null) {
                    promotionService.applyPromotion(usedPromotion);
                }
            }

            return savedOrder;
        } else {
            return orderRepository.save(order);
        }
    }

    @Transactional
    public void cancelOrder(Integer orderId) {
        Order order = findById(orderId);

        if (!"Pending".equals(order.getStatus())) {
            throw new IllegalStateException("Only pending orders can be cancelled!");
        }

        order.setStatus("Cancelled");
        order.setUpdated_at(LocalDate.now());

        if (order.getOrderDetailList() != null) {
            for (OrderDetail orderDetail : order.getOrderDetailList()) {
                Book book = bookService.findById(orderDetail.getBook().getId());
                Integer quantity = orderDetail.getNumber();

                book.setNumber_in_stock(book.getNumber_in_stock() + quantity);
                book.setNumber_sold(book.getNumber_sold() - quantity);
                book.setUpdated_at(new Date());
                bookService.save(book);
            }
        }

        orderRepository.save(order);
    }

    public Page<Order> findByLimit(Integer page, Integer limit, String sortBy) {
        Pageable paging = PageRequest.of(page, limit);
        if (sortBy != null) paging = PageRequest.of(page, limit, Sort.by(sortBy));
        return orderRepository.findAll(paging);
    }

    public Page<Order> findByAdminLimit(Integer adminId, int page, int size, String sortBy) {
        Pageable pageable;
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return orderRepository.findByAdminId(adminId, pageable);
    }

    public Page<Order> findByUser(User user, int page, int limit) {
        return orderRepository.findByUser(
                user,
                PageRequest.of(page, limit, Sort.by("createdAt").descending())
        );
    }

    public void validateCustomerInfo(String fullname, String phone, String address) {
        if (fullname == null || fullname.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }

        if (fullname.length() < 2 || fullname.length() > 50) {
            throw new IllegalArgumentException("Full name must be 2-50 characters");
        }

        if (!fullname.matches("^([A-Z][a-z]+)(\\s[A-Z][a-z]+)*$")) {
            throw new IllegalArgumentException("Full name must be capitalized (ex: Nguyen Van A)");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }

        if (!phone.matches("^0[1-9]\\d{8}$")) {
            throw new IllegalArgumentException("Phone must have 10 digits, start with 0");
        }

        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }

        if (!address.matches("^(?!.*[.,#/^()'\\-]{2,})(?!-)[A-Za-z0-9\\s.,#/^()'\\-]{2,50}$")) {
            throw new IllegalArgumentException("Invalid address");
        }
    }

    @Transactional
    public void validateStatusTransition(Integer orderId, String newStatus) {
        Order order = findById(orderId);
        String currentStatus = order.getStatus();

        // ========== VALIDATION 1: Check for self-transition ==========
        if (currentStatus.equals(newStatus)) {
            throw new IllegalStateException(
                    "Order is already in '" + newStatus + "' status. No changes needed.");
        }

        // ========== VALIDATION 2: Check valid transition ==========
        boolean isValidTransition = false;

        switch (currentStatus) {
            case "Pending":
                // Pending → Approved, Cancelled
                isValidTransition = "Approved".equals(newStatus) || "Cancelled".equals(newStatus);
                break;

            case "Assigned":
                // Assigned → Approved, Cancelled
                isValidTransition = "Approved".equals(newStatus) || "Cancelled".equals(newStatus);
                break;

            case "Approved":
                // Approved → In Delivery, Cancelled
                isValidTransition = "In Delivery".equals(newStatus) || "Cancelled".equals(newStatus);
                break;

            case "In Delivery":
                // In Delivery → Completed, Returned
                isValidTransition = "Completed".equals(newStatus) || "Returned".equals(newStatus);
                break;

            case "Completed":
            case "Cancelled":
            case "Returned":
                // Terminal states - cannot change
                isValidTransition = false;
                break;
        }

        // ========== Throw error if invalid ==========
        if (!isValidTransition) {
            throw new IllegalArgumentException(
                    "Cannot change status from '" + currentStatus + "' to '" + newStatus + "'");
        }

        // ========== RESTORE STOCK IF CANCELLED OR RETURNED ==========
        if ("Cancelled".equals(newStatus) || "Returned".equals(newStatus)) {
            if (order.getOrderDetailList() != null && !order.getOrderDetailList().isEmpty()) {
                for (OrderDetail orderDetail : order.getOrderDetailList()) {
                    Book book = orderDetail.getBook();
                    Integer quantity = orderDetail.getNumber();

                    // Restore stock
                    book.setNumber_in_stock(book.getNumber_in_stock() + quantity);
                    book.setNumber_sold(Math.max(0, book.getNumber_sold() - quantity));
                    book.setUpdated_at(new Date());

                    bookService.save(book);
                }
            }
        }

        // ========== UPDATE: All validations passed, change status ==========
        order.setStatus(newStatus);

        // If status is Returned, set payment_status to UNPAID
        if ("Returned".equals(newStatus)) {
            order.setPayment_status("UNPAID");
        }

        order.setUpdated_at(LocalDate.now());
        orderRepository.save(order);
    }

    /**
     * Confirm payment for a completed order
     * Updates payment_status to PAID and stores payment note
     * <p>
     * Business Rules:
     * - Order must be in "Completed" status
     * - Payment must currently be "UNPAID"
     * - Payment note is optional (default message used if empty)
     *
     * @param orderId     Order ID to confirm payment for
     * @param paymentNote Payment confirmation note (optional)
     * @throws IllegalStateException if order not Completed or already PAID
     */
    @Transactional
    public void confirmPayment(Integer orderId, String paymentNote) {
        Order order = findById(orderId);

        // ========== VALIDATION 1: Check order status ==========
        if (!"Completed".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Payment can only be confirmed for Completed orders. " +
                            "Current status: " + order.getStatus());
        }

        // ========== VALIDATION 2: Check payment not already confirmed ==========
        if ("PAID".equals(order.getPayment_status())) {
            throw new IllegalStateException(
                    "This order payment has already been confirmed! " +
                            "Payment status is already PAID.");
        }

        // ========== UPDATE: Confirm payment ==========
        order.setPayment_status("PAID");
        order.setPayment_note(paymentNote != null ? paymentNote.trim() :
                "Shipper confirmed customer paid cash upon delivery");
        order.setUpdated_at(LocalDate.now());

        orderRepository.save(order);
    }

}