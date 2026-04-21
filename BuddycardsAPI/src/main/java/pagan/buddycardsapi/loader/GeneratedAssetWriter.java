package pagan.buddycardsapi.loader;

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
import java.util.HashMap;
import java.util.Map;

public final class GeneratedAssetWriter {
    private GeneratedAssetWriter() {
    }

    public static void generate(ExternalContentRepository repo, Path root) throws IOException {
        Map<String, Map<String, String>> langByNamespace = new HashMap<>();

        for (CardDefinition card : repo.cards().values()) {
            writeCardItemModel(root, card.namespace(), card.path(), card.texturePath());
            langByNamespace.computeIfAbsent(card.namespace(), k -> new HashMap<>())
                    .put("item." + card.namespace() + "." + card.path(), card.displayName());
        }

        for (PackDefinition pack : repo.packs().values()) {
            writeItemModel(root, pack.namespace(), pack.path(), pack.texturePath());
            langByNamespace.computeIfAbsent(pack.namespace(), k -> new HashMap<>())
                    .put("item." + pack.namespace() + "." + pack.path(), pack.displayName());
        }

        for (BinderDefinition binder : repo.binders().values()) {
            writeItemModel(root, binder.namespace(), binder.path(), binder.itemTexturePath());
            langByNamespace.computeIfAbsent(binder.namespace(), k -> new HashMap<>())
                    .put("item." + binder.namespace() + "." + binder.path(), binder.displayName());
        }

        for (MedalDefinition medal : repo.medals().values()) {
            writeItemModel(root, medal.namespace(), medal.path(), medal.itemTexturePath());
            langByNamespace.computeIfAbsent(medal.namespace(), k -> new HashMap<>())
                    .put("item." + medal.namespace() + "." + medal.path(), medal.displayName());
        }

        for (BoosterBoxDefinition box : repo.boosterBoxes().values()) {
            writeBoosterBoxAssets(root, box);
            langByNamespace.computeIfAbsent(box.namespace(), k -> new HashMap<>())
                    .put("block." + box.namespace() + "." + box.path(), box.displayName());
            langByNamespace.get(box.namespace())
                    .put("item." + box.namespace() + "." + box.path(), box.displayName());
        }

        for (CardSetDefinition set : repo.sets().values()) {
            langByNamespace.computeIfAbsent(set.namespace(), k -> new HashMap<>())
                    .put("item.buddycards.buddycard.set_" + set.id(), set.displayName());

            if (set.description() != null) {
                langByNamespace.get(set.namespace())
                        .put("description.buddycardset." + set.buddycardsSetName(), set.description());
            }
        }

        for (Map.Entry<String, Map<String, String>> entry : langByNamespace.entrySet()) {
            writeLang(root, entry.getKey(), entry.getValue());
        }
    }

