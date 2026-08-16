# Adapting search bookmarks to JEI, REI, and NeoForge

EMI is implemented in this repo. The same feature maps cleanly onto JEI and REI
because all three expose a search string and a bookmark / favorite overlay.

## Shared pieces (keep as-is)

Reuse these classes unchanged:

- `dev.morebookmarks.bookmark.ModBookmarkManager`
- The JSON shape `{ "modBookmarks": ["@azscompanions", "iron", ...] }`

Swap only the config file name (`jei-mod-bookmarks.json` / `rei-mod-bookmarks.json`)
and the UI glue.

---

## JEI 1.21.1 (Forge / NeoForge)

JEI does not have a public “add sidebar section” API. Hook the search field and
the bookmark overlay with a plugin plus a small mixin.

### Plugin

```java
@JeiPlugin
public class MoreBookmarksJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("morebookmarks", "jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        JeiSearchBridge.setRuntime(runtime);
    }
}
```

### Search

| Need | JEI API |
| --- | --- |
| Read search | `runtime.getIngredientFilter().getFilterText()` |
| Write search | `runtime.getIngredientFilter().setFilterText("@mekanism")` |
| Focus search | mixin or accessor on `GuiTextFieldFilter` / `IngredientFilter` |

`@modid` is already a JEI prefix (`IIngredientFilter` / `FilterTextManager`).

### Search-bar button

JEI renders the search box in `mezz.jei.gui.overlay.IngredientListOverlay`
(internal). Mixin `updateBounds` / `drawScreen` / `handleMouseClicked` the same
way this repo mixins `EmiScreenManager`.

Alternative without mixins: `IGuiHandler` + a `ScreenEvent` overlay button
positioned from `IIngredientListOverlay.getBounds()`.

### Bookmark panel

JEI’s left bookmark overlay is `mezz.jei.gui.overlay.bookmarks.BookmarkOverlay`.

Options:

1. **Mixin** `BookmarkOverlay.drawScreen` and append a text section under the
   item grid (closest to this EMI implementation).
2. **Custom `IIngredient`** entries added through JEI’s bookmark list — worse UX
   because they render as item slots, not `@mod` labels.

There is no public `IBookmarkOverlay.addSection` API as of JEI 19.x.

### Key classes

- `mezz.jei.api.runtime.IJeiRuntime`
- `mezz.jei.api.runtime.IIngredientFilter`
- `mezz.jei.api.runtime.IIngredientListOverlay`
- `mezz.jei.api.runtime.IBookmarkOverlay`
- `mezz.jei.gui.overlay.IngredientListOverlay` (mixin target)
- `mezz.jei.gui.overlay.bookmarks.BookmarkOverlay` (mixin target)
- `mezz.jei.gui.input.GuiTextFieldFilter` (search widget)

---

## REI 1.21.1 (Fabric / NeoForge)

REI is the friendliest of the three: favorites are an extension API.

### Custom favorite type

Register a `FavoriteEntryType` whose entries store the `@mod` query string.
They then appear in REI’s existing favorites panel with your renderer
(`@` prefix, tooltip = full query). Click calls:

```java
REIRuntime.getInstance().getSearchTextField().setText(query);
REIRuntime.getInstance().getSearchTextField().setFocused(true);
```

```java
public class ModBookmarkFavoriteEntry extends FavoriteEntry {
    private final String query;

    @Override
    public void doAction(int button) {
        REIRuntime.getInstance().getSearchTextField().setText(query);
    }
}
```

Register with `FavoriteEntryType.registry().register(id, serializer)`.

### Search-bar button

REI’s search field is `me.shedaniel.rei.api.client.gui.widgets.TextField`
from `REIRuntime.getInstance().getSearchTextField()`.

Add a widget via `OverlayRenderer` / `ScreenRegistry.registerDecider`, or mixin
`me.shedaniel.rei.impl.client.gui.widget.EntryWidget` / the overlay search row
(`DefaultDisplayChooserWidget` / `OverlaySearchField` in REI internals).

### Key classes

- `me.shedaniel.rei.api.client.REIRuntime`
- `me.shedaniel.rei.api.client.favorites.FavoriteEntry`
- `me.shedaniel.rei.api.client.favorites.FavoriteEntryType`
- `me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry`
- `me.shedaniel.rei.api.client.gui.widgets.TextField`
- `me.shedaniel.rei.api.client.registry.screen.OverlayRenderer`
- `me.shedaniel.rei.impl.client.gui.widget.search.OverlaySearchField` (mixin if needed)

REI already treats `@` as a namespace / mod filter in its default search
syntax, so applying the bookmarked string is enough.

---

## NeoForge port of this EMI addon

EMI’s own classes (`dev.emi.emi.*`) are **not remapped**. The Fabric mixins in
`EmiScreenManagerMixin` can be reused as-is.

Changes:

1. Dependency: `dev.emi:emi-neoforge:${emi_version}` from TerraformersMC Maven.
2. Annotate the plugin with `@dev.emi.emi.api.EmiEntrypoint` instead of the
   Fabric `emi` entrypoint.
3. Client init: `FMLClientSetupEvent` (or a `@Mod` constructor on the physical
   client) calling `ModBookmarkManager.init(FMLPaths.CONFIGDIR.get().resolve("emi-mod-bookmarks.json"))`.
4. `META-INF/neoforge.mods.toml`:

```toml
modLoader = "javafml"
loaderVersion = "[4,)"
license = "MIT"

[[mods]]
modId = "morebookmarks"
version = "${version}"
displayName = "More Bookmarks"
description = '''Save EMI searches as bookmarks.'''

[[dependencies.morebookmarks]]
modId = "neoforge"
type = "required"
versionRange = "[21.1,)"
side = "CLIENT"

[[dependencies.morebookmarks]]
modId = "emi"
type = "required"
versionRange = "[1.1.18,)"
side = "CLIENT"
```

5. Mixin config is identical; list it under `[[mixins]]` in the NeoForge mods
   toml (or `morebookmarks.mixins.json` next to the classes).
