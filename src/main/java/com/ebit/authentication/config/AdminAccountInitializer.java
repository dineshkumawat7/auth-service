package com.ebit.authentication.config;

import com.ebit.authentication.entity.Role;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Component
@Slf4j
public class AdminAccountInitializer implements ApplicationRunner {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!authRepository.existsByEmail("dkumawat7627@gmail.com")) {
            User user = new User();
            user.setUsername(UUID.randomUUID().toString());
            user.setFirstName("Dinesh");
            user.setLastName("Kumawat");
            user.setEmail("dkumawat7627@gmail.com");
            user.setPhone("7627000907");
            user.setPassword(passwordEncoder.encode("Admin"));
            user.setCreatedAt(LocalDateTime.now());
            user.setEnable(true);
            if (roleRepository.findByName("ROLE_ADMIN") == null) {
                createAdminRole();
            }
            Role role = roleRepository.findByName("ROLE_ADMIN");
            user.setRoles(Collections.singletonList(role));
            authRepository.save(user);
            log.info("Admin account initialized with email {}", user.getEmail());
        }
    }

    private void createAdminRole() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        roleRepository.save(role);
    }
}
