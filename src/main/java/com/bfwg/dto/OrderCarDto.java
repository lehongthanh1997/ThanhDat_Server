package com.bfwg.dto;
import com.bfwg.model.OrderCar;

import java.util.Date;

public class OrderCarDto {
    private long id;
    private long carId;
    private long userId;
    private String season;
    private Date rental_day;
    private Date start_day;
    private long date;
    private int status;
    private String token;
    private Double price;
    private String userName;
    private String carName;



    public OrderCarDto() {
    }

    public OrderCarDto(OrderCar orderCar) {
        this.id = orderCar.getId();
        this.carId = orderCar.getCarId().getId();
        this.userId = orderCar.getUserId().getId();
        this.season = orderCar.getSeason();
        this.userName = orderCar.getUserId().getUsername();
        this.rental_day = orderCar.getRental_day();
        this.start_day = orderCar.getStart_day();
        this.date = orderCar.getDate();
        this.status = orderCar.getStatus();
        this.token = orderCar.getToken();
        this.price = orderCar.getCarId().getPrice();
        this.carName = orderCar.getCarId().getName();
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Date getRental_day() {
        return rental_day;
    }

    public void setRental_day(Date rental_day) {
        this.rental_day = rental_day;
    }

    public Date getStart_day() {
        return start_day;
    }

    public void setStart_day(Date start_day) {
        this.start_day = start_day;
    }
}
