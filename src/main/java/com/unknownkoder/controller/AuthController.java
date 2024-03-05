package com.unknownkoder.controller;

import com.unknownkoder.dto.LoginResponseDtoV2;
import com.unknownkoder.dto.RefreshTokenRequestDto;
import com.unknownkoder.dto.RegistrationDto;
import com.unknownkoder.model.ApplicationUser;
import com.unknownkoder.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public LoginResponseDtoV2 loginUser(@RequestBody RegistrationDto dto, HttpServletResponse httpServletResponse) {
        LoginResponseDtoV2 loginResponseDtoV2 = authenticationService.login(dto.username(), dto.password());
        Cookie cookie = new Cookie("refreshToken", loginResponseDtoV2.refreshToken());
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return loginResponseDtoV2;
    }

    @PostMapping("/refresh")
    public LoginResponseDtoV2 getNewJWT(@RequestBody RefreshTokenRequestDto dto) {
        return authenticationService.getNewJWT(dto);
    }
}
