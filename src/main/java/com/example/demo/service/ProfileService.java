package com.example.demo.service;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository repository;

  public List<Profile> getAll() {
    return repository.findAll();
  }

  public Profile create(CreateProfileRequest request) {

    Profile profile = Profile.builder().fileName(request.fileName()).email(request.email()).build();

    return repository.save(profile);
  }
}
