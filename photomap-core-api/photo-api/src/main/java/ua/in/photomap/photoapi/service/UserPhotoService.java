package ua.in.photomap.photoapi.service;

import org.springframework.data.domain.Page;
import ua.in.photomap.photoapi.dto.UserPhotoDTO;

public interface UserPhotoService {
    Page<UserPhotoDTO> getPhotosByUserId(Long userId, int page, int size);
}
