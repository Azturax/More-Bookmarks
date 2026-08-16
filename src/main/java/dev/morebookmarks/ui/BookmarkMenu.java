package dev.morebookmarks.ui;

import java.util.List;

import dev.morebookmarks.bookmark.ModBookmarkManager;
import dev.morebookmarks.config.MoreBookmarksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Popup anchored to {@link BookmarkButton}: add the current search,
 * list saved bookmarks with remove buttons, and optional clear-all.
 */
public final class BookmarkMenu {
	private static final int WIDTH = 176;
	private static final int ROW = 14;
	private static final int PAD = 4;
	private static final int MAX_VISIBLE = 8;

	private final SearchAccess search;
	private int x;
	private int y;
	private int height;
	private int scroll;
	private String hoveredTooltip;

	public BookmarkMenu(SearchAccess search) {
		this.search = search;
	}

	public void layout(int buttonX, int buttonY, int screenWidth, int screenHeight) {
		int list = visibleCount() * ROW;
		boolean clear = showClearAll();
		height = PAD + ROW + 2 + list + (clear ? ROW + 2 : 0) + PAD;
		x = Math.min(buttonX, screenWidth - WIDTH - 2);
		x = Math.max(2, x);
		y = buttonY - 2 - height;
		if (y < 2) {
			y = buttonY + BookmarkButton.SIZE + 2;
		}
		clampScroll();
	}

	public IntRect getBounds() {
		return new IntRect(x, y, WIDTH, height);
	}

