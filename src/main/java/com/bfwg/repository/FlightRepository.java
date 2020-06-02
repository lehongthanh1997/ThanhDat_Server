package com.bfwg.repository;

import com.bfwg.model.Flight;
import com.bfwg.model.Tour;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FlightRepository  extends JpaRepository<Flight, Long> , JpaSpecificationExecutor<Flight> {
    Page<Flight> findByTourId(long tour, Pageable pageable);
    Optional<Flight> findByIdAndTourId(Long id, Long tour);
    Page<Flight> findByName(String name, Pageable pageable);


}