    private static void writeItemModel(Path root, String namespace, String path, String texturePath) throws IOException {
        Path model = root.resolve("assets")
                .resolve(namespace)
                .resolve("models")
                .resolve("item")
                .resolve(path + ".json");

        Files.createDirectories(model.getParent());

        String json = """
            {
              "parent": "item/generated",
              "textures": {
                "layer0": "%s:item/%s"
              }
            }
            """.formatted(namespace, texturePath);

        Files.writeString(model, json, StandardCharsets.UTF_8);
    }
    private static void writeCardItemModel(Path root, String namespace, String path, String texturePath) throws IOException {
        Path model = root.resolve("assets")
                .resolve(namespace)
                .resolve("models")
                .resolve("item")
                .resolve(path + ".json");

        Files.createDirectories(model.getParent());

        String baseJson = """
            {
              "parent": "buddycards:item/buddycard",
              "overrides": [
                {
                  "model": "%s:item/%s_g1",
                  "predicate": {
                    "buddycards:grade": 1.0
                  }
                },
                {
                  "model": "%s:item/%s_g2",
                  "predicate": {
                    "buddycards:grade": 2.0
                  }
                },
                {
                  "model": "%s:item/%s_g3",
                  "predicate": {
                    "buddycards:grade": 3.0
                  }
                },
                {
                  "model": "%s:item/%s_g4",
                  "predicate": {
                    "buddycards:grade": 4.0
                  }
                },
                {
                  "model": "%s:item/%s_g5",
                  "predicate": {
                    "buddycards:grade": 5.0
                  }
                }
              ],
              "textures": {
                "layer0": "%s:item/%s"
              }
            }
            """.formatted(
                namespace, path,
                namespace, path,
                namespace, path,
                namespace, path,
                namespace, path,
                namespace, texturePath
        );

        Files.writeString(model, baseJson, StandardCharsets.UTF_8);

        writeGradedCardModel(root, namespace, path, texturePath, 1);
        writeGradedCardModel(root, namespace, path, texturePath, 2);
        writeGradedCardModel(root, namespace, path, texturePath, 3);
        writeGradedCardModel(root, namespace, path, texturePath, 4);
        writeGradedCardModel(root, namespace, path, texturePath, 5);
    }
    private static void writeGradedCardModel(Path root, String namespace, String path, String texturePath, int grade) throws IOException {
        Path model = root.resolve("assets")
                .resolve(namespace)
                .resolve("models")
                .resolve("item")
                .resolve(path + "_g" + grade + ".json");

        Files.createDirectories(model.getParent());

        String json = """
            {
              "parent": "buddycards:item/buddycard",
              "textures": {
                "layer0": "%s:item/%s",
                "layer1": "buddycards:item/grade%d"
              }
            }
            """.formatted(namespace, texturePath, grade);

        Files.writeString(model, json, StandardCharsets.UTF_8);
    }

    private static void writeBoosterBoxAssets(Path root, BoosterBoxDefinition box) throws IOException {
        String namespace = box.namespace();
        String path = box.path();

        Path blockModel1 = root.resolve("assets").resolve(namespace).resolve("models").resolve("block").resolve(path + "_1.json");
        Path blockModel2 = root.resolve("assets").resolve(namespace).resolve("models").resolve("block").resolve(path + "_2.json");
        Path blockModel3 = root.resolve("assets").resolve(namespace).resolve("models").resolve("block").resolve(path + "_3.json");
        Path blockModel4 = root.resolve("assets").resolve(namespace).resolve("models").resolve("block").resolve(path + "_4.json");
        Path itemModel = root.resolve("assets").resolve(namespace).resolve("models").resolve("item").resolve(path + ".json");
        Path blockstate = root.resolve("assets").resolve(namespace).resolve("blockstates").resolve(path + ".json");

        Files.createDirectories(blockModel1.getParent());
        Files.createDirectories(itemModel.getParent());
        Files.createDirectories(blockstate.getParent());

        Files.writeString(blockModel1, boosterBoxModelJson(namespace, box, 1), StandardCharsets.UTF_8);
        Files.writeString(blockModel2, boosterBoxModelJson(namespace, box, 2), StandardCharsets.UTF_8);
        Files.writeString(blockModel3, boosterBoxModelJson(namespace, box, 3), StandardCharsets.UTF_8);
        Files.writeString(blockModel4, boosterBoxModelJson(namespace, box, 4), StandardCharsets.UTF_8);

        String itemModelJson = """
            {
              "parent": "%s:block/%s_1",
              "display": {
                "gui": {
                  "rotation": [30, 45, 0],
                  "translation": [0, 0, 0],
                  "scale": [0.625, 0.625, 0.625]
                },
                "ground": {
                  "rotation": [0, 0, 0],
                  "translation": [0, 3, 0],
                  "scale": [0.25, 0.25, 0.25]
                },
                "fixed": {
                  "rotation": [0, 180, 0],
                  "translation": [0, 0, 0],
                  "scale": [0.5, 0.5, 0.5]
                },
                "thirdperson_righthand": {
                  "rotation": [75, 45, 0],
                  "translation": [0, 2.5, 0],
                  "scale": [0.375, 0.375, 0.375]
                },
                "thirdperson_lefthand": {
                  "rotation": [75, 45, 0],
                  "translation": [0, 2.5, 0],
                  "scale": [0.375, 0.375, 0.375]
                },
                "firstperson_righthand": {
                  "rotation": [0, 45, 0],
                  "translation": [0, 0, 0],
                  "scale": [0.4, 0.4, 0.4]
                },
                "firstperson_lefthand": {
                  "rotation": [0, 225, 0],
                  "translation": [0, 0, 0],
                  "scale": [0.4, 0.4, 0.4]
                }
              }
            }
            """.formatted(namespace, path);

        String blockstateJson = """
            {
              "variants": {
                "facing=east,layers=1":  { "model": "%s:block/%s_1", "y": 90 },
                "facing=north,layers=1": { "model": "%s:block/%s_1" },
                "facing=south,layers=1": { "model": "%s:block/%s_1", "y": 180 },
                "facing=west,layers=1":  { "model": "%s:block/%s_1", "y": 270 },

                "facing=east,layers=2":  { "model": "%s:block/%s_2", "y": 90 },
                "facing=north,layers=2": { "model": "%s:block/%s_2" },
                "facing=south,layers=2": { "model": "%s:block/%s_2", "y": 180 },
                "facing=west,layers=2":  { "model": "%s:block/%s_2", "y": 270 },

                "facing=east,layers=3":  { "model": "%s:block/%s_3", "y": 90 },
                "facing=north,layers=3": { "model": "%s:block/%s_3" },
                "facing=south,layers=3": { "model": "%s:block/%s_3", "y": 180 },
                "facing=west,layers=3":  { "model": "%s:block/%s_3", "y": 270 },

                "facing=east,layers=4":  { "model": "%s:block/%s_4", "y": 90 },
                "facing=north,layers=4": { "model": "%s:block/%s_4" },
                "facing=south,layers=4": { "model": "%s:block/%s_4", "y": 180 },
                "facing=west,layers=4":  { "model": "%s:block/%s_4", "y": 270 }
              }
            }
            """.formatted(
                namespace, path, namespace, path, namespace, path, namespace, path,
                namespace, path, namespace, path, namespace, path, namespace, path,
                namespace, path, namespace, path, namespace, path, namespace, path,
                namespace, path, namespace, path, namespace, path, namespace, path
        );

        Files.writeString(itemModel, itemModelJson, StandardCharsets.UTF_8);
        Files.writeString(blockstate, blockstateJson, StandardCharsets.UTF_8);
    }

