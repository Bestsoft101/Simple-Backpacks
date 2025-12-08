package b100.backpacks;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackMenu extends AbstractContainerMenu {
	
	public static final MenuType<?>[] MENU_TYPES_BY_SIZE = new MenuType[] {
		MenuType.GENERIC_9x1,
		MenuType.GENERIC_9x2,
		MenuType.GENERIC_9x3,
		MenuType.GENERIC_9x4,
		MenuType.GENERIC_9x5,
		MenuType.GENERIC_9x6
	};
	
	private BackpackContainer container;
	private int rows;
	
	public static BackpackMenu create(int id, BackpackContainer container, Inventory inventory) {
		int rows = container.getContainerSize() / 9;
		
		MenuType<?> menuType = MENU_TYPES_BY_SIZE[rows - 1];
		
		return new BackpackMenu(menuType, id, container, inventory);
	}
	
	private BackpackMenu(MenuType<?> menuType, int id, BackpackContainer container, Inventory inventory) {
		super(menuType, id);
		this.container = container;
		this.rows = container.getContainerSize() / 9;
		
		// Backpack contents
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < 9; x++) {
				// Shulker box slots prevent backpacks from being placed into other backpacks
				addSlot(new ShulkerBoxSlot(container, y * 9 + x, 0, 0));
			}
		}
		
		// Main inventory
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 9; x++) {
				addSlot(new Slot(inventory, 9 + y * 9 + x, 0, 0));
			}
		}
		
		// Hotbar
		for(int x = 0; x < 9; x++) {
			addSlot(new Slot(inventory, x, 0, 0));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotID) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = slots.get(slotID);
		
		if (slot != null && slot.hasItem()) {
			ItemStack slotItem = slot.getItem().copy();
			if (slotID < rows * 9) {
				if (!this.moveItemStackTo(slotItem, rows * 9, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotItem, 0, rows * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (slotItem.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemStack;
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}
	
}
