package dev.morebookmarks.rei;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.favorites.FavoriteEntry;
import me.shedaniel.rei.api.client.favorites.FavoriteEntryType;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * REI favorite that stores a search query and applies it when clicked.
 */
public final class SearchBookmarkFavoriteEntry extends FavoriteEntry {
	public static final Identifier ID = Identifier.of("morebookmarks", "search");
	private static final String KEY = "query";

	private final String query;

	public SearchBookmarkFavoriteEntry(String query) {
		this.query = query == null ? "" : query;
	}

	public String query() {
		return query;
	}

	@Override
	public boolean isInvalid() {
		return query.isBlank();
	}

	@Override
	public Renderer getRenderer(boolean showcase) {
		return new Renderer() {
			@Override
			public void render(DrawContext graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
				var font = MinecraftClient.getInstance().textRenderer;
				String label = query.startsWith("@") ? query : "@";
				graphics.drawText(font, label, bounds.x + 1, bounds.y + Math.max(1, (bounds.height - 8) / 2), 0xFF88AAFF, false);
			}

			@Override
			public Tooltip getTooltip(me.shedaniel.rei.api.client.gui.widgets.TooltipContext context) {
				return Tooltip.create(Text.translatable("morebookmarks.tooltip.apply", query));
			}
		};
	}

	@Override
	public boolean doAction(int button) {
		if (button != 0 || isInvalid()) {
			return false;
		}
		ReiSearchBridge.INSTANCE.apply(query);
		return true;
	}

	@Override
	public long hashIgnoreAmount() {
		return query.toLowerCase().hashCode();
	}

	@Override
	public FavoriteEntry copy() {
		return new SearchBookmarkFavoriteEntry(query);
	}

	@Override
	public Identifier getType() {
		return ID;
	}

	@Override
	public boolean isSame(FavoriteEntry other) {
		return other instanceof SearchBookmarkFavoriteEntry that && query.equalsIgnoreCase(that.query);
	}

	public enum Type implements FavoriteEntryType<SearchBookmarkFavoriteEntry> {
		INSTANCE;

		@Override
		public DataResult<SearchBookmarkFavoriteEntry> read(NbtCompound object) {
			String value = object.getString(KEY);
			if (value == null || value.isBlank()) {
				return DataResult.error(() -> "Missing search bookmark query");
			}
			return DataResult.success(new SearchBookmarkFavoriteEntry(value), Lifecycle.stable());
		}

		@Override
		public DataResult<SearchBookmarkFavoriteEntry> fromArgs(Object... args) {
			if (args.length == 0 || !(args[0] instanceof String value) || value.isBlank()) {
				return DataResult.error(() -> "Search bookmark expects a query string");
			}
			return DataResult.success(new SearchBookmarkFavoriteEntry(value), Lifecycle.stable());
		}

		@Override
		public NbtCompound save(SearchBookmarkFavoriteEntry entry, NbtCompound tag) {
			tag.putString(KEY, entry.query);
			return tag;
		}
	}
}
