package org.eapo.corresp.pathgetter;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author astal Родитель для классов возвращающих путь к файлу по id документа
 */
public abstract class PathGetter {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    protected static Path pathToWorkDir = Paths.get(".");

    public static Path getPathToWorkDir() {
        return pathToWorkDir;
    }

    public static void setPathToWorkDir(Path path) {
        pathToWorkDir = path;
    }

    abstract public Path get(String idletter);

}
