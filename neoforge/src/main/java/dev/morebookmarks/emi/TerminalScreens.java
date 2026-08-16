package dev.morebookmarks.emi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.mixin.accessor.HandledScreenAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;

/**
 * EMI 1.1.22 has no public screen-bounds provider API. These helpers recover
 * container bounds for {@link AbstractContainerScreen}s EMI skips (empty slot lists)
 * and a few well-known terminal packages that use custom Screen subclasses.
 */
public final class TerminalScreens {
	private static final String[] KNOWN_PREFIXES = {
			"appeng.",
			"com.glodblock.",
			"com.hollingsworth.arsnouveau.",
			"com.simibubi.create.",
			"com.refinedmods.",
			"mekanism.",
			"vazkii.botania.",
			"slimeknights.tconstruct.",
			"aztech.modern_industrialization."
	};

	private TerminalScreens() {
	}

	public static Bounds findBounds(Screen screen) {
		if (screen instanceof AbstractContainerScreen<?> handled) {
			return fromHandled(handled);
		}
		if (isKnownTerminal(screen)) {
			return fromReflection(screen);
		}
		return null;
	}

	private static boolean isKnownTerminal(Screen screen) {
		String name = screen.getClass().getName();
		for (String prefix : KNOWN_PREFIXES) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	private static Bounds fromHandled(AbstractContainerScreen<?> screen) {
		HandledScreenAccessor access = (HandledScreenAccessor) screen;
		int extra = 0;
		if (screen instanceof RecipeUpdateListener provider && provider.getRecipeBookComponent().isVisible()) {
			extra = 177;
		}
		int width = access.getBackgroundWidth() + extra;
		int height = access.getBackgroundHeight();
		if (width <= 0 || height <= 0) {
			return null;
		}
		return new Bounds(access.getX() - extra, access.getY(), width, height);
	}

	private static Bounds fromReflection(Screen screen) {
		Integer x = firstInt(screen, "getX", "getGuiLeft");
		Integer y = firstInt(screen, "getY", "getGuiTop");
		Integer width = firstInt(screen, "getBackgroundWidth", "getXSize");
		Integer height = firstInt(screen, "getBackgroundHeight", "getYSize");
		if (x == null) {
			x = firstField(screen, "x", "guiLeft", "leftPos");
		}
		if (y == null) {
			y = firstField(screen, "y", "guiTop", "topPos");
		}
		if (width == null) {
			width = firstField(screen, "backgroundWidth", "xSize", "imageWidth");
		}
		if (height == null) {
			height = firstField(screen, "backgroundHeight", "ySize", "imageHeight");
		}
		if (x == null || y == null || width == null || height == null || width <= 0 || height <= 0) {
			return null;
		}
		return new Bounds(x, y, width, height);
	}

	private static Integer firstInt(Object target, String... methods) {
		Class<?> type = target.getClass();
		for (String name : methods) {
			try {
				Method method = type.getMethod(name);
				Object value = method.invoke(target);
				if (value instanceof Number number) {
					return number.intValue();
				}
			} catch (ReflectiveOperationException ignored) {
			}
		}
		return null;
	}

	private static Integer firstField(Object target, String... names) {
		Class<?> type = target.getClass();
		while (type != null && type != Object.class) {
			for (String name : names) {
				try {
					Field field = type.getDeclaredField(name);
					field.setAccessible(true);
					Object value = field.get(target);
					if (value instanceof Number number) {
						return number.intValue();
					}
				} catch (ReflectiveOperationException ignored) {
				}
			}
			type = type.getSuperclass();
		}
		return null;
	}
}
