package com.bfwg.dto;

import com.bfwg.model.Tour;
import com.bfwg.model.TourType;

public class TourTypeDto {
    private long id;
    private String name;

    public TourTypeDto() {
    }

    public TourTypeDto(TourType tourType) {
        this.id = tourType.getId();
        this.name = tourType.getName();
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
}
