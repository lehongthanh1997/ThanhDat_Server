package com.bfwg.dto;

import com.bfwg.model.Flight;
import com.bfwg.model.Hotel;
import com.bfwg.model.Tour;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourDto {
    private long id;
    private String title;
    private String arrangements;
    private String food;
    private long tourType;
    private String tourTypeName;
    private Set<Flight> flights;
    private Set<Hotel> hotels;
    private Double price;
    private String image;
    private String location;
    private String duration;
    public TourDto(Tour tour) {
        this.id = tour.getId();
        this.title = tour.getTitle();
        this.arrangements = tour.getArrangements();
        this.flights = tour.getFlights();
        this.hotels = tour.getHotels();
        this.food = tour.getFood();
        this.price = tour.getPrice();
        this.tourType = tour.getTourType().getId();
        this.tourTypeName = tour.getTourType().getName();
        this.image = tour.getImage();
        this.location =tour.getLocation();
        this.duration = tour.getDuration();

    }

    public TourDto(long id, String title,long tourType) {
        this.id = id;
        this.title = title;
        this.tourType = tourType;
    }

    public TourDto(Page<Tour> tours) {
    }

    public TourDto(long id, String title, String image, Double price, long id1) {
        this.id = id;
        this.title = title;
        this.tourType = tourType;
        this.image = image;
        this.price = price;

    }

    public Set<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(Set<Hotel> hotels) {
        this.hotels = hotels;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Set<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Set<Flight> flights) {
        this.flights = flights;
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

    public long getTourType() {
        return tourType;
    }

    public void setTourType(long tourType) {
        this.tourType = tourType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTourTypeName() {
        return tourTypeName;
    }

    public void setTourTypeName(String tourTypeName) {
        this.tourTypeName = tourTypeName;
    }

    public TourDto() {
    }
}
