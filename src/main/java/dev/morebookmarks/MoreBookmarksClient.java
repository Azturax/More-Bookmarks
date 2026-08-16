package dev.morebookmarks;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.morebookmarks.bookmark.ModBookmarkManager;
import dev.morebookmarks.config.MoreBookmarksConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Client entrypoint. Loads options and persisted search bookmarks at startup.
 * Viewer UI is attached only when EMI, JEI, and/or REI is present.
 */
public class MoreBookmarksClient implements ClientModInitializer {
	public static final String MOD_ID = "morebookmarks";
	public static final Logger LOGGER = LoggerFactory.getLogger("MoreBookmarks");

	@Override
	public void onInitializeClient() {
		MoreBookmarksConfig.init();
		ModBookmarkManager.initShared(FabricLoader.getInstance().getConfigDir());
		LOGGER.info("Loaded {} search bookmark(s) from {}", ModBookmarkManager.getBookmarks().size(), getBookmarkFile());

		if (FabricLoader.getInstance().isModLoaded("jei")) {
			dev.morebookmarks.jei.JeiClientHooks.register();
		}
		if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			dev.morebookmarks.rei.ReiClientHooks.register();
		}
	}

	public static Path getBookmarkFile() {
		return FabricLoader.getInstance().getConfigDir().resolve(ModBookmarkManager.SHARED_FILE);
	}
}
