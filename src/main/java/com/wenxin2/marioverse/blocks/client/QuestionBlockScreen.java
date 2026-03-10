package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.RefillCountdownPayload;
import com.wenxin2.marioverse.network.server_bound.data.TimeUnitPayload;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class QuestionBlockScreen extends AbstractContainerScreen<QuestionBlockMenu> {
    public static ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/question_block.png");
    private boolean showIcon = false;
    private boolean initializedFromServer = false;
    private String questionBlockName = "";
    Button clockButton;
    Button confirmButton;
    Button hourButton;
    Button minuteButton;
    Button refillOffButton;
    Button refillOnButton;
    Button secondsButton;
    Button ticksButton;
    EditBox countdownBox;
    Inventory inventory;

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

        int refillTicks = this.menu.getRefillCountdown();
        int frameCount = 8;
        int frameWidth = 16;
        int startU = 0;
        int frame = 0;

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture width, texture height
        graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (refillTicks > 0 && this.minecraft != null && this.minecraft.level != null) {
            long gameTime = this.minecraft.level.getGameTime();
            int speed = Math.max(1, (int) (Math.sqrt(refillTicks) / 2));

            frame = (int) ((gameTime / speed) % frameCount);
        }
        int uOffset = startU + (frame * frameWidth);

        if (this.clockButton.visible) {
            if (this.clockButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 143, this.topPos + 23, uOffset, 184, 16, 16);
            else graphics.blit(GUI, this.leftPos + 143, this.topPos + 23, uOffset, 167, 16, 16);
        }

        if (this.refillOffButton.visible) {
            if (this.refillOffButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 14, this.topPos + 45, 215, 22, 37, 20);
            else graphics.blit(GUI, this.leftPos + 14, this.topPos + 45, 177, 22, 37, 20);
        }

        if (this.refillOnButton.visible) {
            if (this.refillOnButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 14, this.topPos + 45, 215, 1, 37, 20);
            else graphics.blit(GUI, this.leftPos + 14, this.topPos + 45, 177, 1, 37, 20);
        }

        if (this.countdownBox.visible)
            graphics.blit(GUI, this.leftPos + 57, this.topPos + 24, 177, 43, 78, 14);

        if (this.confirmButton.visible) {
            if (this.confirmButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 141, this.topPos + 45, 198, 58, 20, 20);
            else graphics.blit(GUI, this.leftPos + 141, this.topPos + 45, 177, 58, 20, 20);
        }

        if (this.ticksButton.visible) {
            if (this.menu.getTimeUnit() == 0)
                graphics.blit(GUI, this.leftPos + 77, this.topPos + 48, 209, 79, 15, 16);
            else if (this.ticksButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 77, this.topPos + 48, 193, 79, 15, 16);
            else graphics.blit(GUI, this.leftPos + 77, this.topPos + 48, 177, 79, 15, 16);
        }

        if (this.secondsButton.visible) {
            if (this.menu.getTimeUnit() == 1)
                graphics.blit(GUI, this.leftPos + 92, this.topPos + 48, 207, 96, 14, 16);
            else if (this.secondsButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 92, this.topPos + 48, 192, 96, 14, 16);
            else graphics.blit(GUI, this.leftPos + 92, this.topPos + 48, 177, 96, 14, 16);
        }

        if (this.minuteButton.visible) {
            if (this.menu.getTimeUnit() == 2)
                graphics.blit(GUI, this.leftPos + 106, this.topPos + 48, 207, 96, 14, 16);
            else if (this.minuteButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 106, this.topPos + 48, 192, 96, 14, 16);
            else graphics.blit(GUI, this.leftPos + 106, this.topPos + 48, 177, 96, 14, 16);
        }

        if (this.hourButton.visible) {
            if (this.menu.getTimeUnit() == 3)
                graphics.blit(GUI, this.leftPos + 120, this.topPos + 48, 209, 113, 15, 16);
            else if (this.hourButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 120, this.topPos + 48, 193, 113, 15, 16);
            else graphics.blit(GUI, this.leftPos + 120, this.topPos + 48, 177, 113, 15, 16);
        }

        if (this.showIcon)
            graphics.blit(GUI, this.leftPos + 83, this.topPos + 9, 177, 130, 60, 68);
    }

    @Override
    public void init() {
        super.init();

        final Component refillOffButton = Component.translatable("menu.marioverse.question_block.refill_off_button");
        this.refillOffButton = Button.builder(refillOffButton, button -> {
            this.countdownBox.setVisible(true);
            this.clockButton.visible = true;
            this.confirmButton.visible = true;
            this.refillOffButton.visible = false;
            this.refillOnButton.visible = true;
            this.ticksButton.visible = true;
            this.secondsButton.visible = true;
            this.minuteButton.visible = true;
            this.hourButton.visible = true;
            this.showIcon = false;
            if (this.menu.getRefillCountdown() <= -1) {
                PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
                PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, 5));
            }
        }).bounds(this.leftPos + 14, this.topPos + 45, 37, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.refill_off_button.narrate")).build();
        this.refillOffButton.setAlpha(0);
        this.addRenderableWidget(this.refillOffButton);

        final Component refillOnButton = Component.translatable("menu.marioverse.question_block.refill_on_button");
        this.refillOnButton = Button.builder(refillOnButton, button -> {
            this.countdownBox.setVisible(false);
            this.clockButton.visible = false;
            this.confirmButton.visible = false;
            this.refillOffButton.visible = true;
            this.refillOnButton.visible = false;
            this.ticksButton.visible = false;
            this.secondsButton.visible = false;
            this.minuteButton.visible = false;
            this.hourButton.visible = false;
            this.showIcon = true;
            PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, -1));
        }).bounds(this.leftPos + 14, this.topPos + 45, 37, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.refill_on_button.narrate")).build();
        this.refillOnButton.visible = false;
        this.refillOnButton.setAlpha(0);
        this.addRenderableWidget(this.refillOnButton);

        this.countdownBox = new EditBox(this.font, this.leftPos + 59, this.topPos + 27, 70, 16,
                Component.translatable("menu.marioverse.question_block.countdown_box.narrate"));
        this.countdownBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.question_block.countdown_box.tooltip")));
        this.countdownBox.setValue(String.valueOf(this.menu.getRefillCountdown()));
        this.countdownBox.setFilter(filter -> filter.matches("-?\\d*"));
        this.countdownBox.setBordered(false);
        this.countdownBox.setVisible(false);
        this.countdownBox.setMaxLength(34);
        this.addRenderableWidget(this.countdownBox);

        final Component clockButton = Component.translatable("menu.marioverse.question_block.clock_button");
        this.clockButton = Button.builder(clockButton, button -> {
            this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 143, this.topPos + 23, 16, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.clock_button.narrate")).build();
        this.clockButton.visible = false;
        this.clockButton.setAlpha(0);
        this.addRenderableWidget(this.clockButton);

        final Component ticksButton = Component.translatable("menu.marioverse.question_block.ticks_button");
        this.ticksButton = Button.builder(ticksButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 0));
        }).bounds(this.leftPos + 77, this.topPos + 46, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.ticks_button.narrate")).build();
        this.ticksButton.visible = false;
        this.ticksButton.setAlpha(0);
        this.addRenderableWidget(this.ticksButton);

        final Component secondsButton = Component.translatable("menu.marioverse.question_block.seconds_button");
        this.secondsButton = Button.builder(secondsButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 1));
        }).bounds(this.leftPos + 92, this.topPos + 46, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.seconds_button.narrate")).build();
        this.secondsButton.visible = false;
        this.secondsButton.setAlpha(0);
        this.addRenderableWidget(this.secondsButton);

        final Component minuteButton = Component.translatable("menu.marioverse.question_block.minute_button");
        this.minuteButton = Button.builder(minuteButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
        }).bounds(this.leftPos + 106, this.topPos + 46, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.minute_button.narrate")).build();
        this.minuteButton.visible = false;
        this.minuteButton.setAlpha(0);
        this.addRenderableWidget(this.minuteButton);

        final Component hourButton = Component.translatable("menu.marioverse.question_block.hour_button");
        this.hourButton = Button.builder(hourButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 3));
        }).bounds(this.leftPos + 120, this.topPos + 46, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.hour_button.narrate")).build();
        this.hourButton.visible = false;
        this.hourButton.setAlpha(0);
        this.addRenderableWidget(this.hourButton);

        final Component confirmButton = Component.translatable("menu.marioverse.question_block.confirm_button");
        this.confirmButton = Button.builder(confirmButton, button -> {
            this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 141, this.topPos + 43, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.confirm_button.narrate")).build();
        this.confirmButton.visible = false;
        this.confirmButton.setAlpha(0);
        this.addRenderableWidget(this.confirmButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        int refillCountdown = this.menu.getRefillCountdown();

        if (!this.countdownBox.isFocused() && this.countdownBox.visible)
            this.countdownBox.setValue(String.valueOf(this.menu.convertFromTicks(refillCountdown)));

        if (!this.initializedFromServer) {
            if (refillCountdown >= 0) {
                this.countdownBox.setVisible(true);
                this.clockButton.visible = true;
                this.confirmButton.visible = true;
                this.refillOffButton.visible = false;
                this.refillOnButton.visible = true;
                this.ticksButton.visible = true;
                this.secondsButton.visible = true;
                this.minuteButton.visible = true;
                this.hourButton.visible = true;
                this.showIcon = false;
            } else {
                this.countdownBox.setVisible(false);
                this.clockButton.visible = false;
                this.confirmButton.visible = false;
                this.refillOffButton.visible = true;
                this.refillOnButton.visible = false;
                this.ticksButton.visible = false;
                this.secondsButton.visible = false;
                this.minuteButton.visible = false;
                this.hourButton.visible = false;
                this.showIcon = true;
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

       tooltip = Component.translatable("menu.marioverse.question_block.clock_button.tooltip");
        this.clockButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.confirm_button.tooltip");
        this.confirmButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.ticks_button.tooltip");
        this.ticksButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.seconds_button.tooltip");
        this.secondsButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.minute_button.tooltip");
        this.minuteButton.setTooltip(Tooltip.create(tooltip));

       tooltip = Component.translatable("menu.marioverse.question_block.hour_button.tooltip");
        this.hourButton.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public boolean keyPressed(final int keyCode, final int b, final int c) {
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