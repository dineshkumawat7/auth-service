package com.ebit.authentication.service;

import com.ebit.authentication.entity.Role;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.payloads.UserDto;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService, OAuth2UserService<OidcUserRequest, OidcUser> {
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

    @Override
    public User createUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnable(true);
        user.setProvider("local");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (roleRepository.findByName("ROLE_USER") == null) {
            createUserRole();
        }
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(Arrays.asList(role));
        User createdUser = authRepository.save(user);
        return modelMapper.map(createdUser, User.class);
    }

    private Role createUserRole(){
        Role role = new Role();
        role.setName("USER");
        return roleRepository.save(role);
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        Optional<User> existingUser = authRepository.findById(id);
        if(existingUser.isPresent()){
            User user = existingUser.get();
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPhone(userDto.getPhone());
            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = authRepository.save(user);
            return modelMapper.map(updatedUser, User.class);
        }else{
            throw new UsernameNotFoundException("user not found with id '" + id + "'");
        }
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);
        User user = new User();
        user.setUsername(oidcUser.getSubject());
        user.setFirstName(oidcUser.getGivenName());
        user.setLastName(oidcUser.getFamilyName());
        user.setEmail(oidcUser.getEmail());
        user.setPhone(oidcUser.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnable(true);
        user.setProvider("google");
        user.setPassword("oauth2");
        user.setImgUrl(oidcUser.getPicture());
        if(roleRepository.findByName("ROLE_USER")  == null){
            createUserRole();
        }
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(Arrays.asList(role));
        return null;
    }
}
