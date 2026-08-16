package dev.morebookmarks.emi;

import java.util.List;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.screen.EmiScreenManager;
import dev.morebookmarks.bookmark.ModBookmarkManager;
import dev.morebookmarks.config.MoreBookmarksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

/**
 * Left-side list rendered below EMI's favorites sidebar (or above the EMI
 * buttons if that sidebar is hidden / on another edge).
 */
public final class ModBookmarkPanelSection {
	private static final int ROW = 12;
	private static final int HEADER = 12;
	private static final int PAD = 3;

	private int x;
	private int y;
	private int width;
	private int height;
	private int scroll;
	private String hoveredTooltip;

	public void layout(Screen screen) {
		List<String> bookmarks = ModBookmarkManager.getBookmarks();
		int rows = Math.min(maxVisible(), bookmarks.size());
		height = PAD + HEADER + rows * ROW + PAD;
		if (bookmarks.isEmpty()) {
			height = PAD + HEADER + ROW + PAD;
		}

		width = 96;
		x = 2;
		int buttonRow = screen.height - 22;
		y = buttonRow - 2 - height;

		EmiScreenManager.SidebarPanel left = EmiScreenManager.getPanelFor(SidebarSide.LEFT);
		if (left != null && left.isVisible()) {
			Bounds bounds = left.getBounds();
			x = bounds.x();
			width = Math.max(bounds.width(), 80);
			int below = bounds.bottom() + 2;
			int aboveButtons = buttonRow - 2 - height;
			y = Math.min(aboveButtons, Math.max(2, below));
			if (y + height > buttonRow - 2) {
				y = aboveButtons;
			}
		}
		clampScroll();
	}

	public Bounds getBounds() {
		return new Bounds(x, y, width, height);
	}

	public void render(DrawContext context, int mouseX, int mouseY) {
		hoveredTooltip = null;
		Ui.fill(context, x, y, width, height, 0xD0121A24);
		Ui.border(context, x, y, width, height, 0xFF2E5A72);

		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		context.drawText(font, Ui.tr("morebookmarks.panel.header"), x + PAD, y + PAD, 0xFFE8C36A, false);

		List<String> bookmarks = ModBookmarkManager.getBookmarks();
		int rowY = y + PAD + HEADER;
		if (bookmarks.isEmpty()) {
			context.drawText(font, Ui.tr("morebookmarks.panel.empty"), x + PAD, rowY + 2, 0xFF667788, false);
			return;
		}

		int start = scroll;
		int end = Math.min(bookmarks.size(), start + maxVisible());
		for (int i = start; i < end; i++) {
			String bookmark = bookmarks.get(i);
			boolean hover = Ui.contains(x + 1, rowY, width - 2, ROW, mouseX, mouseY);
			if (hover) {
				Ui.fill(context, x + 1, rowY, width - 2, ROW, 0x553D8CFF);
				hoveredTooltip = bookmark;
			}
			drawEntry(context, font, bookmark, x + PAD, rowY + 2, width - PAD * 2);
			rowY += ROW;
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!contains(mouseX, mouseY)) {
			return false;
		}
		List<String> bookmarks = ModBookmarkManager.getBookmarks();
		int rowY = y + PAD + HEADER;
		int start = scroll;
		int end = Math.min(bookmarks.size(), start + maxVisible());
		for (int i = start; i < end; i++) {
			if (Ui.contains(x + 1, rowY, width - 2, ROW, mouseX, mouseY)) {
				if (button == 0) {
					ModBookmarkSearch.apply(bookmarks.get(i));
					Ui.click();
				} else if (button == 1) {
					ModBookmarkManager.remove(bookmarks.get(i));
					Ui.click();
					clampScroll();
				}
				return true;
			}
			rowY += ROW;
		}
		return true;
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!contains(mouseX, mouseY)) {
			return false;
		}
		int max = Math.max(0, ModBookmarkManager.getBookmarks().size() - maxVisible());
		scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(amount)));
		return true;
	}

	public boolean contains(double mouseX, double mouseY) {
		return Ui.contains(x, y, width, height, mouseX, mouseY);
	}

	public String getHoveredTooltip() {
		return hoveredTooltip;
	}

	private static int maxVisible() {
		return MoreBookmarksConfig.get().maxVisiblePanelRows;
	}

	private void clampScroll() {
		int max = Math.max(0, ModBookmarkManager.getBookmarks().size() - maxVisible());
		if (scroll > max) {
			scroll = max;
		}
		if (scroll < 0) {
			scroll = 0;
		}
	}

	private static void drawEntry(DrawContext context, TextRenderer font, String bookmark, int x, int y, int maxWidth) {
		String shown = font.trimToWidth(bookmark, maxWidth);
		if (!shown.equals(bookmark) && shown.length() > 1) {
			shown = shown.substring(0, shown.length() - 1) + "…";
		}
		if (shown.startsWith("@")) {
			context.drawText(font, "@", x, y, 0xFF5555FF, false);
			context.drawText(font, shown.substring(1), x + font.getWidth("@"), y, 0xFFD0D8E8, false);
		} else {
			context.drawText(font, shown, x, y, 0xFFD0D8E8, false);
		}
	}
}
