package b100.backpacks;

import java.util.List;
import java.util.Random;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BackpackUtil {
	
	public static boolean openEquippedBackpack(Player player) {
		if(BackpackMod.TRINKETS_INSTALLED) {
			TrinketComponent trinketComponent = TrinketsApi.getTrinketComponent(player).get();
			List<Tuple<SlotReference, ItemStack>> equipped = trinketComponent.getEquipped(stack -> BackpackUtil.isBackpack(stack));
			if(equipped.size() > 0) {
				return BackpackUtil.openBackpack(player, equipped.get(0).getB());
			}
		}
		return false;
	}
	
	public static boolean openBackpack(Player player, ItemStack item) {
		if(!isPlayerHoldingItem(player, item)) {
			BackpackMod.print("Player " + player.getName().getString() + " is not holding item " + item + ", can't open backpack!");
			return false;
		}
		
		// Assign a random ID to the backpack
		BackpackUtil.getBackpackID(item);
		
		player.awardStat(Stats.ITEM_USED.get(item.getItem()));
		player.openMenu(new BackpackContainer(item));
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 1.0f, 1.0f);
		return true;
	}
	
	public static boolean isPlayerHoldingItem(Player player, ItemStack item) {
		if(BackpackMod.TRINKETS_INSTALLED) {
			TrinketComponent trinketComponent = TrinketsApi.getTrinketComponent(player).get();
			if(trinketComponent.isEquipped(itemStack -> itemStack == item)) {
				return true;
			}
		}
		return player.getInventory().contains(item);
	}
	
	public static int getBackpackID(ItemStack backpack) {
		Integer id = backpack.get(BackpackMod.BACKPACK_ID_COMPONENT);
		
		if(id == null) {
			id = new Random().nextInt();
			backpack.set(BackpackMod.BACKPACK_ID_COMPONENT, id);
		}
		
		return id;
	}
	
	public static int getBackpackSize(ItemStack backpack) {
		Integer backpackSize = backpack.get(BackpackMod.BACKPACK_SIZE_COMPONENT);
		if(backpackSize == null) {
			backpackSize = BackpackMod.BACKPACK_ROWS * 9;
			backpack.set(BackpackMod.BACKPACK_SIZE_COMPONENT, backpackSize);
		}
		return backpackSize;
	}
	
	public static boolean isBackpack(ItemStack item) {
		return item.getItem() instanceof BackpackItem;
	}
	
	public static boolean isBackpack(Item item) {
		return item instanceof BackpackItem;
	}
	
	public static int getBackpackColor(ItemStack item) {
		return getBackpackColor(item.getItem());
	}
	
	public static int getBackpackColor(Item item) {
		if(item instanceof BackpackItem backpack) {
			DyeColor color = backpack.getColor();
			if(color != null) {
				return color.getTextureDiffuseColor() | 0xFF000000;
			}
		}
		return 0xFFF09954;
	}
	
	public static ItemStack dye(ItemStack backpack, DyeColor dye) {
		Item newItem;
		if(dye != null) {
			newItem = BackpackMod.DYED_BACKPACKS.get(dye);
		}else {
			newItem = BackpackMod.BACKPACK;
		}
		return backpack.transmuteCopy(newItem);
	}
	
	public static NonNullList<ItemStack> getBackpackItems(ItemStack backpackItem) {
		NonNullList<ItemStack> items = NonNullList.withSize(getBackpackSize(backpackItem), ItemStack.EMPTY);
		copyBackpackItemsIntoList(backpackItem, items);
		return items;
	}
	
	public static void copyBackpackItemsIntoList(ItemStack backpackItem, NonNullList<ItemStack> items) {
		ItemContainerContents contents = backpackItem.get(DataComponents.CONTAINER);
		if(contents != null) {
			contents.copyInto(items);	
		}
	}
	
	public static void setBackpackItemsFromList(ItemStack backpackItem, NonNullList<ItemStack> items) {
		int backpackSize = getBackpackSize(backpackItem);
		if(items.size() > backpackSize) {
			throw new RuntimeException("Item list is too big! " + items.size() + " > " + backpackSize);
		}
		backpackItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
	}
	
	public static boolean moveItemsIntoContainer(ItemStack backpackItem, Container container) {
		NonNullList<ItemStack> items = getBackpackItems(backpackItem);
		
		boolean changed = false;
		
		for(int i=0; i < items.size(); i++) {
			ItemStack item = items.get(i);
			if(item.isEmpty()) {
				continue;
			}
			
			if(addItemToContainer(item, container)) {
				changed = true;
				
				if(item.getCount() <= 0 || item.isEmpty()) {
					items.set(i, ItemStack.EMPTY);
				}	
			}
		}
		
		if(changed) {
			setBackpackItemsFromList(backpackItem, items);
			
			container.setChanged();
		}
		
		return changed;
	}
	
	public static boolean addItemToContainer(ItemStack item, Container container) {
		boolean changed = false;
		
		// First, search for matching items
		for(int slotID = 0; slotID < container.getContainerSize() && !item.isEmpty() && item.getCount() > 0; slotID++) {
			if(!container.canPlaceItem(slotID, item)) {
				continue;
			}
			
			ItemStack itemInContainer = container.getItem(slotID);
			if(!itemInContainer.isEmpty() && ItemStack.isSameItemSameComponents(item, itemInContainer)) {
				int maxStackSize = Math.min(container.getMaxStackSize(), itemInContainer.getMaxStackSize());
				int amount = Math.min(item.getCount(), maxStackSize - itemInContainer.getCount());
				if(amount > 0) {
					item.setCount(item.getCount() - amount);
					itemInContainer.setCount(itemInContainer.getCount() + amount);
					changed = true;
				}
			}
		}
		
		// Then, move anything remaining into empty slots
		for(int slotID = 0; slotID < container.getContainerSize() && !item.isEmpty() && item.getCount() > 0; slotID++) {
			if(!container.canPlaceItem(slotID, item)) {
				continue;
			}

			ItemStack itemInContainer = container.getItem(slotID);
			if(itemInContainer.isEmpty()) {
				int amount = Math.min(item.getCount(), container.getMaxStackSize());
				if(amount > 0) {
					ItemStack move = item.copy();
					move.setCount(amount);
					item.setCount(item.getCount() - amount);
					container.setItem(slotID, move);
					changed = true;
				}
			}
		}
		return changed;
	}
	
	public static boolean backpackContainsItems(ItemStack backpackItem) {
		NonNullList<ItemStack> items = getBackpackItems(backpackItem);
		for(int i=0; i < items.size(); i++) {
			if(!items.get(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
}
