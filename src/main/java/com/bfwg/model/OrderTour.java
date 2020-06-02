package com.bfwg.model;


import com.bfwg.dto.OrderTourDto;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "order_tour")
public class OrderTour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "userId", nullable = true)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "tourId", nullable = true)
    private Tour tourId;

    private String token;
    private String season;
    private int status;
    private long date;

    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
    @JoinColumn(name = "groupTypeId", nullable = true)
    private GroupType groupTypeId;

    public OrderTour(OrderTourDto orderTourDto) {
        this.id = orderTourDto.getId();
        this.season = orderTourDto.getSeason();
        this.token = orderTourDto.getToken();
        this.status = orderTourDto.getStatus();
        this.date = orderTourDto.getDate();
    }

    public OrderTour() {
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

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Tour getTourId() {
        return tourId;
    }

    public void setTourId(Tour tourId) {
        this.tourId = tourId;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public GroupType getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(GroupType groupTypeId) {
        this.groupTypeId = groupTypeId;
    }
}
