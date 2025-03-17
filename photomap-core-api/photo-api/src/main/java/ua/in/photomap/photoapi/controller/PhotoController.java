package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.in.photomap.common.rest.toolkit.annotation.PreAuthorizeUserOrHigher;
import ua.in.photomap.photoapi.dto.*;
import ua.in.photomap.photoapi.mapper.PhotoMapper;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.service.PhotoManager;
import ua.in.photomap.photoapi.util.FileUtils;
import ua.in.photomap.photoapi.validator.PhotoValidator;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Photo API", description = "API to get and manage photos.")
public class PhotoController {
    private final PhotoManager photoManager;
    private final PhotoValidator photoValidator;

    @GetMapping("/{id}")
    @Operation(summary = "Get photo by id.")
    public PhotoDetailsDTO getPhoto(
            @Parameter(description = "Photo Id", example = "1", required = true)
            @PathVariable Long id) {
        return photoManager.getPhotoDetails(id);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorizeUserOrHigher
    @Operation(summary = "Create a photo with metadata.")
    public PhotoDTO createPhoto(
            @Parameter(description = "Files to upload", required = true)
            @RequestPart("files") List<MultipartFile> multipartFiles,
            @Parameter(description = "Photo metadata", required = true)
            @RequestPart("metadata") PhotoMetadataDTO metadata,
            @Parameter(in = ParameterIn.QUERY, description = "Flag to skip similar photos validation.")
            @RequestParam(value = "similarValidationSkip", defaultValue = "false", required = false)
            boolean similarValidationSkip
    ) {
        log.info("Creating photo with metadata: {}", metadata);
        photoValidator.validateCreateRequest(metadata, multipartFiles);
        List<File> files = FileUtils.convertMultiPartFilesToFiles(multipartFiles);
        if (!similarValidationSkip) {
            photoValidator.validateOnDuplicates(files);
        }
        Photo photo = photoManager.uploadPhoto(metadata, files);
        return PhotoMapper.INSTANCE.photoToPhotoDto(photo);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorizeUserOrHigher
    @Operation(summary = "Update a photo with metadata.")
    public PhotoDTO updatePhoto(
            @Parameter(description = "Photo Id", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Files to upload. Origin photo can be replaced, colorized photo version can be " +
                    "added/replaced.")
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "Photo metadata", required = true)
            @RequestPart("metadata") PhotoUpdateMetadataDTO metadata) {
        photoValidator.validateUpdateRequest(id, metadata, files);
        return PhotoMapper.INSTANCE.photoToPhotoDto(photoManager.updatePhoto(id, metadata, files));
    }

    @DeleteMapping("/{id}")
    @PreAuthorizeUserOrHigher
    @Operation(summary = "Delete a photo.")
    public ResponseEntity<?> deletePhoto(
            @Parameter(description = "Photo Id", example = "1", required = true)
            @PathVariable Long id) {
        photoValidator.validateDeleteRequest(id);
        photoManager.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all photos.")
    public Page<PhotoDetailsDTO> getAllPhotos(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return photoManager.getAll(page, size);
    }

    @GetMapping("/all/sitemap-data")
    @Operation(summary = "Get all photos data needed to generate sitemap.xml.")
    public Page<PhotoSitemapDataDTO> getAllPhotosForSitemap(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "1000")
            @RequestParam(value = "size", defaultValue = "1000") int size) {
        return photoManager.getAllForSitemap(page, size);
    }

    @PostMapping("/thumbnails/query")
    @Operation(summary = "Get photo icons by IDs.")
    public List<PhotoThumbnailDTO> getPhotoIcons(
            @Parameter(description = "List of Photo IDs", required = true)
            @RequestBody List<Long> photoIds) {
        photoValidator.validateGetPhotoIconsRequest(photoIds);
        return photoManager.getThumbnailUrlsByPhotoIds(photoIds);
    }
}
