package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.RefillCountdownPayload;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class QuestionBlockScreen extends AbstractContainerScreen<QuestionBlockMenu> {
    public static ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/question_block.png");
    Button refillOffButton;
    Button refillOnButton;
    Button confirmButton;
    EditBox countdownBox;
    Inventory inventory;
    private String questionBlockName = "";
    private boolean showClockIcon = false;
    private boolean initializedFromServer = false;

    public QuestionBlockScreen(QuestionBlockMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
    }

    @Override
    public void renderLabels(GuiGraphics graphics, int x, int y) {
        if (!this.questionBlockName.isEmpty()) // Question Block "Name"
            graphics.drawString(this.font, this.questionBlockName, this.titleLabelX, this.titleLabelY, 4210752, false);
        else // "Question Block"
            graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        // Inventory
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture width, texture height
        graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.refillOffButton.visible) {
            if (this.refillOffButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 83, this.topPos + 26, 215, 22, 37, 20);
            else graphics.blit(GUI, this.leftPos + 83, this.topPos + 26, 177, 22, 37, 20);
        }

        if (this.refillOnButton.visible) {
            if (this.refillOnButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 83, this.topPos + 26, 215, 1, 37, 20);
            else graphics.blit(GUI, this.leftPos + 83, this.topPos + 26, 177, 1, 37, 20);
        }

        if (this.countdownBox.visible)
            graphics.blit(GUI, this.leftPos + 60, this.topPos + 57, 177, 43, 57, 14);

        if (this.confirmButton.visible) {
            if (this.confirmButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 122, this.topPos + 54, 198, 58, 20, 20);
            else graphics.blit(GUI, this.leftPos + 122, this.topPos + 54, 177, 58, 20, 20);
        }

        if (this.showClockIcon)
            graphics.blit(GUI, this.leftPos + 43, this.topPos + 57, 235, 43, 14, 14);
    }

    @Override
    public void init() {
        super.init();
        final Component rename = Component.translatable("menu.marioverse.warp_pipe.rename_button");

        this.countdownBox = new EditBox(this.font, this.leftPos + 62, this.topPos + 60, 55, 14,
                Component.translatable("menu.marioverse.question_block.countdown_box.narrate"));
        this.countdownBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.question_block.countdown_box.tooltip")));
        this.countdownBox.setValue(String.valueOf(this.menu.getRefillCountdown()));
        this.countdownBox.setFilter(s -> s.matches("-?\\d*"));
        this.countdownBox.setBordered(false);
        this.countdownBox.setVisible(false);
        this.countdownBox.setMaxLength(15);
        this.addRenderableWidget(this.countdownBox);

        this.confirmButton = Button.builder(rename, button -> {
            this.confirmButtonOnPress();
        }).bounds(this.leftPos + 122, this.topPos + 54, 20, 20).build();
        this.confirmButton.visible = false;
        this.confirmButton.setAlpha(0);
        this.addRenderableWidget(this.confirmButton);

        this.refillOffButton = Button.builder(rename, button -> {
            this.countdownBox.setVisible(true);
            this.confirmButton.visible = true;
            this.refillOffButton.visible = false;
            this.refillOnButton.visible = true;
            this.showClockIcon = true;
            if (this.menu.getRefillCountdown() <= -1)
                PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, 6000));
        }).bounds(this.leftPos + 83, this.topPos + 26, 37, 20).build();
        this.refillOffButton.setAlpha(0);
        this.addRenderableWidget(this.refillOffButton);

        this.refillOnButton = Button.builder(rename, button -> {
            this.countdownBox.setVisible(false);
            this.confirmButton.visible = false;
            this.refillOffButton.visible = true;
            this.refillOnButton.visible = false;
            this.showClockIcon = false;
            PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, -1));
        }).bounds(this.leftPos + 83, this.topPos + 26, 37, 20).build();
        this.refillOnButton.visible = false;
        this.refillOnButton.setAlpha(0);
        this.addRenderableWidget(this.refillOnButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        int refillCountdown = this.menu.getRefillCountdown();

        if (!this.countdownBox.isFocused() && this.countdownBox.visible)
            this.countdownBox.setValue(String.valueOf(refillCountdown));

        if (!this.initializedFromServer) {
            if (refillCountdown >= 0) {
                this.countdownBox.setVisible(true);
                this.confirmButton.visible = true;
                this.refillOffButton.visible = false;
                this.refillOnButton.visible = true;
                this.showClockIcon = true;
            } else {
                this.countdownBox.setVisible(false);
                this.confirmButton.visible = false;
                this.refillOffButton.visible = true;
                this.refillOnButton.visible = false;
                this.showClockIcon = false;
            }

            this.initializedFromServer = true;
        }
    }

    @Override
    // Draws the screen and all the components in it.
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        Component tooltip = Component.literal("");

       tooltip = Component.translatable("menu.marioverse.question_block.refill_off_button.tooltip");
        this.refillOffButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.refill_on_button.tooltip");
        this.refillOnButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.confirm_button.tooltip");
        this.confirmButton.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public boolean keyPressed(final int keyCode, final int b, final int c)
    {
        if (this.countdownBox.isFocused() && (keyCode == GLFW.GLFW_KEY_ESCAPE
                || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
                this.confirmButtonOnPress();
            this.countdownBox.setFocused(false);
            return false;
        }

        if (this.countdownBox.isFocused() && keyCode == GLFW.GLFW_KEY_E) {
            this.countdownBox.setFocused(true);
            return true;
        }

        return super.keyPressed(keyCode, b, c);
    }

    private void confirmButtonOnPress() {
        String value = this.countdownBox.getValue();
        int parsed = -1;

        if (!value.isEmpty() && !value.equals("-"))
            parsed = Integer.parseInt(value);

        if (this.minecraft != null && this.minecraft.getConnection() != null)
            PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, parsed));
    }
}
