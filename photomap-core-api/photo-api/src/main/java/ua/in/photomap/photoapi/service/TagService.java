package ua.in.photomap.photoapi.service;

import ua.in.photomap.photoapi.dto.TagDTO;
import ua.in.photomap.photoapi.model.Tag;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<Tag> findAll();

    List<TagDTO> findByNameContains(String name);

    Set<Tag> getExistingTagsOrCreateNew(List<String> tagNames);
}
