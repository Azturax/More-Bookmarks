package dev.morebookmarks.jei;

import dev.morebookmarks.ui.BookmarkButton;
import dev.morebookmarks.ui.SearchAccess;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.screen.Screen;

/**
 * JEI search get/set plus search-field geometry for the bookmark button.
 * Fabric compiles against JEI's intermediary API only, so search-field
 * focus/position uses public overlay + {@link IGuiProperties}.
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
	}

	public static boolean positionButton(BookmarkButton button, Screen screen) {
		if (!isAvailable()) {
			return false;
		}
		IGuiProperties props = guiProperties(screen);
		if (props == null) {
			return false;
		}
		int listX = props.guiLeft() + props.guiXSize() + 4;
		button.setPosition(Math.max(2, listX - BookmarkButton.SIZE - 2), screen.height - 18);
		return true;
	}

	public static IGuiProperties guiProperties(Screen screen) {
		if (runtime == null) {
			return null;
		}
		return runtime.getScreenHelper().getGuiProperties(screen).orElse(null);
	}
}
