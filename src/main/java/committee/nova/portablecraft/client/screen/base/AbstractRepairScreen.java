package committee.nova.portablecraft.client.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import committee.nova.portablecraft.common.menus.base.AbstractRepairedContainer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/3/13 9:11
 * Version: 1.0
 */
public abstract class AbstractRepairScreen<T extends AbstractRepairedContainer> extends HandledScreen<T> implements ScreenHandlerListener {
    private final Identifier texture;

    public AbstractRepairScreen(T handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title);
        this.texture = texture;
    }

    protected void setup() {
    }

    @Override
    protected void init() {
        super.init();
        this.setup();
        ((AbstractRepairedContainer) this.handler).addListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        ((AbstractRepairedContainer) this.handler).removeListener(this);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        this.renderForeground(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void renderForeground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(matrices, i + 59, j + 20, 0, this.backgroundHeight + (((AbstractRepairedContainer) this.handler).getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((((AbstractRepairedContainer) this.handler).getSlot(0).hasStack() || ((AbstractRepairedContainer) this.handler).getSlot(1).hasStack()) && !((AbstractRepairedContainer) this.handler).getSlot(2).hasStack()) {
            this.drawTexture(matrices, i + 99, j + 45, this.backgroundWidth, 0, 28, 21);
        }
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
    }
}
