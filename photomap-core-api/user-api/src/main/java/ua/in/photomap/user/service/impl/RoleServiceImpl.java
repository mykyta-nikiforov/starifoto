package ua.in.photomap.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.rest.toolkit.exception.InternalException;
import ua.in.photomap.user.model.Role;
import ua.in.photomap.user.repository.RoleRepository;
import ua.in.photomap.user.service.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final static Long USER_ROLE_ID = 1L;

    private final RoleRepository roleRepository;

    @Override
    public Role getUserRole() {
        return roleRepository.findById(USER_ROLE_ID)
                .orElseThrow(() -> new InternalException("User role not found"));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> findAllById(List<Long> ids) {
        return roleRepository.findAllById(ids);
    }
}
