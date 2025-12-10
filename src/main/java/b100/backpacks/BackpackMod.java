package b100.backpacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public class BackpackMod implements ModInitializer {
	
	public static final int MAX_ROWS = 9;
	
	public static final String MODID = "backpacks";
	
	public static final Item BACKPACK = new BackpackItem(new Item.Properties().stacksTo(1));
	
	public static final DataComponentType<Integer> BACKPACK_SIZE_COMPONENT = dataComponent(builder -> builder.persistent(ExtraCodecs.intRange(9, MAX_ROWS * 9)).networkSynchronized(ByteBufCodecs.VAR_INT));
	
	public static final List<MenuType<BackpackMenu>> BACKPACK_MENU_TYPES;
	static {
		List<MenuType<BackpackMenu>> backpackMenus = new ArrayList<>();
		for(int i=0; i < MAX_ROWS; i++) {
			final int rows = i + 1;
			backpackMenus.add(new MenuType<>((id, inventory) -> BackpackMenu.create(id, rows, inventory), FeatureFlags.VANILLA_SET));
		}
		BACKPACK_MENU_TYPES = Collections.unmodifiableList(backpackMenus);
	}
	
	public static final int BACKPACK_ROWS = 3;
	
	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, id("backpack"), BACKPACK);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("size"), BACKPACK_SIZE_COMPONENT);
		
		for(int i=0; i < BACKPACK_MENU_TYPES.size(); i++) {
			final int rows = i + 1;
			Registry.register(BuiltInRegistries.MENU, id("menu_9x" + rows), BACKPACK_MENU_TYPES.get(i));
			MenuScreens.register(BACKPACK_MENU_TYPES.get(i), BackpackScreen::new);
		}
	}
	
	private static <T> DataComponentType<T> dataComponent(Consumer<DataComponentType.Builder<T>> consumer) {
		DataComponentType.Builder<T> builder = DataComponentType.builder();
		consumer.accept(builder);
		return builder.build();
	}
	
	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
	
}
