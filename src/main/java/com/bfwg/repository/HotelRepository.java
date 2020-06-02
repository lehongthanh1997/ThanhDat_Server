package com.bfwg.repository;

import com.bfwg.model.Flight;
import com.bfwg.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HotelRepository extends JpaRepository<Hotel, Long>  , JpaSpecificationExecutor<Hotel> {
    Page<Hotel> findByName(String name, Pageable pageable);

}
