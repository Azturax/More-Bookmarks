package dev.morebookmarks.jei;

import dev.morebookmarks.mixin.JeiIngredientListOverlayAccessor;
import dev.morebookmarks.ui.BookmarkButton;
import dev.morebookmarks.ui.SearchAccess;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.input.GuiTextFieldFilter;
import net.minecraft.client.gui.screens.Screen;

/**
 * JEI search get/set plus search-field geometry for the bookmark button.
 */
public final class JeiSearchBridge implements SearchAccess {
	public static final JeiSearchBridge INSTANCE = new JeiSearchBridge();

	private static IJeiRuntime runtime;

	private JeiSearchBridge() {
	}

	public static void setRuntime(IJeiRuntime value) {
		runtime = value;
	}

	public static IJeiRuntime getRuntime() {
		return runtime;
	}

	public static boolean isAvailable() {
		return runtime != null && runtime.getIngredientListOverlay().isListDisplayed();
	}

	@Override
	public String current() {
		if (runtime == null) {
			return "";
		}
		return runtime.getIngredientFilter().getFilterText();
	}

	@Override
	public void apply(String query) {
		if (runtime == null) {
			return;
		}
		runtime.getIngredientFilter().setFilterText(query == null ? "" : query);
		GuiTextFieldFilter field = searchField();
		if (field != null) {
			field.setFocused(true);
		}
	}

	public static boolean positionButton(BookmarkButton button, Screen screen) {
		if (!isAvailable()) {
			return false;
		}
		GuiTextFieldFilter field = searchField();
		if (field != null && field.isVisible()) {
			button.setPosition(field.getX() - BookmarkButton.SIZE - 2, field.getY());
			return true;
		}
		IGuiProperties props = runtime.getScreenHelper().getGuiProperties(screen).orElse(null);
		if (props == null) {
			return false;
		}
		int listX = props.guiLeft() + props.guiXSize() + 4;
		button.setPosition(listX - BookmarkButton.SIZE - 2, screen.height - 18);
		return true;
	}

	public static IGuiProperties guiProperties(Screen screen) {
		if (runtime == null) {
			return null;
		}
		return runtime.getScreenHelper().getGuiProperties(screen).orElse(null);
	}

	private static GuiTextFieldFilter searchField() {
		if (runtime == null) {
			return null;
		}
		IIngredientListOverlay overlay = runtime.getIngredientListOverlay();
		if (overlay instanceof JeiIngredientListOverlayAccessor accessor) {
			try {
				return accessor.morebookmarks$getSearchField();
			} catch (Throwable ignored) {
				return null;
			}
		}
		return null;
	}
}
