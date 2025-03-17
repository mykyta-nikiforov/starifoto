package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ua.in.photomap.photoapi.dto.UserPhotoDTO;
import ua.in.photomap.photoapi.service.UserPhotoService;

@RestController
@RequestMapping("/api/photo/user")
@RequiredArgsConstructor
@Tag(name = "User Photo API", description = "API to work with user photos")
public class UserPhotoController {
    private final UserPhotoService userPhotoService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get photos by user id.")
    public Page<UserPhotoDTO> getPhotosByUserId(
            @Parameter(description = "User Id", example = "1", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Page number", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return userPhotoService.getPhotosByUserId(userId, page, size);
    }
}
