package ua.in.photomap.photoapi.migration;


import ua.in.photomap.photoapi.migration.script.Script;

public interface ScriptExecutor {

    void execute(Script script);

}
