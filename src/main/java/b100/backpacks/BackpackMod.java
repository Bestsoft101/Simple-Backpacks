package b100.backpacks;

import java.util.function.Consumer;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

public class BackpackMod implements ModInitializer {
	
	public static final String MODID = "backpacks";
	
	public static final Item BACKPACK = new BackpackItem(new Item.Properties().stacksTo(1));
	
	public static final DataComponentType<Integer> BACKPACK_SIZE_COMPONENT = dataComponent("backpack_size", builder -> builder.persistent(ExtraCodecs.intRange(9, 54)).networkSynchronized(ByteBufCodecs.VAR_INT));
	
	public static final int BACKPACK_SIZE = 27;
	
	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, id("backpack"), BACKPACK);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("size"), BACKPACK_SIZE_COMPONENT);
	}
	
	private static <T> DataComponentType<T> dataComponent(String name, Consumer<DataComponentType.Builder<T>> consumer) {
		DataComponentType.Builder<T> builder = DataComponentType.builder();
		consumer.accept(builder);
		DataComponentType<T> type = builder.build();
		return type;
	}
	
	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
	
}
