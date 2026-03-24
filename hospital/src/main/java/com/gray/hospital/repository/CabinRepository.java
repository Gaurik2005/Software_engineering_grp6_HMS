package com.gray.hospital.repository;

import com.gray.hospital.entity.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CabinRepository extends JpaRepository<Cabin, Long> {
}