package com.bfwg.rest;

import com.bfwg.dto.CarDto;
//import com.bfwg.mail.MailController;
import com.bfwg.model.Car;
import com.bfwg.model.ModelCar;
import com.bfwg.repository.CarRepository;
import com.bfwg.repository.ModelCarRepository;
import com.bfwg.search.CarSpecification;
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

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class CarController {

//    @Autowired
//    private MailController mailController;

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ModelCarRepository modelCarRepository;


    @GetMapping("/car/model/{modelId}")
    public ResponseEntity<Object> getAllCarByModelId(@PathVariable(value = "modelId") Long modelId,
                                                     @RequestParam(defaultValue = "0") String page,
                                                     @RequestParam(defaultValue = "5") String limit) {
        List<Car> car = carRepository.findByModelId(modelId,  PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit)));
        if (!modelCarRepository.findById(modelId).isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("MODEL CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        System.out.println(car.size());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(car.stream().map(x -> new CarDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        car.size(),
                        car.size()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/car/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createCar(@Valid @RequestBody CarDto carDto) {
        Optional<ModelCar> modelCar = modelCarRepository.findById(carDto.getModel());
        if (!modelCar.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("MODEL CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Car car = new Car(carDto);
        car.setModel(modelCar.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new CarDto(carRepository.save(car)))
                .build(), HttpStatus.OK);
    }

    //    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/car/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllCar(@RequestParam(defaultValue = "0") String page,
                                            @RequestParam(defaultValue = "5") String limit) {
        List<Car> cars = carRepository.findAll();

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(carRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit))).stream().map(x -> new CarDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        cars.size(),
                        cars.size()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/car/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneCar(@PathVariable Long id) {
        Optional<Car> car = carRepository.findById(id);
        if (!car.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        System.out.println(new CarDto(car.get()));
        CarDto carDto = new CarDto(car.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(carDto)
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/car/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> editCar(@PathVariable Long id, @Valid @RequestBody CarDto carDto) {
        Optional<ModelCar> modelCar = modelCarRepository.findById(carDto.getModel());
        if (!modelCar.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("MODEL CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<Car> car = carRepository.findById(id);
        if (!car.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        car.get().setName(carDto.getName());
        car.get().setPrice(carDto.getPrice());
        car.get().setSize(carDto.getSize());
        car.get().setImage(carDto.getImage());
        car.get().setAirConditioner(carDto.getAirConditioner());
        car.get().setDriver(carDto.getDriver());
        car.get().setSeatingCapacity(carDto.getSeatingCapacity());
        car.get().setModel(modelCar.get());
        carRepository.save(car.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new CarDto((car.get())))
                .build(), HttpStatus.OK);
    }


    @RequestMapping(value = "/car/getAllss", method = RequestMethod.GET)
    public ResponseEntity<Object> getAll(Pageable pageable) {
        System.out.println("Sending Email...");
//
//        try {
//            //sendEmail();
//            mailController.sendEmailWithAttachment("asdasdasd");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("Done");
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(carRepository.findAll().stream().map(x -> new CarDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/car/searchByName/{name}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByName(@PathVariable String name, Pageable pageable) {
        Page<Car> car = carRepository.findByName(name, pageable);
        CarDto carDto = new CarDto(car);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(carDto)
                .setPagination(new RESTPagination(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        car.getTotalPages(),
                        car.getNumberOfElements()))
                .build(), HttpStatus.OK);
    }


//    @PreAuthorize("hasRole('USER')")
//    @RequestMapping(value = "/car/model/{modelId}", method = RequestMethod.POST)
//    public Car createCar(@PathVariable (value = "modelId") Long modelId,
//                                 @Valid @RequestBody Car car) {
//        return modelCarRepository.findById(modelId).map(model -> {
//            car.setModel(model);
//            return carRepository.save(car);
//        }).orElseThrow(() -> new ResourceNotFoundException("Model Id",modelId));
//
////    }
//    @PreAuthorize("hasRole('USER')")
//    @RequestMapping(value = "/car/{carId}/model/{modelId}", method = RequestMethod.PUT)
//    public Car updateCar(@PathVariable (value = "carId") Long carId,
//                                 @PathVariable (value = "modelId") Long modelId,
//                                 @Valid @RequestBody Car carRequest) {
//        if(!modelCarRepository.existsById(carId)) {
//            throw new ResourceNotFoundException("Model Car  ", modelId );
//        }
//        return carRepository.findById(carId).map(car -> {
//            car.setName(carRequest.getName());
//            car.setSize(carRequest.getSize());
//            car.setPrice(carRequest.getPrice());
//            return carRepository.save(car);
//        }).orElseThrow(() -> new ResourceNotFoundException("car " , carId));
//
//    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/car/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCar(@PathVariable (value = "id") Long carId)
                                           {
//        Optional<Car> car = carRepository.findById(carId);
         Car car = carRepository.findCarById(carId);
        if (car == null){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        car.setModel(null);
        carRepository.save(car);
        carRepository.delete(car);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(null)
                .build(), HttpStatus.OK);
    }

    @GetMapping(value = "/car/search-test")
    public ResponseEntity<Object> getList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "priceMin", required = false) String priceMin,
            @RequestParam(value = "priceMax", required = false) String priceMax,

            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Specification specification = Specification.where(null);
        if (name != null && name.length() > 0) {
            specification = specification.and(new CarSpecification(new SearchCriteria("name", ":", name)));
        }
        if (priceMin != null) {
            specification = specification.and(new CarSpecification(new SearchCriteria("price", ">", priceMin)));
        }
        if (priceMax != null) {
            specification = specification.and(new CarSpecification(new SearchCriteria("price", "<", priceMax)));
        }

        Page<Car> cars = carRepository.findAll(specification, PageRequest.of(page - 1, limit));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(cars.getContent().stream().map(x -> new CarDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(page,
                        limit,
                        cars.getTotalPages(),
                        cars.getTotalElements()))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/car/random", method = RequestMethod.GET)
    public ResponseEntity<Object> getRandomTour() {
        Random random = new Random();
        List<Car> cars = carRepository.findAll();
        ArrayList<Car> tourArrayList = new ArrayList<>();

        if (cars.size()>3) {
            for (int i = 0; i < 3; i++) {
                int randomInteger = random.nextInt(cars.size());
                Car tour = cars.get(randomInteger);
                tourArrayList.add(tour);
            }
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.OK.value())
                    .setMessage("Success!")
                    .setData(tourArrayList.stream().map(x -> new CarDto(x)).collect(Collectors.toList()))
                    .build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourArrayList.stream().map(x -> new CarDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }
}
