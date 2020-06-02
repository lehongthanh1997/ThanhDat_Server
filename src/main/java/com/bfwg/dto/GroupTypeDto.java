package com.bfwg.dto;

import com.bfwg.model.GroupType;

public class GroupTypeDto {
    private long id;
    private String name;


    public GroupTypeDto(GroupType groupType) {
        this.id = groupType.getId();
        this.name = groupType.getName();
    }

    public GroupTypeDto() {
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
