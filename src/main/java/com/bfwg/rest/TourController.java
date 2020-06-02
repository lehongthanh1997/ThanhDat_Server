package com.bfwg.rest;

import com.bfwg.dto.CarDto;
import com.bfwg.dto.HotelDto;
import com.bfwg.dto.TourDto;
import com.bfwg.model.*;
import com.bfwg.repository.TourRepository;
import com.bfwg.repository.TourTypeRepository;
import com.bfwg.search.OrderSpecification;
import com.bfwg.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class TourController {
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourTypeRepository tourTypeRepository;

    @GetMapping("/tour/tourType/{tourTypeId}")
    public ResponseEntity<Object> getAllTourByTourType(@PathVariable(value = "tourTypeId") Long tourType,
                                                       @RequestParam(defaultValue = "0") String page,
                                                       @RequestParam(defaultValue = "5") String limit) {
        if (!tourTypeRepository.findById(tourType).isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Page<Tour> tours = tourRepository.findByTourTypeId(tourType, PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tours.stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        tours.getSize(),
                        tours.getNumberOfElements()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createTour(@Valid @RequestBody TourDto tourDto) {
        Optional<TourType> tourType = tourTypeRepository.findById(tourDto.getTourType());
        if (!tourType.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Tour tour = new Tour(tourDto);
        tour.setTourType(tourType.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new TourDto(tourRepository.save(tour)))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllTour(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "5") String limit) {

        List<Tour> tours = tourRepository.findAll();
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit))).stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        tours.size(),
                        tours.size()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneTour(@PathVariable Long id) {
        Optional<Tour> tour = tourRepository.findById(id);
        if (!tour.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        TourDto tourDto = new TourDto(tour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourDto)
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> editTour(@PathVariable Long id, @Valid @RequestBody TourDto tourDto) {
        Optional<TourType> tourType = tourTypeRepository.findById(tourDto.getTourType());
        if (!tourType.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<Tour> tour = tourRepository.findById(id);
        if (!tour.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
//        tour.get().setId(id);
        tour.get().setTitle(tourDto.getTitle());
        tour.get().setLocation(tourDto.getLocation());
        tour.get().setImage(tourDto.getImage());
        tour.get().setPrice(tourDto.getPrice());
        tour.get().setArrangements(tourDto.getArrangements());
        tour.get().setFood(tourDto.getFood());
        tour.get().setTourType(tourType.get());
        tour.get().setDuration(tourDto.getDuration());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new TourDto(tourRepository.save(tour.get())))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/searchByTitle/{title}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByName(@PathVariable String title,
                                               @RequestParam(defaultValue = "0") String page,
                                               @RequestParam(defaultValue = "5") String limit) {
        Page<Tour> tours = tourRepository.findByTitle(title, PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tours.stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        tours.getTotalPages(),
                        tours.getTotalElements()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/searchByPrice/{price}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByPrice(@PathVariable Double price,
                                                @RequestParam(defaultValue = "0") String page,
                                                @RequestParam(defaultValue = "5") String limit) {
        Page<Tour> tours = tourRepository.findByPrice(price, PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tours.stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        tours.getTotalPages(),
                        tours.getTotalElements()))
                .build(), HttpStatus.OK);
    }

    //
    @RequestMapping(value = "/tour/searchByLocation/{location}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByLocation(@PathVariable String location,
                                                   @RequestParam(defaultValue = "0") String page,
                                                   @RequestParam(defaultValue = "5") String limit) {
        Page<Tour> tours = tourRepository.findByLocation(location,PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tours.stream().map(x->new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        tours.getTotalPages(),
                        tours.getTotalElements()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/searchByTourTypeName/{name}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByTourTypeName(@PathVariable(value = "name") String name,
                                                       @RequestParam(defaultValue = "0") String page,
                                                       @RequestParam(defaultValue = "5") String limit) {
        if (tourTypeRepository.findByName(name).isEmpty()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }


        Page<Tour> tours = tourRepository.findByTourTypeName(name, PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tours.stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        tours.getTotalPages(),
                        tours.getTotalElements()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/search", method = RequestMethod.GET)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") String priceMin,
            @RequestParam(defaultValue = "10000000000000") String priceMax) {

        List<Tour> tours = tourRepository.findAllByTitleAndLocationAndPriceBetween(title, location, Double.valueOf(priceMin), Double.valueOf(priceMax));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tours.stream().map(x -> new TourDto(x)).collect(Collectors.toList()))

                .build(), HttpStatus.OK);
    }


    @GetMapping(value = "/tour/search-test")
    public ResponseEntity<Object> getList(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "priceMin", required = false) String priceMin,
            @RequestParam(value = "priceMax", required = false) String priceMax,

            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Specification specification = Specification.where(null);
        if (location != null && location.length() > 0) {
            specification = specification.and(new OrderSpecification(new SearchCriteria("location", ":", location)));
        }
        if (title != null && title.length() > 0) {
            specification = specification.and(new OrderSpecification(new SearchCriteria("title", ":", title)));
        }
        if (priceMin != null) {
            specification = specification.and(new OrderSpecification(new SearchCriteria("price", ">", priceMin)));
        }
        if (priceMax != null) {
            specification = specification.and(new OrderSpecification(new SearchCriteria("price", "<", priceMax)));
        }
        Page<Tour> tourPage = tourRepository.findAll(specification, PageRequest.of(page - 1, limit));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourPage.getContent().stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(page,
                        limit,
                        tourPage.getTotalPages(),
                        tourPage.getTotalElements()))
                .build(), HttpStatus.OK);
    }


    @RequestMapping(value = "/tour/getAll-test", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllTourTest() {

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourRepository.findAll().stream().map(x -> new TourDto(x.getId(), x.getTitle(), x.getTourType().getId())).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tour/random", method = RequestMethod.GET)
    public ResponseEntity<Object> getRandomTour() {
        Random random = new Random();
        List<Tour> tourList = tourRepository.findAll();
        ArrayList<Tour> tourArrayList = new ArrayList<>();

        if (tourList.size()>3) {
            for (int i = 0; i < 3; i++) {
                int randomInteger = random.nextInt(tourList.size());
                Tour tour = tourList.get(randomInteger);
                tourArrayList.add(tour);
            }
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.OK.value())
                    .setMessage("Success!")
                    .setData(tourArrayList.stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                    .build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourRepository.findAll().stream().map(x -> new TourDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/tour/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCar(@PathVariable (value = "id") Long tourId)
    {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (!tour.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        tour.get().setTourType(null);
        tour.get().setFlights(null);
        tour.get().setHotels(null);
        tourRepository.save(tour.get());
        tourRepository.delete(tour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(null)
                .build(), HttpStatus.OK);
    }



}
