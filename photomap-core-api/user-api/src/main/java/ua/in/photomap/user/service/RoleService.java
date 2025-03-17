package ua.in.photomap.user.service;

import ua.in.photomap.user.model.Role;

import java.util.List;

public interface RoleService {
    Role getUserRole();

    List<Role> findAll();

    List<Role> findAllById(List<Long> ids);
}
