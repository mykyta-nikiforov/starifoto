package ua.in.photomap.geojsongenerator.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.Arrays;
import java.util.List;

@ChangeUnit(id = "V1_001__Indexes", order = "1")
@RequiredArgsConstructor
public class V1_001__Indexes {
    private final MongoTemplate mongoTemplate;
    private final String TAGS_INDEX_NAME = "properties_tags_index";
    private final String COORDINATES_INDEX_NAME = "geometry_coordinates_index";
    private final String YEARS_INDEX_NAME = "properties_yearStart_yearEnd_index";

    private final List<String> INDEX_NAMES = Arrays.asList(TAGS_INDEX_NAME, COORDINATES_INDEX_NAME,
            YEARS_INDEX_NAME);


    @Execution
    public void addIndexes() {
        IndexOperations indexOperations = mongoTemplate.indexOps("photo-features");
        indexOperations.ensureIndex(new Index().on("geometry.coordinates", Sort.Direction.ASC).named(COORDINATES_INDEX_NAME));
        indexOperations.ensureIndex(new Index().on("properties.tags", Sort.Direction.ASC).named(TAGS_INDEX_NAME));
        indexOperations.ensureIndex(new Index().on("properties.yearStart", Sort.Direction.ASC)
                .on("properties.yearEnd", Sort.Direction.ASC).named(YEARS_INDEX_NAME));
    }

    @RollbackExecution
    public void rollback() {
        IndexOperations indexOperations = mongoTemplate.indexOps("photo-features");
        indexOperations.getIndexInfo()
                .stream()
                .filter(i -> INDEX_NAMES.contains(i.getName()))
                .forEach(i -> indexOperations.dropIndex(i.getName()));
    }
}