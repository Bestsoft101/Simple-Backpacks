package b100.backpacks;

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
		BackpackUtil.openBackpack(player, item);
		return InteractionResultHolder.sidedSuccess(item, level.isClientSide);
	}
	
	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	
}
