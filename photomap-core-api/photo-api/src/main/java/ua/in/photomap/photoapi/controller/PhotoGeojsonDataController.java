package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.common.rest.toolkit.annotation.PreAuthorizeAdmin;
import ua.in.photomap.photoapi.service.MapDataService;
import ua.in.photomap.photoapi.service.PhotoService;

@RestController
@RequestMapping("/api/photo/geojson")
@RequiredArgsConstructor
@Tag(name = "Photo GeoJSON data", description = "Operations with photos data needed to store GeoJSON in MongoDB")
public class PhotoGeojsonDataController {
    private final PhotoService photoService;
    private final MapDataService mapDataService;

    @GetMapping("/all")
    @Operation(summary = "Get photos data needed to store GeoJSON in MongoDB. Used by geojson-generator")
    public Page<PhotoGeoJsonDataDTO> getAllPhotos(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return photoService.getGeoJsonDataPage(page, size);
    }

    @PostMapping("/regenerate")
    @Operation(summary = "Regenerate GeoJSON collection for all the photos")
    @ApiResponse(responseCode = "204", description = "GeoJSON data regenerated")
    @PreAuthorizeAdmin
    public ResponseEntity<?> regenerate() {
        mapDataService.regenerateGeoJsonData();
        return ResponseEntity.noContent().build();
    }
}
