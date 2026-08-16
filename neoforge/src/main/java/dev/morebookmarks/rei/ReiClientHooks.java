package dev.morebookmarks.rei;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Draws and handles REI bookmark widgets on top of REI's overlay.
 */
public final class ReiClientHooks {
	private ReiClientHooks() {
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener(ReiClientHooks::onRender);
		NeoForge.EVENT_BUS.addListener(ReiClientHooks::onMousePressed);
		NeoForge.EVENT_BUS.addListener(ReiClientHooks::onMouseScrolled);
		NeoForge.EVENT_BUS.addListener(ReiClientHooks::onKeyPressed);
	}

	private static void onRender(ScreenEvent.Render.Post event) {
		if (!ReiSearchBridge.isAvailable() && !ReiBookmarkOverlay.INSTANCE.hud().isMenuOpen()) {
			return;
		}
		ReiBookmarkOverlay.INSTANCE.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getScreen());
		ReiBookmarkOverlay.INSTANCE.renderForeground(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getScreen());
	}

	private static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
		if (ReiBookmarkOverlay.INSTANCE.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
			event.setCanceled(true);
		}
	}

	private static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
		if (ReiBookmarkOverlay.INSTANCE.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaY())) {
			event.setCanceled(true);
		}
	}

	private static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (ReiBookmarkOverlay.INSTANCE.keyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}
}
