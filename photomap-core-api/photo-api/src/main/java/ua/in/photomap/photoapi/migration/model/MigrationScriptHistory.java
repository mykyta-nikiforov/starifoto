package ua.in.photomap.photoapi.migration.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.photoapi.migration.script.Script;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "migration_script_history")
public class MigrationScriptHistory {

    @Id()
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version")
    private Integer version;

    @Column(name = "script_name")
    private String scriptName;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    public MigrationScriptHistory(Script script) {
        this.version = script.getVersion();
        this.scriptName = script.getScriptName();
        this.executedAt = LocalDateTime.now();
    }
}
