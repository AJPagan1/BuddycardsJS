package pagan.buddycardsapi.client;

import com.wildcard.buddycards.registries.BuddycardsMisc;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import pagan.buddycardsapi.registry.BcaItems;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CreativeTabHandler {
    private CreativeTabHandler() {
    }

    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == BuddycardsMisc.CARDS_TAB.getKey()) {
            addAll(event, BcaItems.CARDS.values());
        }

        if (event.getTabKey() == BuddycardsMisc.MAIN_TAB.getKey()) {
            addAll(event, BcaItems.PACKS.values());
            addAll(event, BcaItems.BINDERS.values());
            addAll(event, BcaItems.BOOSTER_BOX_ITEMS.values());
            addAll(event, BcaItems.BUDDYSTEEL_MEDALS.values());
            addAll(event, BcaItems.LUMINIS_MEDALS.values());
            addAll(event, BcaItems.ZYLEX_MEDALS.values());
        }
    }

    private static void addAll(BuildCreativeModeTabContentsEvent event, Iterable<? extends RegistryObject<? extends Item>> items) {
        for (RegistryObject<? extends Item> item : items) {
            if (item != null && item.isPresent()) {
                event.accept(item.get());
            }
        }
    }
}