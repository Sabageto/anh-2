package com.thunga.web.repository;

import com.thunga.web.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {

    Series findByName(String name);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.series.id = :seriesId")
    int countBooksBySeriesId(@Param("seriesId") Integer seriesId);
}