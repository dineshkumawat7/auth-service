package com.ebit.authentication.service;

import com.ebit.authentication.config.JwtUtils;
import com.ebit.authentication.entity.Role;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.exception.UserAlreadyExistsException;
import com.ebit.authentication.payloads.AuthRequestDto;
import com.ebit.authentication.payloads.OAuth2UserDto;
import com.ebit.authentication.payloads.UserRegistrationDto;
import com.ebit.authentication.payloads.UserUpdateDto;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public User saveOrUpdateUser(Object userDto) {
        if(roleRepository.findByName("ROLE_USER") == null){
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }
        Optional<User> existingUser = Optional.empty();
        if (userDto instanceof OAuth2UserDto oAuth2UserDto) {
            existingUser = authRepository.findByEmail(oAuth2UserDto.getEmail());
            if (existingUser.isPresent()) {
                User userToUpdate = existingUser.get();
                if (userToUpdate.getOauth2Id() == null) {
                    userToUpdate.setOauth2Id(oAuth2UserDto.getOauth2Id());
                    userToUpdate.setOauth2Provider(oAuth2UserDto.getOauth2Provider());
                    userToUpdate.setImgUrl(oAuth2UserDto.getImgUrl());
                }
                User updatedOauth2User = authRepository.save(userToUpdate);
                log.info("existing oauth2 user updated with email: {} and provider: {}", updatedOauth2User.getEmail(), updatedOauth2User.getOauth2Provider());
                return updatedOauth2User;
            } else {
                User newUser = new User();
                newUser.setUsername(extractUsernameFromEmail(oAuth2UserDto.getEmail()));
                newUser.setFirstName(oAuth2UserDto.getFirstName());
                newUser.setLastName(oAuth2UserDto.getLastName());
                newUser.setEmail(oAuth2UserDto.getEmail());
                newUser.setPhone(oAuth2UserDto.getPhone());
                newUser.setPassword(passwordEncoder.encode("oauth2"));
                newUser.setImgUrl(oAuth2UserDto.getImgUrl());
                newUser.setEnable(true);
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
                newUser.setOauth2Id(oAuth2UserDto.getOauth2Id());
                newUser.setOauth2Provider(oAuth2UserDto.getOauth2Provider());
                User createdOAuth2User = authRepository.save(newUser);
                log.info("new oauth2 user registration success with email: {} and provider: {}", createdOAuth2User.getEmail(), createdOAuth2User.getOauth2Provider());
                return createdOAuth2User;
            }
        } else if (userDto instanceof UserRegistrationDto registrationDto) {
            existingUser = authRepository.findByEmail(registrationDto.getEmail());
            if (existingUser.isPresent()) {
                log.info("user conflict with email: {}" , registrationDto.getEmail());
                throw new UserAlreadyExistsException("An account with this email already exists. Please log in or use a different email.");
            }
            User newUser = new User();
            newUser.setUsername(extractUsernameFromEmail(registrationDto.getEmail()));
            newUser.setFirstName(registrationDto.getFirstName());
            newUser.setLastName(registrationDto.getLastName());
            newUser.setEmail(registrationDto.getEmail());
            newUser.setPhone(registrationDto.getPhone());
            newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            newUser.setImgUrl(registrationDto.getImgUrl());
            newUser.setEnable(true);
            newUser.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
            newUser.setCreatedAt(LocalDateTime.now());
            User createdStandardUser = authRepository.save(newUser);
            log.info("new standard user registration success with email: {}", createdStandardUser.getEmail());
            return createdStandardUser;
        } else if (userDto instanceof UserUpdateDto updateDto) {
            existingUser = authRepository.findById(updateDto.getId());
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setFirstName(updateDto.getFirstName());
                user.setLastName(updateDto.getLastName());
                user.setPhone(updateDto.getPhone());
                user.setEmail(updateDto.getEmail());
                user.setImgUrl(updateDto.getImgUrl());
                user.setUpdatedAt(LocalDateTime.now());
                user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
                User updatedUser = authRepository.save(user);
                log.info("existing standard user updated with email: {}", updatedUser.getEmail());
                return updatedUser;
            } else {
                throw new IllegalArgumentException("User not found.");
            }
        }
        throw new IllegalArgumentException("Unsupported user DTO type");
    }

    private String extractUsernameFromEmail(String email){
        if(email != null && email.contains("@")){
            int index = email.indexOf("@");
            return email.substring(0, index);
        }
        throw new IllegalArgumentException("Invalid email address");
    }

    @Override
    public String authenticate(AuthRequestDto authRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtUtils.generateToken(authRequestDto.getEmail());
        }
        throw new BadCredentialsException("invalid username/email or password");
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> optionalUser = authRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(!passwordEncoder.matches(oldPassword, user.getPassword())){
                throw new IllegalArgumentException("old password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            return true;
        }
        throw new IllegalArgumentException("user not found with email: " + email);
    }
}
