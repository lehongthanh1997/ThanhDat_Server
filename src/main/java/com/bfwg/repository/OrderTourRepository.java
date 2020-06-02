package com.bfwg.repository;

import com.bfwg.model.OrderTour;
import com.bfwg.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface OrderTourRepository extends JpaRepository<OrderTour, Long>, JpaSpecificationExecutor<OrderTour> {
    List<OrderTour> findAllByUserIdId (long userId);
    OrderTour findByToken (String token);
    ArrayList<OrderTour> findByDateBetweenAndStatus (long minDate , long maxDate,int status);
    List<OrderTour> findAllByTourIdId (long tourId);


}
