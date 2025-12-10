package b100.backpacks.client;

import b100.backpacks.BackpackMod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class BackpackModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		for(int i=0; i < BackpackMod.BACKPACK_MENU_TYPES.size(); i++) {
			final int rows = i + 1;
			Registry.register(BuiltInRegistries.MENU, BackpackMod.id("menu_9x" + rows), BackpackMod.BACKPACK_MENU_TYPES.get(i));
			MenuScreens.register(BackpackMod.BACKPACK_MENU_TYPES.get(i), BackpackScreen::new);
		}
	}
	
}
