package ua.in.photomap.photoapi.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ua.in.photomap.photoapi.migration.model.MigrationScriptHistory;
import ua.in.photomap.photoapi.migration.repository.MigrationScriptHistoryRepository;
import ua.in.photomap.photoapi.migration.script.Script;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScriptRunner implements ApplicationListener<ApplicationReadyEvent> {

    private final List<Script> scripts;
    private final MigrationScriptHistoryRepository historyRepository;
    private final ScriptExecutor scriptExecutor;

    public ScriptRunner(List<Script> scripts, MigrationScriptHistoryRepository historyRepository, ScriptExecutor scriptExecutor) {
        this.scripts = scripts.stream().
                sorted(Comparator.comparingInt(Script::getVersion))
                .collect(Collectors.toList());
        this.historyRepository = historyRepository;
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("Script runner started");
        validateScriptVersions(scripts);
        Optional<Integer> lastExecuted = historyRepository.getLastExecutedScriptVersion();
        if (lastExecuted.isPresent()) {
            log.info("The last successfully executed script version - {}", lastExecuted.get());
        } else {
            log.info("No previous successfully executed scripts");
        }
        List<Script> scriptsToExecute = scripts.stream()
                .filter(script -> lastExecuted.isEmpty() || script.getVersion() > lastExecuted.get())
                .sorted(Comparator.comparingInt(Script::getVersion))
                .toList();
        for (Script script : scriptsToExecute) {
            log.info("Started executing script version - {}", script.getVersion());
            scriptExecutor.execute(script);
            log.info("Script version - {} successfully executed", script.getVersion());
            MigrationScriptHistory migrationScriptHistory = new MigrationScriptHistory(script);
            historyRepository.saveAndFlush(migrationScriptHistory);
        }
        log.info("Script runner finished");
    }

    private void validateScriptVersions(List<Script> scripts) {
        List<Integer> duplicateVersions = scripts.stream()
                .map(Script::getVersion)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(p -> p.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateVersions.isEmpty()) {
            throw new IllegalArgumentException("There are some duplicate versions - " + duplicateVersions);
        }
    }
}