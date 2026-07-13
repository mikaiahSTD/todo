package com.example.demo.repository;

import com.example.demo.entity.Profile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {}
