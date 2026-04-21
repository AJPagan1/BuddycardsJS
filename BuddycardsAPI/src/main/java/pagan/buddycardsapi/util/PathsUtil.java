package pagan.buddycardsapi.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathsUtil {
    public static final String DATA_SUBFOLDER_NAME = "buddyinfo";

    private PathsUtil() {
    }

    public static Path gameRoot() {
        return Paths.get("").toAbsolutePath();
    }

    public static Path root() {
        return gameRoot().resolve("kubejs");
    }

    public static Path dataRoot() {
        return root().resolve("data");
    }

    public static Path assetsRoot() {
        return root().resolve("assets");
    }

    public static Path apiRootForNamespace(Path namespacePath) {
        return namespacePath.resolve(DATA_SUBFOLDER_NAME);
    }
}