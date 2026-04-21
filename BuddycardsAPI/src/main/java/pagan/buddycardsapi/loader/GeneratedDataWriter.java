package pagan.buddycardsapi.loader;

import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.content.BinderDefinition;
import pagan.buddycardsapi.content.BoosterBoxDefinition;
import pagan.buddycardsapi.content.CardDefinition;
import pagan.buddycardsapi.content.CardSetDefinition;
import pagan.buddycardsapi.content.ExternalContentRepository;
import pagan.buddycardsapi.content.MedalDefinition;
import pagan.buddycardsapi.content.PackDefinition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class GeneratedDataWriter {
    private GeneratedDataWriter() {
    }

    public static void generate(ExternalContentRepository repository, Path root) throws IOException {
        writeBuddycardsCardTag(repository, root);
        writeSetSpecificCardTags(repository, root);
        writeCuriosMedalTag(repository, root);
        writeBoosterBoxRecipes(repository, root);
        writeBinderRecipes(repository, root);
    }

    private static void writeBuddycardsCardTag(ExternalContentRepository repository, Path root) throws IOException {
        Path tagFile = root.resolve("data")
                .resolve("buddycards")
                .resolve("tags")
                .resolve("items")
                .resolve("buddycards.json");

        Files.createDirectories(tagFile.getParent());

        List<String> values = new ArrayList<>();
        for (CardDefinition definition : repository.cards().values()) {
            values.add("\"" + definition.id() + "\"");
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"replace\": false,\n");
        json.append("  \"values\": [\n");

        for (int i = 0; i < values.size(); i++) {
            json.append("    ").append(values.get(i));
            if (i < values.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");

        Files.writeString(tagFile, json.toString(), StandardCharsets.UTF_8);
    }

    private static void writeSetSpecificCardTags(ExternalContentRepository repository, Path root) throws IOException {
        for (CardSetDefinition set : repository.sets().values()) {
            String tagName = "buddycards_" + set.path();

            Path tagFile = root.resolve("data")
                    .resolve("buddycards")
                    .resolve("tags")
                    .resolve("items")
                    .resolve(tagName + ".json");

            Files.createDirectories(tagFile.getParent());

            List<String> values = new ArrayList<>();
            for (CardDefinition card : repository.cards().values()) {
                if (set.id().equals(card.set())) {
                    values.add("\"" + card.id() + "\"");
                }
            }

            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"replace\": false,\n");
            json.append("  \"values\": [\n");

            for (int i = 0; i < values.size(); i++) {
                json.append("    ").append(values.get(i));
                if (i < values.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }

            json.append("  ]\n");
            json.append("}\n");

            Files.writeString(tagFile, json.toString(), StandardCharsets.UTF_8);
        }
    }

    private static void writeCuriosMedalTag(ExternalContentRepository repository, Path root) throws IOException {
        Path tagFile = root.resolve("data")
                .resolve("curios")
                .resolve("tags")
                .resolve("items")
                .resolve("medal.json");

        Files.createDirectories(tagFile.getParent());

        List<String> values = new ArrayList<>();
        for (MedalDefinition definition : repository.medals().values()) {
            values.add("\"" + definition.id() + "\"");
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"replace\": false,\n");
        json.append("  \"values\": [\n");

        for (int i = 0; i < values.size(); i++) {
            json.append("    ").append(values.get(i));
            if (i < values.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");

        Files.writeString(tagFile, json.toString(), StandardCharsets.UTF_8);
    }

    private static void writeBoosterBoxRecipes(ExternalContentRepository repository, Path root) throws IOException {
        for (BoosterBoxDefinition box : repository.boosterBoxes().values()) {
            String packId = resolvePackId(repository, box);
            if (packId == null || packId.isBlank()) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Skipping recipe generation for booster box '{}' because no pack could be resolved", box.id());
                continue;
            }

            PackDefinition pack = repository.packs().get(packId);
            if (pack == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Skipping recipe generation for booster box '{}' because pack '{}' does not exist", box.id(), packId);
                continue;
            }

            String boxItemId = box.id();
            String packItemId = pack.id();

            Path recipesRoot = root.resolve("data")
                    .resolve(box.namespace())
                    .resolve("recipes");

            Files.createDirectories(recipesRoot);

            Path packToBox = recipesRoot.resolve(box.path() + "_from_packs.json");
            Path boxToPack = recipesRoot.resolve(pack.path() + "_from_" + box.path() + ".json");

            String shaped = """
                    {
                      "type": "minecraft:crafting_shaped",
                      "pattern": [
                        "PPP",
                        "PPP"
                      ],
                      "key": {
                        "P": {
                          "item": "%s"
                        }
                      },
                      "result": {
                        "item": "%s",
                        "count": 1
                      }
                    }
                    """.formatted(packItemId, boxItemId);

            String shapeless = """
                    {
                      "type": "minecraft:crafting_shapeless",
                      "ingredients": [
                        {
                          "item": "%s"
                        }
                      ],
                      "result": {
                        "item": "%s",
                        "count": 6
                      }
                    }
                    """.formatted(boxItemId, packItemId);

            Files.writeString(packToBox, shaped, StandardCharsets.UTF_8);
            Files.writeString(boxToPack, shapeless, StandardCharsets.UTF_8);
        }
    }

    private static void writeBinderRecipes(ExternalContentRepository repository, Path root) throws IOException {
        for (BinderDefinition binder : repository.binders().values()) {
            if (binder.set() == null || binder.set().isBlank()) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Skipping binder recipe generation for '{}' because set is missing", binder.id());
                continue;
            }

            String setPath = extractSetPath(binder.set());
            if (setPath == null || setPath.isBlank()) {
                BuddycardsApiBridgeMod.LOGGER.warn("[BuddycardsAPI] Skipping binder recipe generation for '{}' because set path could not be resolved", binder.id());
                continue;
            }

            String tagId = "buddycards:buddycards_" + setPath;

            Path recipeFile = root.resolve("data")
                    .resolve(binder.namespace())
                    .resolve("recipes")
                    .resolve(binder.path() + ".json");

            Files.createDirectories(recipeFile.getParent());

            String json = """
                    {
                      "type": "minecraft:crafting_shapeless",
                      "ingredients": [
                        {
                          "item": "minecraft:leather"
                        },
                        {
                          "tag": "%s"
                        },
                        {
                          "tag": "%s"
                        },
                        {
                          "tag": "%s"
                        }
                      ],
                      "result": {
                        "item": "%s",
                        "count": 1
                      }
                    }
                    """.formatted(tagId, tagId, tagId, binder.id());

            Files.writeString(recipeFile, json, StandardCharsets.UTF_8);
        }
    }

    private static String extractSetPath(String setId) {
        if (setId == null || setId.isBlank()) {
            return null;
        }

        int colon = setId.indexOf(':');
        if (colon < 0 || colon == setId.length() - 1) {
            return null;
        }

        return setId.substring(colon + 1);
    }

    private static String resolvePackId(ExternalContentRepository repository, BoosterBoxDefinition box) {
        if (box.pack() != null && !box.pack().isBlank()) {
            return box.pack();
        }

        if (box.set() != null && !box.set().isBlank()) {
            for (PackDefinition candidate : repository.packs().values()) {
                if (box.set().equals(candidate.set())) {
                    return candidate.id();
                }
            }
        }

        return null;
    }
}