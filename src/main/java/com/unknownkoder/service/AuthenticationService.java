package com.unknownkoder.service;

import com.unknownkoder.dto.LoginResponseDtoV2;
import com.unknownkoder.dto.RefreshTokenRequestDto;
import com.unknownkoder.model.ApplicationUser;
import com.unknownkoder.model.RefreshToken;
import com.unknownkoder.model.Role;
import com.unknownkoder.repository.RefreshTokenRepository;
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

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    public ApplicationUser registerUser(String username, String password) {
        String encodedPassword = encoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        return userRepository.save(new ApplicationUser(username, encodedPassword, authorities));
    }

    public LoginResponseDtoV2 login(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            ApplicationUser applicationUser = userRepository.findByUsername(username).get();

            String jwt = tokenService.generateJWT(auth);
            String newRefreshToken = UUID.randomUUID().toString();

            // Check if RefreshToken exists for logging user
            Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(applicationUser);
            if (existingRefreshToken.isEmpty()) {
                RefreshToken rt = new RefreshToken(newRefreshToken, applicationUser);
                refreshTokenRepository.save(rt);
                return new LoginResponseDtoV2(applicationUser, jwt, rt.getToken());
            } else {
                return new LoginResponseDtoV2(applicationUser, jwt, existingRefreshToken.get().getToken());
            }

        } catch (AuthenticationException e) {
            LOG.info("Authentication Failed: {}", e.getMessage());
            return new LoginResponseDtoV2(null, "", "");
        }
    }

    public LoginResponseDtoV2 getNewJWT(RefreshTokenRequestDto dto) {
        String refreshToken = dto.refreshToken();
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);

        // If RefreshToken absent throw error and ask to re-login
        if (tokenOpt.isEmpty()) {
            LOG.info("Refresh Token not found, please login again!");
            return new LoginResponseDtoV2(null, "", "");
        }

        // If RefreshToken is present and expired, delete token and ask to re-login
        if (tokenOpt.get().getExpiry().isBefore(Instant.now())) {
            refreshTokenRepository.delete(tokenOpt.get());
            LOG.info("Refresh Token expired, please login again!");
            return new LoginResponseDtoV2(null, "", "");
        }

        // RefreshToken is present and is non-expired then generate new JWT
        String username = tokenOpt.get().getUser().getUsername();
        ApplicationUser applicationUser = userRepository.findByUsername(username).get();
        String jwt = tokenService.generateJWT(applicationUser);

        return new LoginResponseDtoV2(applicationUser, jwt, refreshToken);
    }
}
