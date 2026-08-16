package dev.morebookmarks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import mezz.jei.gui.input.GuiTextFieldFilter;
import mezz.jei.gui.overlay.IngredientListOverlay;

@Mixin(IngredientListOverlay.class)
public interface JeiIngredientListOverlayAccessor {
	@Accessor(value = "searchField", remap = false)
	GuiTextFieldFilter morebookmarks$getSearchField();
}
