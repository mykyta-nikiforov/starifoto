package ua.in.photomap.photoapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ua.in.photomap.photoapi.dto.TagDTO;
import ua.in.photomap.photoapi.model.Tag;

@Mapper
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    @Mapping(target = "name", source = "name")
    TagDTO tagToTagDto(Tag entity);

}