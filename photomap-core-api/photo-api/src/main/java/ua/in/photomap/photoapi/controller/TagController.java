package ua.in.photomap.photoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.in.photomap.photoapi.dto.TagDTO;
import ua.in.photomap.photoapi.model.Tag;
import ua.in.photomap.photoapi.service.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/photo/tag")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag API", description = "API to work with tags")
public class TagController {
    private final TagService tagService;

    // TODO replace with Page<TagDTO>
    @GetMapping("/all")
    @Operation(summary = "Get all tags.")
    public List<Tag> getPhoto() {
        return tagService.findAll();
    }

    // TODO replace with Page<TagDTO>
    @GetMapping("/search")
    @Operation(summary = "Search tags by name.")
    public List<TagDTO> searchTags(
            @Parameter(description = "Tag query", required = true)
            @RequestParam String name) {
        return tagService.findByNameContains(name);
    }
}
