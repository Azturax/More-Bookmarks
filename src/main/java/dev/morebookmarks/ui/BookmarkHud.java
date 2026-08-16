package dev.morebookmarks.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import dev.morebookmarks.config.MoreBookmarksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Coordinates the search-bar button, popup menu, and left-side panel
 * for JEI / REI (EMI keeps its own overlay).
 */
public final class BookmarkHud {
	private final SearchAccess search;
	private final BookmarkButton button = new BookmarkButton();
	private final BookmarkMenu menu;
	private final BookmarkPanel panel;
	private boolean menuOpen;
	private boolean searchVisible;

	public BookmarkHud(SearchAccess search) {
		this.search = search;
		this.menu = new BookmarkMenu(search);
		this.panel = new BookmarkPanel(search);
	}

	public BookmarkButton getButton() {
		return button;
	}

	public BookmarkPanel getPanel() {
		return panel;
	}

	public void toggleMenu() {
		if (!MoreBookmarksConfig.get().showSearchButton) {
			menuOpen = false;
			return;
		}
		menuOpen = !menuOpen;
	}

	public void closeMenu() {
		menuOpen = false;
	}

	public boolean isMenuOpen() {
		return menuOpen;
	}

	public void setSearchVisible(boolean visible) {
		this.searchVisible = visible;
		if (!visible) {
			closeMenu();
		}
	}

	public boolean isSearchVisible() {
		return searchVisible;
	}

	public void layoutMenu(Screen screen) {
		if (menuOpen && MoreBookmarksConfig.get().showSearchButton && searchVisible) {
			menu.layout(button.getX(), button.getY(), screen.width, screen.height);
		}
	}

	public void render(DrawContext context, int mouseX, int mouseY) {
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (config.showSearchButton && searchVisible) {
			button.render(context, mouseX, mouseY);
		}
		if (config.showLeftPanel) {
			panel.render(context, mouseX, mouseY);
		}
	}

	public void renderForeground(DrawContext context, int mouseX, int mouseY, Screen screen) {
		if (menuOpen && MoreBookmarksConfig.get().showSearchButton && searchVisible) {
			menu.layout(button.getX(), button.getY(), screen.width, screen.height);
			menu.render(context, mouseX, mouseY);
		}
		drawTooltip(context, mouseX, mouseY);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int buttonCode) {
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (menuOpen) {
			if (menu.mouseClicked(mouseX, mouseY, buttonCode, this::closeMenu)) {
				return true;
			}
			if (!button.contains(mouseX, mouseY)) {
				closeMenu();
			}
		}
		if (config.showSearchButton && searchVisible && buttonCode == 0 && button.contains(mouseX, mouseY)) {
			Ui.click();
			toggleMenu();
			return true;
		}
		return config.showLeftPanel && panel.mouseClicked(mouseX, mouseY, buttonCode);
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (menuOpen && menu.mouseScrolled(mouseX, mouseY, amount)) {
			return true;
		}
		return MoreBookmarksConfig.get().showLeftPanel && panel.mouseScrolled(mouseX, mouseY, amount);
	}

	public boolean keyPressed(int keyCode) {
		if (menuOpen && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			closeMenu();
			return true;
		}
		return false;
	}

	public List<IntRect> getExclusionBounds() {
		List<IntRect> list = new ArrayList<>(3);
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (config.showSearchButton && searchVisible) {
			list.add(new IntRect(button.getX() - 1, button.getY() - 1, BookmarkButton.SIZE + 2, BookmarkButton.SIZE + 2));
		}
		if (config.showLeftPanel) {
			list.add(panel.getBounds());
		}
		if (menuOpen) {
			list.add(menu.getBounds());
		}
		return list;
	}

	private void drawTooltip(DrawContext context, int mouseX, int mouseY) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer font = client.textRenderer;
		Text text = null;
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (menuOpen) {
			String key = menu.getHoveredTooltip();
			if (key != null) {
				text = key.startsWith("morebookmarks.") ? Text.translatable(key) : Text.literal(key);
			}
		} else if (config.showSearchButton && searchVisible && button.isHovered()) {
			text = button.getTooltip().get(0);
		} else if (config.showLeftPanel && panel.getHoveredTooltip() != null) {
			text = Text.translatable("morebookmarks.tooltip.apply", panel.getHoveredTooltip());
		}
		if (text != null) {
			context.drawTooltip(font, text, mouseX, mouseY);
		}
	}
}
