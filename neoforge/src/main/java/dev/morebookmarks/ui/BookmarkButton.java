package dev.morebookmarks.ui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Small {@code @} icon parked next to a recipe-viewer search bar.
 */
public final class BookmarkButton {
	public static final int SIZE = 12;

	private int x;
	private int y;
	private boolean hovered;

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHovered() {
		return hovered;
	}

	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		hovered = Ui.contains(x, y, SIZE, SIZE, mouseX, mouseY);
		int bg = hovered ? 0xFF2A4A6A : 0xFF16202C;
		int border = hovered ? 0xFF7EB6FF : 0xFF3D6B8A;
		Ui.fill(graphics, x, y, SIZE, SIZE, bg);
		Ui.border(graphics, x, y, SIZE, SIZE, border);

		var font = Minecraft.getInstance().font;
		int textWidth = font.width("@");
		graphics.drawString(font, "@", x + (SIZE - textWidth) / 2, y + 2, 0xFF88AAFF, false);
	}

	public boolean contains(double mouseX, double mouseY) {
		return Ui.contains(x, y, SIZE, SIZE, mouseX, mouseY);
	}

	public List<Component> getTooltip() {
		return List.of(Ui.tr("morebookmarks.tooltip.manage"));
	}
}
