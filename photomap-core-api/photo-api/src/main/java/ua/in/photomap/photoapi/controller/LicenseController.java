package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.photoapi.dto.LicenseDTO;
import ua.in.photomap.photoapi.service.LicenseService;

import java.util.List;

@RestController
@RequestMapping("/api/photo/license")
@RequiredArgsConstructor
@Tag(name = "License API", description = "API to work with licenses")
public class LicenseController {
    private final LicenseService licenseService;

    @GetMapping("/active")
    @Operation(description = "Get active licenses.")
    public List<LicenseDTO> getActiveLicenses() {
        return licenseService.getActiveLicenses();
    }
}
