package com.bfwg.model;

import com.bfwg.dto.TourTypeDto;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tour_type")
public class TourType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tourType")
    private Set<Tour> tours;

    public TourType() {
    }

    public TourType(TourTypeDto tourTypeDto) {
        this.id = tourTypeDto.getId();
        this.name = tourTypeDto.getName();
    }

    public Set<Tour> getTours() {
        return tours;
    }

    public void setTours(Set<Tour> tours) {
        this.tours = tours;
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
