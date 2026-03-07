package com.thunga.web.repository;

import com.thunga.web.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    Optional<Promotion> findByCode(String code);

    List<Promotion> findByCodeIgnoreCase(String code);

    Page<Promotion> findAll(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END " + "FROM OrderDetail od WHERE od.promotion.promotionId = :promotionId")
    boolean existsInOrderDetail(@Param("promotionId") Integer promotionId);

    List<Promotion> findByStatusAndStartDateLessThanEqual(String status, LocalDate date);

    List<Promotion> findByStatusAndEndDateLessThan(String status, LocalDate date);
}