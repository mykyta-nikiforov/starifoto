package ua.in.photomap.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ua.in.photomap.user.dto.UserDTO;
import ua.in.photomap.user.dto.UserPublicDTO;
import ua.in.photomap.user.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "provider", source = "provider")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "hasPassword", source = "password", qualifiedByName = "hasPassword")
    @Mapping(target = "isEnabled", source = "isEnabled")
    @Mapping(target = "createdAt", source = "createdAt")
    UserDTO userToUserDto(User entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "createdAt", source = "createdAt")
    UserPublicDTO userToUserPublicDto(User entity);

    @Named("hasPassword")
    default Boolean hasPassword(String password) {
        return password != null;
    }
}