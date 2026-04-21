package pagan.buddycardsapi.content;

public record LootInjectionDefinition(
        String namespace,
        String path,
        String targetLootTable,
        String item,
        Integer weight,
        Integer count
) {
    public String id() { return namespace + ":" + path; }
}
