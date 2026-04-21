package pagan.buddycardsapi.content;

public record BinderDefinition(
        String id,
        String namespace,
        String path,
        String set,
        String displayName,
        Integer rows,
        String guiTexturePath
) {
    public String itemTexturePath() {
        String folder = extractSetFolder();
        return folder == null ? path : folder + "/" + path;
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