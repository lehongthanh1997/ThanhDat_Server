package com.bfwg.repository;

import com.bfwg.model.OrderCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface OrderCarRepository extends JpaRepository<OrderCar, Long> {
    List<OrderCar> findAllByUserIdId (long userId);
    OrderCar findByToken (String token);
    ArrayList<OrderCar> findByDateBetweenAndStatus (long minDate , long maxDate, int status);
    List<OrderCar> findAllByCarIdId (long carId);



}
