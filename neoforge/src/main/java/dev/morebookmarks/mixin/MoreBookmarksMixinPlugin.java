package dev.morebookmarks.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.neoforged.fml.loading.LoadingModList;

/**
 * Applies viewer mixins only when that viewer is loaded so EMI/JEI/REI
 * can each be missing at runtime.
 */
public class MoreBookmarksMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		String target = targetClassName.replace('/', '.');
		String name = mixinClassName.substring(mixinClassName.replace('/', '.').lastIndexOf('.') + 1);
		if (target.startsWith("dev.emi.emi") || name.startsWith("Emi")) {
			return isLoaded("emi");
		}
		if (target.startsWith("mezz.jei") || name.startsWith("Jei")) {
			return isLoaded("jei");
		}
		if (target.startsWith("me.shedaniel.rei") || name.startsWith("Rei")) {
			return isLoaded("roughlyenoughitems");
		}
		return true;
	}

	private static boolean isLoaded(String modId) {
		return LoadingModList.get().getModFileById(modId) != null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
