package com.ebit.authentication.config;

import com.ebit.authentication.entity.Role;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class AdminAccountInitializer implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(AdminAccountInitializer.class);
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
            user.setFirstName("Dinesh");
            user.setLastName("Kumawat");
            user.setEmail("dkumawat7627@gmail.com");
            user.setPhone("7627000907");
            user.setPassword(passwordEncoder.encode("Admin"));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setEnable(true);
            if (roleRepository.findByName("ROLE_ADMIN") == null) {
                createAdminRole();
            }
            Role role = roleRepository.findByName("ROLE_ADMIN");
            user.setRoles(Arrays.asList(role));
            authRepository.save(user);
            logger.info("Admin account initialized with email {}", user.getEmail());
        }
    }

    private Role createAdminRole() {
        Role role = new Role();
        role.setName("ADMIN");
        return roleRepository.save(role);
    }
}
