# BuddycardsAPI — Complete Guide

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Forge](https://img.shields.io/badge/Forge-47.4.10-orange)
![Status](https://img.shields.io/badge/status-stable-success)

BuddycardsAPI is a **fully data-driven extension mod for Buddycards** that allows you to create complete card systems using only JSON files and textures.

This README is a **complete, no-compromises guide**. Nothing is omitted.

---

# 📦 Requirements

Install all:

- Minecraft 1.20.1
- Forge 47.4.10
- Buddycards
- KubeJS
- Curios
- BuddycardsAPI

---

# 🚀 Initial Setup

1. Install mods
2. Launch game once
3. Open `.minecraft/kubejs/`

---

# 📁 Folder Structure

## Data

kubejs/data/<namespace>/buddyinfo/

Folders:
- sets/
- cards/
- packs/
- binders/
- booster_boxes/
- medals/
- loot_injections/

## Assets

kubejs/assets/<namespace>/textures/

Folders:
- item/<set>/
- block/<set>/
- gui/

---

# 🧠 System Overview

The API reads JSON and automatically generates:

- models
- blockstates
- lang files
- recipes
- tags
- Curios integration

---

# 🧾 FULL CREATION PROCESS

---

## 1. Create Set

```json
{
  "displayName": "Test Set"
}
```

---

## 2. Create Cards

```json
{
  "displayName": "Test Card",
  "set": "test:test_set",
  "number": 1,
  "rarity": "common"
}
```

### Rarities
- common
- uncommon
- rare
- epic

---

## 3. Create Pack

```json
{
  "displayName": "Test Pack",
  "set": "test:test_set"
}
```

---

## 4. Create Binder

```json
{
  "displayName": "Test Binder",
  "set": "test:test_set"
}
```

Crafting:
- 1 leather
- 3 cards from same set

---

## 5. Create Booster Box

```json
{
  "displayName": "Test Booster Box",
  "pack": "test:test_pack"
}
```

Crafting:
- 6 packs → box
- 1 box → 6 packs

---

## 6. Create Medal

```json
{
  "displayName": "Test Medal",
  "set": "test:test_set",
  "effect": "minecraft:strength"
}
```

---

# 🎨 TEXTURES

## Items
textures/item/<set>/<item>.png

## GUI
textures/gui/<binder>.png

## Booster Box
_bottom.png  
_side.png  
_top.png  

---

# ⚙️ AUTO FEATURES

- Foils
- Grading overlays
- Rarity colors
- Recipes
- Tags
- Creative tabs

---

# 🧱 CRAFTING SYSTEM

## Buddysteel Blend
- 1 iron block
- 1 lapis block
- 3 cards

## Binder
- 1 leather
- 3 cards (same set)

## Booster Box
- 6 packs

---

# 🧪 TESTING

Check:
- creative tabs
- crafting
- pack opening
- grading
- binder UI

---

# ⚠️ TROUBLESHOOTING

Items missing:
- namespace wrong

Textures broken:
- wrong path

Recipes broken:
- tags missing

Crash:
- bad JSON

---

# 🧪 BEST PRACTICE

Delete generated:
- models
- blockstates
- lang
- recipes
- tags

Then relaunch.

---

# 📦 EXAMPLE PACK

Included separately.

---

# ✅ FEATURES COMPLETE

- cards
- packs
- binders
- booster boxes
- medals
- recipes
- tags
- grading
- foils
- creative tabs

---

# ❌ NOT INCLUDED

- battle system
- abilities

---

# 📜 LICENSE

Respects Buddycards license.
