package ua.in.photomap.photoapi.migration.script;

public interface Script {

    void start();

    int getVersion();

    default String getScriptName(){
        return this.getClass().getSimpleName();
    }
}
