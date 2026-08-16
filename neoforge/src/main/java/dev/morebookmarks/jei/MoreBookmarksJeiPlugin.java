package dev.morebookmarks.jei;

import java.util.Collection;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class MoreBookmarksJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath("morebookmarks", "jei");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		JeiSearchBridge.setRuntime(jeiRuntime);
	}

	@Override
	public void onRuntimeUnavailable() {
		JeiSearchBridge.setRuntime(null);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGlobalGuiHandler(new IGlobalGuiHandler() {
			@Override
			public Collection<Rect2i> getGuiExtraAreas() {
				return JeiBookmarkOverlay.INSTANCE.getExclusionRects();
			}
		});
	}
}
