package com.home.repository;

import com.home.entity.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
}