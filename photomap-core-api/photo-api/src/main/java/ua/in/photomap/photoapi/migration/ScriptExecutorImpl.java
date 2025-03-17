package ua.in.photomap.photoapi.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.in.photomap.photoapi.migration.script.Script;

@Component
@Slf4j
public class ScriptExecutorImpl implements ScriptExecutor {

    @Override
    public void execute(Script script) {
        try {
            script.start();
        } catch (Throwable e) {
            log.error("Error during script running", e);
            throw new IllegalArgumentException("Failed to execute script - " + script.getVersion(), e);
        }
    }
}
