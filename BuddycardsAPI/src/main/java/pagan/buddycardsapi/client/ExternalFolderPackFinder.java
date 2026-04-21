package pagan.buddycardsapi.client;

import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.util.PathsUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;

import java.nio.file.Files;

public final class ExternalFolderPackFinder {
    private ExternalFolderPackFinder() {}

    public static void register(AddPackFindersEvent event) {
        if (!Files.isDirectory(PathsUtil.root())) return;
        if (event.getPackType() != PackType.CLIENT_RESOURCES && event.getPackType() != PackType.SERVER_DATA) return;

        var packId = BuddycardsApiBridgeMod.MOD_ID + ":external_" + event.getPackType().getDirectory();
        var displayName = Component.literal("Buddycards API External " + event.getPackType().getDirectory());
        var path = PathsUtil.root();

        var pack = new PathPackResources(packId, path, false);
        event.addRepositorySource(consumer -> {
            Pack.ResourcesSupplier supplier = ignored -> pack;
            Pack.Info info = Pack.readPackInfo("pack.mcmeta", supplier);
            if (info == null) {
                return;
            }
            Pack wrapped = Pack.create(packId, displayName, true, supplier, info, event.getPackType(), Pack.Position.TOP, true, PackSource.BUILT_IN);
            if (wrapped != null) {
                consumer.accept(wrapped);
            }
        });
    }
}
