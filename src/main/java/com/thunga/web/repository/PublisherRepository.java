package com.thunga.web.repository;

import com.thunga.web.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

    Publisher findByName(String name);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.publisher.id = :publisherId")
    int countBooksByPublisherId(@Param("publisherId") Integer publisherId);
}