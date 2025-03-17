package ua.in.photomap.photoapi.service;

import ua.in.photomap.photoapi.dto.LicenseDTO;
import ua.in.photomap.photoapi.model.License;

import java.util.List;
import java.util.Optional;

public interface LicenseService {
    List<LicenseDTO> getActiveLicenses();

    Optional<License> findById(Integer id);

    License getById(Integer id);
}
