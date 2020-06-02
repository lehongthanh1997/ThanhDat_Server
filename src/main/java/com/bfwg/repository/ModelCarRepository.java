package com.bfwg.repository;

import com.bfwg.model.Car;
import com.bfwg.model.ModelCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelCarRepository extends JpaRepository<ModelCar, Long> {
}
