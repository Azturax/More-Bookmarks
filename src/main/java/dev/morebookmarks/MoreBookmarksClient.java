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
 * All in-game UI is driven by EMI mixins + {@link dev.morebookmarks.emi.ModBookmarkOverlay}.
 */
public class MoreBookmarksClient implements ClientModInitializer {
	public static final String MOD_ID = "morebookmarks";
	public static final Logger LOGGER = LoggerFactory.getLogger("MoreBookmarks");

	@Override
	public void onInitializeClient() {
		MoreBookmarksConfig.init();
		ModBookmarkManager.init(getBookmarkFile());
		LOGGER.info("Loaded {} search bookmark(s) from {}", ModBookmarkManager.getBookmarks().size(), getBookmarkFile());
	}

	public static Path getBookmarkFile() {
		return FabricLoader.getInstance().getConfigDir().resolve("emi-mod-bookmarks.json");
	}
}
