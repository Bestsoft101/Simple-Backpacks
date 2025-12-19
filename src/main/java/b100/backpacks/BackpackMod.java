package b100.backpacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.serialization.Codec;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class BackpackMod implements ModInitializer {
	
	public static final String MODID = "backpacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final boolean INDEV = FabricLoader.getInstance().isDevelopmentEnvironment();
	public static final boolean TRINKETS_INSTALLED = FabricLoader.getInstance().isModLoaded("trinkets");
	
	static {
		print("Trinkets installed: " + TRINKETS_INSTALLED);
	}
	
	public static DyeColor[] ORDERED_DYES = new DyeColor[] {
		DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK, DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW,
		DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK
	};
	
	// Config
	public static final int MAX_ROWS = 9;
	public static final int BACKPACK_ROWS = 3;
	
	// Items
	public static final BackpackItem BACKPACK = new BackpackItem(new Item.Properties().stacksTo(1));
	public static final Map<DyeColor, BackpackItem> DYED_BACKPACKS;
	public static final List<BackpackItem> ALL_BACKPACKS;
	static {
		Map<DyeColor, BackpackItem> dyedBackpacks = new HashMap<>();
		
		for(DyeColor color : ORDERED_DYES) {
			dyedBackpacks.put(color, new BackpackItem(new Item.Properties().stacksTo(1), color));
		}
		
		DYED_BACKPACKS = Collections.unmodifiableMap(dyedBackpacks);
		
		List<BackpackItem> allBackpacks = new ArrayList<>();
		
		allBackpacks.add(BACKPACK);
		for(DyeColor color : ORDERED_DYES) {
			allBackpacks.add(DYED_BACKPACKS.get(color));
		}
		
		ALL_BACKPACKS = Collections.unmodifiableList(allBackpacks);
	}
	
	// Crafting
	public static final RecipeSerializer<BackpackColoring> BACKPACK_COLORING = new SimpleCraftingRecipeSerializer<>(BackpackColoring::new);
	
	// Data components
	public static final DataComponentType<Integer> BACKPACK_SIZE_COMPONENT = dataComponent(builder -> builder.persistent(ExtraCodecs.intRange(9, MAX_ROWS * 9)).networkSynchronized(ByteBufCodecs.VAR_INT));
	
	/**
	 * Each backpack has a random ID assigned to differentiate between them. Low chance of multiple backpacks having the same ID,
	 * but it's only used for the item switch animation, so it's fine.
	 */
	public static final DataComponentType<Integer> BACKPACK_ID_COMPONENT = dataComponent(builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	
	// Menu types
	public static final List<MenuType<BackpackMenu>> BACKPACK_MENU_TYPES;
	static {
		List<MenuType<BackpackMenu>> backpackMenus = new ArrayList<>();
		for(int i=0; i < MAX_ROWS; i++) {
			final int rows = i + 1;
			backpackMenus.add(new MenuType<>((id, inventory) -> BackpackMenu.create(id, rows, inventory), FeatureFlags.VANILLA_SET));
		}
		BACKPACK_MENU_TYPES = Collections.unmodifiableList(backpackMenus);
	}
	
	// Payload type
	public static final CustomPacketPayload.Type<CustomPacketPayload> CUSTOM_PAYLOAD_TYPE = new CustomPacketPayload.Type<>(BackpackMod.id("open_backpack"));
	
	@Override
	public void onInitialize() {
		// Register everything
		for(BackpackItem item : ALL_BACKPACKS) {
			Registry.register(BuiltInRegistries.ITEM, getBackpackID(item.getColor()), item);
		}
		
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id("backpack_coloring"), BACKPACK_COLORING);
		
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("size"), BACKPACK_SIZE_COMPONENT);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("id"), BACKPACK_ID_COMPONENT);
		
		// Add to creative mode
		for(BackpackItem item : ALL_BACKPACKS) {
			addToCreativeMenu(CreativeModeTabs.TOOLS_AND_UTILITIES, item);
		}
		
		// Setup packet
		if(TRINKETS_INSTALLED) {
			PayloadTypeRegistry.playC2S().register(BackpackMod.CUSTOM_PAYLOAD_TYPE, CustomPacketPayload.codec((packet, bb) -> {}, byteBuf -> () -> CUSTOM_PAYLOAD_TYPE));
			ServerPlayNetworking.registerGlobalReceiver(BackpackMod.CUSTOM_PAYLOAD_TYPE, (payload, context) -> BackpackUtil.openEquippedBackpack(context.player()));
		}
	}
	
	private static void addToCreativeMenu(ResourceKey<CreativeModeTab> tab, Item item) {
		ItemGroupEvents.modifyEntriesEvent(tab).register((itemGroup) -> itemGroup.accept(item));
	}
	
	private static <T> DataComponentType<T> dataComponent(Consumer<DataComponentType.Builder<T>> consumer) {
		DataComponentType.Builder<T> builder = DataComponentType.builder();
		consumer.accept(builder);
		return builder.build();
	}
	
	public static ResourceLocation getBackpackID(DyeColor color) {
		if(color != null) {
			return id(color.getName() + "_backpack");
		}
		return id("backpack");
	}
	
	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
	
	public static void print(String str) {
		if(INDEV) {
			System.out.print("[Backpacks] " + str + "\n");	
		}else {
			LOGGER.info("[Backpacks] " + str);
		}
	}
	
}
