package pagan.buddycardsapi.loader;

import pagan.buddycardsapi.util.PathsUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PackMetadataWriter {
    private PackMetadataWriter() {}

    public static void ensurePackMeta() throws IOException {
        Path packMeta = PathsUtil.root().resolve("pack.mcmeta");
        if (Files.exists(packMeta)) {
            return;
        }
        String json = """
                {
                  "pack": {
                    "description": "Buddycards API external assets",
                    "pack_format": 15
                  }
                }
                """;
        Files.writeString(packMeta, json, StandardCharsets.UTF_8);
    }
}
