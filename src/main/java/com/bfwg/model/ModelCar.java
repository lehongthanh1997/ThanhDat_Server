package com.bfwg.model;

import com.bfwg.dto.ModelCarDto;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="modelcar")
public class ModelCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "model")
    private Set<Car> cars;

    public ModelCar() {
    }

    public ModelCar(ModelCarDto modelCarDto) {
        this.name = modelCarDto.getName();
        this.id = modelCarDto.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }
}