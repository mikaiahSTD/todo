package com.example.demo.service;

import com.example.demo.dto.CreateProfileRequest;
import com.example.demo.entity.Profile;
import com.example.demo.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository repository;

  public List<Profile> getAll() {
    return repository.findAll();
  }

  public Profile create(CreateProfileRequest request) {

    Profile profile =
        Profile.builder().fileName(request.file().getOriginalFilename()).email(request.email()).build();

    return repository.save(profile);
  }
}
