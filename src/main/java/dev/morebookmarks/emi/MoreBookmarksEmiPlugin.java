package dev.morebookmarks.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;

/**
 * EMI plugin entrypoint. Registers exclusion rectangles so EMI's sidebars
 * and search bar do not cover the search-bookmark widgets.
 * Screen attachment for extra terminals is handled in
 * {@link dev.morebookmarks.mixin.EmiScreenBaseMixin} because EMI 1.1.22
 * has no public screen-bounds provider API.
 */
public class MoreBookmarksEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addGenericExclusionArea((screen, consumer) -> {
			for (Bounds bounds : ModBookmarkOverlay.INSTANCE.getExclusionBounds()) {
				if (bounds != null && !bounds.empty()) {
					consumer.accept(bounds);
				}
			}
		});
	}
}
