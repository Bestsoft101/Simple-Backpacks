package b100.backpacks.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import b100.backpacks.BackpackMod;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

@Mixin(value = ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	
	/**
	 * Remove the animation of the held backback going down and up when the contents are changed.
	 * To keep the animation when actually switching between different backpacks, compare the backpack IDs.
	 */
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean modifyItemStackMatchMethod(ItemStack stack1, ItemStack stack2, Operation<Boolean> original) {
		if(stack1.getItem() == BackpackMod.BACKPACK && stack2.getItem() == BackpackMod.BACKPACK) {
			return BackpackMod.getBackpackID(stack1) == BackpackMod.getBackpackID(stack2);
		}
		return original.call(stack1, stack2);
	}
	
}
