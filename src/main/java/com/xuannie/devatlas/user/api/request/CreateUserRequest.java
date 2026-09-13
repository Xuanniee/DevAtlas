package com.xuannie.devatlas.user.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateUserRequest {
    @NotBlank(message = "User's name cannot be empty when registering.")
    @NotNull(message = "User's name cannot be null when registering.")
    private String name;

    @NotBlank(message = "User's email cannot be empty when registering.")
    @NotNull(message = "User's email cannot be null when registering.")
    private String email;

    @NotBlank(message = "User's password cannot be empty when registering.")
    @NotNull(message = "User's password cannot be null when registering.")
    private String password;
}
