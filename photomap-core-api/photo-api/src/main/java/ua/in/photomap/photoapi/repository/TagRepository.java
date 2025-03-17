package ua.in.photomap.photoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.in.photomap.photoapi.model.Tag;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameContainsIgnoreCase(String name);

    Set<Tag> findByNameIn(List<String> tagNames);
}
