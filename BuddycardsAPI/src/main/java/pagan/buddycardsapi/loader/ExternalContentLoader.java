package pagan.buddycardsapi.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.content.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ExternalContentLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ExternalContentLoader() {
    }

    public static ExternalContentRepository load(Path root) {
        Map<String, CardSetDefinition> sets = new LinkedHashMap<>();
        Map<String, CardDefinition> cards = new LinkedHashMap<>();
        Map<String, PackDefinition> packs = new LinkedHashMap<>();
        Map<String, BinderDefinition> binders = new LinkedHashMap<>();
        Map<String, BoosterBoxDefinition> boosterBoxes = new LinkedHashMap<>();
        Map<String, MedalDefinition> medals = new LinkedHashMap<>();
        Map<String, LootInjectionDefinition> lootInjections = new LinkedHashMap<>();

        Path dataRoot = root.resolve("data");
        if (!Files.exists(dataRoot)) {
            BuddycardsApiBridgeMod.LOGGER.info("[BuddycardsAPI] No data directory found at {}", dataRoot);
            return new ExternalContentRepository(
                    sets, cards, packs, binders, boosterBoxes, medals, lootInjections
            );
        }

        try (Stream<Path> namespaces = Files.list(dataRoot)) {
            namespaces.filter(Files::isDirectory).forEach(namespacePath -> {
                String namespace = namespacePath.getFileName().toString();
                Path apiRoot = namespacePath.resolve("buddyinfo");
                if (!Files.isDirectory(apiRoot)) {
                    return;
                }

                loadSets(apiRoot.resolve("sets"), namespace, sets);
                loadCards(apiRoot.resolve("cards"), namespace, cards);
                loadPacks(apiRoot.resolve("packs"), namespace, packs);
                loadBinders(apiRoot.resolve("binders"), namespace, binders);
                loadBoosterBoxes(apiRoot.resolve("booster_boxes"), namespace, boosterBoxes);
                loadMedals(apiRoot.resolve("medals"), namespace, medals);
                loadLootInjections(apiRoot.resolve("loot_injections"), namespace, lootInjections);
            });
        } catch (IOException e) {
            BuddycardsApiBridgeMod.LOGGER.error("Failed reading BuddycardsAPI data root {}", dataRoot, e);
        }

        validateReferences(sets, cards, packs, binders, boosterBoxes, medals, lootInjections);

        return new ExternalContentRepository(
                sets, cards, packs, binders, boosterBoxes, medals, lootInjections
        );
    }

    private static void loadSets(Path dir, String namespace, Map<String, CardSetDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());

            CardSetDefinition def = new CardSetDefinition(
                    namespace,
                    itemPath,
                    orDefault(getString(raw, "displayName"), title(itemPath)),
                    getString(raw, "description"),
                    orDefault(getString(raw, "binderPath"), itemPath),
                    defaultInt(getInteger(raw, "defaultCardsPerPack"), 3),
                    defaultInt(getInteger(raw, "defaultFoilsPerPack"), 1)
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate set id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void loadCards(Path dir, String namespace, Map<String, CardDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());
            String setId = getString(raw, "set");
            if (isBlank(setId)) {
                warn(file, "Card is missing required field 'set'");
                return;
            }

            String rarity = orDefault(getString(raw, "rarity"), "common");
            if (!isSupportedRarity(rarity)) {
                warn(file, "Unsupported rarity '" + rarity + "', defaulting to 'common'");
                rarity = "common";
            }

            CardDefinition def = new CardDefinition(
                    namespace,
                    itemPath,
                    orDefault(getString(raw, "displayName"), title(itemPath)),
                    setId,
                    defaultInt(getInteger(raw, "number"), 1),
                    rarity,
                    defaultInt(getInteger(raw, "cost"), 0),
                    defaultInt(getInteger(raw, "power"), 0),
                    defaultTexturePath(namespace, setId, getString(raw, "texturePath"), itemPath),
                    defaultBool(getBoolean(raw, "shinyByDefault"), false)
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate card id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void loadPacks(Path dir, String namespace, Map<String, PackDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());

            Map<String, Integer> rarityWeights = new LinkedHashMap<>();
            JsonObject weightsObj = getObject(raw, "rarityWeights");
            if (weightsObj != null) {
                for (Map.Entry<String, JsonElement> entry : weightsObj.entrySet()) {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                        rarityWeights.put(entry.getKey(), entry.getValue().getAsInt());
                    }
                }
            }

            if (rarityWeights.isEmpty()) {
                rarityWeights.put("common", 70);
                rarityWeights.put("uncommon", 20);
                rarityWeights.put("rare", 8);
                rarityWeights.put("epic", 2);
                warn(file, "Missing rarityWeights, using defaults");
            }

            List<PackEntryDefinition> entries = List.of();
            if (raw.has("entries") && raw.get("entries").isJsonArray()) {
                entries = raw.getAsJsonArray("entries").asList().stream().map(element -> {
                    JsonObject obj = element.getAsJsonObject();
                    return new PackEntryDefinition(
                            getString(obj, "card"),
                            defaultInt(getInteger(obj, "weight"), 1)
                    );
                }).toList();
            }

            PackDefinition def = new PackDefinition(
                    namespace,
                    itemPath,
                    orDefault(getString(raw, "displayName"), title(itemPath)),
                    defaultInt(getInteger(raw, "cards"), 3),
                    defaultInt(getInteger(raw, "foils"), 1),
                    getString(raw, "set"),
                    defaultTexturePath(namespace, getString(raw, "set"), getString(raw, "texturePath"), itemPath),
                    rarityWeights,
                    entries
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate pack id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void loadBinders(Path dir, String namespace, Map<String, BinderDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());
            String id = namespace + ":" + itemPath;
            String setId = getString(raw, "set");

            if (isBlank(setId)) {
                warn(file, "Binder is missing required field 'set'");
                return;
            }

            BinderDefinition def = new BinderDefinition(
                    id,
                    namespace,
                    itemPath,
                    setId,
                    orDefault(getString(raw, "displayName"), title(itemPath)),
                    defaultInt(getInteger(raw, "rows"), 6),
                    orDefault(getString(raw, "guiTexturePath"), itemPath)
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate binder id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void loadBoosterBoxes(Path dir, String namespace, Map<String, BoosterBoxDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());
            String id = namespace + ":" + itemPath;

            String setId = getString(raw, "set");
            String packId = getString(raw, "pack");

            if (isBlank(setId) && isBlank(packId)) {
                warn(file, "Booster box must define either 'set' or 'pack'");
                return;
            }

            BoosterBoxDefinition def = new BoosterBoxDefinition(
                    namespace,
                    itemPath,
                    orDefault(getString(raw, "displayName"), title(itemPath)),
                    setId,
                    packId,
                    id
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate booster box id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void loadMedals(Path dir, String namespace, Map<String, MedalDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());
            String setId = getString(raw, "set");
            String effect = getString(raw, "effect");
            String medalType = orDefault(getString(raw, "medalType"), "buddysteel");

            if (isBlank(setId)) {
                warn(file, "Medal is missing required field 'set'");
                return;
            }

            if (isBlank(effect)) {
                warn(file, "Medal is missing required field 'effect'");
                return;
            }

            if (!isSupportedMedalType(medalType)) {
                warn(file, "Unsupported medalType '" + medalType + "', defaulting to 'buddysteel'");
                medalType = "buddysteel";
            }

            MedalDefinition def = new MedalDefinition(
                    namespace,
                    itemPath,
                    orDefault(getString(raw, "displayName"), title(itemPath)),
                    setId,
                    medalType,
                    effect,
                    defaultInt(getInteger(raw, "level"), 1),
                    defaultInt(getInteger(raw, "duration"), 220),
                    defaultTexturePath(namespace, setId, getString(raw, "itemTexturePath"), itemPath)
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate medal id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void loadLootInjections(Path dir, String namespace, Map<String, LootInjectionDefinition> out) {
        forEachJson(dir, file -> {
            JsonObject raw = readObject(file);
            if (raw == null) return;

            String itemPath = stripJson(file.getFileName().toString());
            String targetLootTable = getString(raw, "targetLootTable");
            String item = getString(raw, "item");

            if (isBlank(targetLootTable)) {
                warn(file, "Loot injection is missing required field 'targetLootTable'");
                return;
            }

            if (isBlank(item)) {
                warn(file, "Loot injection is missing required field 'item'");
                return;
            }

            LootInjectionDefinition def = new LootInjectionDefinition(
                    namespace,
                    itemPath,
                    targetLootTable,
                    item,
                    defaultInt(getInteger(raw, "weight"), 1),
                    defaultInt(getInteger(raw, "count"), 1)
            );

            if (out.containsKey(def.id())) {
                warn(file, "Duplicate loot injection id '" + def.id() + "', overriding previous definition");
            }

            out.put(def.id(), def);
        });
    }

    private static void validateReferences(
            Map<String, CardSetDefinition> sets,
            Map<String, CardDefinition> cards,
            Map<String, PackDefinition> packs,
            Map<String, BinderDefinition> binders,
            Map<String, BoosterBoxDefinition> boosterBoxes,
            Map<String, MedalDefinition> medals,
            Map<String, LootInjectionDefinition> lootInjections
    ) {
        for (CardDefinition card : cards.values()) {
            if (isBlank(card.set()) || !sets.containsKey(card.set())) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Card '{}' references missing set '{}'", card.id(), card.set());
            }
        }

        for (PackDefinition pack : packs.values()) {
            if (!isBlank(pack.set()) && !sets.containsKey(pack.set())) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Pack '{}' references missing set '{}'", pack.id(), pack.set());
            }

            if (pack.entries() != null) {
                for (PackEntryDefinition entry : pack.entries()) {
                    if (isBlank(entry.card()) || !cards.containsKey(entry.card())) {
                        BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Pack '{}' references missing card '{}'", pack.id(), entry.card());
                    }
                }
            }
        }

        for (BinderDefinition binder : binders.values()) {
            if (isBlank(binder.set()) || !sets.containsKey(binder.set())) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Binder '{}' references missing set '{}'", binder.id(), binder.set());
            }
        }

        for (BoosterBoxDefinition box : boosterBoxes.values()) {
            if (!isBlank(box.pack()) && !packs.containsKey(box.pack())) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Booster box '{}' references missing pack '{}'", box.id(), box.pack());
            }

            if (isBlank(box.pack()) && !isBlank(box.set())) {
                boolean found = packs.values().stream().anyMatch(p -> box.set().equals(p.set()));
                if (!found) {
                    BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Booster box '{}' references set '{}' but no matching pack was found", box.id(), box.set());
                }
            }
        }

        for (MedalDefinition medal : medals.values()) {
            if (isBlank(medal.set()) || !sets.containsKey(medal.set())) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Medal '{}' references missing set '{}'", medal.id(), medal.set());
            }
        }

        for (LootInjectionDefinition loot : lootInjections.values()) {
            if (isBlank(loot.item())) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Loot injection '{}' has blank item reference", loot.id());
            }
        }
    }

    private static void forEachJson(Path dir, ThrowingConsumer<Path> consumer) {
        if (!Files.isDirectory(dir)) return;

        try (Stream<Path> files = Files.list(dir)) {
            files.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            consumer.accept(path);
                        } catch (Exception e) {
                            BuddycardsApiBridgeMod.LOGGER.error("Failed to read BuddycardsAPI file {}", path, e);
                        }
                    });
        } catch (IOException e) {
            BuddycardsApiBridgeMod.LOGGER.error("Failed to list BuddycardsAPI directory {}", dir, e);
        }
    }

    private static JsonObject readObject(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement element = GSON.fromJson(reader, JsonElement.class);
            return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (Exception e) {
            BuddycardsApiBridgeMod.LOGGER.error("Failed parsing BuddycardsAPI json {}", file, e);
            return null;
        }
    }

    private static String stripJson(String fileName) {
        return fileName.endsWith(".json") ? fileName.substring(0, fileName.length() - 5) : fileName;
    }

    private static String title(String path) {
        String[] parts = path.split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            if (!builder.isEmpty()) builder.append(' ');
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }

    private static String orDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int defaultInt(Integer value, int fallback) {
        return value == null ? fallback : value;
    }

    private static boolean defaultBool(Boolean value, boolean fallback) {
        return value == null ? fallback : value;
    }

    private static String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsString();
    }

    private static Integer getInteger(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsInt();
    }

    private static Boolean getBoolean(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsBoolean();
    }

    private static JsonObject getObject(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonObject()) return null;
        return obj.getAsJsonObject(key);
    }

    private static void warn(Path file, String message) {
        BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] {} -> {}", file, message);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean isSupportedRarity(String rarity) {
        return rarity.equalsIgnoreCase("common")
                || rarity.equalsIgnoreCase("uncommon")
                || rarity.equalsIgnoreCase("rare")
                || rarity.equalsIgnoreCase("epic");
    }

    private static boolean isSupportedMedalType(String medalType) {
        return medalType.equalsIgnoreCase("buddysteel")
                || medalType.equalsIgnoreCase("luminis")
                || medalType.equalsIgnoreCase("zylex");
    }

    private static String defaultTexturePath(String namespace, String setId, String explicit, String itemPath) {
        if (!isBlank(explicit)) {
            return explicit;
        }

        String setFolder = extractSetFolder(namespace, setId);
        if (setFolder == null) {
            return itemPath;
        }

        return setFolder + "/" + itemPath;
    }

    private static String extractSetFolder(String namespace, String setId) {
        if (isBlank(setId)) {
            return null;
        }

        int colon = setId.indexOf(':');
        if (colon < 0) {
            return null;
        }

        String setNamespace = setId.substring(0, colon);
        String setPath = setId.substring(colon + 1);

        if (!namespace.equals(setNamespace)) {
            return setPath;
        }

        return setPath;
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T value) throws Exception;
    }
}