package dev.morebookmarks.jei;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Draws and handles JEI bookmark widgets on top of JEI's overlay.
 */
public final class JeiClientHooks {
	private JeiClientHooks() {
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener(JeiClientHooks::onRender);
		NeoForge.EVENT_BUS.addListener(JeiClientHooks::onMousePressed);
		NeoForge.EVENT_BUS.addListener(JeiClientHooks::onMouseScrolled);
		NeoForge.EVENT_BUS.addListener(JeiClientHooks::onKeyPressed);
	}

	private static void onRender(ScreenEvent.Render.Post event) {
		if (!JeiSearchBridge.isAvailable() && !JeiBookmarkOverlay.INSTANCE.hud().isMenuOpen()) {
			return;
		}
		JeiBookmarkOverlay.INSTANCE.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getScreen());
		JeiBookmarkOverlay.INSTANCE.renderForeground(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getScreen());
	}

	private static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
		if (JeiBookmarkOverlay.INSTANCE.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
			event.setCanceled(true);
		}
	}

	private static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
		if (JeiBookmarkOverlay.INSTANCE.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaY())) {
			event.setCanceled(true);
		}
	}

	private static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (JeiBookmarkOverlay.INSTANCE.keyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}
}
