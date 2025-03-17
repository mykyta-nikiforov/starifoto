package ua.in.photomap.photoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.in.photomap.photoapi.model.License;

import java.util.List;

public interface LicenseRepository extends JpaRepository<License, Integer> {
    List<License> findAllByActiveTrue();
}
