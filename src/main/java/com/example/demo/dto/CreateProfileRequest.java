package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CreateProfileRequest(@NotBlank MultipartFile file, @Email @NotBlank String email) {}
