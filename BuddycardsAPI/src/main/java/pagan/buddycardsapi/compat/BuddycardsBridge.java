package pagan.buddycardsapi.compat;

import pagan.buddycardsapi.medal.ConfiguredMedalType;
import com.google.common.collect.ImmutableListMultimap;
import com.wildcard.buddycards.block.BuddycardBoosterBoxBlock;
import com.wildcard.buddycards.core.BuddycardSet;
import com.wildcard.buddycards.item.BuddysteelSetMedalItem;
import com.wildcard.buddycards.item.BuddycardBinderItem;
import com.wildcard.buddycards.item.BuddycardItem;
import com.wildcard.buddycards.item.LuminisSetMedalItem;
import com.wildcard.buddycards.item.ZylexSetMedalItem;
import com.wildcard.buddycards.registries.BuddycardsBlocks;
import com.wildcard.buddycards.registries.BuddycardsItems;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

public final class BuddycardsBridge {
    private BuddycardsBridge() {}
    public static final BuddycardsItems.BuddycardRequirement ALWAYS_TRUE = () -> true;
    public static BuddycardSet createSet(String buddycardsSetName) { return new BuddycardSet(buddycardsSetName); }
    public static BuddycardItem createCard(BuddycardSet set, int number, String rarityName, Item.Properties properties, int cost, int power) {
        BuddycardItem item = new BuddycardItem(ALWAYS_TRUE, set, number, rarity(rarityName), properties, cost, power, ImmutableListMultimap.of());
        set.addCard(item);
        return item;
    }
    public static BuddycardBinderItem createBinder(BuddycardSet set, String namespace, String guiTexturePath, Item.Properties properties) {
        return new BuddycardBinderItem(properties, set, new ResourceLocation(namespace, "textures/gui/" + guiTexturePath + ".png"));
    }
    public static Block createBoosterBoxBlock() { return new BuddycardBoosterBoxBlock(ALWAYS_TRUE, BuddycardsBlocks.BOOSTER_BOX_PROPERTIES); }
    public static BuddysteelSetMedalItem createBuddysteelMedal(BuddycardSet set, String effectName, int level, int duration, Item.Properties properties) {
        return new ConfiguredBuddysteelSetMedalItem(new ConfiguredMedalType(ConfiguredMedalType.resolveEffect(effectName), level, duration), set, properties);
    }
    public static LuminisSetMedalItem createLuminisMedal(BuddycardSet set, String effectName, int level, int duration, Item.Properties properties) {
        return new ConfiguredLuminisSetMedalItem(new ConfiguredMedalType(ConfiguredMedalType.resolveEffect(effectName), level, duration), set, properties);
    }
    public static ZylexSetMedalItem createZylexMedal(BuddycardSet set, String effectName, int level, int duration, Item.Properties properties) {
        return new ConfiguredZylexSetMedalItem(new ConfiguredMedalType(ConfiguredMedalType.resolveEffect(effectName), level, duration), set, properties);
    }
    public static SimpleWeightedRandomList<Rarity> buildWeights(java.util.Map<String, Integer> raw) {
        SimpleWeightedRandomList.Builder<Rarity> builder = SimpleWeightedRandomList.builder();
        if (raw == null || raw.isEmpty()) return builder.add(Rarity.COMMON,70).add(Rarity.UNCOMMON,20).add(Rarity.RARE,10).add(Rarity.EPIC,1).build();
        raw.forEach((rarity, weight) -> builder.add(rarity(rarity), weight));
        return builder.build();
    }
    public static Rarity rarity(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) { case "uncommon" -> Rarity.UNCOMMON; case "rare" -> Rarity.RARE; case "epic" -> Rarity.EPIC; default -> Rarity.COMMON; };
    }
}
