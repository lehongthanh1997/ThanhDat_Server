package com.bfwg.rest;


import com.bfwg.dto.CarDto;
import com.bfwg.dto.FlightDto;
import com.bfwg.dto.TourDto;
import com.bfwg.model.Car;
import com.bfwg.model.Flight;
import com.bfwg.model.Tour;
import com.bfwg.model.TourType;
import com.bfwg.repository.FlightRepository;
import com.bfwg.repository.TourRepository;
import com.bfwg.search.FlightSpecification;
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
public class FlightController {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private TourRepository tourRepository;

    @GetMapping("/flight/tour/{tourId}")
    public ResponseEntity<Object> getAllFlightByTour(@PathVariable(value = "tourId") Long tour,
                                                     @RequestParam(defaultValue = "0") String page,
                                                     @RequestParam(defaultValue = "5") String limit) {
        if (!tourRepository.findById(tour).isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Page<Flight> flights = flightRepository.findByTourId(tour, PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(flights.stream().map(x->new FlightDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        flights.getTotalPages(),
                        flights.getNumberOfElements()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/flight/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createFlight(@Valid @RequestBody FlightDto flightDto) {
        Optional<Tour> tour = tourRepository.findById(flightDto.getTour());
        if (!tour.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Flight flight = new Flight(flightDto);
        flight.setTour(tour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new FlightDto(flightRepository.save(flight)))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/flight/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllFlight(@RequestParam(defaultValue = "0") String page,
                                               @RequestParam(defaultValue = "5") String limit) {
        List<Flight> flights = flightRepository.findAll();

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(flightRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit))).stream().map(x -> new FlightDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        flights.size(),
                        flights.size()))
                .build(), HttpStatus.OK);
    }
    @RequestMapping(value = "/flight/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneFlight(@PathVariable Long id) {
        Optional<Flight> flight = flightRepository.findById(id);
        if (!flight.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        System.out.println(new FlightDto(flight.get()));
        FlightDto flightDto = new FlightDto(flight.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(flightDto)
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/flight/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> editFlight(@PathVariable Long id,@Valid @RequestBody FlightDto flightDto){
        Optional<Tour> tour = tourRepository.findById(flightDto.getTour());
        if (!tour.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<Flight> flight = flightRepository.findById(id);
        if (!flight.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("FLIGHT NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
//        flight.get().setId(id);
        flight.get().setName(flightDto.getName());
        flight.get().setImage(flightDto.getImage());
        flight.get().setPrice(flightDto.getPrice());
        flight.get().setBrand(flightDto.getBrand());
        flight.get().setDescription(flightDto.getDescription());
        flight.get().setSchedule(flightDto.getSchedule());
        flight.get().setTour(tour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new FlightDto(flightRepository.save(flight.get())))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/flight/searchByName/{name}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByName(@PathVariable String name,Pageable pageable) {
        Page<Flight> flights = flightRepository.findByName(name, pageable);
        FlightDto flightDto = new FlightDto(flights);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(flightDto)
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/flight/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFlight(@PathVariable (value = "id") Long carId) {
        Optional<Flight> flight = flightRepository.findById(carId);
        if (!flight.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("FLIGHT NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        flight.get().setTour(null);
        flightRepository.save(flight.get());
        flightRepository.delete(flight.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(null)
                .build(), HttpStatus.OK);
    }

    @GetMapping(value = "/flight/search-test")
    public ResponseEntity<Object> getList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "priceMin", required = false) String priceMin,
            @RequestParam(value = "priceMax", required = false) String priceMax,

            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Specification specification = Specification.where(null);
        if (name != null && name.length() > 0) {
            specification = specification.and(new FlightSpecification(new SearchCriteria("name", ":", name)));
        }
        if (priceMin != null) {
            specification = specification.and(new FlightSpecification(new SearchCriteria("price", ">", priceMin)));
        }
        if (priceMax != null) {
            specification = specification.and(new FlightSpecification(new SearchCriteria("price", "<", priceMax)));
        }

        Page<Flight> flights = flightRepository.findAll(specification, PageRequest.of(page - 1, limit));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(flights.getContent().stream().map(x -> new FlightDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(page,
                        limit,
                        flights.getTotalPages(),
                        flights.getTotalElements()))
                .build(), HttpStatus.OK);
    }

}
