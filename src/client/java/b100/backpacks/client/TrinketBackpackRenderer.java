/**
 * Based on TrinketBackpackRenderer from Inmis by Draylar, licensed MIT
 * https://github.com/Draylar/inmis/blob/bc6615ec5e3561d4e6ad4b53dd2ea9a72447c811/src/main/java/draylar/inmis/client/TrinketBackpackRenderer.java
 */
package b100.backpacks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TrinketBackpackRenderer implements TrinketRenderer {

	@Override
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, PoseStack matrices, MultiBufferSource vertexConsumers, int light,
		LivingEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		
		matrices.pushPose();
		
        // Initial transformation
        matrices.mulPose(Axis.XP.rotationDegrees(180));
        matrices.translate(0, -0.3, -0.2);
        
        float scale = 0.75f;
        matrices.scale(scale, scale, scale);
        
        // Sneaking
        if (player.isCrouching()) {
            matrices.mulPose(Axis.XP.rotationDegrees(28));
            matrices.translate(0.0, -0.2, -0.1);
        }
        
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, player.level(), 0);
        matrices.popPose();
	}
}
