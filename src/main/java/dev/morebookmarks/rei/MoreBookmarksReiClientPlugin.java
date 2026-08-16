package dev.morebookmarks.rei;

import java.util.List;

import dev.morebookmarks.bookmark.ModBookmarkManager;
import me.shedaniel.rei.api.client.favorites.FavoriteEntry;
import me.shedaniel.rei.api.client.favorites.FavoriteEntryType;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MoreBookmarksReiClientPlugin implements REIClientPlugin {
	@Override
	public void registerFavorites(FavoriteEntryType.Registry registry) {
		registry.register(SearchBookmarkFavoriteEntry.ID, SearchBookmarkFavoriteEntry.Type.INSTANCE);
		List<String> bookmarks = ModBookmarkManager.getBookmarks();
		if (!bookmarks.isEmpty()) {
			FavoriteEntry[] entries = bookmarks.stream()
					.map(SearchBookmarkFavoriteEntry::new)
					.toArray(FavoriteEntry[]::new);
			registry.getOrCrateSection(Text.translatable("morebookmarks.panel.header")).add(entries);
		}
	}

	@Override
	public void registerExclusionZones(ExclusionZones zones) {
		zones.register(Screen.class, screen -> ReiBookmarkOverlay.INSTANCE.getExclusionRectangles());
	}
}
