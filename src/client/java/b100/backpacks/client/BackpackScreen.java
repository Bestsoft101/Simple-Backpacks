package b100.backpacks.client;

import b100.backpacks.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> implements MenuAccess<BackpackMenu> {
	
	private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
	
	private int rows;
	
	public BackpackScreen(BackpackMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		rows = menu.getRows();
		
		imageHeight = 114 + rows * 18;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.render(guiGraphics, i, j, f);
		this.renderTooltip(guiGraphics, i, j);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float f, int mouseX, int mouseY) {
		int x0 = (width - imageWidth) / 2;
		int y0 = (height - imageHeight) / 2;
		
		guiGraphics.blit(CONTAINER_BACKGROUND, x0, y0, 0, 0, imageWidth, 17);
		guiGraphics.blit(CONTAINER_BACKGROUND, x0, y0 + rows * 18 + 17, 0, 125, imageWidth, 97);
		
		int yOffset = 17;
		int repeats = Math.ceilDiv(rows, 6);
		for(int i=0; i < repeats; i++) {
			int remaining = rows - i * 6;
			remaining = Math.min(remaining, 6);
			
			guiGraphics.blit(CONTAINER_BACKGROUND, x0, y0 + yOffset, 0, 17, imageWidth, 18 * remaining);	
			yOffset += 18 * remaining;
		}
	}
	
	@Override
	public boolean keyPressed(int i, int j, int k) {
		if(BackpackModClient.keyBinding != null && BackpackModClient.keyBinding.matches(i, j)) {
			onClose();
		}
		return super.keyPressed(i, j, k);
	}
	
}
