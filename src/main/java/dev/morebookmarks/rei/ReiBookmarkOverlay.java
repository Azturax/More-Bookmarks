package dev.morebookmarks.rei;

import java.util.ArrayList;
import java.util.List;

import dev.morebookmarks.config.MoreBookmarksConfig;
import dev.morebookmarks.ui.BookmarkHud;
import dev.morebookmarks.ui.IntRect;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

/**
 * REI-side bookmark chrome: button left of the search field, list on the left.
 */
public final class ReiBookmarkOverlay {
	public static final ReiBookmarkOverlay INSTANCE = new ReiBookmarkOverlay();

	private final BookmarkHud hud = new BookmarkHud(ReiSearchBridge.INSTANCE);

	private ReiBookmarkOverlay() {
	}

	public BookmarkHud hud() {
		return hud;
	}

	public void layout(Screen screen) {
		boolean visible = ReiSearchBridge.isAvailable();
		hud.setSearchVisible(visible);
		if (visible && MoreBookmarksConfig.get().showSearchButton) {
			ReiSearchBridge.positionButton(hud.getButton(), screen);
		}
		if (MoreBookmarksConfig.get().showLeftPanel) {
			layoutPanel(screen);
		}
		hud.layoutMenu(screen);
	}

	public void render(DrawContext context, int mouseX, int mouseY, Screen screen) {
		if (!ReiSearchBridge.isAvailable() && !MoreBookmarksConfig.get().showLeftPanel) {
			return;
		}
		layout(screen);
		hud.render(context, mouseX, mouseY);
	}

	public void renderForeground(DrawContext context, int mouseX, int mouseY, Screen screen) {
		if (!ReiSearchBridge.isAvailable() && !hud.isMenuOpen()) {
			return;
		}
		hud.renderForeground(context, mouseX, mouseY, screen);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return (ReiSearchBridge.isAvailable() || MoreBookmarksConfig.get().showLeftPanel)
				&& hud.mouseClicked(mouseX, mouseY, button);
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return hud.mouseScrolled(mouseX, mouseY, amount);
	}

	public boolean keyPressed(int keyCode) {
		return hud.keyPressed(keyCode);
	}

	public List<Rectangle> getExclusionRectangles() {
		List<Rectangle> rects = new ArrayList<>();
		for (IntRect bounds : hud.getExclusionBounds()) {
			if (!bounds.empty()) {
				rects.add(new Rectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height()));
			}
		}
		return rects;
	}

	private void layoutPanel(Screen screen) {
		int height = hud.getPanel().computeHeight();
		int width = 96;
		int x = 2;
		int y = screen.height - 24 - height;
		Rectangle fav = ReiSearchBridge.favoritesArea();
		if (fav != null && fav.width > 0 && fav.height > 0) {
			x = fav.x;
			width = Math.max(80, fav.width);
			int below = fav.getMaxY() + 2;
			int aboveBottom = screen.height - 24 - height;
			y = Math.min(aboveBottom, Math.max(2, below));
			if (y + height > screen.height - 22) {
				y = aboveBottom;
			}
		}
		hud.getPanel().layoutAt(x, y, width);
	}
}
