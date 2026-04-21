package pagan.buddycardsapi.content;

public record CardDefinition(
        String namespace,
        String path,
        String displayName,
        String set,
        Integer number,
        String rarity,
        Integer cost,
        Integer power,
        String texturePath,
        Boolean shinyByDefault
) {
    public String id() {
        return namespace + ":" + path;
    }
}