package pagan.buddycardsapi.content;

import java.util.LinkedHashMap;
import java.util.Map;

public record ExternalContentRepository(
        Map<String, CardSetDefinition> sets,
        Map<String, CardDefinition> cards,
        Map<String, PackDefinition> packs,
        Map<String, BinderDefinition> binders,
        Map<String, BoosterBoxDefinition> boosterBoxes,
        Map<String, MedalDefinition> medals,
        Map<String, LootInjectionDefinition> lootInjections
) {
    public static ExternalContentRepository empty() {
        return new ExternalContentRepository(
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>()
        );
    }
}
