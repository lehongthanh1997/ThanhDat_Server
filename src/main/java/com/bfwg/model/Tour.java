package com.bfwg.model;


import com.bfwg.dto.FlightDto;
import com.bfwg.dto.TourDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String arrangements;
    private String food;
    private String image;
    private String location;
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "tourType", nullable = true)
    private TourType tourType;
    private Double price;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tour")
    @JsonIgnore
    private Set<Flight> flights;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tourId")
    @JsonIgnore
    private Set<Hotel> hotels;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tourId")
    @JsonIgnore
    private Set<OrderTour> orderTours;

    public Tour() {
    }

    public Tour(TourDto tourDto) {
        this.id = tourDto.getId();
        this.title = tourDto.getTitle();
        this.arrangements = tourDto.getArrangements();
        this.food = tourDto.getFood();
        this.price = tourDto.getPrice();
        this.location =tourDto.getLocation();
        this.duration = tourDto.getDuration();
        this.image =tourDto.getImage();
    }

//    public Set<OrderTour> getOrderTours() {
//        return orderTours;
//    }
//
//    public void setOrderTours(Set<OrderTour> orderTours) {
//        this.orderTours = orderTours;
//    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;
    }

    public Set<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(Set<Hotel> hotels) {
        this.hotels = hotels;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArrangements() {
        return arrangements;
    }

    public void setArrangements(String arrangements) {
        this.arrangements = arrangements;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public TourType getTourType() {
        return tourType;
    }

    public void setTourType(TourType tourType) {
        this.tourType = tourType;
    }
}
