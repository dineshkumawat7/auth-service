package com.ebit.authentication.service;

import com.ebit.authentication.entity.Role;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.payloads.OAuth2UserDto;
import com.ebit.authentication.payloads.UserRegistrationDto;
import com.ebit.authentication.payloads.UserUpdateDto;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.repository.RoleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService userService;

    @Test
    public void testSaveOrUpdateUser_NewOAuth2User() {
        // Given
        OAuth2UserDto oAuth2UserDto = new OAuth2UserDto();
        oAuth2UserDto.setEmail("newuser@example.com");
        oAuth2UserDto.setOauth2Id("oauth2-id");
        oAuth2UserDto.setOauth2Provider("google");
        oAuth2UserDto.setFirstName("John");
        oAuth2UserDto.setLastName("Doe");
        oAuth2UserDto.setPhone("1234567890");
        oAuth2UserDto.setImgUrl("http://example.com/image.jpg");

        Role role = new Role();
        role.setName("ROLE_USER");

        User newUser = new User();
        newUser.setUsername(UUID.randomUUID().toString());
        newUser.setFirstName(oAuth2UserDto.getFirstName());
        newUser.setLastName(oAuth2UserDto.getLastName());
        newUser.setEmail(oAuth2UserDto.getEmail());
        newUser.setPhone(oAuth2UserDto.getPhone());
        newUser.setPassword("encoded-password");
        newUser.setImgUrl(oAuth2UserDto.getImgUrl());
        newUser.setEnable(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setRoles(Collections.singletonList(role));
        newUser.setOauth2Id(oAuth2UserDto.getOauth2Id());
        newUser.setOauth2Provider(oAuth2UserDto.getOauth2Provider());

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);
        when(authRepository.findByEmail(oAuth2UserDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("oauth2")).thenReturn("encoded-password");
        when(authRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.saveOrUpdateUser(oAuth2UserDto);

        // Then
        assertNotNull(result);
        assertEquals(oAuth2UserDto.getEmail(), result.getEmail());
        verify(roleRepository).findByName("ROLE_USER");
        verify(authRepository).findByEmail(oAuth2UserDto.getEmail());
        verify(authRepository).save(result);
    }

    @Test
    public void testSaveOrUpdateUser_ExistingOAuth2User() {
        // Given
        OAuth2UserDto oAuth2UserDto = new OAuth2UserDto();
        oAuth2UserDto.setEmail("existinguser@example.com");
        oAuth2UserDto.setOauth2Id("oauth2-id");
        oAuth2UserDto.setOauth2Provider("google");

        User existingUser = new User();
        existingUser.setOauth2Id(null);
        existingUser.setOauth2Provider("facebook");

        User updatedUser = new User();
        updatedUser.setOauth2Id(oAuth2UserDto.getOauth2Id());
        updatedUser.setOauth2Provider(oAuth2UserDto.getOauth2Provider());

        when(authRepository.findByEmail(oAuth2UserDto.getEmail())).thenReturn(Optional.of(existingUser));
        when(authRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.saveOrUpdateUser(oAuth2UserDto);

        // Then
        assertNotNull(result);
        assertEquals(oAuth2UserDto.getOauth2Id(), result.getOauth2Id());
        assertEquals(oAuth2UserDto.getOauth2Provider(), result.getOauth2Provider());
        verify(authRepository).findByEmail(oAuth2UserDto.getEmail());
        verify(authRepository).save(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveOrUpdateUser_ExistingOAuth2User_DifferentProvider() {
        // Given
        OAuth2UserDto oAuth2UserDto = new OAuth2UserDto();
        oAuth2UserDto.setEmail("existinguser@example.com");
        oAuth2UserDto.setOauth2Id("oauth2-id");
        oAuth2UserDto.setOauth2Provider("google");

        User existingUser = new User();
        existingUser.setOauth2Id("oauth2-id");
        existingUser.setOauth2Provider("facebook");

        when(authRepository.findByEmail(oAuth2UserDto.getEmail())).thenReturn(Optional.of(existingUser));

        // When
        userService.saveOrUpdateUser(oAuth2UserDto); // Should throw an exception
    }

    @Test
    public void testSaveOrUpdateUser_NewStandardUser() {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("newuser@example.com");
        registrationDto.setPassword(passwordEncoder.encode("password"));
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");
        registrationDto.setPhone("1234567890");
        registrationDto.setImgUrl("http://example.com/image.jpg");

        Role role = new Role();
        role.setName("ROLE_USER");

        User newUser = new User();
        newUser.setUsername(UUID.randomUUID().toString());
        newUser.setFirstName(registrationDto.getFirstName());
        newUser.setLastName(registrationDto.getLastName());
        newUser.setEmail(registrationDto.getEmail());
        newUser.setPhone(registrationDto.getPhone());
        newUser.setPassword(passwordEncoder.encode("password"));
        newUser.setImgUrl(registrationDto.getImgUrl());
        newUser.setEnable(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setRoles(Collections.singletonList(role));

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);
        when(authRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encoded-password");
        when(authRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.saveOrUpdateUser(registrationDto);

        // Then
        assertNotNull(result);
        assertEquals(registrationDto.getEmail(), result.getEmail());
        verify(roleRepository).findByName("ROLE_USER");
        verify(authRepository).findByEmail(registrationDto.getEmail());
        verify(authRepository).save(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveOrUpdateUser_ExistingStandardUser() {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("existinguser@example.com");

        when(authRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.of(new User()));

        // When
        userService.saveOrUpdateUser(registrationDto); // Should throw an exception
    }

    @Test
    public void testSaveOrUpdateUser_UpdateUser() {
        // Given
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(1L);
        updateDto.setEmail("updateduser@example.com");
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setPhone("0987654321");
        updateDto.setImgUrl("http://example.com/newimage.jpg");
        updateDto.setPassword(passwordEncoder.encode("new_password"));

        User existingUser = new User();
        existingUser.setId(1L);

        User updatedUser = new User();
        updatedUser.setEmail(updateDto.getEmail());
        updatedUser.setFirstName(updateDto.getFirstName());
        updatedUser.setLastName(updateDto.getLastName());
        updatedUser.setPhone(updateDto.getPhone());
        updatedUser.setImgUrl(updateDto.getImgUrl());
        updatedUser.setPassword("encoded-newpassword");
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(authRepository.findById(updateDto.getId())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateDto.getPassword())).thenReturn("encoded-newpassword");
        when(authRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.saveOrUpdateUser(updateDto);

        // Then
        assertNotNull(result);
        assertEquals(updateDto.getEmail(), result.getEmail());
        assertEquals(updateDto.getFirstName(), result.getFirstName());
        verify(authRepository).findById(updateDto.getId());
        verify(authRepository).save(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveOrUpdateUser_UserNotFound() {
        // Given
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(1L);

        when(authRepository.findById(updateDto.getId())).thenReturn(Optional.empty());

        // When
        userService.saveOrUpdateUser(updateDto); // Should throw an exception
    }
}