	public void render(DrawContext context, int mouseX, int mouseY) {
		hoveredTooltip = null;
		Ui.fill(context, x, y, WIDTH, height, 0xF010141C);
		Ui.border(context, x, y, WIDTH, height, 0xFF3D6B8A);

		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int rowY = y + PAD;

		boolean canAdd = ModBookmarkManager.canBookmark(search.current());
		boolean addHover = Ui.contains(x + PAD, rowY, WIDTH - PAD * 2, ROW, mouseX, mouseY);
		drawRow(context, font, x + PAD, rowY, WIDTH - PAD * 2, addHover && canAdd,
				canAdd ? 0xFFB8E0A8 : 0xFF666666,
				Ui.tr("morebookmarks.menu.add"));
		if (addHover) {
			hoveredTooltip = canAdd
					? "morebookmarks.tooltip.add"
					: addDisabledTooltip();
		}
		rowY += ROW + 2;
		Ui.fill(context, x + PAD, rowY - 1, WIDTH - PAD * 2, 1, 0xFF2A3A4A);

		List<String> bookmarks = ModBookmarkManager.getBookmarks();
		int start = scroll;
		int end = Math.min(bookmarks.size(), start + MAX_VISIBLE);
		for (int i = start; i < end; i++) {
			String bookmark = bookmarks.get(i);
			boolean rowHover = Ui.contains(x + PAD, rowY, WIDTH - PAD * 2 - 12, ROW, mouseX, mouseY);
			boolean removeHover = Ui.contains(x + WIDTH - PAD - 12, rowY, 12, ROW, mouseX, mouseY);
			if (rowHover) {
				Ui.fill(context, x + PAD, rowY, WIDTH - PAD * 2 - 12, ROW, 0x663D8CFF);
			}
			drawBookmarkLabel(context, font, bookmark, x + PAD + 2, rowY + 3, WIDTH - PAD * 2 - 18);
			int removeColor = removeHover ? 0xFFFF6666 : 0xFFCC8888;
			context.drawText(font, "x", x + WIDTH - PAD - 9, rowY + 3, removeColor, false);
			if (rowHover) {
				hoveredTooltip = bookmark;
			} else if (removeHover) {
				hoveredTooltip = "morebookmarks.tooltip.remove";
			}
			rowY += ROW;
		}

		if (showClearAll()) {
			Ui.fill(context, x + PAD, rowY, WIDTH - PAD * 2, 1, 0xFF2A3A4A);
			rowY += 2;
			boolean clearHover = Ui.contains(x + PAD, rowY, WIDTH - PAD * 2, ROW, mouseX, mouseY);
			drawRow(context, font, x + PAD, rowY, WIDTH - PAD * 2, clearHover, 0xFFFFAAAA,
					Ui.tr("morebookmarks.menu.clear"));
			if (clearHover) {
				hoveredTooltip = "morebookmarks.tooltip.clear";
			}
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button, Runnable closeMenu) {
		if (!contains(mouseX, mouseY)) {
			return false;
		}
		if (button != 0) {
			return true;
		}

		int rowY = y + PAD;
		if (Ui.contains(x + PAD, rowY, WIDTH - PAD * 2, ROW, mouseX, mouseY)) {
			if (ModBookmarkManager.add(search.current())) {
				Ui.click();
			}
			return true;
		}
		rowY += ROW + 2;

		List<String> bookmarks = ModBookmarkManager.getBookmarks();
		int start = scroll;
		int end = Math.min(bookmarks.size(), start + MAX_VISIBLE);
		for (int i = start; i < end; i++) {
			String bookmark = bookmarks.get(i);
			if (Ui.contains(x + WIDTH - PAD - 12, rowY, 12, ROW, mouseX, mouseY)) {
				ModBookmarkManager.remove(bookmark);
				Ui.click();
				clampScroll();
				return true;
			}
			if (Ui.contains(x + PAD, rowY, WIDTH - PAD * 2 - 12, ROW, mouseX, mouseY)) {
				search.apply(bookmark);
				Ui.click();
				closeMenu.run();
				return true;
			}
			rowY += ROW;
		}

		if (showClearAll()) {
			rowY += 2;
			if (Ui.contains(x + PAD, rowY, WIDTH - PAD * 2, ROW, mouseX, mouseY)) {
				ModBookmarkManager.clear();
				Ui.click();
				scroll = 0;
				return true;
			}
		}
		return true;
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!contains(mouseX, mouseY)) {
			return false;
		}
		int max = Math.max(0, ModBookmarkManager.getBookmarks().size() - MAX_VISIBLE);
		scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(amount)));
		return true;
	}

	public boolean contains(double mouseX, double mouseY) {
		return Ui.contains(x, y, WIDTH, height, mouseX, mouseY);
	}

	public String getHoveredTooltip() {
		return hoveredTooltip;
	}

	private String addDisabledTooltip() {
		if (!ModBookmarkManager.isValid(search.current())) {
			return "morebookmarks.tooltip.add.disabled";
		}
		return "morebookmarks.tooltip.add.disabled.at";
	}

	private static boolean showClearAll() {
		return MoreBookmarksConfig.get().showClearAll && !ModBookmarkManager.getBookmarks().isEmpty();
	}

	private int visibleCount() {
		return Math.min(MAX_VISIBLE, ModBookmarkManager.getBookmarks().size());
	}

	private void clampScroll() {
		int max = Math.max(0, ModBookmarkManager.getBookmarks().size() - MAX_VISIBLE);
		if (scroll > max) {
			scroll = max;
		}
		if (scroll < 0) {
			scroll = 0;
		}
	}

	private static void drawRow(DrawContext context, TextRenderer font, int x, int y, int width, boolean hover, int color, Text text) {
		if (hover) {
			Ui.fill(context, x, y, width, ROW, 0x663D8CFF);
		}
		context.drawText(font, text, x + 2, y + 3, color, false);
	}

	private static void drawBookmarkLabel(DrawContext context, TextRenderer font, String bookmark, int x, int y, int maxWidth) {
		String shown = font.trimToWidth(bookmark, maxWidth);
		if (!shown.equals(bookmark) && shown.length() > 1) {
			shown = shown.substring(0, shown.length() - 1) + "…";
		}
		int atColor = 0xFF5555FF;
		int restColor = 0xFFE8EEF8;
		if (shown.startsWith("@")) {
			context.drawText(font, "@", x, y, atColor, false);
			context.drawText(font, shown.substring(1), x + font.getWidth("@"), y, restColor, false);
		} else {
			context.drawText(font, shown, x, y, restColor, false);
		}
	}
}
