package pagan.buddycardsapi.registry;

import com.wildcard.buddycards.core.BuddycardSet;
import com.wildcard.buddycards.item.BuddysteelSetMedalItem;
import com.wildcard.buddycards.item.BuddycardBinderItem;
import com.wildcard.buddycards.item.BuddycardBoosterBoxItem;
import com.wildcard.buddycards.item.BuddycardItem;
import com.wildcard.buddycards.item.BuddycardPackItem;
import com.wildcard.buddycards.item.LuminisSetMedalItem;
import com.wildcard.buddycards.item.ZylexSetMedalItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.compat.BuddycardsBridge;
import pagan.buddycardsapi.compat.DataDrivenBuddycardPackItem;
import pagan.buddycardsapi.content.BinderDefinition;
import pagan.buddycardsapi.content.BoosterBoxDefinition;
import pagan.buddycardsapi.content.CardDefinition;
import pagan.buddycardsapi.content.CardSetDefinition;
import pagan.buddycardsapi.content.MedalDefinition;
import pagan.buddycardsapi.content.PackDefinition;
import pagan.buddycardsapi.content.PackEntryDefinition;
import pagan.buddycardsapi.loader.ExternalContentBootstrap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class BcaItems {
    private static final Map<String, DeferredRegister<Item>> ITEM_REGISTERS = new LinkedHashMap<>();

    public static final Map<String, BuddycardSet> SETS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<BuddycardItem>> CARDS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<BuddycardPackItem>> PACKS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<BuddycardBinderItem>> BINDERS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Item>> BOOSTER_BOX_ITEMS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<BuddysteelSetMedalItem>> BUDDYSTEEL_MEDALS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<LuminisSetMedalItem>> LUMINIS_MEDALS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<ZylexSetMedalItem>> ZYLEX_MEDALS = new LinkedHashMap<>();

    private static boolean initialized = false;

    private BcaItems() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerSets();
        registerCards();
        registerPacks();
        registerBinders();
        registerBoosterBoxes();
        registerMedals();
    }

    public static void registerAll(IEventBus modBus) {
        for (DeferredRegister<Item> register : ITEM_REGISTERS.values()) {
            register.register(modBus);
        }
    }

    private static DeferredRegister<Item> itemsForNamespace(String namespace) {
        return ITEM_REGISTERS.computeIfAbsent(
                namespace,
                ns -> DeferredRegister.create(ForgeRegistries.ITEMS, ns)
        );
    }

    private static void registerSets() {
        for (CardSetDefinition definition : ExternalContentBootstrap.REPOSITORY.sets().values()) {
            SETS.put(definition.id(), BuddycardsBridge.createSet(definition.buddycardsSetName()));
        }
    }

    private static void registerCards() {
        for (CardDefinition definition : ExternalContentBootstrap.REPOSITORY.cards().values()) {
            BuddycardSet set = SETS.get(definition.set());
            if (set == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping card {} because set {} is missing", definition.id(), definition.set());
                continue;
            }

            DeferredRegister<Item> register = itemsForNamespace(definition.namespace());

            CARDS.put(
                    definition.id(),
                    register.register(
                            definition.path(),
                            () -> BuddycardsBridge.createCard(
                                    set,
                                    definition.number() == null ? 1 : definition.number(),
                                    definition.rarity(),
                                    new Item.Properties().stacksTo(64),
                                    definition.cost() == null ? 0 : definition.cost(),
                                    definition.power() == null ? 0 : definition.power()
                            )
                    )
            );
        }
    }

    private static void registerPacks() {
        for (PackDefinition definition : ExternalContentBootstrap.REPOSITORY.packs().values()) {
            final BuddycardSet set = definition.set() != null && !definition.set().isBlank()
                    ? SETS.get(definition.set())
                    : null;

            if (set == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping pack {} because set {} is missing", definition.id(), definition.set());
                continue;
            }

            DeferredRegister<Item> register = itemsForNamespace(definition.namespace());

            PACKS.put(
                    definition.id(),
                    register.register(
                            definition.path(),
                            () -> new DataDrivenBuddycardPackItem(
                                    set,
                                    definition.cards() != null ? definition.cards() : defaultCardsForSet(definition.set()),
                                    definition.foils() != null ? definition.foils() : defaultFoilsForSet(definition.set()),
                                    BuddycardsBridge.buildWeights(definition.rarityWeights()),
                                    new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON),
                                    buildPackTable(definition)
                            )
                    )
            );
        }
    }

    private static void registerBinders() {
        for (BinderDefinition definition : ExternalContentBootstrap.REPOSITORY.binders().values()) {
            BuddycardSet set = SETS.get(definition.set());
            if (set == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping binder {} because set {} is missing", definition.id(), definition.set());
                continue;
            }

            DeferredRegister<Item> register = itemsForNamespace(definition.namespace());

            BINDERS.put(
                    definition.id(),
                    register.register(
                            definition.path(),
                            () -> BuddycardsBridge.createBinder(
                                    set,
                                    definition.namespace(),
                                    definition.guiTexturePath(),
                                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                            )
                    )
            );
        }
    }

    private static void registerBoosterBoxes() {
        for (BoosterBoxDefinition definition : ExternalContentBootstrap.REPOSITORY.boosterBoxes().values()) {
            String resolvedPackId = resolveBoosterBoxPackId(definition);
            if (resolvedPackId == null || resolvedPackId.isBlank()) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping booster box {} because no pack could be resolved", definition.id());
                continue;
            }

            RegistryObject<BuddycardPackItem> packObject = PACKS.get(resolvedPackId);
            if (packObject == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping booster box {} because pack {} is missing", definition.id(), resolvedPackId);
                continue;
            }

            var blockObject = BcaBlocks.BOOSTER_BOX_BLOCKS.get(definition.id());
            if (blockObject == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping booster box {} because block {} is missing", definition.id(), definition.id());
                continue;
            }

            DeferredRegister<Item> register = itemsForNamespace(definition.namespace());

            BOOSTER_BOX_ITEMS.put(
                    definition.id(),
                    register.register(
                            definition.path(),
                            () -> new BuddycardBoosterBoxItem(
                                    blockObject.get(),
                                    packObject::get,
                                    new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)
                            )
                    )
            );
        }
    }

    private static void registerMedals() {
        for (MedalDefinition definition : ExternalContentBootstrap.REPOSITORY.medals().values()) {
            BuddycardSet set = SETS.get(definition.set());
            if (set == null) {
                BuddycardsApiBridgeMod.LOGGER.warn("Skipping medal {} because set {} is missing", definition.id(), definition.set());
                continue;
            }

            DeferredRegister<Item> register = itemsForNamespace(definition.namespace());

            int level = definition.level() == null ? 1 : definition.level();
            int duration = definition.duration() == null ? 220 : definition.duration();

            switch (definition.medalType().toLowerCase(Locale.ROOT)) {
                case "luminis" -> {
                    var obj = register.register(
                            definition.path(),
                            () -> BuddycardsBridge.createLuminisMedal(
                                    set,
                                    definition.effect(),
                                    level,
                                    duration,
                                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                            )
                    );
                    LUMINIS_MEDALS.put(definition.id(), obj);
                    set.setLuminisMedal(obj::get);
                }
                case "zylex" -> {
                    var obj = register.register(
                            definition.path(),
                            () -> BuddycardsBridge.createZylexMedal(
                                    set,
                                    definition.effect(),
                                    level,
                                    duration,
                                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                            )
                    );
                    ZYLEX_MEDALS.put(definition.id(), obj);
                    set.setZylexMedal(obj::get);
                }
                default -> {
                    var obj = register.register(
                            definition.path(),
                            () -> BuddycardsBridge.createBuddysteelMedal(
                                    set,
                                    definition.effect(),
                                    level,
                                    duration,
                                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
                            )
                    );
                    BUDDYSTEEL_MEDALS.put(definition.id(), obj);
                    set.setMedal(obj::get);
                }
            }
        }
    }

    private static String resolveBoosterBoxPackId(BoosterBoxDefinition definition) {
        if (definition.pack() != null && !definition.pack().isBlank()) {
            return definition.pack();
        }

        if (definition.set() != null && !definition.set().isBlank()) {
            for (PackDefinition candidate : ExternalContentBootstrap.REPOSITORY.packs().values()) {
                if (definition.set().equals(candidate.set())) {
                    return candidate.id();
                }
            }
        }

        return null;
    }

    private static Map<Rarity, List<Supplier<BuddycardItem>>> buildPackTable(PackDefinition definition) {
        Map<Rarity, List<Supplier<BuddycardItem>>> table = new EnumMap<>(Rarity.class);
        for (Rarity rarity : List.of(Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC)) {
            table.put(rarity, new ArrayList<>());
        }

        if (definition.entries() != null && !definition.entries().isEmpty()) {
            for (PackEntryDefinition entry : definition.entries()) {
                RegistryObject<BuddycardItem> obj = CARDS.get(entry.card());
                if (obj == null) {
                    continue;
                }

                int weight = Math.max(1, entry.weight() == null ? 1 : entry.weight());
                for (int i = 0; i < weight; i++) {
                    table.get(obj.get().getRarity()).add(obj::get);
                }
            }
            return table;
        }

        if (definition.set() != null && !definition.set().isBlank()) {
            for (Map.Entry<String, RegistryObject<BuddycardItem>> entry : CARDS.entrySet()) {
                CardDefinition cardDefinition = ExternalContentBootstrap.REPOSITORY.cards().get(entry.getKey());
                if (cardDefinition != null && definition.set().equals(cardDefinition.set())) {
                    table.get(BuddycardsBridge.rarity(cardDefinition.rarity())).add(entry.getValue()::get);
                }
            }
        }

        return table;
    }

    private static int defaultCardsForSet(String setId) {
        CardSetDefinition set = ExternalContentBootstrap.REPOSITORY.sets().get(setId);
        return set != null && set.defaultCardsPerPack() != null ? set.defaultCardsPerPack() : 3;
    }

    private static int defaultFoilsForSet(String setId) {
        CardSetDefinition set = ExternalContentBootstrap.REPOSITORY.sets().get(setId);
        return set != null && set.defaultFoilsPerPack() != null ? set.defaultFoilsPerPack() : 1;
    }
}