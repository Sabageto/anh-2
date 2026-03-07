package com.thunga.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "parent_id")
    private Integer parentId;

    private Integer level;
    
    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private Collection<Book> bookList;

    private Date created_at;
    private Date updated_at;

    // Transient field để chứa children
    @Transient
    private List<Category> children;

    // Transient field để đếm tổng số sách (bao gồm cả children)
    @Transient
    private Integer totalBooks;
}