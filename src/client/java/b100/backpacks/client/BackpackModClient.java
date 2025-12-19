package b100.backpacks.client;

import org.lwjgl.glfw.GLFW;

import b100.backpacks.BackpackItem;
import b100.backpacks.BackpackMod;
import b100.backpacks.BackpackUtil;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.item.ItemStack;

public class BackpackModClient implements ClientModInitializer {
	
	public static KeyMapping keyBinding;
	
	@Override
	public void onInitializeClient() {
		for(int i=0; i < BackpackMod.BACKPACK_MENU_TYPES.size(); i++) {
			final int rows = i + 1;
			Registry.register(BuiltInRegistries.MENU, BackpackMod.id("menu_9x" + rows), BackpackMod.BACKPACK_MENU_TYPES.get(i));
			MenuScreens.register(BackpackMod.BACKPACK_MENU_TYPES.get(i), BackpackScreen::new);
		}
		
		for(BackpackItem item : BackpackMod.ALL_BACKPACKS) {
			ColorProviderRegistry.ITEM.register(BackpackModClient::getBackpackColor, item);
		}
		
		if(BackpackMod.TRINKETS_INSTALLED) {
			keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key." + BackpackMod.MODID + ".openBackpack", GLFW.GLFW_KEY_B, "category." + BackpackMod.MODID));
			
			for(BackpackItem item : BackpackMod.ALL_BACKPACKS) {
				TrinketRendererRegistry.registerRenderer(item, TrinketBackpackRenderer.INSTANCE);	
			}
		}
		ClientTickEvents.END_CLIENT_TICK.register(BackpackModClient::onClientEndTick);
	}
	
	public static void onClientEndTick(Minecraft client) {
		if(keyBinding != null) {
			while(keyBinding.consumeClick()) {
				openBackpack(client);
			}	
		}
	}
	
	public static void openBackpack(Minecraft client) {
		client.level.sendPacketToServer(new ServerboundCustomPayloadPacket(() -> BackpackMod.CUSTOM_PAYLOAD_TYPE));
	}
	
	public static int getBackpackColor(ItemStack item, int layer) {
		return layer == 0 ? BackpackUtil.getBackpackColor(item) : 0xFFFFFFFF;
	}
	
}
