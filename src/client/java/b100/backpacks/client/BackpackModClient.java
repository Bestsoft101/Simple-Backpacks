package b100.backpacks.client;

import org.lwjgl.glfw.GLFW;

import b100.backpacks.BackpackMod;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;

public class BackpackModClient implements ClientModInitializer {
	
	public static KeyMapping keyBinding;
	
	@Override
	public void onInitializeClient() {
		for(int i=0; i < BackpackMod.BACKPACK_MENU_TYPES.size(); i++) {
			final int rows = i + 1;
			Registry.register(BuiltInRegistries.MENU, BackpackMod.id("menu_9x" + rows), BackpackMod.BACKPACK_MENU_TYPES.get(i));
			MenuScreens.register(BackpackMod.BACKPACK_MENU_TYPES.get(i), BackpackScreen::new);
		}
		
		if(BackpackMod.TRINKETS_INSTALLED) {
			keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key." + BackpackMod.MODID + ".openBackpack", GLFW.GLFW_KEY_B, "category." + BackpackMod.MODID));
			
			TrinketRendererRegistry.registerRenderer(BackpackMod.BACKPACK, new TrinketBackpackRenderer());
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
	
}
