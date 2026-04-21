package pagan.buddycardsapi.content;

import java.util.List;
import java.util.Map;

public record PackDefinition(
        String namespace,
        String path,
        String displayName,
        Integer cards,
        Integer foils,
        String set,
        String texturePath,
        Map<String, Integer> rarityWeights,
        List<PackEntryDefinition> entries
) {
    public String id() { return namespace + ":" + path; }
}
