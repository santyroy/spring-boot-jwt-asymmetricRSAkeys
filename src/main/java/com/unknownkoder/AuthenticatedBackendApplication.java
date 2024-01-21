package com.unknownkoder;

import com.unknownkoder.model.ApplicationUser;
import com.unknownkoder.model.Role;
import com.unknownkoder.repository.RoleRepository;
import com.unknownkoder.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class AuthenticatedBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticatedBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (roleRepository.findByAuthority("ADMIN").isPresent()) return;

            Role adminRole = new Role("ADMIN");
            Role userRole = new Role("USER");
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            ApplicationUser admin = new ApplicationUser("admin", encoder.encode("password"), roles);
            userRepository.save(admin);
        };
    }

}
