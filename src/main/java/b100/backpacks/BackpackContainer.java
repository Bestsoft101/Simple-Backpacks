package b100.backpacks;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BackpackContainer extends SimpleContainer implements MenuProvider {

	private final ItemStack backpackItem;
	
	public static BackpackContainer create(ItemStack item) {
		Integer backpackSize = item.get(BackpackMod.BACKPACK_SIZE_COMPONENT);
		if(backpackSize == null) {
			backpackSize = BackpackMod.BACKPACK_ROWS * 9;
			item.set(BackpackMod.BACKPACK_SIZE_COMPONENT, backpackSize);
		}
		
		return new BackpackContainer(item, backpackSize);
	}
	
	protected BackpackContainer(ItemStack item, int size) {
		super(size);
		this.backpackItem = item;
		
		ItemContainerContents contents = backpackItem.get(DataComponents.CONTAINER);
		if(contents != null) {
			contents.copyInto(items);	
		}
		
		addListener(container -> backpackItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items)));
	}
	
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return BackpackMenu.create(id, this, inventory);
	}

	@Override
	public Component getDisplayName() {
		return backpackItem.getHoverName();
	}
}
