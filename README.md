# More Bookmarks

Client-side [EMI](https://github.com/emilyploszaj/emi) addon for Minecraft 1.21.1 (**Fabric** and **NeoForge**).

Save any EMI search (`iron`, `diamond sword`, `#logs`, `$tooltip`, `@mekanism`, …) as a bookmark. They show up in a dedicated section on the left of the item browser. Click one to put that query back in the search bar and show the filtered index.

JEI / REI port notes: [`docs/JEI_REI_NEOFORGE.md`](docs/JEI_REI_NEOFORGE.md).

## What you get

1. **Search-bar icon** — a small `@` button to the left of EMI’s search field. Tooltip: *Manage search bookmarks*.
2. **Popup menu**
   - *Add current search as bookmark* (enabled when the query is non-empty)
   - Saved bookmarks, each with an `x` to remove
   - *Clear all* (optional)
   - Click a row to apply that search and focus the EMI index
3. **Left panel** — *Search Bookmarks* list under EMI’s favorites sidebar (or above the EMI / recipe-tree buttons if the left sidebar is hidden). Left-click applies the search; right-click removes it. `@` entries keep a blue `@` prefix.
4. **Terminals** — EMI overlay + bookmarks also attach to standard container screens EMI would otherwise skip (empty-slot container screens and well-known AE2 / Ars / Create-style packages).
5. **Storage** — bookmarks in `config/emi-mod-bookmarks.json`; client options in `config/morebookmarks.json`.
6. **Config screen** — Cloth Config options (Fabric: Mod Menu; NeoForge: Mods list) to toggle the button, left panel, any-search vs `@`-only, panel rows, and Clear all.

Missing mods are fine: the bookmark stays, EMI just returns no hits.

## Config format

Bookmarks (`config/emi-mod-bookmarks.json`):

```json
{
	"modBookmarks": [
		"@azscompanions",
		"iron",
		"#logs",
		"@mekanism"
	]
}
```

Newest entries are stored first. Duplicates are ignored (case-insensitive); adding an existing query moves it to the top. See [`example/emi-mod-bookmarks.json`](example/emi-mod-bookmarks.json).

Options (`config/morebookmarks.json`): search-bar button, left panel, allow any search (default on), max visible panel rows, show Clear all.

## Code map

| Class | Role |
| --- | --- |
| `ModBookmarkManager` | Validate, de-dupe, load/save bookmark JSON |
| `MoreBookmarksConfig` | Client options |
| `ModBookmarkButton` | Search-bar `@` icon + tooltip |
| `ModBookmarkMenu` | Popup next to the search bar |
| `ModBookmarkPanelSection` | Left-side list under favorites |
| `ModBookmarkOverlay` | Layout, input, exclusion bounds |
| `MoreBookmarksEmiPlugin` | EMI exclusion areas so sidebars do not overlap our widgets |
| `EmiScreenBaseMixin` | Extra `HandledScreen` / terminal bounds so EMI attaches |
| `EmiScreenManagerMixin` | Hooks EMI’s overlay (EMI does not use vanilla screen children) |

EMI public API used: `EmiApi.getSearchText()` / `setSearchText()`, `EmiScreenManager.focusSearchSidebarType(INDEX)`, `EmiPlugin`, `EmiRegistry.addGenericExclusionArea`.

Mixin targets:

- `EmiScreenManager.addWidgets` — position the button after EMI places the search bar
- `EmiScreenManager.render` / `drawForeground` — draw button, panel, menu, tooltips
- `EmiScreenManager.mouseClicked` / `mouseScrolled` / `keyPressed` — handle our widgets first (Esc closes the menu)
- `EmiScreenBase.of` — supply bounds when EMI would skip a container screen

## Build

Requirements: **JDK 21** (not 25 — Loom 1.10 / Gradle 8.12 / ModDevGradle target Java 21). Git optional.

Set `JAVA_HOME` to a JDK 21 install before running Gradle, for example:

```bat
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Fabric

From the repo root:

```bat
gradlew.bat genSources
gradlew.bat build
```

The remapped jar is `build/libs/more-bookmarks-1.0.0.jar`. Drop it in `mods/` next to EMI and Fabric API. Cloth Config + Mod Menu are recommended for the in-game options screen.

Dev client:

```bat
gradlew.bat runClient
```

Loom pulls EMI from [TerraformersMC Maven](https://maven.terraformersmc.com/) (`dev.emi:emi-fabric:1.1.22+1.21.1`).

### NeoForge

The NeoForge port is a separate Gradle module under `neoforge/` (official Mojmap, ModDevGradle). It does not change the Fabric build. From the repo root:

```bat
gradlew.bat -p neoforge build
```

The playable jar is `neoforge/build/libs/more-bookmarks-neoforge-1.0.0.jar`. Drop it in `mods/` next to EMI (NeoForge) and NeoForge itself. Cloth Config is recommended so the Mods-list config screen appears.

Dev client:

```bat
gradlew.bat -p neoforge runClient
```

EMI comes from the same TerraformersMC Maven (`dev.emi:emi-neoforge:1.1.22+1.21.1`). NeoForge version is set in `neoforge/gradle.properties` (`neo_version`).

### In-game checks

1. Open inventory, a crafting table, a chest, or a terminal (AE2 / Ars / Create).
2. Type `iron` or `@minecraft` in EMI’s search bar.
3. Click the `@` button left of the search field → *Add current search as bookmark*.
4. Confirm the entry appears under **Search Bookmarks** on the left.
5. Clear the search, then click the bookmark — the bar should show the query and the index should filter.
6. Restart the game and confirm `config/emi-mod-bookmarks.json` reloads.
7. Remove one entry with the menu `x` or a right-click on the left list.
8. Open the config screen (Mod Menu on Fabric, Mods list on NeoForge) to toggle options.

## Versions

| Piece | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| Yarn (Fabric) | 1.21.1+build.3 |
| Fabric Loader | 0.16.14 |
| Fabric API | 0.115.6+1.21.1 |
| NeoForge | 21.1.248 |
| EMI | 1.1.22+1.21.1 (`emi-fabric` / `emi-neoforge`) |
| Cloth Config | 15.0.140 (recommended) |
| Mod Menu | 11.0.3 (Fabric, recommended) |

Bump `emi_version` in `gradle.properties` if you need a newer EMI build. The hooks above have been stable since EMI 1.1.18.
