package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, updatable = false)
  private Instant createdDate;

  @PrePersist
  public void prePersist() {
    createdDate = Instant.now();
  }
}
