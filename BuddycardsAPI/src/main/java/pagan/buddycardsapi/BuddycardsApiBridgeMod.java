package pagan.buddycardsapi;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import pagan.buddycardsapi.loader.ExternalContentBootstrap;
import pagan.buddycardsapi.registry.BcaBlocks;
import pagan.buddycardsapi.registry.BcaItems;

@Mod(BuddycardsApiBridgeMod.MOD_ID)
public class BuddycardsApiBridgeMod {
    public static final String MOD_ID = "buddycardsapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BuddycardsApiBridgeMod() {
        ExternalContentBootstrap.bootstrap();

        BcaBlocks.init();
        BcaItems.init();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BcaBlocks.registerAll(modBus);
        BcaItems.registerAll(modBus);

        LOGGER.info("BuddycardsAPI initialized");
    }
}