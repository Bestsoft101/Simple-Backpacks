package b100.backpacks;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackpackItem extends Item {

	public BackpackItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack item = player.getItemInHand(interactionHand);
		
		// Assign a random ID to the backpack
		BackpackMod.getBackpackID(item);
		
		player.openMenu(new BackpackContainer(item));
		player.awardStat(Stats.ITEM_USED.get(this));
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 1.0f, 1.0f);
		
		return InteractionResultHolder.sidedSuccess(item, level.isClientSide);
	}
	
	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	
}
