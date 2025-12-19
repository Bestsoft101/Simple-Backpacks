package b100.backpacks;

import java.util.List;
import java.util.Random;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
	
}
