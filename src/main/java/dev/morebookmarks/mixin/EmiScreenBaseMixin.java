package dev.morebookmarks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.screen.EmiScreenBase;
import dev.morebookmarks.emi.TerminalScreens;
import net.minecraft.client.gui.screen.Screen;

/**
 * EMI only attaches to {@code HandledScreen}s that already have slots.
 * Empty-slot terminals and a few custom container screens still have a
 * background we can measure, so we supply bounds when EMI would skip them.
 */
@Mixin(EmiScreenBase.class)
public class EmiScreenBaseMixin {
	@Inject(method = "of", at = @At("RETURN"), cancellable = true, remap = false)
	private static void morebookmarks$extraScreens(Screen screen, CallbackInfoReturnable<EmiScreenBase> cir) {
		EmiScreenBase current = cir.getReturnValue();
		if (current != null && !current.isEmpty()) {
			return;
		}
		Bounds bounds = TerminalScreens.findBounds(screen);
		if (bounds != null && !bounds.empty()) {
			cir.setReturnValue(EmiScreenBaseInvoker.morebookmarks$create(screen, bounds));
		}
	}
}
