package dev.morebookmarks.emi;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.screen.EmiScreenManager;
import dev.morebookmarks.config.MoreBookmarksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Coordinates the search-bar button, popup menu, and left-side panel.
 * Invoked from {@link dev.morebookmarks.mixin.EmiScreenManagerMixin}.
 */
public final class ModBookmarkOverlay {
	public static final ModBookmarkOverlay INSTANCE = new ModBookmarkOverlay();

	private final ModBookmarkButton button = new ModBookmarkButton();
	private final ModBookmarkMenu menu = new ModBookmarkMenu();
	private final ModBookmarkPanelSection panel = new ModBookmarkPanelSection();
	private boolean menuOpen;

	private ModBookmarkOverlay() {
	}

	public ModBookmarkButton getButton() {
		return button;
	}

	public ModBookmarkPanelSection getPanel() {
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

	/**
	 * Called after EMI finishes laying out the search bar and chrome buttons.
	 */
	public void afterAddWidgets(Screen screen) {
		if (EmiScreenManager.isDisabled()) {
			return;
		}
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (config.showSearchButton && EmiScreenManager.search.isVisible()) {
			int searchX = EmiScreenManager.search.getX();
			int searchY = EmiScreenManager.search.getY();
			button.setPosition(searchX - ModBookmarkButton.SIZE - 2, searchY + 3);
		} else {
			closeMenu();
		}
		if (config.showLeftPanel) {
			panel.layout(screen);
		}
		if (menuOpen && config.showSearchButton) {
			menu.layout(button.getX(), button.getY(), screen.width, screen.height);
		}
	}

	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (EmiScreenManager.isDisabled()) {
			return;
		}
		Screen screen = Minecraft.getInstance().screen;
		if (screen == null) {
			return;
		}
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (config.showSearchButton && EmiScreenManager.search.isVisible()) {
			button.render(graphics, mouseX, mouseY);
		}
		if (config.showLeftPanel) {
			panel.layout(screen);
			panel.render(graphics, mouseX, mouseY);
		}
	}

	public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
		if (EmiScreenManager.isDisabled()) {
			return;
		}
		Screen screen = Minecraft.getInstance().screen;
		if (screen == null) {
			return;
		}
		if (menuOpen && MoreBookmarksConfig.get().showSearchButton && EmiScreenManager.search.isVisible()) {
			menu.layout(button.getX(), button.getY(), screen.width, screen.height);
			menu.render(graphics, mouseX, mouseY);
		}
		drawTooltip(graphics, mouseX, mouseY);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int buttonCode) {
		if (EmiScreenManager.isDisabled()) {
			return false;
		}
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (menuOpen) {
			if (menu.mouseClicked(mouseX, mouseY, buttonCode)) {
				return true;
			}
			if (!button.contains(mouseX, mouseY)) {
				closeMenu();
			}
		}
		if (config.showSearchButton && EmiScreenManager.search.isVisible() && button.mouseClicked(mouseX, mouseY, buttonCode)) {
			return true;
		}
		return config.showLeftPanel && panel.mouseClicked(mouseX, mouseY, buttonCode);
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (EmiScreenManager.isDisabled()) {
			return false;
		}
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

	public List<Bounds> getExclusionBounds() {
		List<Bounds> list = new ArrayList<>(3);
		if (EmiScreenManager.isDisabled()) {
			return list;
		}
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (config.showSearchButton && EmiScreenManager.search.isVisible()) {
			list.add(new Bounds(button.getX() - 1, button.getY() - 1, ModBookmarkButton.SIZE + 2, ModBookmarkButton.SIZE + 2));
		}
		if (config.showLeftPanel) {
			list.add(panel.getBounds());
		}
		if (menuOpen) {
			list.add(menu.getBounds());
		}
		return list;
	}

	private void drawTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
		Minecraft client = Minecraft.getInstance();
		Font font = client.font;
		Component text = null;
		MoreBookmarksConfig.Data config = MoreBookmarksConfig.get();
		if (menuOpen) {
			String key = menu.getHoveredTooltip();
			if (key != null) {
				text = key.startsWith("morebookmarks.") ? Component.translatable(key) : Component.literal(key);
			}
		} else if (config.showSearchButton && button.isHovered()) {
			text = button.getTooltip().get(0);
		} else if (config.showLeftPanel && panel.getHoveredTooltip() != null) {
			text = Component.translatable("morebookmarks.tooltip.apply", panel.getHoveredTooltip());
		}
		if (text != null) {
			graphics.renderTooltip(font, text, mouseX, mouseY);
		}
	}
}
