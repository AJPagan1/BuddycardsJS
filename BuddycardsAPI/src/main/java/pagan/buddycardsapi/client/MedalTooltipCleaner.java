package pagan.buddycardsapi.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import pagan.buddycardsapi.content.MedalDefinition;
import pagan.buddycardsapi.loader.ExternalContentBootstrap;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MedalTooltipCleaner {
    private MedalTooltipCleaner() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty()) {
            return;
        }

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (id == null) {
            return;
        }

        MedalDefinition medal = ExternalContentBootstrap.REPOSITORY.medals().get(id.toString());
        if (medal == null) {
            return;
        }

        List<Component> tooltip = event.getToolTip();
        if (tooltip.size() <= 1) {
            return;
        }

        Iterator<Component> it = tooltip.listIterator(1);
        while (it.hasNext()) {
            Component line = it.next();
            String text = line.getString();
            if (text == null) {
                continue;
            }

            String normalized = text.trim().toLowerCase();

            if (normalized.startsWith("effect:")) {
                it.remove();
            }
        }
    }
}