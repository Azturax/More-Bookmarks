package dev.morebookmarks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import dev.morebookmarks.emi.ModBookmarkOverlay;
import net.minecraft.client.gui.screen.Screen;

/**
 * Integration points into EMI's overlay, which is drawn and input-handled
 * outside of vanilla {@link Screen} children.
 *
 * <ul>
 *   <li>{@code addWidgets} — EMI has just positioned the search bar; we park
 *       the search-bookmark button beside it and lay out the left panel.</li>
 *   <li>{@code render} / {@code drawForeground} — draw the button, panel, menu,
 *       and tooltips on top of EMI chrome.</li>
 *   <li>{@code mouseClicked} / {@code mouseScrolled} / {@code keyPressed} —
 *       consume input for our widgets before EMI's favorites / search handle it.</li>
 * </ul>
 */
@Mixin(EmiScreenManager.class)
public class EmiScreenManagerMixin {
	@Inject(method = "addWidgets", at = @At("TAIL"))
	private static void morebookmarks$afterAddWidgets(Screen screen, CallbackInfo ci) {
		ModBookmarkOverlay.INSTANCE.afterAddWidgets(screen);
	}

	// remap = false: these are EMI methods that share names with vanilla Screen APIs.
	@Inject(method = "render(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("RETURN"), remap = false)
	private static void morebookmarks$render(EmiDrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ModBookmarkOverlay.INSTANCE.render(context.raw(), mouseX, mouseY, delta);
	}

	@Inject(method = "drawForeground(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V", at = @At("TAIL"), remap = false)
	private static void morebookmarks$drawForeground(EmiDrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ModBookmarkOverlay.INSTANCE.renderForeground(context.raw(), mouseX, mouseY);
	}

	@Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true, remap = false)
	private static void morebookmarks$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (ModBookmarkOverlay.INSTANCE.mouseClicked(mouseX, mouseY, button)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "mouseScrolled(DDD)Z", at = @At("HEAD"), cancellable = true, remap = false)
	private static void morebookmarks$mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
		if (ModBookmarkOverlay.INSTANCE.mouseScrolled(mouseX, mouseY, amount)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true, remap = false)
	private static void morebookmarks$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (ModBookmarkOverlay.INSTANCE.keyPressed(keyCode)) {
			cir.setReturnValue(true);
		}
	}
}
