package dev.morebookmarks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

public class MoreBookmarksModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
				return parent;
			}
			return MoreBookmarksConfigScreen.create(parent);
		};
	}
}
