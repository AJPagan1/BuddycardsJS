package pagan.buddycardsapi.content;

public record BoosterBoxDefinition(
        String namespace,
        String path,
        String displayName,
        String set,
        String pack,
        String id
) {
    public String bottomTexturePath() {
        String folder = extractSetFolder();
        String tex = path + "_bottom";
        return folder == null ? tex : folder + "/" + tex;
    }

    public String sideTexturePath() {
        String folder = extractSetFolder();
        String tex = path + "_side";
        return folder == null ? tex : folder + "/" + tex;
    }

    public String topTexturePath() {
        String folder = extractSetFolder();
        String tex = path + "_top";
        return folder == null ? tex : folder + "/" + tex;
    }

    private String extractSetFolder() {
        if (set == null || set.isBlank()) {
            return null;
        }

        int colon = set.indexOf(':');
        if (colon < 0 || colon == set.length() - 1) {
            return null;
        }

        return set.substring(colon + 1);
    }
}