package b100.backpacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BackpackMod implements ModInitializer {
	
	public static final int MAX_ROWS = 9;
	
	public static final String MODID = "backpacks";
	
	public static final Item BACKPACK = new BackpackItem(new Item.Properties().stacksTo(1));

	public static final DataComponentType<Integer> BACKPACK_SIZE_COMPONENT = dataComponent(builder -> builder.persistent(ExtraCodecs.intRange(9, MAX_ROWS * 9)).networkSynchronized(ByteBufCodecs.VAR_INT));
	
	/**
	 * Each backpack has a random ID assigned to differentiate between them. Low chance of multiple backpacks having the same ID,
	 * but it's only used for the item switch animation, so it's fine.
	 */
	public static final DataComponentType<Integer> BACKPACK_ID_COMPONENT = dataComponent(builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	
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
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("id"), BACKPACK_ID_COMPONENT);
	}
	
	private static <T> DataComponentType<T> dataComponent(Consumer<DataComponentType.Builder<T>> consumer) {
		DataComponentType.Builder<T> builder = DataComponentType.builder();
		consumer.accept(builder);
		return builder.build();
	}
	
	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
	
	public static int getBackpackID(ItemStack backpack) {
		Integer id = backpack.get(BACKPACK_ID_COMPONENT);
		
		if(id == null) {
			id = new Random().nextInt();
			backpack.set(BACKPACK_ID_COMPONENT, id);
		}
		
		return id;
	}
	
}
