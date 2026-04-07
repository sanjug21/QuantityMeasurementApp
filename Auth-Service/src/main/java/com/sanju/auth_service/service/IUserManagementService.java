package com.sanju.auth_service.service;

import com.sanju.auth_service.dto.LoginDTO;
import com.sanju.auth_service.dto.SignupDTO;
import com.sanju.auth_service.dto.TokenDTO;
import com.sanju.auth_service.dto.UserDTO;

public interface IUserManagementService {
    TokenDTO signup(SignupDTO signupDTO);
    TokenDTO login(LoginDTO loginDTO);
    UserDTO getUserDetails();
}

