package com.bfwg.repository;

import com.bfwg.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    List<Car> findByModelId(Long modelId, Pageable pageable);
    Page<Car> findByName (String name,Pageable pageable);
    Optional<Car> findByIdAndModelId(Long id, Long modelId);
    List<Car> findByModelId (long id);
    Car findCarById (long id);
}
