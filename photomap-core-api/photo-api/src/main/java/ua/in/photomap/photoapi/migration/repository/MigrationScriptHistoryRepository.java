package ua.in.photomap.photoapi.migration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.in.photomap.photoapi.migration.model.MigrationScriptHistory;

import java.util.Optional;

@Repository
public interface MigrationScriptHistoryRepository extends JpaRepository<MigrationScriptHistory, Long> {

    @Query("SELECT msh.version FROM MigrationScriptHistory msh ORDER BY msh.version DESC LIMIT 1")
    Optional<Integer> getLastExecutedScriptVersion();
}
