package dev.morebookmarks.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.morebookmarks.MoreBookmarks;

/**
 * Client options persisted in {@code config/morebookmarks.json}.
 * Bookmark strings stay in {@code config/emi-mod-bookmarks.json}.
 */
public final class MoreBookmarksConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final int MIN_PANEL_ROWS = 1;
	private static final int MAX_PANEL_ROWS = 16;

	private static Path file;
	private static Data data = new Data();

	private MoreBookmarksConfig() {
	}

	public static void init(Path configFile) {
		file = Objects.requireNonNull(configFile, "configFile");
		load();
	}

	public static Data get() {
		return data;
	}

	public static void load() {
		if (file == null || !Files.isRegularFile(file)) {
			data = new Data();
			return;
		}
		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			Data parsed = GSON.fromJson(reader, Data.class);
			data = parsed == null ? new Data() : parsed;
			data.clamp();
		} catch (Exception e) {
			MoreBookmarks.LOGGER.warn("Failed to read {}", file, e);
			data = new Data();
		}
	}

	public static void save() {
		if (file == null) {
			return;
		}
		data.clamp();
		try {
			Path parent = file.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			MoreBookmarks.LOGGER.warn("Failed to write {}", file, e);
		}
	}

	public static final class Data {
		public boolean showSearchButton = true;
		public boolean showLeftPanel = true;
		public boolean allowAnySearch = true;
		public int maxVisiblePanelRows = 6;
		public boolean showClearAll = true;

		void clamp() {
			if (maxVisiblePanelRows < MIN_PANEL_ROWS) {
				maxVisiblePanelRows = MIN_PANEL_ROWS;
			}
			if (maxVisiblePanelRows > MAX_PANEL_ROWS) {
				maxVisiblePanelRows = MAX_PANEL_ROWS;
			}
		}
	}
}
