package dev.morebookmarks.emi;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

final class Ui {
	private Ui() {
	}

	static void fill(DrawContext context, int x, int y, int width, int height, int color) {
		context.fill(x, y, x + width, y + height, color);
	}

	static void border(DrawContext context, int x, int y, int width, int height, int color) {
		fill(context, x, y, width, 1, color);
		fill(context, x, y + height - 1, width, 1, color);
		fill(context, x, y, 1, height, color);
		fill(context, x + width - 1, y, 1, height, color);
	}

	static boolean contains(int x, int y, int width, int height, double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	static void click() {
		MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	static Text tr(String key) {
		return Text.translatable(key);
	}

	static Text tr(String key, Object... args) {
		return Text.translatable(key, args);
	}
}
