package ua.in.photomap.photoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.in.photomap.photoapi.dto.PhotoSitemapDataDTO;
import ua.in.photomap.photoapi.dto.supercluster.DetailedPropertiesDTO;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.model.SimilarPhotoData;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @EntityGraph(value = "photo-entity-graph")
    @Query("SELECT p FROM Photo p WHERE p.id = :id")
    Optional<Photo> findByIdFetchGraph(Long id);

    @EntityGraph(value = "photo-entity-graph")
    @Query("SELECT p FROM Photo p " +
            "WHERE p.id IN :photoIds " +
            "ORDER BY p.createdAt DESC")
    List<Photo> findAllGraphByIds(List<Long> photoIds);

    @Query(value = "SELECT p.id FROM Photo p " +
            "ORDER BY p.createdAt DESC")
    Page<Long> findAllPhotoIds(Pageable pageable);

    @Query(value = "SELECT p.id FROM Photo p " +
            "WHERE p.userId = :userId " +
            "ORDER BY p.createdAt DESC")
    Page<Long> findAllPhotoIdsByUserId(Long userId, Pageable pageable);

    @EntityGraph("photo-geojson-data-graph")
    @Query("SELECT p FROM Photo p " +
            "WHERE p.id IN :photoIds " +
            "ORDER BY p.createdAt DESC")
    List<Photo> findAllGeoJsonGraphByIds(List<Long> photoIds);

    boolean existsByIdAndUserId(Long photoId, Long userId);

    @Query("SELECT new ua.in.photomap.photoapi.dto.supercluster.DetailedPropertiesDTO(" +
            "p.id, p.title, fOriginal.url, COALESCE(fColorized.url, null), fOriginal.width, fOriginal.height) " +
            "FROM Photo p " +
            "JOIN p.files fOriginal ON fOriginal.fileType = 'ORIGINAL' " +
            "LEFT JOIN p.files fColorized ON fColorized.fileType = 'COLORIZED' " +
            "WHERE p.id IN :photoIds")
    List<DetailedPropertiesDTO> findAllGalleryDetailsByIds(List<Long> photoIds);

    @Query("SELECT new ua.in.photomap.photoapi.dto.PhotoSitemapDataDTO(" +
            "p.id, p.updatedAt, f.url) " +
            "FROM Photo p " +
            "LEFT JOIN p.files f ON f.fileType = 'ORIGINAL' " +
            "ORDER BY p.createdAt DESC")
    Page<PhotoSitemapDataDTO> findAllSitemapDataPage(Pageable pageable);

    @Query(value = "SELECT photo.id as id, " +
            "photo_service.levenshtein(cast(if.image_phash as text), cast(:hash as text)) as similarity " +
            " FROM photo_service.image_file if " +
            "JOIN photo_service.photo_image_file photo_image_file ON if.id = photo_image_file.image_file_id " +
            "JOIN photo_service.photo photo ON photo.id = photo_image_file.photo_id " +
            "WHERE photo_service.levenshtein(cast(if.image_phash as text), cast(:hash as text)) <= 3 " +
            "LIMIT 5",
            nativeQuery = true)
    List<SimilarPhotoData> findSimilarPhotosByHash(String hash);
}
