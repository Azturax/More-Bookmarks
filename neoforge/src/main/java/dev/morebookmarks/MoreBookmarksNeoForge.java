package dev.morebookmarks;

import dev.morebookmarks.bookmark.ModBookmarkManager;
import dev.morebookmarks.config.MoreBookmarksConfig;
import dev.morebookmarks.config.MoreBookmarksConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client entrypoint. Loads options and persisted search bookmarks at startup.
 * All in-game UI is driven by EMI mixins + {@link dev.morebookmarks.emi.ModBookmarkOverlay}.
 */
@Mod(value = MoreBookmarks.MOD_ID, dist = Dist.CLIENT)
public class MoreBookmarksNeoForge {
	public MoreBookmarksNeoForge(ModContainer container) {
		MoreBookmarksConfig.init(FMLPaths.CONFIGDIR.get().resolve("morebookmarks.json"));
		ModBookmarkManager.init(FMLPaths.CONFIGDIR.get().resolve("emi-mod-bookmarks.json"));
		MoreBookmarks.LOGGER.info("Loaded {} search bookmark(s) from {}",
				ModBookmarkManager.getBookmarks().size(),
				FMLPaths.CONFIGDIR.get().resolve("emi-mod-bookmarks.json"));

		if (ModList.get().isLoaded("cloth_config")) {
			container.registerExtensionPoint(IConfigScreenFactory.class,
					(modContainer, parent) -> MoreBookmarksConfigScreen.create(parent));
		}
	}
}
