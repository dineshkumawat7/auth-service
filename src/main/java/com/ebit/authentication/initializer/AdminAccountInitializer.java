package com.ebit.authentication.initializer;

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

import java.util.Collections;

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
        String email = "dkumawat7627@gmail.com";
        int index = email.indexOf("@");
        if (!authRepository.existsByEmail(email)) {
            if (roleRepository.findByName("ROLE_ADMIN") == null) {
                createAdminRole();
            }
            User user = User.builder()
                    .username(email.substring(0, index))
                    .firstName("Dinesh")
                    .lastName("Kumawat")
                    .email("dkumawat7627@gmail.com")
                    .phone("7627000907")
                    .password(passwordEncoder.encode("Admin"))
                    .roles(Collections.singletonList(roleRepository.findByName("ROLE_ADMIN")))
                    .build();
            authRepository.save(user);
            log.info("Admin account initialized with email {}", user.getEmail());
        }
    }

    // create ROLE_ADMIN if role not exists
    private void createAdminRole() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        roleRepository.save(role);
    }
}
