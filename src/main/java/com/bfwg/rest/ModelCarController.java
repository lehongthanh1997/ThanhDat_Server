package com.bfwg.rest;

import com.bfwg.dto.ModelCarDto;
import com.bfwg.dto.TourTypeDto;
import com.bfwg.exceptions.ResourceNotFoundException;
import com.bfwg.model.Car;
import com.bfwg.model.ModelCar;
import com.bfwg.model.TourType;
import com.bfwg.repository.CarRepository;
import com.bfwg.repository.ModelCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//import javax.jws.WebParam;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping( value = "/api")
public class ModelCarController {
    @Autowired
    private ModelCarRepository modelCarRepository;
    @Autowired
    private CarRepository carRepository;
    @RequestMapping(value = "/model-car/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllPosts(Pageable pageable) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(modelCarRepository.findAll().stream().map(x -> new ModelCarDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/model-car/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createPost(@Valid @RequestBody ModelCarDto modelCarDto) {
        ModelCar modelCar = new ModelCar(modelCarDto);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new ModelCarDto(modelCarRepository.save(modelCar)))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/model-car/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getModelCarById(@PathVariable Long id){
        Optional<ModelCar> modelCar = modelCarRepository.findById(id);
        if (!modelCar.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        ModelCarDto modelCarDto = new ModelCarDto(modelCar.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(modelCarDto)
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/model-car/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateModelCar(@PathVariable Long id, @Valid @RequestBody ModelCarDto modelCarDto) {
        Optional<ModelCar> modelCar = modelCarRepository.findById(id);
        if (!modelCar.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("MODEL CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        modelCar.get().setName(modelCarDto.getName());
        modelCarRepository.save(modelCar.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new ModelCarDto((modelCar.get())))
                .build(), HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/model-car/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteModelCar(@PathVariable (value = "id") Long modelId) {
        Optional<ModelCar> modelCar = modelCarRepository.findById(modelId);
        if (!modelCar.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("MODEL CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        List<Car> car = carRepository.findByModelId(modelId);
        for (int i= 0;i<car.size();i++){
            carRepository.delete(car.get(i));
        }
        modelCarRepository.delete(modelCar.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(null)
                .build(), HttpStatus.OK);
    }
}
