package com.taskmanager.task_api.repository;

import com.taskmanager.task_api.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    @Query("select a from AppUser a where a.username = ?1")
    Optional<AppUser> findByUsername(String username);
}
