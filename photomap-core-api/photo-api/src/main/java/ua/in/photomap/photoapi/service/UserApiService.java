package ua.in.photomap.photoapi.service;


import ua.in.photomap.common.photo.model.dto.UserBasicDTO;

import java.util.List;
import java.util.Optional;

public interface UserApiService {

    Optional<UserBasicDTO> findById(Long id);
    List<UserBasicDTO> findAllBasicByIds(List<Long> ids);
}
