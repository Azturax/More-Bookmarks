package dev.morebookmarks.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.morebookmarks.config.MoreBookmarksConfig;
import dev.morebookmarks.ui.BookmarkHud;
import dev.morebookmarks.ui.IntRect;
import mezz.jei.api.gui.handlers.IGuiProperties;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

/**
 * JEI-side bookmark chrome: button left of the search field, list on the left.
 */
public final class JeiBookmarkOverlay {
	public static final JeiBookmarkOverlay INSTANCE = new JeiBookmarkOverlay();

	private final BookmarkHud hud = new BookmarkHud(JeiSearchBridge.INSTANCE);

	private JeiBookmarkOverlay() {
	}

	public BookmarkHud hud() {
		return hud;
	}

	public void layout(Screen screen) {
		boolean visible = JeiSearchBridge.isAvailable();
		hud.setSearchVisible(visible);
		if (visible && MoreBookmarksConfig.get().showSearchButton) {
			JeiSearchBridge.positionButton(hud.getButton(), screen);
		}
		if (MoreBookmarksConfig.get().showLeftPanel) {
			layoutPanel(screen);
		}
		hud.layoutMenu(screen);
	}

	public void render(GuiGraphics graphics, int mouseX, int mouseY, Screen screen) {
		if (!JeiSearchBridge.isAvailable() && !MoreBookmarksConfig.get().showLeftPanel) {
			return;
		}
		layout(screen);
		hud.render(graphics, mouseX, mouseY);
	}

	public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, Screen screen) {
		if (!JeiSearchBridge.isAvailable() && !hud.isMenuOpen()) {
			return;
		}
		hud.renderForeground(graphics, mouseX, mouseY, screen);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return (JeiSearchBridge.isAvailable() || MoreBookmarksConfig.get().showLeftPanel)
				&& hud.mouseClicked(mouseX, mouseY, button);
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return hud.mouseScrolled(mouseX, mouseY, amount);
	}

	public boolean keyPressed(int keyCode) {
		return hud.keyPressed(keyCode);
	}

	public Collection<Rect2i> getExclusionRects() {
		List<Rect2i> rects = new ArrayList<>();
		for (IntRect bounds : hud.getExclusionBounds()) {
			if (!bounds.empty()) {
				rects.add(new Rect2i(bounds.x(), bounds.y(), bounds.width(), bounds.height()));
			}
		}
		return rects;
	}

	private void layoutPanel(Screen screen) {
		int height = hud.getPanel().computeHeight();
		int width = 96;
		int x = 2;
		int y = screen.height - 24 - height;
		IGuiProperties props = JeiSearchBridge.guiProperties(screen);
		if (props != null) {
			int left = props.guiLeft();
			if (left > 8) {
				width = Math.max(80, Math.min(96, left - 4));
			}
			y = Math.max(2, props.screenHeight() - 28 - height);
		}
		hud.getPanel().layoutAt(x, y, width);
	}
}
