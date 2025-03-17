package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.in.photomap.photoapi.dto.TagDTO;
import ua.in.photomap.photoapi.mapper.TagMapper;
import ua.in.photomap.photoapi.model.Tag;
import ua.in.photomap.photoapi.repository.TagRepository;
import ua.in.photomap.photoapi.service.TagService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<TagDTO> findByNameContains(String name) {
        return tagRepository.findByNameContainsIgnoreCase(name).stream()
                .map(TagMapper.INSTANCE::tagToTagDto)
                .toList();
    }

    @Override
    public Set<Tag> getExistingTagsOrCreateNew(List<String> tagNames) {
        List<String> formattedTagsNames = tagNames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        // Find existing tags by their names
        Set<Tag> result = tagRepository.findByNameIn(formattedTagsNames);

        // Create new tags for the ones that don't exist
        List<String> existingTagNames = result.stream()
                .map(Tag::getName)
                .toList();

        List<Tag> newTags = formattedTagsNames.stream()
                .filter(tagName -> !existingTagNames.contains(tagName))
                .map(tagName -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    return newTag;
                })
                .collect(Collectors.toList());

        // Save all new tags at once
        newTags = tagRepository.saveAll(newTags);

        // Combine existing and newly created tags
        result.addAll(newTags);

        return result;
    }
}
