package b100.backpacks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BackpackItem extends Item {

	@Nullable
	private final DyeColor color;
	
	public BackpackItem(Properties properties) {
		this(properties, null);
	}
	
	public BackpackItem(Properties properties, DyeColor dyeColor) {
		super(properties);
		
		this.color = dyeColor;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack item = player.getItemInHand(interactionHand);
		BackpackUtil.openBackpack(player, item);
		return InteractionResultHolder.sidedSuccess(item, level.isClientSide);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		if(color != null) {
			Player player = context.getPlayer();
			Level world = context.getLevel();
			BlockPos pos = context.getClickedPos();
			
			BlockState state = world.getBlockState(pos);
			if(state.is(Blocks.WATER_CAULDRON)) {
				InteractionHand hand = context.getHand();
				ItemStack backpack = player.getItemInHand(hand);
				
				if(!world.isClientSide) {
					LayeredCauldronBlock.lowerFillLevel(state, world, pos);

					player.level().playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.GENERIC_SWIM, SoundSource.PLAYERS, 1.0f, 1.0f);
					
					player.setItemInHand(hand, BackpackUtil.dye(backpack, null));
				}
				
				return InteractionResult.sidedSuccess(world.isClientSide);
			}	
		}
		
		
		return super.useOn(context);
	}
	
	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	
	@Nullable
	public DyeColor getColor() {
		return color;
	}
	
}
