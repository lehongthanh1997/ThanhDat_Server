package com.bfwg.rest;

import com.bfwg.dto.GroupTypeDto;
import com.bfwg.dto.ModelCarDto;
import com.bfwg.model.GroupType;
import com.bfwg.model.ModelCar;
import com.bfwg.repository.GroupTypeRepository;
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
public class GroupTypeController {
    @Autowired
    private GroupTypeRepository groupTypeRepository;


    @RequestMapping(value = "/groupType/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllGroupType(Pageable pageable) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(groupTypeRepository.findAll().stream().map(x -> new GroupTypeDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/groupType/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createPost(@Valid @RequestBody GroupTypeDto groupTypeDto) {
        GroupType groupType = new GroupType(groupTypeDto);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new GroupTypeDto(groupTypeRepository.save(groupType)))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/groupType/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getGroupTypeById(@PathVariable Long id){
        Optional<GroupType> groupType = groupTypeRepository.findById(id);
        if (!groupType.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        GroupTypeDto groupTypeDto = new GroupTypeDto(groupType.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(groupTypeDto)
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/groupType/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateGroupType(@PathVariable Long id, @Valid @RequestBody GroupTypeDto groupTypeDto) {
        Optional<GroupType> groupType = groupTypeRepository.findById(id);
        if (!groupType.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("GROUP TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        groupType.get().setName(groupTypeDto.getName());
        groupTypeRepository.save(groupType.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new GroupTypeDto((groupType.get())))
                .build(), HttpStatus.OK);
    }
}
