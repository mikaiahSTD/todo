package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.CreateProfileRequest;
import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailProfileValidated;
import com.example.demo.service.ProfileService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService service;
  private final EventProducer<SendEmailProfileValidated> eventProducer;

  @GetMapping
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok().body(service.getAll());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> create(@Valid @ModelAttribute CreateProfileRequest request) {

    var profile = service.create(request);

    var event =
        SendEmailProfileValidated.builder()
            .profileId(profile.getId())
            .fileName(profile.getFileName())
            .to(profile.getEmail())
            .build();

    eventProducer.accept(List.of(event));

    return ResponseEntity.status(HttpStatus.CREATED).body(profile);
  }
}
