# BuddycardsJS

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Forge](https://img.shields.io/badge/Forge-47.4.10-orange)
![Status](https://img.shields.io/badge/status-stable-success)
![Data Driven](https://img.shields.io/badge/system-data--driven-blue)

BuddycardsJS is a data-driven KubeJS addon for Buddycards that lets you create Buddycards-style content with JSON and textures.

This README covers the current feature set, including:

- sets
- cards
- packs
- binders
- booster boxes
- medals
- loot injections
- generated recipes, models, blockstates, tags, and language files
- default texture naming rules
- custom texture path overrides

---

## Requirements

Install all of these:

- Minecraft **1.20.1**
- Forge **47.4.10**
- Buddycards
- KubeJS
- Curios
- BuddycardsJS

---

## What BuddycardsJS Does

BuddycardsJS reads JSON files from your `kubejs` folder and generates the files needed to make your content work in-game.

It can automatically generate:

- item models
- block models
- blockstates
- `en_us.json`
- crafting recipes
- card tags
- Curios medal tags
- loot modifier files
- injected loot tables

---

## Folder Structure

### Data

Put JSON files here:

```text
kubejs/data/<namespace>/buddyinfo/
```

Supported folders:

```text
sets/
cards/
packs/
binders/
booster_boxes/
medals/
loot_injections/
```

### Assets

Put textures here:

```text
kubejs/assets/<namespace>/textures/
```

Supported folders:

```text
item/<set_path>/
block/<set_path>/
gui/
```

---

## Namespace Rules

Your namespace comes from the folder under `kubejs/data`.

Example:

```text
kubejs/data/example/buddyinfo/
```

This means your ids will be things like:

- `example:example_set`
- `example:buddycard_example`
- `example:buddycard_pack_example`
- `example:buddycard_binder_example`
- `example:buddycard_booster_box_example`
- `example:buddycard_medal_example`

Always use the correct namespace in your JSON references.

---

## Important Texture Naming Rule

Default texture names use the **set id path**, not the display name.

Example set JSON:

```json
{
  "displayName": "Example Set"
}
```

If the set id is:

```json
"set": "example:example_set"
```

then the default texture names are based on:

```text
example_set
```

not:

```text
Example Set
```

So spaces and capitalization in `displayName` do not affect default texture file names.

---

## Sets

Path:

```text
kubejs/data/<namespace>/buddyinfo/sets/<set_name>.json
```

### Full layout

```json
{
  "displayName": "Example Set",
  "description": "An example BuddycardsJS set.",
  "defaultCardsPerPack": 3,
  "defaultFoilsPerPack": 1
}
```

### Fields

- `displayName` — optional
- `description` — optional
- `defaultCardsPerPack` — optional
- `defaultFoilsPerPack` — optional

### Defaults

- `displayName` defaults from file name
- `defaultCardsPerPack` defaults to `3`
- `defaultFoilsPerPack` defaults to `1`

---

## Cards

Path:

```text
kubejs/data/<namespace>/buddyinfo/cards/<card_name>.json
```

### Full layout

```json
{
  "displayName": "Buddycard Example",
  "set": "example:example_set",
  "number": 1,
  "rarity": "common",
  "cost": 0,
  "power": 0,
  "texturePath": "example_set/custom_card_art",
  "shinyByDefault": false,
  "tooltip": "This is example flavor text."
}
```

### Fields

- `displayName` — optional
- `set` — required
- `number` — optional
- `rarity` — optional
- `cost` — optional
- `power` — optional
- `texturePath` — optional
- `shinyByDefault` — optional
- `tooltip` — optional

### Rarity options

- `common`
- `uncommon`
- `rare`
- `epic`

### Defaults

- `displayName` defaults from file name
- `number` defaults to `1`
- `rarity` defaults to `common`
- `cost` defaults to `0`
- `power` defaults to `0`
- `shinyByDefault` defaults to `false`

### Default card texture name

If `texturePath` is not provided, BuddycardsJS looks for:

```text
textures/item/<set_path>/<card_number>.png
```

So for:

```json
{
  "set": "example:example_set",
  "number": 1
}
```

the default texture file is:

```text
kubejs/assets/example/textures/item/example_set/1.png
```

### Custom card texture path

If you want a custom file name, use:

```json
"texturePath": "example_set/custom_card_art"
```

which points to:

```text
kubejs/assets/example/textures/item/example_set/custom_card_art.png
```

### Card tooltip text

If you provide:

```json
"tooltip": "This is example flavor text."
```

BuddycardsJS generates the language key Buddycards already expects:

```text
item.<namespace>.<card_name>.tooltip
```

So this fills the existing Buddycards tooltip translation key instead of creating a separate tooltip system.

---

## Packs

Path:

```text
kubejs/data/<namespace>/buddyinfo/packs/<pack_name>.json
```

### Full layout

```json
{
  "displayName": "Buddycard Pack Example",
  "set": "example:example_set",
  "cards": 3,
  "foils": 1,
  "texturePath": "example_set/custom_pack",
  "rarityWeights": {
    "common": 70,
    "uncommon": 20,
    "rare": 8,
    "epic": 2
  },
  "entries": [
    {
      "card": "example:buddycard_example",
      "weight": 1
    }
  ]
}
```

### Fields

- `displayName` — optional
- `set` — recommended
- `cards` — optional
- `foils` — optional
- `texturePath` — optional
- `rarityWeights` — optional
- `entries` — optional

### Defaults

- `displayName` defaults from file name
- `cards` defaults to the set value, otherwise `3`
- `foils` defaults to the set value, otherwise `1`
- `rarityWeights` defaults to:
  - common `70`
  - uncommon `20`
  - rare `8`
  - epic `2`

### Default pack texture name

If `texturePath` is not provided, BuddycardsJS looks for:

```text
textures/item/<set_path>/pack.png
```

Example:

```text
kubejs/assets/example/textures/item/example_set/pack.png
```

### Custom pack texture path

Example:

```json
"texturePath": "example_set/custom_pack"
```

which points to:

```text
kubejs/assets/example/textures/item/example_set/custom_pack.png
```

### Notes

- Packs use Buddycards-style set tooltip behavior
- Pack name color matches Buddycards
- Packs stack like Buddycards
- Packs can be used in booster box recipes and loot injections

---

## Binders

Path:

```text
kubejs/data/<namespace>/buddyinfo/binders/<binder_name>.json
```

### Full layout

```json
{
  "displayName": "Buddycard Binder Example",
  "set": "example:example_set",
  "rows": 6,
  "large": false,
  "itemTexturePath": "example_set/custom_binder",
  "guiTexturePath": "custom_binder_gui"
}
```

### Large binder example

```json
{
  "displayName": "Buddycard Large Binder Example",
  "set": "example:example_set",
  "large": true
}
```

### Fields

- `displayName` — optional
- `set` — required
- `rows` — optional
- `large` — optional
- `itemTexturePath` — optional
- `guiTexturePath` — optional

### Defaults

- `displayName` defaults from file name
- `rows` defaults to `6`
- `large` defaults to `false`

### Binder types

BuddycardsJS supports both normal and large binders.

- normal binder: `"large": false`
- large binder: `"large": true`

### Default binder item texture names

If `itemTexturePath` is not provided:

Normal binder:

```text
textures/item/<set_path>/binder.png
```

Large binder:

```text
textures/item/<set_path>/large_binder.png
```

Examples:

```text
kubejs/assets/example/textures/item/example_set/binder.png
kubejs/assets/example/textures/item/example_set/large_binder.png
```

### Default binder GUI texture names

If `guiTexturePath` is not provided:

Normal binder:

```text
textures/gui/<set_name>_binder.png
```

Large binder:

```text
textures/gui/<set_name>_large_binder.png
```

Examples:

```text
kubejs/assets/example/textures/gui/example_set_binder.png
kubejs/assets/example/textures/gui/example_set_large_binder.png
```

### Custom binder texture paths

Item texture:

```json
"itemTexturePath": "example_set/custom_binder"
```

GUI texture:

```json
"guiTexturePath": "custom_binder_gui"
```

That points to:

```text
kubejs/assets/example/textures/item/example_set/custom_binder.png
kubejs/assets/example/textures/gui/custom_binder_gui.png
```

### Binder recipe

Binders are generated with a recipe that uses:

- `1` leather
- `3` cards from the same set

BuddycardsJS automatically generates the needed set tag.

---

## Booster Boxes

Path:

```text
kubejs/data/<namespace>/buddyinfo/booster_boxes/<booster_box_name>.json
```

### Full layout

```json
{
  "displayName": "Buddycard Booster Box Example",
  "set": "example:example_set",
  "pack": "example:buddycard_pack_example",
  "topTexturePath": "example_set/custom_top",
  "sideTexturePath": "example_set/custom_side",
  "bottomTexturePath": "example_set/custom_bottom"
}
```

### Fields

- `displayName` — optional
- `set` — recommended
- `pack` — strongly recommended
- `topTexturePath` — optional
- `sideTexturePath` — optional
- `bottomTexturePath` — optional

### Notes

- `pack` must match a real pack id exactly
- if the pack id is wrong, the booster box will not resolve correctly
- booster boxes render like Buddycards, with the display transforms applied in the `_1` block model instead of the item model

### Default booster box texture names

If custom block texture paths are not provided, BuddycardsJS looks for:

```text
textures/block/<set_path>/<set_name>_top.png
textures/block/<set_path>/<set_name>_side.png
textures/block/<set_path>/<set_name>_bottom.png
```

Example:

```text
kubejs/assets/example/textures/block/example_set/example_set_top.png
kubejs/assets/example/textures/block/example_set/example_set_side.png
kubejs/assets/example/textures/block/example_set/example_set_bottom.png
```

### Custom booster box texture paths

Example:

```json
{
  "topTexturePath": "example_set/custom_top",
  "sideTexturePath": "example_set/custom_side",
  "bottomTexturePath": "example_set/custom_bottom"
}
```

which points to:

```text
kubejs/assets/example/textures/block/example_set/custom_top.png
kubejs/assets/example/textures/block/example_set/custom_side.png
kubejs/assets/example/textures/block/example_set/custom_bottom.png
```

### Booster box recipes

BuddycardsJS automatically generates:

- `6 packs -> 1 booster box`
- `1 booster box -> 6 packs`

---

## Medals

Path:

```text
kubejs/data/<namespace>/buddyinfo/medals/<medal_name>.json
```

### Full layout

```json
{
  "displayName": "Buddycard Medal Example",
  "set": "example:example_set",
  "medalType": "buddysteel",
  "effect": "minecraft:strength",
  "level": 1,
  "duration": 220,
  "itemTexturePath": "example_set/custom_medal"
}
```

### Fields

- `displayName` — optional
- `set` — required
- `medalType` — optional
- `effect` — required
- `level` — optional
- `duration` — optional
- `itemTexturePath` — optional

### Medal type options

- `buddysteel`
- `luminis`
- `zylex`

### Defaults

- `displayName` defaults from file name
- `medalType` defaults to `buddysteel`
- `level` defaults to `1`
- `duration` defaults to `220`

### Default medal texture name

If `itemTexturePath` is not provided, BuddycardsJS looks for:

```text
textures/item/<set_path>/<medal_type>_medal.png
```

Examples:

```text
kubejs/assets/example/textures/item/example_set/buddysteel_medal.png
kubejs/assets/example/textures/item/example_set/luminis_medal.png
kubejs/assets/example/textures/item/example_set/zylex_medal.png
```

### Custom medal texture path

Example:

```json
"itemTexturePath": "example_set/custom_medal"
```

which points to:

```text
kubejs/assets/example/textures/item/example_set/custom_medal.png
```

### Notes

- Medals are added to the Curios medal tag automatically
- BuddycardsJS removes the effect line while preserving the normal set line and advanced tooltip behavior

---

## Loot Injections

BuddycardsJS uses a simple user-facing loot format and generates the more complex Buddycards-style loot modifier files in the background.

Path:

```text
kubejs/data/<namespace>/buddyinfo/loot_injections/<file_name>.json
```

### Full layout

```json
{
  "targetLootTable": "simple_dungeon",
  "pack": "example:buddycard_pack_example",
  "weight": 1,
  "rolls": 2,
  "count": {
    "min": 1,
    "max": 2
  },
  "empty": 3
}
```

### Fields

- `targetLootTable` — required
- `pack` — required
- `weight` — optional
- `rolls` — optional
- `count.min` — optional
- `count.max` — optional
- `empty` — optional

### Defaults

- `weight` defaults to `1`
- `rolls` defaults to `1`
- `count.min` defaults to `1`
- `count.max` defaults to `2`
- `empty` defaults to `3`

### Empty weight option

BuddycardsJS supports an `empty` field in loot injections.

This is the **empty weight** for the generated loot pool. It controls how often a roll gives nothing instead of your pack.

Example:

```json
{
  "targetLootTable": "simple_dungeon",
  "pack": "example:buddycard_pack_example",
  "weight": 1,
  "rolls": 2,
  "count": {
    "min": 1,
    "max": 2
  },
  "empty": 3
}
```

### What `empty` means

`empty` is the weighted chance that the roll gives nothing.

So this:

```json
"weight": 1,
"empty": 3
```

means each roll is weighted like:

- 1 part = your pack
- 3 parts = nothing

This makes loot injections feel much closer to Buddycards, since the pack is not guaranteed on every roll.

### Short alias support

If `targetLootTable` does not contain a namespace, BuddycardsJS assumes it is a chest loot table and expands it to:

```text
minecraft:chests/<value>
```

So:

```json
"targetLootTable": "simple_dungeon"
```

becomes:

```json
"targetLootTable": "minecraft:chests/simple_dungeon"
```

### Full id support

If `targetLootTable` already contains a namespace, BuddycardsJS uses it exactly as written.

So these are valid:

```json
"targetLootTable": "minecraft:chests/end_city_treasure"
```

```json
"targetLootTable": "minecraft:gameplay/fishing/treasure"
```

```json
"targetLootTable": "minecraft:archaeology/desert_pyramid"
```

### Recommended use

Use short aliases for:
- structure chest loot

Use full ids for:
- fishing
- archaeology
- entity loot
- anything outside `minecraft:chests/`

### What BuddycardsJS generates from loot injections

From one simple JSON file, BuddycardsJS automatically generates:

- `data/<namespace>/loot_modifiers/<file>.json`
- `data/<namespace>/loot_tables/inject/<file>.json`
- `data/forge/loot_modifiers/global_loot_modifiers.json`

---

## Automatic Generation Summary

BuddycardsJS can generate:

### Assets

```text
kubejs/assets/<namespace>/models/item/
kubejs/assets/<namespace>/models/block/
kubejs/assets/<namespace>/blockstates/
kubejs/assets/<namespace>/lang/en_us.json
```

### Data

```text
kubejs/data/<namespace>/recipes/
kubejs/data/buddycards/tags/items/
kubejs/data/curios/tags/items/medal.json
kubejs/data/<namespace>/loot_modifiers/
kubejs/data/<namespace>/loot_tables/inject/
kubejs/data/forge/loot_modifiers/global_loot_modifiers.json
```

---

## Creative Tabs

BuddycardsJS content is placed into Buddycards’ creative tabs automatically:

- cards go into the Buddycards cards tab
- packs, binders, booster boxes, and medals go into the Buddycards main tab

---

## Crafting Summary

### Buddysteel Blend

BuddycardsJS cards are automatically added to the Buddycards card tag, so they work in the original Buddycards Buddysteel Blend recipe.

### Binder

Generated automatically:
- `1` leather
- `3` cards from the same set

### Booster Box

Generated automatically:
- `6` packs -> `1` booster box
- `1` booster box -> `6` packs

---

## Important Notes

### Buddycards 4.3.0 foil compatibility

BuddycardsJS now generates Buddycards 4.3.0-compatible card item models with support for:

- foil-only variants
- grade-only variants
- grade + foil variants

This means foil cards and graded foil cards render correctly with the updated Buddycards foil system.

### Card stacking

Cards stack if they are truly identical.

They may not stack if they differ by:
- foil state
- grading
- NBT

### Generated files

Do not manually edit generated files unless you know exactly what you are doing. They can be regenerated and overwritten.

### Fresh worlds after big registry changes

If you rename namespaces, mod ids, or item ids, use a fresh test world.

---

## Testing Workflow

When testing texture path or generated asset changes, keep only:

- source JSON
- source textures

and delete generated files such as:

```text
models/
blockstates/
lang/
recipes/
tags/
loot_modifiers/
loot_tables/inject/
```

Then relaunch the game and let BuddycardsJS regenerate them.

For texture-path testing specifically, the most important generated files to delete are:

```text
kubejs/assets/<namespace>/models/item/
kubejs/assets/<namespace>/models/block/
kubejs/assets/<namespace>/blockstates/
kubejs/assets/<namespace>/lang/en_us.json
```

Do not delete your source files under:

```text
kubejs/data/<namespace>/buddyinfo/
kubejs/assets/<namespace>/textures/
```

---

## Troubleshooting

### `.\gradlew.bat` is not recognized

You are probably not in the project folder. Make sure you are in the folder that contains:

- `build.gradle`
- `settings.gradle`
- `gradlew.bat`

### Items not showing up

Check:
- namespace spelling
- file path spelling
- JSON validity

### Booster box not generating

Most common reasons:
- wrong folder path
- wrong `pack` id
- missing set/pack references

### Booster box icon or hand render looks wrong

BuddycardsJS booster boxes should match Buddycards by:

- using a simple item model that parents to the `_1` block model
- storing the display transforms in the `_1` block model
- leaving `_2`, `_3`, and `_4` as plain block models

If the render still looks wrong, delete the generated booster box item/block model files and let them regenerate.

### Too much loot in structures

Lower:
- `rolls`
- `count.max`

and increase:
- `empty`

To get closer to Buddycards feel, use something like:

```json
{
  "targetLootTable": "simple_dungeon",
  "pack": "example:buddycard_pack_example",
  "weight": 1,
  "rolls": 2,
  "count": {
    "min": 1,
    "max": 2
  },
  "empty": 3
}
```

### Seeing `item.<namespace>.<card>.tooltip` in-game

That means the generated language file is missing the tooltip key. Make sure the card JSON has a `tooltip` string and that the generated `en_us.json` was regenerated.

---

## Example Minimal Set

### `sets/example_set.json`

```json
{
  "displayName": "Example Set"
}
```

### `cards/buddycard_example.json`

```json
{
  "displayName": "Buddycard Example",
  "set": "example:example_set",
  "number": 1,
  "rarity": "common",
  "tooltip": "This is example flavor text."
}
```

Default texture file:

```text
kubejs/assets/example/textures/item/example_set/1.png
```

### `packs/buddycard_pack_example.json`

```json
{
  "displayName": "Buddycard Pack Example",
  "set": "example:example_set"
}
```

Default texture file:

```text
kubejs/assets/example/textures/item/example_set/pack.png
```

### `binders/buddycard_binder_example.json`

```json
{
  "displayName": "Buddycard Binder Example",
  "set": "example:example_set",
  "rows": 6
}
```

Default texture files:

```text
kubejs/assets/example/textures/item/example_set/binder.png
kubejs/assets/example/textures/gui/example_set_binder.png
```

### `binders/buddycard_large_binder_example.json`

```json
{
  "displayName": "Buddycard Large Binder Example",
  "set": "example:example_set",
  "rows": 6,
  "large": true
}
```

Default texture files:

```text
kubejs/assets/example/textures/item/example_set/large_binder.png
kubejs/assets/example/textures/gui/example_set_large_binder.png
```

### `booster_boxes/buddycard_booster_box_example.json`

```json
{
  "displayName": "Buddycard Booster Box Example",
  "set": "example:example_set",
  "pack": "example:buddycard_pack_example"
}
```

Default texture files:

```text
kubejs/assets/example/textures/block/example_set/example_set_top.png
kubejs/assets/example/textures/block/example_set/example_set_side.png
kubejs/assets/example/textures/block/example_set/example_set_bottom.png
```

### `medals/buddycard_medal_example.json`

```json
{
  "displayName": "Buddycard Medal Example",
  "set": "example:example_set",
  "effect": "minecraft:strength"
}
```

Default texture file:

```text
kubejs/assets/example/textures/item/example_set/buddysteel_medal.png
```

### `loot_injections/simple_dungeon_example.json`

```json
{
  "targetLootTable": "simple_dungeon",
  "pack": "example:buddycard_pack_example",
  "weight": 1,
  "rolls": 2,
  "count": {
    "min": 1,
    "max": 2
  },
  "empty": 3
}
```

---

## License / Credits

BuddycardsJS extends Buddycards and depends on Buddycards being present.

Respect the original Buddycards license and asset usage rules when distributing content packs, example packs, or derivative work.
