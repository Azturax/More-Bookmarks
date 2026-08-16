package dev.morebookmarks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.screen.EmiScreenBase;
import net.minecraft.client.gui.screens.Screen;

@Mixin(EmiScreenBase.class)
public interface EmiScreenBaseInvoker {
	@Invoker(value = "<init>", remap = false)
	static EmiScreenBase morebookmarks$create(Screen screen, Bounds bounds) {
		throw new AssertionError();
	}
}
