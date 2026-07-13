package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.CreateProfileRequest;
import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailProfileValidated;
import com.example.demo.entity.Profile;
import com.example.demo.service.ProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService service;
  private final EventProducer<SendEmailProfileValidated> eventProducer;

  @GetMapping
  public List<Profile> getAll() {
    return service.getAll();
  }

  @PostMapping
  public Profile create(@Valid @RequestBody CreateProfileRequest request) {
    var profile = service.create(request);
    var event = SendEmailProfileValidated.builder().build();
    eventProducer.accept(List.of(event));
    return service.create(profile);
  }
}
