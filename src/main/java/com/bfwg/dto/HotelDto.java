package com.bfwg.dto;

import com.bfwg.model.Hotel;
import org.springframework.data.domain.Page;

public class HotelDto {
    private long id;

    private String name;
    private double price;
    private String service;
    private long tourId;
    private String tourName;
    private String image;



    public HotelDto(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.price = hotel.getPrice();
        this.service = hotel.getService();
        this.tourId = hotel.getTourId().getId();
        this.tourName= hotel.getTourId().getTitle();
        this.image = hotel.getImage();
    }

    public HotelDto() {
    }

    public HotelDto(Page<Hotel> hotels) {
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public long getTourId() {
        return tourId;
    }

    public void setTourId(long tourId) {
        this.tourId = tourId;
    }
}
