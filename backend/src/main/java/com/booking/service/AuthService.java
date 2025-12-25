package com.booking.service;

import com.booking.dto.LoginRequest;
import com.booking.dto.LoginResponse;
import com.booking.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    
    public LoginResponse login(LoginRequest request) {
        User user = userService.getUserByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        return new LoginResponse(user, "Login successful");
    }
}
