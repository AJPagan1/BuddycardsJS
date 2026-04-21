package pagan.buddycardsapi.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.compat.BuddycardsBridge;
import pagan.buddycardsapi.content.BoosterBoxDefinition;
import pagan.buddycardsapi.loader.ExternalContentBootstrap;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BcaBlocks {
    private static final Map<String, DeferredRegister<Block>> BLOCK_REGISTERS = new LinkedHashMap<>();

    public static final Map<String, RegistryObject<Block>> BOOSTER_BOX_BLOCKS = new LinkedHashMap<>();
    private static boolean initialized = false;

    private BcaBlocks() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        for (BoosterBoxDefinition definition : ExternalContentBootstrap.REPOSITORY.boosterBoxes().values()) {
            DeferredRegister<Block> register = blocksForNamespace(definition.namespace());

            BOOSTER_BOX_BLOCKS.put(
                    definition.id(),
                    register.register(definition.path(), BuddycardsBridge::createBoosterBoxBlock)
            );
        }
    }

    public static void registerAll(IEventBus modBus) {
        for (DeferredRegister<Block> register : BLOCK_REGISTERS.values()) {
            register.register(modBus);
        }
    }

    private static DeferredRegister<Block> blocksForNamespace(String namespace) {
        return BLOCK_REGISTERS.computeIfAbsent(
                namespace,
                ns -> DeferredRegister.create(ForgeRegistries.BLOCKS, ns)
        );
    }
}