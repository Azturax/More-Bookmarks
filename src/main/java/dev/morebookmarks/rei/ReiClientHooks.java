package dev.morebookmarks.rei;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

/**
 * Draws and handles REI bookmark widgets on top of REI's overlay.
 */
public final class ReiClientHooks {
	private ReiClientHooks() {
	}

	public static void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			ScreenEvents.afterRender(screen).register((scr, context, mouseX, mouseY, delta) -> {
				if (!ReiSearchBridge.isAvailable() && !ReiBookmarkOverlay.INSTANCE.hud().isMenuOpen()) {
					return;
				}
				ReiBookmarkOverlay.INSTANCE.render(context, mouseX, mouseY, scr);
				ReiBookmarkOverlay.INSTANCE.renderForeground(context, mouseX, mouseY, scr);
			});
			ScreenMouseEvents.allowMouseClick(screen).register((scr, mouseX, mouseY, button) ->
					!ReiBookmarkOverlay.INSTANCE.mouseClicked(mouseX, mouseY, button));
			ScreenMouseEvents.allowMouseScroll(screen).register((scr, mouseX, mouseY, horizontal, vertical) ->
					!ReiBookmarkOverlay.INSTANCE.mouseScrolled(mouseX, mouseY, vertical));
			ScreenKeyboardEvents.allowKeyPress(screen).register((scr, key, scancode, modifiers) ->
					!ReiBookmarkOverlay.INSTANCE.keyPressed(key));
		});
	}
}
