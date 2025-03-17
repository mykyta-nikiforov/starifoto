package ua.in.photomap.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ua.in.photomap.user.dto.RoleDTO;
import ua.in.photomap.user.model.Role;

import java.util.List;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO roleToRoleDto(Role entity);

    List<RoleDTO> rolesToRoleDtos(List<Role> entities);
}
