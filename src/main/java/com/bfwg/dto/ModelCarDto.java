package com.bfwg.dto;

import com.bfwg.model.ModelCar;

public class ModelCarDto {
    private long id;
    private String name;

    public ModelCarDto(ModelCar modelCar) {
        this.id = modelCar.getId();
        this.name = modelCar.getName();
    }


    public ModelCarDto() {
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
