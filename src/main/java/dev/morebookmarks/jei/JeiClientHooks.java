package dev.morebookmarks.jei;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

/**
 * Draws and handles JEI bookmark widgets on top of JEI's overlay.
 */
public final class JeiClientHooks {
	private JeiClientHooks() {
	}

	public static void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			ScreenEvents.afterRender(screen).register((scr, context, mouseX, mouseY, delta) -> {
				if (!JeiSearchBridge.isAvailable() && !JeiBookmarkOverlay.INSTANCE.hud().isMenuOpen()) {
					return;
				}
				JeiBookmarkOverlay.INSTANCE.render(context, mouseX, mouseY, scr);
				JeiBookmarkOverlay.INSTANCE.renderForeground(context, mouseX, mouseY, scr);
			});
			ScreenMouseEvents.allowMouseClick(screen).register((scr, mouseX, mouseY, button) ->
					!JeiBookmarkOverlay.INSTANCE.mouseClicked(mouseX, mouseY, button));
			ScreenMouseEvents.allowMouseScroll(screen).register((scr, mouseX, mouseY, horizontal, vertical) ->
					!JeiBookmarkOverlay.INSTANCE.mouseScrolled(mouseX, mouseY, vertical));
			ScreenKeyboardEvents.allowKeyPress(screen).register((scr, key, scancode, modifiers) ->
					!JeiBookmarkOverlay.INSTANCE.keyPressed(key));
		});
	}
}
