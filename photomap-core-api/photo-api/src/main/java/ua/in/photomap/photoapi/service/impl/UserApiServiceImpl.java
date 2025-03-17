package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.in.photomap.common.photo.model.dto.UserBasicDTO;
import ua.in.photomap.common.photo.model.dto.batch.GetUsersByIdsRequest;
import ua.in.photomap.common.photo.model.dto.batch.GetUsersByIdsResponse;
import ua.in.photomap.common.rest.toolkit.exception.InternalException;
import ua.in.photomap.photoapi.service.UserApiService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApiServiceImpl implements UserApiService {
    private final RestTemplate restTemplate;

    @Value("${photomap.user-api.base-url}")
    private String userApiBaseUrl;

    @Override
    public Optional<UserBasicDTO> findById(Long id) {
        List<UserBasicDTO> user = findAllBasicByIds(List.of(id));
        return user.isEmpty() ? Optional.empty() : Optional.of(user.get(0));
    }

    @Override
    public List<UserBasicDTO> findAllBasicByIds(List<Long> ids) {
        GetUsersByIdsRequest request = new GetUsersByIdsRequest(ids);
        GetUsersByIdsResponse response = restTemplate.postForObject(userApiBaseUrl + "/internal/user/basic-info", request, GetUsersByIdsResponse.class);
        if (response == null) {
            throw new InternalException("User service did not respond");
        }
        return response.getUsers();
    }
}
