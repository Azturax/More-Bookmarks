package dev.morebookmarks.rei;

import dev.morebookmarks.ui.BookmarkButton;
import dev.morebookmarks.ui.SearchAccess;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.TextField;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.overlay.ScreenOverlay;
import net.minecraft.client.gui.screens.Screen;

/**
 * REI search get/set plus search-field geometry for the bookmark button.
 */
public final class ReiSearchBridge implements SearchAccess {
	public static final ReiSearchBridge INSTANCE = new ReiSearchBridge();

	private ReiSearchBridge() {
	}

	public static boolean isAvailable() {
		REIRuntime runtime = runtimeOrNull();
		return runtime != null && runtime.isOverlayVisible() && runtime.getSearchTextField() != null;
	}

	@Override
	public String current() {
		TextField field = searchField();
		return field == null ? "" : field.getText();
	}

	@Override
	public void apply(String query) {
		TextField field = searchField();
		if (field == null) {
			return;
		}
		field.setText(query == null ? "" : query);
		field.setFocused(true);
		REIRuntime.getInstance().getOverlay().ifPresent(ScreenOverlay::queueReloadSearch);
	}

	public static boolean positionButton(BookmarkButton button, Screen screen) {
		if (!isAvailable()) {
			return false;
		}
		Rectangle bounds = searchBounds();
		if (bounds != null) {
			button.setPosition(bounds.x - BookmarkButton.SIZE - 2, bounds.y + Math.max(0, (bounds.height - BookmarkButton.SIZE) / 2));
			return true;
		}
		REIRuntime runtime = runtimeOrNull();
		if (runtime == null) {
			return false;
		}
		ScreenOverlay overlay = runtime.getOverlay().orElse(null);
		if (overlay == null) {
			return false;
		}
		Rectangle area = overlay.getBounds();
		button.setPosition(area.x - BookmarkButton.SIZE - 2, area.getMaxY() - BookmarkButton.SIZE - 2);
		return true;
	}

	public static Rectangle favoritesArea() {
		REIRuntime runtime = runtimeOrNull();
		if (runtime == null) {
			return null;
		}
		try {
			return runtime.calculateFavoritesListArea();
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static Rectangle searchBounds() {
		TextField field = searchField();
		if (field instanceof WidgetWithBounds widget) {
			return widget.getBounds();
		}
		return null;
	}

	private static TextField searchField() {
		REIRuntime runtime = runtimeOrNull();
		return runtime == null ? null : runtime.getSearchTextField();
	}

	private static REIRuntime runtimeOrNull() {
		try {
			return REIRuntime.getInstance();
		} catch (Throwable ignored) {
			return null;
		}
	}
}
