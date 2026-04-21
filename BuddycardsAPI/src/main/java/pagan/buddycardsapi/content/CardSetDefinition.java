package pagan.buddycardsapi.content;

public record CardSetDefinition(
        String namespace,
        String path,
        String displayName,
        String description,
        String binderPath,
        Integer defaultCardsPerPack,
        Integer defaultFoilsPerPack
) {
    public String id() { return namespace + ":" + path; }
    public String buddycardsSetName() { return id(); }
}
