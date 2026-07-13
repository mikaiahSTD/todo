package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(@NotBlank String fileName, @Email @NotBlank String email) {}
