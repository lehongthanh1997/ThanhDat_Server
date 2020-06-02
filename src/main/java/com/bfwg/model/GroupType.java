package com.bfwg.model;

import com.bfwg.dto.GroupTypeDto;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "group_type")
public class GroupType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orderTour")
//    private Set<OrderTour> orderTours;

    public GroupType(GroupTypeDto groupTypeDto) {
        this.id = groupTypeDto.getId();
        this.name = groupTypeDto.getName();
    }

    public GroupType() {
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
