package com.feedback.dto;

import com.feedback.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(max = 20, message = "Username must be at most 20 characters")
    private String username;

    // Password is intentionally unused in current forms/flows. Keep for future registration/security work.
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 60, message = "Email must be at most 60 characters")
    private String email;

    @Size(max = 60, message = "Full name must be at most 60 characters")
    private String fullName;

    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    private String bio;

    @Size(max = 80, message = "Picture path must be at most 80 characters")
    private String picture;

    private Role role;
    private Instant createdAt;
}
