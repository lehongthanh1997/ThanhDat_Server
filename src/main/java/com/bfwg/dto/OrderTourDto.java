package com.bfwg.dto;

import com.bfwg.model.OrderTour;

import java.util.Date;

public class OrderTourDto {

    private long id;
    private long userId;
    private long tourId;
    private String token;
    private String season;
    private String tourName;
    private String userName;
    private long date;
    private Double price;

    private int status;
    private long groupTypeId;
    private String groupTypeName;


    public OrderTourDto(OrderTour orderTour) {

        this.id = orderTour.getId();
        this.season = orderTour.getSeason();
        this.token = orderTour.getToken();
        this.userId = orderTour.getUserId().getId();
        this.userName = orderTour.getUserId().getUsername();
        this.tourId = orderTour.getTourId().getId();
        this.tourName = orderTour.getTourId().getTitle();
        this.price = orderTour.getTourId().getPrice();
        this.date = orderTour.getDate();
        this.groupTypeName = orderTour.getGroupTypeId().getName();
        this.groupTypeId = orderTour.getGroupTypeId().getId();
        this.status = orderTour.getStatus();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupTypeName() {
        return groupTypeName;
    }

    public void setGroupTypeName(String groupTypeName) {
        this.groupTypeName = groupTypeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public OrderTourDto() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTourId() {
        return tourId;
    }

    public void setTourId(long tourId) {
        this.tourId = tourId;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public long getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(long groupTypeId) {
        this.groupTypeId = groupTypeId;
    }
}
