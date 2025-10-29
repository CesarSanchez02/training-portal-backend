package com.training.portal.controller;

import com.training.portal.model.User;
import com.training.portal.security.JwtUtil;
import com.training.portal.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService IUserService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {

        Optional<User> optionalUser = IUserService.findByEmail(request.getEmail());
        if (optionalUser.isEmpty() ||
        !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(Map.of(
                            "status", 401,
                            "message", "Usuario o contraseña incorrectos."
                    ));
        }
        User user = optionalUser.get();



        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(" Credenciales incorrectas");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Inicio de sesión exitoso.",
                "token", token,
                "role", user.getRole(),
                "fullName", user.getFullName(),
                "id", user.getId()
        ));
    }
}
