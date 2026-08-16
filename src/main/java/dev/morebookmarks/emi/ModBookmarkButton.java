package dev.morebookmarks.emi;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Small {@code @} icon parked next to EMI's search bar.
 */
public final class ModBookmarkButton {
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

	public void render(DrawContext context, int mouseX, int mouseY) {
		hovered = Ui.contains(x, y, SIZE, SIZE, mouseX, mouseY);
		int bg = hovered ? 0xFF2A4A6A : 0xFF16202C;
		int border = hovered ? 0xFF7EB6FF : 0xFF3D6B8A;
		Ui.fill(context, x, y, SIZE, SIZE, bg);
		Ui.border(context, x, y, SIZE, SIZE, border);

		var textRenderer = MinecraftClient.getInstance().textRenderer;
		int textWidth = textRenderer.getWidth("@");
		context.drawText(textRenderer, "@", x + (SIZE - textWidth) / 2, y + 2, 0xFF88AAFF, false);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0 || !Ui.contains(x, y, SIZE, SIZE, mouseX, mouseY)) {
			return false;
		}
		Ui.click();
		ModBookmarkOverlay.INSTANCE.toggleMenu();
		return true;
	}

	public List<Text> getTooltip() {
		return List.of(Ui.tr("morebookmarks.tooltip.manage"));
	}

	public boolean contains(double mouseX, double mouseY) {
		return Ui.contains(x, y, SIZE, SIZE, mouseX, mouseY);
	}
}
