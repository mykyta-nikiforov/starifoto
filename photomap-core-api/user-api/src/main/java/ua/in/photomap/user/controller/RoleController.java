package ua.in.photomap.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.common.rest.toolkit.annotation.PreAuthorizeModeratorOrHigher;
import ua.in.photomap.user.dto.RoleDTO;
import ua.in.photomap.user.mapper.RoleMapper;
import ua.in.photomap.user.service.RoleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/role")
@Tag(name = "Role API", description = "API to work with roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/{all}")
    @PreAuthorizeModeratorOrHigher
    @Operation(description = "Get all roles")
    public List<RoleDTO> getAll() {
        return roleService.findAll().stream()
                .map(RoleMapper.INSTANCE::roleToRoleDto)
                .toList();
    }
}
