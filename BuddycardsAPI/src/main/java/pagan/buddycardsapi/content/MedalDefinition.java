package pagan.buddycardsapi.content;

public record MedalDefinition(
        String namespace,
        String path,
        String displayName,
        String set,
        String medalType,
        String effect,
        Integer level,
        Integer duration,
        String itemTexturePath
) {
    public String id() { return namespace + ":" + path; }
}
