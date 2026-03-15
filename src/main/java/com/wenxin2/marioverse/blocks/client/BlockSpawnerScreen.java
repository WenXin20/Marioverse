package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.RefillCountdownPayload;
import com.wenxin2.marioverse.network.server_bound.data.TimeUnitPayload;
import com.wenxin2.marioverse.registries.SoundRegistry;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import org.lwjgl.glfw.GLFW;

public class BlockSpawnerScreen extends AbstractContainerScreen<BlockSpawnerMenu> {
    public static ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/block_spawner.png");
    private boolean showIcon = false;
    private boolean initializedFromServer = false;
    private String blockSpawnerName = "";
    Button clockButton;
    Button confirmButton;
    Button hourButton;
    Button minuteButton;
    Button secondsButton;
    Button ticksButton;
    EditBox countdownBox;
    Inventory inventory;

    public BlockSpawnerScreen(BlockSpawnerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
    }

    @Override
    public void renderLabels(GuiGraphics graphics, int x, int y) {
        if (!this.blockSpawnerName.isEmpty()) // Block Spawner "Name"
            graphics.drawString(this.font, this.blockSpawnerName, this.titleLabelX, this.titleLabelY, 4210752, false);
        else // "Block Spawner"
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

        Component tooltip = null;
        final Component refillOffButton = Component.translatable("menu.marioverse.question_block.refill_off_button");

        this.countdownBox = new EditBox(this.font, this.leftPos + 59, this.topPos + 27, 70, 16,
                Component.translatable("menu.marioverse.question_block.countdown_box.narrate"));
        this.countdownBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.question_block.countdown_box.tooltip")));
        this.countdownBox.setValue(String.valueOf(this.menu.getRefillCountdown()));
        this.countdownBox.setFilter(filter -> filter.matches("-?\\d*"));
        this.countdownBox.setBordered(false);
        this.countdownBox.setVisible(true);
        this.countdownBox.setMaxLength(34);
        this.addRenderableWidget(this.countdownBox);

        final Component clockButton = Component.translatable("menu.marioverse.question_block.clock_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.clock_button.tooltip");
        this.clockButton = Button.builder(clockButton, button -> {
            this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 143, this.topPos + 23, 16, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.clock_button.narrate")).build();
        this.clockButton.visible = true;
        this.clockButton.setAlpha(0);
        this.clockButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.clockButton);

        final Component ticksButton = Component.translatable("menu.marioverse.question_block.ticks_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.ticks_button.tooltip");
        this.ticksButton = Button.builder(ticksButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 0));
        }).bounds(this.leftPos + 77, this.topPos + 48, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.ticks_button.narrate")).build();
        this.ticksButton.visible = true;
        this.ticksButton.setAlpha(0);
        this.ticksButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.ticksButton);

        final Component secondsButton = Component.translatable("menu.marioverse.question_block.seconds_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.seconds_button.tooltip");
        this.secondsButton = Button.builder(secondsButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 1));
        }).bounds(this.leftPos + 92, this.topPos + 48, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.seconds_button.narrate")).build();
        this.secondsButton.visible = true;
        this.secondsButton.setAlpha(0);
        this.secondsButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.secondsButton);

        final Component minuteButton = Component.translatable("menu.marioverse.question_block.minute_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.minute_button.tooltip");
        this.minuteButton = Button.builder(minuteButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
        }).bounds(this.leftPos + 106, this.topPos + 48, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.minute_button.narrate")).build();
        this.minuteButton.visible = true;
        this.minuteButton.setAlpha(0);
        this.minuteButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.minuteButton);

        final Component hourButton = Component.translatable("menu.marioverse.question_block.hour_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.hour_button.tooltip");
        this.hourButton = Button.builder(hourButton, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 3));
        }).bounds(this.leftPos + 120, this.topPos + 48, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.hour_button.narrate")).build();
        this.hourButton.visible = true;
        this.hourButton.setAlpha(0);
        this.hourButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.hourButton);

        final Component confirmButton = Component.translatable("menu.marioverse.question_block.confirm_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.confirm_button.tooltip");
        this.confirmButton = Button.builder(confirmButton, button -> {
            this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 141, this.topPos + 45, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.question_block.confirm_button.narrate")).build();
        this.confirmButton.visible = true;
        this.confirmButton.setAlpha(0);
        this.confirmButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.confirmButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        int refillCountdown = this.menu.getRefillCountdown();

        if (!this.countdownBox.isFocused() && this.countdownBox.visible)
            this.countdownBox.setValue(String.valueOf(this.menu.convertFromTicks(refillCountdown)));
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
        Component tooltip = null;

        if (this.hoveredSlot != null) {
            ItemStack stack = this.hoveredSlot.getItem();
            if (stack.isEmpty()) {
                if (this.hoveredSlot.index == 0) {
                    tooltip = Component.translatable("menu.marioverse.block_spawner.disguise_slot.tooltip");
                    graphics.renderTooltip(this.font, this.font.split(tooltip, 115), mouseX, mouseY);
                    return;
                }
                if (this.hoveredSlot.index == 1) {
                    tooltip = Component.translatable("menu.marioverse.block_spawner.replace_slot.tooltip");
                    graphics.renderTooltip(this.font, this.font.split(tooltip, 115), mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderTooltip(graphics, mouseX, mouseY);
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