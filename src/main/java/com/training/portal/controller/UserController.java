package com.training.portal.controller;

import com.training.portal.model.User;
import com.training.portal.repository.UserBadgeRepository;
import com.training.portal.repository.UserChapterProgressRepository;
import com.training.portal.repository.UserRepository;
import com.training.portal.service.IUserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {
    private final IUserService IUserService;
    private final UserRepository userRepository;
    private final UserChapterProgressRepository progressRepository;
    private final UserBadgeRepository badgeRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(IUserService.findAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Error de validación en los datos enviados.",
                    "errors", errors
            ));
        }

        if (IUserService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "message", "El correo ya está registrado."
                    ));
        }

        User newUser = IUserService.save(user);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED)
                .body(Map.of(
                        "status", 201,
                        "message", "Usuario registrado exitosamente.",
                        "user", Map.of(
                                "id", newUser.getId(),
                                "email", newUser.getEmail(),
                                "role", newUser.getRole()
                        )
                ));
    }



    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/progress")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getUserProgress(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "progress", progressRepository.findByUser(user),
                "badges", badgeRepository.findByUser(user)
        ));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        IUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
