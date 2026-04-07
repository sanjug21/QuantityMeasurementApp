package com.sanju.auth_service.service;

import com.sanju.auth_service.dto.LoginDTO;
import com.sanju.auth_service.dto.SignupDTO;
import com.sanju.auth_service.dto.TokenDTO;
import com.sanju.auth_service.dto.UserDTO;
import com.sanju.auth_service.entity.User;
import com.sanju.auth_service.exception.InvalidCredentialsException;
import com.sanju.auth_service.exception.UserAlreadyExistsException;
import com.sanju.auth_service.exception.UserNotFoundException;
import com.sanju.auth_service.repository.UserManagementRepository;
import com.sanju.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements IUserManagementService {

    private final UserManagementRepository userManagementRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public TokenDTO signup(SignupDTO signupDTO) {
        if (userManagementRepository.findByUsername(signupDTO.getUsername()) != null) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userManagementRepository.findByEmail(signupDTO.getEmail()) != null) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        
        userManagementRepository.save(user);
        
        String jwtToken = jwtService.generateToken(user);
        return TokenDTO.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        User user = userManagementRepository.findByEmail(loginDTO.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            String jwtToken = jwtService.generateToken(user);
            return TokenDTO.builder()
                    .token(jwtToken)
                    .build();
        }
        throw new InvalidCredentialsException("Invalid password");
    }

    @Override
    public UserDTO getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("User not authenticated");
        }
        
        String email = authentication.getName();
        User user = userManagementRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

