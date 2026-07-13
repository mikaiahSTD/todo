package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.CreateProfileRequest;
import com.example.demo.entity.Profile;
import com.example.demo.repository.ProfileRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

  @Mock private ProfileRepository repository;

  @InjectMocks private ProfileService service;

  private MockMultipartFile multipartFile;

  @BeforeEach
  void setUp() {
    multipartFile =
        new MockMultipartFile(
            "file", "photo-vacances.png", "image/png", "fake-image-content".getBytes());
  }

  @Test
  void getAll_shouldReturnAllProfilesFromRepository() {
    Profile p1 =
        Profile.builder()
            .id(UUID.randomUUID())
            .fileName("a.png")
            .email("a@test.com")
            .createdDate(Instant.now())
            .build();
    Profile p2 =
        Profile.builder()
            .id(UUID.randomUUID())
            .fileName("b.png")
            .email("b@test.com")
            .createdDate(Instant.now())
            .build();
    when(repository.findAll()).thenReturn(List.of(p1, p2));

    List<Profile> result = service.getAll();

    assertThat(result).containsExactly(p1, p2);
    verify(repository, times(1)).findAll();
    verifyNoMoreInteractions(repository);
  }

  @Test
  void create_shouldSaveProfileWithEmailFromRequest() {
    CreateProfileRequest request = new CreateProfileRequest(multipartFile, "user@test.com");
    when(repository.save(any(Profile.class)))
        .thenAnswer(
            invocation -> {
              Profile toSave = invocation.getArgument(0);
              return toSave.toBuilder().id(UUID.randomUUID()).createdDate(Instant.now()).build();
            });

    Profile result = service.create(request);

    assertThat(result.getId()).isNotNull();
    assertThat(result.getEmail()).isEqualTo("user@test.com");
    assertThat(result.getCreatedDate()).isNotNull();
  }

  @Test
  void create_shouldUseOriginalFilenameNotMultipartFieldName() {
    CreateProfileRequest request = new CreateProfileRequest(multipartFile, "user@test.com");
    when(repository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Profile result = service.create(request);

    assertThat(result.getFileName()).isEqualTo(multipartFile.getOriginalFilename());
    assertThat(result.getFileName()).isNotEqualTo(multipartFile.getName()); // != "file"
  }
}
