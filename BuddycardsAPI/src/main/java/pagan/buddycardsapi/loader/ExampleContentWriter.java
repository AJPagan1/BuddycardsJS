package pagan.buddycardsapi.loader;

import pagan.buddycardsapi.util.PathsUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExampleContentWriter {
    private ExampleContentWriter() {}

    public static void ensureExampleFiles() throws IOException {
        Path base = PathsUtil.dataRoot().resolve("example").resolve(PathsUtil.DATA_SUBFOLDER_NAME);
        Files.createDirectories(base.resolve("sets"));
        Files.createDirectories(base.resolve("cards"));
        Files.createDirectories(base.resolve("packs"));
        Files.createDirectories(base.resolve("binders"));
        Files.createDirectories(base.resolve("booster_boxes"));
        Files.createDirectories(base.resolve("medals"));
        Files.createDirectories(base.resolve("loot_injections"));
        Files.createDirectories(PathsUtil.assetsRoot().resolve("example/textures/item"));
        Files.createDirectories(PathsUtil.assetsRoot().resolve("example/textures/block"));
        Files.createDirectories(PathsUtil.assetsRoot().resolve("example/textures/gui"));

        writeIfMissing(base.resolve("sets/ember.json"), """
{
  "displayName": "Ember Set",
  "description": "A sample set created by Buddycards API Bridge.",
  "defaultCardsPerPack": 3,
  "defaultFoilsPerPack": 1
}
""");
        writeIfMissing(base.resolve("cards/ember_spirit.json"), """
{
  "displayName": "Ember Spirit",
  "set": "example:ember",
  "number": 1,
  "rarity": "rare"
}
""");
        writeIfMissing(base.resolve("packs/ember_pack.json"), """
{
  "displayName": "Ember Pack",
  "set": "example:ember",
  "cards": 3,
  "foils": 1,
  "rarityWeights": {
    "common": 70,
    "uncommon": 20,
    "rare": 10,
    "epic": 1
  },
  "entries": [
    { "card": "example:ember_spirit", "weight": 10 }
  ]
}
""");
        writeIfMissing(base.resolve("binders/ember_binder.json"), """
{
  "displayName": "Ember Binder",
  "set": "example:ember"
}
""");
        writeIfMissing(base.resolve("booster_boxes/ember_booster_box.json"), """
{
  "displayName": "Ember Booster Box",
  "pack": "example:ember_pack"
}
""");
        writeIfMissing(base.resolve("medals/ember_medal.json"), """
{
  "displayName": "Ember Medal",
  "set": "example:ember",
  "medalType": "buddysteel",
  "effect": "Strength",
  "level": 2,
  "duration": 220
}
""");
        writeIfMissing(base.resolve("loot_injections/ember_dungeon_pack.json"), """
{
  "targetLootTable": "minecraft:chests/simple_dungeon",
  "item": "example:ember_pack",
  "weight": 4,
  "count": 1
}
""");
    }

    private static void writeIfMissing(Path path, String text) throws IOException {
        if (!Files.exists(path)) Files.writeString(path, text, StandardCharsets.UTF_8);
    }
}
