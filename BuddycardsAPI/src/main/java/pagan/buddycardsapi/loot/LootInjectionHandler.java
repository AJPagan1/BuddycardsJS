package pagan.buddycardsapi.loot;

import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.content.LootInjectionDefinition;
import pagan.buddycardsapi.loader.ExternalContentBootstrap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BuddycardsApiBridgeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootInjectionHandler {
    private LootInjectionHandler() {}

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName() == null) return;
        for (LootInjectionDefinition definition : ExternalContentBootstrap.REPOSITORY.lootInjections().values()) {
            ResourceLocation target = ResourceLocation.tryParse(definition.targetLootTable());
            if (target == null || !target.equals(event.getName())) continue;
            ResourceLocation itemId = ResourceLocation.tryParse(definition.item());
            if (itemId == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) continue;
            int weight = Math.max(1, definition.weight() == null ? 1 : definition.weight());
            int count = Math.max(1, definition.count() == null ? 1 : definition.count());
            event.getTable().addPool(
                    LootPool.lootPool()
                            .name("buddycardsapi/" + definition.path())
                            .setRolls(ConstantValue.exactly(1))
                            .add(
                                    LootItem.lootTableItem(item)
                                            .setWeight(weight)
                                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                            )
                            .build()
            );
        }
    }
}
