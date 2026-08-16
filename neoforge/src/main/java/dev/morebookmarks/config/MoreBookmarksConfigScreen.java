package dev.morebookmarks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MoreBookmarksConfigScreen {
	private MoreBookmarksConfigScreen() {
	}

	public static Screen create(Screen parent) {
		MoreBookmarksConfig.Data d = MoreBookmarksConfig.get();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("morebookmarks.config.title"))
				.setSavingRunnable(MoreBookmarksConfig::save);
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("morebookmarks.config.category.general"));
		ConfigEntryBuilder entry = builder.entryBuilder();

		general.addEntry(entry.startBooleanToggle(Component.translatable("morebookmarks.config.show_search_button"), d.showSearchButton)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("morebookmarks.config.show_search_button.tooltip"))
				.setSaveConsumer(v -> MoreBookmarksConfig.get().showSearchButton = v)
				.build());
		general.addEntry(entry.startBooleanToggle(Component.translatable("morebookmarks.config.show_left_panel"), d.showLeftPanel)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("morebookmarks.config.show_left_panel.tooltip"))
				.setSaveConsumer(v -> MoreBookmarksConfig.get().showLeftPanel = v)
				.build());
		general.addEntry(entry.startBooleanToggle(Component.translatable("morebookmarks.config.allow_any_search"), d.allowAnySearch)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("morebookmarks.config.allow_any_search.tooltip"))
				.setSaveConsumer(v -> MoreBookmarksConfig.get().allowAnySearch = v)
				.build());
		general.addEntry(entry.startIntSlider(Component.translatable("morebookmarks.config.panel_rows"), d.maxVisiblePanelRows, 1, 16)
				.setDefaultValue(6)
				.setTooltip(Component.translatable("morebookmarks.config.panel_rows.tooltip"))
				.setSaveConsumer(v -> MoreBookmarksConfig.get().maxVisiblePanelRows = v)
				.build());
		general.addEntry(entry.startBooleanToggle(Component.translatable("morebookmarks.config.show_clear_all"), d.showClearAll)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("morebookmarks.config.show_clear_all.tooltip"))
				.setSaveConsumer(v -> MoreBookmarksConfig.get().showClearAll = v)
				.build());

		return builder.build();
	}
}
