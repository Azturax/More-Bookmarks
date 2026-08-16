package dev.morebookmarks.bookmark;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.morebookmarks.MoreBookmarksClient;
import dev.morebookmarks.config.MoreBookmarksConfig;

/**
 * Load/save, validate, and mutate the search bookmark list.
 * Most recently added entries are stored at the front of the list.
 * {@code @} queries remain supported; they are one kind of saved search.
 */
public final class ModBookmarkManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final String KEY = "modBookmarks";

	private static final List<String> BOOKMARKS = new ArrayList<>();
	private static final List<Runnable> LISTENERS = new ArrayList<>();
	private static Path configFile;

	private ModBookmarkManager() {
	}

	public static void init(Path file) {
		configFile = Objects.requireNonNull(file, "configFile");
		load();
	}

	public static List<String> getBookmarks() {
		return Collections.unmodifiableList(BOOKMARKS);
	}

	public static void addChangeListener(Runnable listener) {
		LISTENERS.add(listener);
	}

	/**
	 * A stored bookmark is valid when it is non-empty after trim.
	 * Used when loading so existing {@code @} and free-text entries both survive.
	 */
	public static boolean isValid(String raw) {
		return raw != null && !raw.trim().isEmpty();
	}

	/**
	 * Whether the current search may be saved. Honors the {@code allowAnySearch}
	 * config: when false, only {@code @} queries with a non-blank suffix qualify.
	 */
	public static boolean canBookmark(String raw) {
		if (!isValid(raw)) {
			return false;
		}
		if (MoreBookmarksConfig.get().allowAnySearch) {
			return true;
		}
		String trimmed = raw.trim();
		return trimmed.startsWith("@") && trimmed.length() > 1 && !trimmed.substring(1).isBlank();
	}

	public static String normalize(String raw) {
		return raw == null ? "" : raw.trim();
	}

	/**
	 * @return {@code true} if the list changed (added or moved to top)
	 */
	public static boolean add(String raw) {
		if (!canBookmark(raw)) {
			return false;
		}
		String value = normalize(raw);
		String key = value.toLowerCase(Locale.ROOT);
		int existing = indexOfIgnoreCase(key);
		if (existing == 0) {
			return false;
		}
		if (existing > 0) {
			BOOKMARKS.remove(existing);
		}
		BOOKMARKS.add(0, value);
		persist();
		return true;
	}

	public static boolean remove(String raw) {
		if (raw == null) {
			return false;
		}
		int index = indexOfIgnoreCase(normalize(raw).toLowerCase(Locale.ROOT));
		if (index < 0) {
			return false;
		}
		BOOKMARKS.remove(index);
		persist();
		return true;
	}

	public static boolean contains(String raw) {
		if (raw == null) {
			return false;
		}
		return indexOfIgnoreCase(normalize(raw).toLowerCase(Locale.ROOT)) >= 0;
	}

	public static void clear() {
		if (BOOKMARKS.isEmpty()) {
			return;
		}
		BOOKMARKS.clear();
		persist();
	}

	public static void load() {
		BOOKMARKS.clear();
		if (configFile == null || !Files.isRegularFile(configFile)) {
			return;
		}
		try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);
			if (root == null || !root.has(KEY) || !root.get(KEY).isJsonArray()) {
				return;
			}
			JsonArray array = root.getAsJsonArray(KEY);
			for (JsonElement element : array) {
				if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
					continue;
				}
				String value = normalize(element.getAsString());
				if (!isValid(value) || contains(value)) {
					continue;
				}
				BOOKMARKS.add(value);
			}
		} catch (Exception e) {
			MoreBookmarksClient.LOGGER.warn("Failed to read {}", configFile, e);
		}
	}

	public static void save() {
		if (configFile == null) {
			return;
		}
		JsonObject root = new JsonObject();
		JsonArray array = new JsonArray();
		for (String bookmark : BOOKMARKS) {
			array.add(bookmark);
		}
		root.add(KEY, array);
		try {
			Path parent = configFile.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			try (Writer writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
				GSON.toJson(root, writer);
			}
		} catch (IOException e) {
			MoreBookmarksClient.LOGGER.warn("Failed to write {}", configFile, e);
		}
	}

	private static void persist() {
		save();
		for (Runnable listener : LISTENERS) {
			try {
				listener.run();
			} catch (Exception e) {
				MoreBookmarksClient.LOGGER.warn("Bookmark listener failed", e);
			}
		}
	}

	private static int indexOfIgnoreCase(String key) {
		for (int i = 0; i < BOOKMARKS.size(); i++) {
			if (BOOKMARKS.get(i).toLowerCase(Locale.ROOT).equals(key)) {
				return i;
			}
		}
		return -1;
	}
}
