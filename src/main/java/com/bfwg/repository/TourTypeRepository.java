package com.bfwg.repository;

import com.bfwg.model.Tour;
import com.bfwg.model.TourType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourTypeRepository extends JpaRepository<TourType, Long> {
    List<TourType> findByName(String name);

}
