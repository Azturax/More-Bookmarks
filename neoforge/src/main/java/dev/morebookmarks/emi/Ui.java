package dev.morebookmarks.emi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

final class Ui {
	private Ui() {
	}

	static void fill(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + height, color);
	}

	static void border(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		fill(graphics, x, y, width, 1, color);
		fill(graphics, x, y + height - 1, width, 1, color);
		fill(graphics, x, y, 1, height, color);
		fill(graphics, x + width - 1, y, 1, height, color);
	}

	static boolean contains(int x, int y, int width, int height, double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	static void click() {
		Minecraft.getInstance().getSoundManager()
				.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	static Component tr(String key) {
		return Component.translatable(key);
	}

	static Component tr(String key, Object... args) {
		return Component.translatable(key, args);
	}
}
