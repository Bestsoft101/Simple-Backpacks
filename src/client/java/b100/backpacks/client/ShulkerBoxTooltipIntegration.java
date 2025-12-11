package b100.backpacks.client;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;

import b100.backpacks.BackpackMod;

public class ShulkerBoxTooltipIntegration implements ShulkerBoxTooltipApi {

	@Override
	public void registerProviders(PreviewProviderRegistry registry) {
		registry.register(BackpackMod.id("backpack"), new BackpackPreviewProvider(BackpackMod.BACKPACK_ROWS * 9, false), BackpackMod.BACKPACK);
	}
	
	public static class BackpackPreviewProvider extends BlockEntityPreviewProvider {

		public BackpackPreviewProvider(int defaultMaxInvSize, boolean defaultCanUseLootTables) {
			super(defaultMaxInvSize, defaultCanUseLootTables);
		}
		
		@Override
		public int getInventoryMaxSize(PreviewContext context) {
			Integer size = context.stack().get(BackpackMod.BACKPACK_SIZE_COMPONENT);
			if(size != null) {
				return size;
			}
			return super.getInventoryMaxSize(context);
		}
		
	}
	
}
