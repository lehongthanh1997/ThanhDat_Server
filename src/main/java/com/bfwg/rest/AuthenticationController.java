package com.bfwg.rest;

import com.bfwg.common.DeviceProvider;
import com.bfwg.model.User;
import com.bfwg.model.UserTokenState;
import com.bfwg.repository.UserRepository;
import com.bfwg.security.TokenHelper;
import com.bfwg.security.auth.JwtAuthenticationRequest;
import com.bfwg.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fan.jin on 2017-05-10.
 */

@RestController
@RequestMapping( value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE )
public class AuthenticationController {

    @Autowired
    TokenHelper tokenHelper;
    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private DeviceProvider deviceProvider;

    @RequestMapping(value = "/set-user", method = RequestMethod.GET)
    public User ok() {
        User byUsername = new User();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        byUsername.setPassword(bCryptPasswordEncoder.encode("123456"));
        return userRepository.save(byUsername);
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Object> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ) throws AuthenticationException, IOException {
        // Perform the security
        User byUsername = userRepository.findByUsernameContaining(authenticationRequest.getUsername());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        byUsername.setPassword(bCryptPasswordEncoder.encode("123456"));
//        userRepository.save(byUsername);
        if (!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(), byUsername.getPassword())){

            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("Invalid username or password!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body("{}");
        }
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );


        // Inject into security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token creation
        User user = (User)authentication.getPrincipal();
        int userId = Math.toIntExact(user.getId());
        String jws = tokenHelper.generateToken(user.getUsername());
        int expiresIn = tokenHelper.getExpiredIn();
        // Return the token
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("SUCCESS !")
                .setData(new UserTokenState(userId,jws,expiresIn))
                .build(), HttpStatus.OK);
//        return ResponseEntity.ok(new UserTokenState(userId,jws, expiresIn));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshAuthenticationToken(
            HttpServletRequest request,
            HttpServletResponse response,
            Principal principal
            ) {

        String authToken = tokenHelper.getToken( request );

//        Device device = deviceProvider.getCurrentDevice(request);

        if (authToken != null && principal != null) {

            // TODO check user password last update
            String refreshedToken = tokenHelper.refreshToken(authToken);
            int expiresIn = tokenHelper.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
        } else {
            UserTokenState userTokenState = new UserTokenState();
            return ResponseEntity.accepted().body(userTokenState);
        }
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
        userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);
        Map<String, String> result = new HashMap<>();
        result.put( "result", "success" );
        return ResponseEntity.accepted().body(result);
    }

    static class PasswordChanger {
        public String oldPassword;
        public String newPassword;
    }
}
