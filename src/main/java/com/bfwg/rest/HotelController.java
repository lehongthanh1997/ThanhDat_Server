package com.bfwg.rest;

import com.bfwg.dto.CarDto;
import com.bfwg.dto.FlightDto;
import com.bfwg.dto.HotelDto;
import com.bfwg.model.*;
import com.bfwg.repository.CarRepository;
import com.bfwg.repository.HotelRepository;
import com.bfwg.repository.TourRepository;
import com.bfwg.search.HotelSpecification;
import com.bfwg.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping( value = "/api")
public class HotelController {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;

    @RequestMapping(value = "/hotel/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllHotel( @RequestParam(defaultValue = "0") String page,
                                               @RequestParam(defaultValue = "5") String limit) {
        List<Hotel> hotels = hotelRepository.findAll();

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(hotelRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit))).stream().map(x -> new HotelDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        hotels.size(),
                        hotels.size()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/hotel/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneHotel(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (!hotel.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        HotelDto hotelDto = new HotelDto(hotel.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(hotelDto)
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/hotel/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createHotel(@Valid @RequestBody HotelDto hotelDto) {
        Optional<Tour> tour = tourRepository.findById(hotelDto.getTourId());
        if (!tour.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Hotel hotel = new Hotel(hotelDto);
        hotel.setTourId(tour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new HotelDto(hotelRepository.save(hotel)))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/hotel/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> editHotel(@PathVariable Long id,@Valid @RequestBody HotelDto hotelDto){
        Optional<Tour> tour = tourRepository.findById(hotelDto.getTourId());
        if (!tour.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (!hotel.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("HOTEL NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        hotel.get().setName(hotelDto.getName());
        hotel.get().setPrice(hotelDto.getPrice());
        hotel.get().setImage(hotelDto.getImage());
        hotel.get().setService(hotelDto.getService());
        hotel.get().setTourId(tour.get());
        hotelRepository.save(hotel.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new HotelDto((hotel.get())))
                .build(), HttpStatus.OK);
    }


    @RequestMapping(value = "/hotel/searchByName/{name}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByName(@PathVariable String name,Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findByName(name, pageable);
        HotelDto hotelDto = new HotelDto(hotels);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(hotelDto)
                .build(), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/hotel/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteHotel(@PathVariable (value = "id") Long carId) {
        Optional<Hotel> hotel = hotelRepository.findById(carId);
        if (!hotel.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("HOTEL NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        hotel.get().setTourId(null);
        hotelRepository.save(hotel.get());
        hotelRepository.delete(hotel.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(null)
                .build(), HttpStatus.OK);
    }

    @GetMapping(value = "/hotel/search-test")
    public ResponseEntity<Object> getList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "priceMin", required = false) String priceMin,
            @RequestParam(value = "priceMax", required = false) String priceMax,

            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Specification specification = Specification.where(null);
        if (name != null && name.length() > 0) {
            specification = specification.and(new HotelSpecification(new SearchCriteria("name", ":", name)));
        }
        if (priceMin != null) {
            specification = specification.and(new HotelSpecification(new SearchCriteria("price", ">", priceMin)));
        }
        if (priceMax != null) {
            specification = specification.and(new HotelSpecification(new SearchCriteria("price", "<", priceMax)));
        }

        Page<Hotel> hotels = hotelRepository.findAll(specification, PageRequest.of(page - 1, limit));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(hotels.getContent().stream().map(x -> new HotelDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(page,
                        limit,
                        hotels.getTotalPages(),
                        hotels.getTotalElements()))
                .build(), HttpStatus.OK);
    }

}