    private static String boosterBoxModelJson(String namespace, BoosterBoxDefinition box, int layers) {
        String path = box.path();

        return switch (layers) {
            case 1 -> """
                    {
                      "parent": "block/block",
                      "textures": {
                        "0": "%s:block/%s",
                        "1": "%s:block/%s",
                        "2": "%s:block/%s",
                        "particle": "%s:block/%s"
                      },
                      "elements": [
                        {
                          "from": [0, 0, 0],
                          "to": [16, 4, 16],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "east":  { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "west":  { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 16], "texture": "#2" },
                            "down":  { "uv": [0, 0, 16, 16], "texture": "#0" }
                          }
                        },
                        {
                          "from": [0, 4, 0],
                          "to": [16, 8, 1],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "east":  { "uv": [0, 0, 1, 4], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "west":  { "uv": [0, 0, 1, 4], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 1], "texture": "#1" },
                            "down":  { "uv": [0, 0, 16, 1], "texture": "#1" }
                          }
                        }
                      ]
                    }
                    """.formatted(
                    namespace, box.bottomTexturePath(),
                    namespace, box.sideTexturePath(),
                    namespace, box.topTexturePath(),
                    namespace, box.bottomTexturePath()
            );
            case 2 -> """
                    {
                      "parent": "block/block",
                      "textures": {
                        "0": "%s:block/%s",
                        "1": "%s:block/%s",
                        "2": "%s:block/%s",
                        "particle": "%s:block/%s"
                      },
                      "elements": [
                        {
                          "from": [0, 0, 0],
                          "to": [16, 8, 16],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 8], "texture": "#1" },
                            "east":  { "uv": [0, 0, 16, 8], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 8], "texture": "#1" },
                            "west":  { "uv": [0, 0, 16, 8], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 16], "texture": "#2" },
                            "down":  { "uv": [0, 0, 16, 16], "texture": "#0" }
                          }
                        },
                        {
                          "from": [0, 8, 0],
                          "to": [16, 12, 1],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "east":  { "uv": [0, 0, 1, 4], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "west":  { "uv": [0, 0, 1, 4], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 1], "texture": "#1" },
                            "down":  { "uv": [0, 0, 16, 1], "texture": "#1" }
                          }
                        }
                      ]
                    }
                    """.formatted(
                    namespace, box.bottomTexturePath(),
                    namespace, box.sideTexturePath(),
                    namespace, box.topTexturePath(),
                    namespace, box.bottomTexturePath()
            );
            case 3 -> """
                    {
                      "parent": "block/block",
                      "textures": {
                        "0": "%s:block/%s",
                        "1": "%s:block/%s",
                        "2": "%s:block/%s",
                        "particle": "%s:block/%s"
                      },
                      "elements": [
                        {
                          "from": [0, 0, 0],
                          "to": [16, 12, 16],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 12], "texture": "#1" },
                            "east":  { "uv": [0, 0, 16, 12], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 12], "texture": "#1" },
                            "west":  { "uv": [0, 0, 16, 12], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 16], "texture": "#2" },
                            "down":  { "uv": [0, 0, 16, 16], "texture": "#0" }
                          }
                        },
                        {
                          "from": [0, 12, 0],
                          "to": [16, 16, 1],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "east":  { "uv": [0, 0, 1, 4], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 4], "texture": "#1" },
                            "west":  { "uv": [0, 0, 1, 4], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 1], "texture": "#1" },
                            "down":  { "uv": [0, 0, 16, 1], "texture": "#1" }
                          }
                        }
                      ]
                    }
                    """.formatted(
                    namespace, box.bottomTexturePath(),
                    namespace, box.sideTexturePath(),
                    namespace, box.topTexturePath(),
                    namespace, box.bottomTexturePath()
            );
            default -> """
                    {
                      "parent": "block/block",
                      "textures": {
                        "0": "%s:block/%s",
                        "1": "%s:block/%s",
                        "2": "%s:block/%s",
                        "particle": "%s:block/%s"
                      },
                      "elements": [
                        {
                          "from": [0, 0, 0],
                          "to": [16, 16, 16],
                          "faces": {
                            "north": { "uv": [0, 0, 16, 16], "texture": "#1" },
                            "east":  { "uv": [0, 0, 16, 16], "texture": "#1" },
                            "south": { "uv": [0, 0, 16, 16], "texture": "#1" },
                            "west":  { "uv": [0, 0, 16, 16], "texture": "#1" },
                            "up":    { "uv": [0, 0, 16, 16], "texture": "#2" },
                            "down":  { "uv": [0, 0, 16, 16], "texture": "#0" }
                          }
                        }
                      ]
                    }
                    """.formatted(
                    namespace, box.bottomTexturePath(),
                    namespace, box.sideTexturePath(),
                    namespace, box.topTexturePath(),
                    namespace, box.bottomTexturePath()
            );
        };
    }

    private static void writeLang(Path root, String namespace, Map<String, String> entries) throws IOException {
        Path lang = root.resolve("assets")
                .resolve(namespace)
                .resolve("lang")
                .resolve("en_us.json");

        Files.createDirectories(lang.getParent());

        StringBuilder builder = new StringBuilder();
        builder.append("{\n");

        boolean first = true;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (!first) {
                builder.append(",\n");
            }
            first = false;

            builder.append("  \"")
                    .append(escapeJson(entry.getKey()))
                    .append("\": \"")
                    .append(escapeJson(entry.getValue() == null ? "" : entry.getValue()))
                    .append("\"");
        }

        builder.append("\n}\n");
        Files.writeString(lang, builder.toString(), StandardCharsets.UTF_8);
    }

    private static String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}