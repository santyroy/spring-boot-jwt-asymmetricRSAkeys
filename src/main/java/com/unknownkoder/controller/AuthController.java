package com.unknownkoder.controller;

import com.unknownkoder.dto.LoginResponseDto;
import com.unknownkoder.dto.RegistrationDto;
import com.unknownkoder.model.ApplicationUser;
import com.unknownkoder.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationDto dto) {
        return authenticationService.registerUser(dto.username(), dto.password());
    }

    @PostMapping("/login")
    public LoginResponseDto loginUser(@RequestBody RegistrationDto dto) {
        return authenticationService.login(dto.username(), dto.password());
    }
}
