# BuddycardsAPI — Full Tutorial & Guide

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Forge](https://img.shields.io/badge/Forge-47.4.10-orange)
![Status](https://img.shields.io/badge/status-stable-success)

BuddycardsAPI is a **data-driven extension mod** for Buddycards that lets you create complete card sets using JSON + textures—no Java required.

This document is a **full, start-to-finish tutorial**. If you follow it exactly, you will successfully create a working custom card set.

---

# 🧭 Table of Contents

1. Setup
2. Folder Structure
3. Creating Your First Set
4. Adding Cards
5. Creating Packs
6. Creating Binders
7. Creating Booster Boxes
8. Creating Medals
9. Adding Textures
10. Running & Testing
11. Troubleshooting

---

# 1️⃣ Setup

## Required Mods

Install all of these:

- Minecraft **1.20.1**
- Forge **47.4.10**
- Buddycards
- KubeJS
- Curios
- BuddycardsAPI

Launch the game once to generate the `kubejs` folder.

---

# 2️⃣ Folder Structure

Everything happens inside the `kubejs` folder.

## Data (JSON files)

```
kubejs/data/<namespace>/buddyinfo/
```

Example namespace:

```
kubejs/data/test/buddyinfo/
```

## Assets (textures)

```
kubejs/assets/<namespace>/textures/
```

---

# 3️⃣ Create Your First Set

Create:

```
kubejs/data/test/buddyinfo/sets/test_set.json
```

```json
{
  "displayName": "Test Set"
}
```

That’s it. You now have a valid set.

---

# 4️⃣ Add Cards

Create:

```
kubejs/data/test/buddyinfo/cards/test_card.json
```

```json
{
  "displayName": "Test Card",
  "set": "test:test_set",
  "number": 1,
  "rarity": "common"
}
```

## Supported rarities

- common
- uncommon
- rare
- epic

---

# 5️⃣ Create Packs

```
kubejs/data/test/buddyinfo/packs/test_pack.json
```

```json
{
  "displayName": "Test Pack",
  "set": "test:test_set"
}
```

Packs automatically:

- pull cards from the set
- apply rarity weights
- support foils

---

# 6️⃣ Create Binders

```
kubejs/data/test/buddyinfo/binders/test_binder.json
```

```json
{
  "displayName": "Test Binder",
  "set": "test:test_set"
}
```

Binders automatically:

- open GUI
- store cards

---

# 7️⃣ Create Booster Boxes

```
kubejs/data/test/buddyinfo/booster_boxes/test_booster_box.json
```

```json
{
  "displayName": "Test Booster Box",
  "pack": "test:test_pack"
}
```

Automatically generates:

- 6 packs → box
- box → 6 packs

---

# 8️⃣ Create Medals

```
kubejs/data/test/buddyinfo/medals/test_medal.json
```

```json
{
  "displayName": "Test Medal",
  "set": "test:test_set",
  "effect": "Strength"
}
```

Medals:

- work with Curios
- apply effects

---

# 9️⃣ Add Textures

## Card

```
kubejs/assets/test/textures/item/test_set/test_card.png
```

## Pack

```
kubejs/assets/test/textures/item/test_set/test_pack.png
```

## Binder

```
kubejs/assets/test/textures/item/test_set/test_binder.png
```

GUI:

```
kubejs/assets/test/textures/gui/test_binder.png
```

## Booster Box

```
test_booster_box_bottom.png
test_booster_box_side.png
test_booster_box_top.png
```

---

# 🔟 Run the Game

Launch Minecraft.

BuddycardsAPI will automatically generate:

- models
- blockstates
- lang
- recipes

Your items will appear in:

- Buddycards Cards tab
- Buddycards Main tab

---

# 🧪 Testing

Test:

- opening packs
- placing booster boxes
- grading cards
- foil cards
- binder GUI

---

# ⚠️ Troubleshooting

## Nothing shows up

- check namespace spelling
- check file paths

## Missing textures

- verify texture folder path
- ensure PNG names match JSON

## Items not crafting

- ensure pack + booster box both exist

## Game crashes

- check logs for missing set or invalid ID

---

# ✅ Done!

You now have a fully working Buddycards content pack.

---

# 🚀 Next Ideas

- add more cards
- balance rarity weights
- create multiple sets
- add loot injections

---

This API is now fully usable for creating large-scale card mods.

