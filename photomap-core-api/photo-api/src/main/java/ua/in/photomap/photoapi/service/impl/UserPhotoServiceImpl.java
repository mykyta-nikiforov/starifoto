package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.in.photomap.photoapi.dto.UserPhotoDTO;
import ua.in.photomap.photoapi.mapper.PhotoMapper;
import ua.in.photomap.photoapi.service.PhotoService;
import ua.in.photomap.photoapi.service.UserPhotoService;

@Service
@RequiredArgsConstructor
public class UserPhotoServiceImpl implements UserPhotoService {
    private final PhotoService photoService;

    @Override
    public Page<UserPhotoDTO> getPhotosByUserId(Long userId, int page, int size) {
        return photoService.getAllByUserId(userId, Pageable.ofSize(size).withPage(page))
                .map(PhotoMapper.INSTANCE::approvedPhotoToUserPhotoDto);
    }
}