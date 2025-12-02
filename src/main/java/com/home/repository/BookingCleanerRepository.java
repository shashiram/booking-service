package com.home.repository;

import com.home.entity.BookingCleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingCleanerRepository extends JpaRepository<BookingCleaner, Long> {
}