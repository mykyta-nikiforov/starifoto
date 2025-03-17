package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.photoapi.dto.PhotoSitemapDataDTO;
import ua.in.photomap.photoapi.mapper.PhotoMapper;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.repository.PhotoRepository;
import ua.in.photomap.photoapi.service.PhotoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;

    @Override
    public Optional<Photo> findById(Long id) {
        return photoRepository.findByIdFetchGraph(id);
    }

    @Override
    public Photo getById(Long id) {
        return photoRepository.findByIdFetchGraph(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
    }

    @Override
    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    @Override
    public void saveAll(List<Photo> photos) {
        photoRepository.saveAll(photos);
    }

    @Override
    public Page<Photo> getAll(int page, int size) {
        Page<Long> photoIds = photoRepository.findAllPhotoIds(PageRequest.of(page, size));
        if (!photoIds.isEmpty()) {
            List<Photo> photos = photoRepository.findAllGraphByIds(photoIds.getContent());
            return new PageImpl<>(photos, PageRequest.of(page, size), photoIds.getTotalElements());
        } else {
            return Page.empty();
        }
    }

    @Override
    public Page<PhotoGeoJsonDataDTO> getGeoJsonDataPage(int page, int size) {
        Page<Long> photoIdsPage = photoRepository.findAllPhotoIds(PageRequest.of(page, size));
        log.info("Got photo ids page. Page: {}/{}. Size: {}", page, photoIdsPage.getTotalPages(), photoIdsPage.getSize());
        if (!photoIdsPage.isEmpty()) {
            List<Long> photoIds = photoIdsPage.getContent();
            return photoRepository.findAllGeoJsonGraphByIds(photoIds)
                    .stream()
                    .map(PhotoMapper.INSTANCE::photoToGeoJsonDataDto)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), list ->
                            new PageImpl<>(list, PageRequest.of(page, size), photoIdsPage.getTotalElements())));
        } else {
            return Page.empty();
        }
    }

    @Override
    public Page<Photo> getAllByUserId(Long userId, Pageable pageable) {
        Page<Long> photoIdsPage = photoRepository.findAllPhotoIdsByUserId(userId, pageable);
        if (!photoIdsPage.isEmpty()) {
            List<Long> photoIds = photoIdsPage.getContent();
            List<Photo> photos = photoRepository.findAllGraphByIds(photoIds);
            return new PageImpl<>(photos, pageable, photoIdsPage.getTotalElements());
        } else {
            return Page.empty();
        }
    }

    @Override
    public void deleteById(Long photoId) {
        photoRepository.deleteById(photoId);
    }

    @Override
    public Page<PhotoSitemapDataDTO> getAllForSitemap(int page, int size) {
        return photoRepository.findAllSitemapDataPage(PageRequest.of(page, size));
    }
}
