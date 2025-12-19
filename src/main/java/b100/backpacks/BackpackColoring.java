package b100.backpacks;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BackpackColoring extends CustomRecipe {

	public BackpackColoring(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput crafting, Level level) {
		int backpackCount = 0;
		int dyeCount = 0;
		
		for(int i=0; i < crafting.size(); i++) {
			ItemStack item = crafting.getItem(i);
			if(item.isEmpty()) {
				continue;
			}
			
			if(isDye(item)) {
				dyeCount++;
			}else if(BackpackUtil.isBackpack(item)) {
				backpackCount++;
			}else {
				return false;
			}
			
			if(backpackCount > 1 || dyeCount > 1) {
				return false;
			}
		}
		
		return backpackCount == 1 && dyeCount == 1;
	}

	@Override
	public ItemStack assemble(CraftingInput crafting, Provider provider) {
		ItemStack backpack = null;
		DyeItem dye = null;
		
		for(int i=0; i < crafting.size(); i++) {
			ItemStack item = crafting.getItem(i);
			if(item.isEmpty()) {
				continue;
			}
			
			if(isDye(item)) {
				dye = (DyeItem) item.getItem();
			}else if(BackpackUtil.isBackpack(item)) {
				backpack = item;
			}
		}
		
		if(backpack == null) throw new RuntimeException("Backpack is null!");
		if(dye == null) throw new RuntimeException("Dye is null!");
		
		return BackpackUtil.dye(backpack, dye.getDyeColor());
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BackpackMod.BACKPACK_COLORING;
	}
	
	public static boolean isDye(ItemStack item) {
		return item.getItem() instanceof DyeItem;
	}
	
}
