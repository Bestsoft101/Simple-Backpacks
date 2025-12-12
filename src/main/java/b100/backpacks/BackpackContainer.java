package b100.backpacks;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BackpackContainer implements Container, MenuProvider {

	private final ItemStack item;
	
	private final int size;
	private final NonNullList<ItemStack> items;
	
	public BackpackContainer(ItemStack item) {
		this.item = item;
		
		Integer backpackSize = item.get(BackpackMod.BACKPACK_SIZE_COMPONENT);
		if(backpackSize == null) {
			backpackSize = BackpackMod.BACKPACK_ROWS * 9;
			item.set(BackpackMod.BACKPACK_SIZE_COMPONENT, backpackSize);
		}
		
		size = backpackSize;
		items = NonNullList.withSize(size, ItemStack.EMPTY);
		
		ItemContainerContents contents = item.get(DataComponents.CONTAINER);
		if(contents != null) {
			contents.copyInto(items);	
		}
	}

	@Override
	public ItemStack getItem(int slot) {
		return items.get(slot);
	}

	@Override
	public void setItem(int slot, ItemStack itemStack) {
		items.set(slot, itemStack);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
		if(!stack.isEmpty()) {
			this.setChanged();
		}
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack itemStack = items.get(slot);
		if(itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		items.set(slot, ItemStack.EMPTY);
		return itemStack;
	}
	
	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack item : items) {
			if(!item.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getContainerSize() {
		return size;
	}

	@Override
	public void setChanged() {
		ItemContainerContents contents = ItemContainerContents.fromItems(items);
		item.set(DataComponents.CONTAINER, contents);
	}

	@Override
	public boolean stillValid(Player player) {
		return BackpackUtil.isPlayerHoldingItem(player, item);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return BackpackMenu.create(id, this, inventory);
	}

	@Override
	public Component getDisplayName() {
		return item.getHoverName();
	}
	
}
