package com.training.portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio.")
    @Size(min = 3, max = 80, message = "El nombre completo debe tener entre 3 y 80 caracteres.")
    private String fullName;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Debe proporcionar un correo electrónico válido.")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";
}
