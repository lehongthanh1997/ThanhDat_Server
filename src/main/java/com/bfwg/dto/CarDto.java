package com.bfwg.dto;

import com.bfwg.model.Car;
import com.bfwg.model.ModelCar;
import org.springframework.data.domain.Page;

import java.util.Optional;

public class CarDto {
    private long id;
    private String name;
    private String size;
    private int seatingCapacity;
    private int driver;
    private int airConditioner;
    private double price;
    private long model;
    private String image;

    public CarDto() {
    }

    public CarDto(Car car) {
        this.id = car.getId();
        this.name = car.getName();
        this.size = car.getSize();
        this.seatingCapacity = car.getSeatingCapacity();
        this.driver = car.getDriver();
        this.airConditioner = car.getAirConditioner();
        this.price = car.getPrice();
        this.image = car.getImage();
        this.model = car.getModel().getId();
    }

    public CarDto(Optional<Car> car) {
    }

    public CarDto(Page<Car> car) {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public int getDriver() {
        return driver;
    }

    public void setDriver(int driver) {
        this.driver = driver;
    }

    public int getAirConditioner() {
        return airConditioner;
    }

    public void setAirConditioner(int airConditioner) {
        this.airConditioner = airConditioner;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getModel() {
        return model;
    }

    public void setModel(long model) {
        this.model = model;
    }
}
