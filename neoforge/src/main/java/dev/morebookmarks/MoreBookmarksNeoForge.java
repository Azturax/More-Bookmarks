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
 * Viewer UI is attached only when EMI, JEI, and/or REI is present.
 */
@Mod(value = MoreBookmarks.MOD_ID, dist = Dist.CLIENT)
public class MoreBookmarksNeoForge {
	public MoreBookmarksNeoForge(ModContainer container) {
		MoreBookmarksConfig.init(FMLPaths.CONFIGDIR.get().resolve("morebookmarks.json"));
		ModBookmarkManager.initShared(FMLPaths.CONFIGDIR.get());
		MoreBookmarks.LOGGER.info("Loaded {} search bookmark(s) from {}",
				ModBookmarkManager.getBookmarks().size(),
				FMLPaths.CONFIGDIR.get().resolve(ModBookmarkManager.SHARED_FILE));

		if (ModList.get().isLoaded("cloth_config")) {
			container.registerExtensionPoint(IConfigScreenFactory.class,
					(modContainer, parent) -> MoreBookmarksConfigScreen.create(parent));
		}
		if (ModList.get().isLoaded("jei")) {
			dev.morebookmarks.jei.JeiClientHooks.register();
		}
		if (ModList.get().isLoaded("roughlyenoughitems")) {
			dev.morebookmarks.rei.ReiClientHooks.register();
		}
	}
}
