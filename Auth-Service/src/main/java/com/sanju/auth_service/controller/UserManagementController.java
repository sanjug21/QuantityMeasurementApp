package com.sanju.auth_service.controller;

import com.sanju.auth_service.dto.LoginDTO;
import com.sanju.auth_service.dto.SignupDTO;
import com.sanju.auth_service.dto.TokenDTO;
import com.sanju.auth_service.dto.UserDTO;
import com.sanju.auth_service.service.IUserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserManagementController {

    private final IUserManagementService userManagementService;

    @PostMapping("/signup")
    public ResponseEntity<TokenDTO> signup(@RequestBody SignupDTO signupDTO) {
        TokenDTO token = userManagementService.signup(signupDTO);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        TokenDTO token = userManagementService.login(loginDTO);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserDetails() {
        UserDTO userDTO = userManagementService.getUserDetails();
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}

