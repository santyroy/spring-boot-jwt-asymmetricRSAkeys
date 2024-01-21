package com.unknownkoder.service;

import com.unknownkoder.dto.LoginResponseDto;
import com.unknownkoder.model.ApplicationUser;
import com.unknownkoder.model.Role;
import com.unknownkoder.repository.RoleRepository;
import com.unknownkoder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ApplicationUser registerUser(String username, String password) {
        String encodedPassword = encoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        return userRepository.save(new ApplicationUser(username, encodedPassword, authorities));
    }

    public LoginResponseDto login(String username, String password) {
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = tokenService.generateJWT(auth);

            return new LoginResponseDto(userRepository.findByUsername(username).get(), token);

        } catch (AuthenticationException e) {
            LOG.info("Authentication Failed: {}", e.getMessage());
            return new LoginResponseDto(null, "");
        }
    }
}
