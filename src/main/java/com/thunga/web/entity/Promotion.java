package com.thunga.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Integer promotionId;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "start_date", nullable = false, length = 10)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, length = 10)
    private LocalDate endDate;

    @Column(name = "max_usage", nullable = false)
    private Integer maxUsage;

    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "min_order")
    private Double minOrder;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PromotionBook> promotionBooks;

    @Transient
    public String getDisplayStatus() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(this.startDate)) {
            return "CREATED";
        }
        if (today.isAfter(this.endDate)) {
            return "EXPIRED";
        }
        return "ACTIVE";
    }

}
