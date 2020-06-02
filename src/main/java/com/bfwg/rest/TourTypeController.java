package com.bfwg.rest;

import com.bfwg.dto.TourTypeDto;
import com.bfwg.model.TourType;
import com.bfwg.repository.TourTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping( value = "/api")
public class TourTypeController {
    @Autowired
    private TourTypeRepository tourTypeRepository;

    @RequestMapping(value = "/tourType/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllTourType(Pageable pageable) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(tourTypeRepository.findAll().stream().map(x -> new TourTypeDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/tourType/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createTourType(@Valid @RequestBody TourTypeDto tourTypeDto) {
        TourType tourType = new TourType(tourTypeDto);
        System.out.println(tourType.getName());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new TourTypeDto(tourTypeRepository.save(tourType)))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tourType/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getTourTypeById(@PathVariable Long id){
        Optional<TourType> tourType = tourTypeRepository.findById(id);
        if (!tourType.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        TourTypeDto tourTypeDto = new TourTypeDto(tourType.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(tourTypeDto)
                .build(), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/tourType/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> get(@PathVariable Long id,@Valid @RequestBody TourTypeDto tourTypeDto){
        Optional<TourType> tourType = tourTypeRepository.findById(id);
        if (!tourType.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        tourType.get().setName(tourTypeDto.getName());
        tourTypeRepository.save(tourType.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new TourTypeDto((tourType.get())))
                .build(), HttpStatus.OK);
    }

}
