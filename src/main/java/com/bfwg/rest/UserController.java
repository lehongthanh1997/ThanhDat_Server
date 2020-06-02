package com.bfwg.rest;

import com.bfwg.dto.UserDto;
import com.bfwg.model.Authority;
import com.bfwg.model.User;
import com.bfwg.model.UserRoleName;
import com.bfwg.model.UserTokenState;
import com.bfwg.repository.AuthorityRepository;
import com.bfwg.repository.UserRepository;
import com.bfwg.security.TokenHelper;
import com.bfwg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by fan.jin on 2016-10-15.
 */

@RestController
@RequestMapping( value = "/api", produces = MediaType.APPLICATION_JSON_VALUE )
public class UserController {

    @Autowired
    TokenHelper tokenHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;


    @RequestMapping( method = GET, value = "/user/{userId}" )
    @PreAuthorize("hasRole('ADMIN')")
    public User loadById( @PathVariable Long userId ) {
        return this.userService.findById( userId );
    }

    @RequestMapping( method = GET, value= "/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> loadAll() {
        return this.userService.findAll();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<Object> createPost(@Valid @RequestBody UserDto userDto) {
        User userexist = userRepository.findByUsername(userDto.getUsername());
        if (userexist!= null){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.BAD_REQUEST.value())
                    .setMessage("FAIL!")
                    .setData("Username exist")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        User user = new User(userDto);
        user.setAuthorities(authorityRepository.findAllById(1));
        userRepository.save(user);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE USER SUCCESS!")
                .setData(" (-_-) ")
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user/edit", method = RequestMethod.PUT)
    public ResponseEntity<Object> editUser(Principal user,@Valid @RequestBody UserDto userDto) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        User userexist = userRepository.findByUsername(user.getName());
        userexist.setUsername(userDto.getUsername());
        userexist.setPassword(userDto.getPassword());
        userexist.setEmail(userDto.getEmail());
        userexist.setFirstName(userDto.getFirstName());
        userexist.setLastName(userDto.getLastName());
        userexist.setPhoneNumber(userDto.getPhoneNumber());
        userRepository.save(userexist);
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userexist.getUsername(),
                        userexist.getPassword()
                )
        );


        // Inject into security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token creation
        User usero = (User)authentication.getPrincipal();
        int userId = Math.toIntExact(usero.getId());
        String jws = tokenHelper.generateToken(usero.getUsername());
        int expiresIn = tokenHelper.getExpiredIn();
        // Return the token
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS !")
                .setData(new UserTokenState(userId,jws,expiresIn))
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<Object> getUser(Principal user) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS!")
                .setData(userRepository.findByUsername(user.getName()))
                .build(), HttpStatus.OK);
    }

    /*
     *  We are not using userService.findByUsername here(we could),
     *  so it is good that we are making sure that the user has role "ROLE_USER"
     *  to access this endpoint.
     */
    @RequestMapping("/whoami")
    @PreAuthorize("hasRole('USER')")
    public User user(Principal user) {
        return this.userService.findByUsername(user.getName());
    }
}
