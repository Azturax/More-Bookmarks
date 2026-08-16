# Adapting search bookmarks to JEI, REI, and NeoForge

EMI, JEI, and REI are all implemented. The same saved-search list is shared:

- File: `config/morebookmarks-searches.json`
- Shape: `{ "modBookmarks": ["@azscompanions", "iron", ...] }`
- Legacy `config/emi-mod-bookmarks.json` is migrated once if the shared file is missing

`dev.morebookmarks.bookmark.ModBookmarkManager` is unchanged aside from that shared-file init.

EMI, JEI, and REI are **optional** soft-depends. Mixin plugin `MoreBookmarksMixinPlugin` applies only the mixins for viewers that are loaded. With none of the three present, the mod no-ops.

---

## JEI 1.21.1 (Fabric / NeoForge) — shipped

Compile against `mezz.jei:jei-1.21.1-fabric` / `jei-1.21.1-neoforge` **19.21.2.313**
from `https://maven.blamejared.com/`. Soft-depend `jei`.

| Need | What shipped |
| --- | --- |
| Plugin | `@JeiPlugin` `MoreBookmarksJeiPlugin` + Fabric `jei_mod_plugin` entrypoint |
| Read/write search | `JeiSearchBridge` → `IIngredientFilter.getFilterText()` / `setFilterText()` |
| Focus search | NeoForge: `GuiTextFieldFilter.setFocused(true)` via accessor. Fabric: `setFilterText` only (Yarn cannot compile against JEI’s official-mapped impl jar) |
| Search-bar button | Screen events (Fabric `ScreenEvents` / NeoForge `ScreenEvent`). NeoForge parks it on the real search field; Fabric estimates from `IGuiProperties` |
| Left panel | Custom list on the left; registered as `IGlobalGuiHandler` extra areas so JEI yields space |
| Terminals | Uses JEI’s own overlay; no extra screen-bounds work |

Internals used on NeoForge: `mezz.jei.gui.overlay.IngredientListOverlay.searchField` (accessor mixin).
Fabric compiles against `jei-1.21.1-common-api-intermediary` only — the official-mapped full JEI jar
duplicates Mojmap types (`EditBox`, `ResourceLocation`) onto a Yarn classpath.
Public API has no `IIngredientListOverlay.getBounds()` on 19.21.x.

### Leftover limitations

- No public “add sidebar section” API; the left list is our widget, not a JEI bookmark-grid section.
- Fabric JEI button is placed from `IGuiProperties` (left of the ingredient list, near the bottom), not the exact `GuiTextFieldFilter` box. Centered search bar is not tracked on Fabric.
- Fabric JEI apply sets filter text but does not programmatically focus the search widget.
- JEI bookmark item grid and our text list can sit in the same left strip; extra areas shrink JEI’s grid.
- `runClient` does not put JEI on the runtime classpath (EMI is the default dev viewer).

---

## REI 1.21.1 (Fabric / NeoForge) — shipped

Compile against `me.shedaniel:RoughlyEnoughItems-fabric` / `RoughlyEnoughItems-neoforge` **16.0.799**
from `https://maven.shedaniel.me/`. Soft-depend `roughlyenoughitems`.

| Need | What shipped |
| --- | --- |
| Plugin | `MoreBookmarksReiClientPlugin` (`rei_client` on Fabric, `@REIPluginClient` on NeoForge) |
| Read/write/focus search | `ReiSearchBridge` → `REIRuntime.getSearchTextField()` |
| Search-bar button + menu + left list | Same HUD as JEI, placed from the search field’s `WidgetWithBounds` (or overlay bounds) |
| Favorites type | `SearchBookmarkFavoriteEntry` / `FavoriteEntryType`; click applies the query |
| Exclusion | `ExclusionZones` so REI’s lists do not cover our widgets |
| Terminals | Uses REI’s own overlay |

### Leftover limitations

- Favorite-type section is populated at REI plugin reload from the current list; later add/remove in our HUD does not rewrite REI’s favorites picker until a reload.
- If the search field is not a `WidgetWithBounds`, the button uses the overlay rectangle instead of the exact text box.
- `runClient` does not put REI on the runtime classpath (EMI is the default dev viewer).

---

## NeoForge port of the EMI addon

**Implemented** in `neoforge/` (ModDevGradle + official Mojmap). Build with
`gradlew.bat -p neoforge build`. The playable jar is
`neoforge/build/libs/more-bookmarks-neoforge-1.0.0.jar`.

EMI’s own classes (`dev.emi.emi.*`) are **not remapped**. Mixin targets on
`EmiScreenManager` are the same; Minecraft type names in those mixins use
Mojmap (`Screen`, `GuiGraphics`, `AbstractContainerScreen`).

EMI is optional (`type = "optional"` in `neoforge.mods.toml`). The mixin plugin
skips EMI mixins when EMI is absent.

---

## Shared pieces

Reuse unchanged:

- `dev.morebookmarks.bookmark.ModBookmarkManager` (plus `initShared`)
- JSON key `modBookmarks`

JEI/REI UI lives in `dev.morebookmarks.ui` (`BookmarkHud`, `BookmarkButton`,
`BookmarkMenu`, `BookmarkPanel`) so the two viewers share chrome. EMI still uses
its original `dev.morebookmarks.emi.*` widgets so that path stays untouched.
