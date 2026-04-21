package pagan.buddycardsapi.compat;

import com.wildcard.buddycards.core.BuddycardSet;
import com.wildcard.buddycards.item.BuddycardItem;
import com.wildcard.buddycards.item.BuddycardSetPackItem;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DataDrivenBuddycardPackItem extends BuddycardSetPackItem {
    private final Map<Rarity, List<Supplier<BuddycardItem>>> packTable;

    public DataDrivenBuddycardPackItem(
            BuddycardSet set,
            int cards,
            int foils,
            SimpleWeightedRandomList<Rarity> rarityWeights,
            Item.Properties properties,
            Map<Rarity, List<Supplier<BuddycardItem>>> packTable
    ) {
        super(set, cards, foils, rarityWeights, properties);
        this.packTable = new EnumMap<>(Rarity.class);

        for (Rarity rarity : List.of(Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC)) {
            this.packTable.put(rarity, new ArrayList<>(packTable.getOrDefault(rarity, List.of())));
        }
    }

    @Override
    public List<BuddycardItem> getPossibleCards(Rarity rarity) {
        List<Supplier<BuddycardItem>> suppliers = packTable.getOrDefault(rarity, List.of());
        List<BuddycardItem> cards = new ArrayList<>();

        for (Supplier<BuddycardItem> supplier : suppliers) {
            BuddycardItem item = supplier.get();
            if (item != null) {
                cards.add(item);
            }
        }

        return cards;
    }
}