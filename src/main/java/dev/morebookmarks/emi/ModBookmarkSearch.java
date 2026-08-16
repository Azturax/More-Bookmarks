package dev.morebookmarks.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.screen.EmiScreenManager;

/**
 * Thin wrapper around EMI's public search API.
 */
public final class ModBookmarkSearch {
	private ModBookmarkSearch() {
	}

	public static String current() {
		return EmiApi.getSearchText();
	}

	public static void apply(String query) {
		EmiApi.setSearchText(query);
		EmiScreenManager.search.setFocused(true);
		EmiScreenManager.focusSearchSidebarType(SidebarType.INDEX);
		if (!EmiScreenManager.hasSidebarVisible(SidebarType.INDEX)) {
			EmiScreenManager.focusSidebarType(SidebarType.INDEX);
		}
		EmiScreenManager.updateSearchSidebar();
	}
}
