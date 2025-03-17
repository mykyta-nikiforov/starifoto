package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.photoapi.dto.LicenseDTO;
import ua.in.photomap.photoapi.mapper.LicenseMapper;
import ua.in.photomap.photoapi.model.License;
import ua.in.photomap.photoapi.repository.LicenseRepository;
import ua.in.photomap.photoapi.service.LicenseService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {
    private final LicenseRepository licenseRepository;

    @Override
    @Cacheable(value = "active-licenses", unless = "#result.isEmpty()")
    public List<LicenseDTO> getActiveLicenses() {
        return licenseRepository.findAllByActiveTrue().stream()
                .map(LicenseMapper.INSTANCE::licenseToLicenseDto)
                .toList();
    }

    @Override
    public Optional<License> findById(Integer id) {
        return licenseRepository.findById(id);
    }

    @Override
    public License getById(Integer id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("License with id %d not found".formatted(id)));
    }
}
