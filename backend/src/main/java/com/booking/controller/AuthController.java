package com.booking.controller;

import com.booking.dto.LoginRequest;
import com.booking.dto.LoginResponse;
import com.booking.entity.User;
import com.booking.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            LoginResponse response = authService.login(request);
            session.setAttribute("userId", response.getId());
            session.setAttribute("userRole", response.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }
    
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
        }
        
        User.Role role = (User.Role) session.getAttribute("userRole");
        return ResponseEntity.ok(new CurrentUserResponse(userId, role));
    }
    
    // Helper classes
    record ErrorResponse(String error) {}
    record MessageResponse(String message) {}
    record CurrentUserResponse(Long userId, User.Role role) {}
}
