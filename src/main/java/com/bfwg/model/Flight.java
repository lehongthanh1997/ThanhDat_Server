package com.bfwg.model;


import com.bfwg.dto.FlightDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private double price;
    private String brand;
    private String schedule;
    private String description;
    private String image;
    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "tour_id", nullable = true)
    private Tour tour;


    public Flight() {
    }

    public Flight(FlightDto flightDto) {
        this.id = flightDto.getId();
        this.name = flightDto.getName();
        this.price = flightDto.getPrice();
        this.brand = flightDto.getBrand();
        this.schedule = flightDto.getSchedule();
        this.description = flightDto.getDescription();
        this.image = flightDto.getImage();
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }
}
