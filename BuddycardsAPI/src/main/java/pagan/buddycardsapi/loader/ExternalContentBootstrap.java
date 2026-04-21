package pagan.buddycardsapi.loader;

import pagan.buddycardsapi.BuddycardsApiBridgeMod;
import pagan.buddycardsapi.content.ExternalContentRepository;
import pagan.buddycardsapi.util.PathsUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class ExternalContentBootstrap {
    public static ExternalContentRepository REPOSITORY = emptyRepository();

    private ExternalContentBootstrap() {
    }

    private static ExternalContentRepository emptyRepository() {
        return new ExternalContentRepository(
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of()
        );
    }

    public static void bootstrap() {
        Path root = PathsUtil.root();

        try {
            Files.createDirectories(root);
            Files.createDirectories(PathsUtil.dataRoot());
            Files.createDirectories(PathsUtil.assetsRoot());

            REPOSITORY = ExternalContentLoader.load(root);

            try {
                GeneratedAssetWriter.generate(REPOSITORY, root);
            } catch (Exception e) {
                BuddycardsApiBridgeMod.LOGGER.error("Failed to generate BuddycardsAPI assets", e);
            }

            try {
                GeneratedDataWriter.generate(REPOSITORY, root);
            } catch (Exception e) {
                BuddycardsApiBridgeMod.LOGGER.error("Failed to generate BuddycardsAPI data", e);
            }

            BuddycardsApiBridgeMod.LOGGER.info(
                    "Loaded BuddycardsAPI content: {} sets, {} cards, {} packs, {} binders, {} booster boxes, {} medals, {} loot injections",
                    REPOSITORY.sets().size(),
                    REPOSITORY.cards().size(),
                    REPOSITORY.packs().size(),
                    REPOSITORY.binders().size(),
                    REPOSITORY.boosterBoxes().size(),
                    REPOSITORY.medals().size(),
                    REPOSITORY.lootInjections().size()
            );

            BuddycardsApiBridgeMod.LOGGER.info("Bootstrapped BuddycardsAPI content at {}", root.toAbsolutePath());
        } catch (IOException e) {
            BuddycardsApiBridgeMod.LOGGER.error("Failed to bootstrap external Buddycards API content", e);
        }
    }
}