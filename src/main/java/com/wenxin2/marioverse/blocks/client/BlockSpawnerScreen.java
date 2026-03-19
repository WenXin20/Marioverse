package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.slots.GhostSlot;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.BlockFacePayload;
import com.wenxin2.marioverse.network.server_bound.data.MenuTypePayload;
import com.wenxin2.marioverse.network.server_bound.data.PlacementDirectionPayload;
import com.wenxin2.marioverse.network.server_bound.data.PlacementOffsetPayload;
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
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

public class BlockSpawnerScreen extends AbstractContainerScreen<BlockSpawnerMenu> {
    public static ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/block_spawner.png");
    private int lastMenuType = -1;
    private String blockSpawnerName = "";
    Button clockButton;
    Button confirmButton;
    Button disguiseButton;
    Button hourButton;
    Button minuteButton;
    Button northBlockFaceButton;
    Button northButton;
    Button placementButton;
    Button replaceButton;
    Button secondsButton;
    Button ticksButton;
    EditBox countdownBox;
    EditBox placementOffsetBox;
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

        this.updateSlotPositions();
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

        if (this.clockButton.visible && this.menu.getMenuType() == 0) {
            if (this.clockButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 126, this.topPos + 22, uOffset, 184, 16, 16);
            else graphics.blit(GUI, this.leftPos + 126, this.topPos + 22, uOffset, 167, 16, 16);
        }

        if (this.countdownBox.visible && this.menu.getMenuType() == 0)
            graphics.blit(GUI, this.leftPos + 41, this.topPos + 24, 177, 0, 78, 14);

        if (this.placementOffsetBox.visible && this.menu.getMenuType() == 1)
            graphics.blit(GUI, this.leftPos + 107, this.topPos + 34, 177, 15, 38, 18);

        if (this.confirmButton.visible) {
            if (this.confirmButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 124, this.topPos + 46, 198, 97, 20, 20);
            else graphics.blit(GUI, this.leftPos + 124, this.topPos + 46, 177, 97, 20, 20);
        }

        if (this.replaceButton.visible) {
            if (this.menu.getMenuType() == 0)
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 8, 219, 34, 20, 20);
            else if (this.replaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 8, 198, 34, 20, 20);
            else graphics.blit(GUI, this.leftPos + 149, this.topPos + 8, 177, 34, 20, 20);
        }

        if (this.placementButton.visible) {
            if (this.menu.getMenuType() == 1)
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 33, 219, 55, 20, 20);
            else if (this.placementButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 33, 198, 55, 20, 20);
            else graphics.blit(GUI, this.leftPos + 149, this.topPos + 33, 177, 55, 20, 20);
        }

        if (this.disguiseButton.visible) {
            if (this.menu.getMenuType() == 2)
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 58, 219, 76, 20, 20);
            else if (this.disguiseButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 58, 198, 76, 20, 20);
            else graphics.blit(GUI, this.leftPos + 149, this.topPos + 58, 177, 76, 20, 20);
        }

        if (this.ticksButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 0)
                graphics.blit(GUI, this.leftPos + 61, this.topPos + 48, 209, 139, 15, 16);
            else if (this.ticksButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 61, this.topPos + 48, 193, 139, 15, 16);
            else graphics.blit(GUI, this.leftPos + 61, this.topPos + 48, 177, 139, 15, 16);
        }

        if (this.secondsButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 1)
                graphics.blit(GUI, this.leftPos + 76, this.topPos + 48, 207, 156, 14, 16);
            else if (this.secondsButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 76, this.topPos + 48, 192, 156, 14, 16);
            else graphics.blit(GUI, this.leftPos + 76, this.topPos + 48, 177, 156, 14, 16);
        }

        if (this.minuteButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 2)
                graphics.blit(GUI, this.leftPos + 90, this.topPos + 48, 207, 156, 14, 16);
            else if (this.minuteButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 90, this.topPos + 48, 192, 156, 14, 16);
            else graphics.blit(GUI, this.leftPos + 90, this.topPos + 48, 177, 156, 14, 16);
        }

        if (this.hourButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 3)
                graphics.blit(GUI, this.leftPos + 104, this.topPos + 48, 209, 173, 15, 16);
            else if (this.hourButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 104, this.topPos + 48, 193, 173, 15, 16);
            else graphics.blit(GUI, this.leftPos + 104, this.topPos + 48, 177, 173, 15, 16);
        }

        if (this.northButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 2)
                graphics.blit(GUI, this.leftPos + 58, this.topPos + 6, 34, 201, 16, 22);
            else if (this.northButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 58, this.topPos + 6, 17, 201, 16, 22);
            else graphics.blit(GUI, this.leftPos + 58, this.topPos + 6, 0, 201, 16, 22);
        }

        if (this.northBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 2)
                graphics.blit(GUI, this.leftPos + 59, this.topPos + 29, 102, 211, 14, 4);
            else if (this.northBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 59, this.topPos + 29, 102, 206, 14, 4);
            else graphics.blit(GUI, this.leftPos + 59, this.topPos + 29, 102, 201, 14, 4);
        }

        if (this.menu.getMenuType() == 0 || this.menu.getMenuType() == 2) {
            // Replace Slot
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 21, 216, 15, 18, 18);
            // Disguise Slot
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 47, 216, 15, 18, 18);
        } else {
            graphics.blit(GUI, this.leftPos + 57, this.topPos + 34, 216, 15, 18, 18);
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 34, 216, 15, 18, 18);
        }
    }

    @Override
    public void init() { // Order buttons/widgets initialized is the order the 'tab' key will select it
        super.init();
        int menuType = this.menu.getMenuType();

        Component tooltip = Component.literal("");
        Component buttonName = Component.literal("");

        this.countdownBox = new EditBox(this.font, this.leftPos + 43, this.topPos + 27, 70, 16,
                Component.translatable("menu.marioverse.block_spawner.countdown_box.narrate"));
        this.countdownBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.block_spawner.countdown_box.tooltip")));
        this.countdownBox.setFilter(filter -> filter.matches("-?\\d*"));
        this.countdownBox.setBordered(false);
        this.countdownBox.setMaxLength(34);
        this.addRenderableWidget(this.countdownBox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.clock_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.clock_button.tooltip");
        this.clockButton = Button.builder(buttonName, button -> {
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 126, this.topPos + 22, 16, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.clock_button.narrate")).build();
        this.clockButton.setAlpha(0);
        this.clockButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.clockButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.ticks_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.ticks_button.tooltip");
        this.ticksButton = Button.builder(buttonName, button -> {
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 0));
        }).bounds(this.leftPos + 61, this.topPos + 48, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.ticks_button.narrate")).build();
        this.ticksButton.setAlpha(0);
        this.ticksButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.ticksButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.seconds_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.seconds_button.tooltip");
        this.secondsButton = Button.builder(buttonName, button -> {
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 1));
        }).bounds(this.leftPos + 76, this.topPos + 48, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.seconds_button.narrate")).build();
        this.secondsButton.setAlpha(0);
        this.secondsButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.secondsButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.minute_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.minute_button.tooltip");
        this.minuteButton = Button.builder(buttonName, button -> {
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
        }).bounds(this.leftPos + 90, this.topPos + 48, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.minute_button.narrate")).build();
        this.minuteButton.setAlpha(0);
        this.minuteButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.minuteButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.hour_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.hour_button.tooltip");
        this.hourButton = Button.builder(buttonName, button -> {
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 3));
        }).bounds(this.leftPos + 104, this.topPos + 48, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.hour_button.narrate")).build();
        this.hourButton.setAlpha(0);
        this.hourButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.hourButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.confirm_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.confirm_button.tooltip");
        this.confirmButton = Button.builder(buttonName, button -> {
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 124, this.topPos + 46, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.confirm_button.narrate")).build();
        this.confirmButton.setAlpha(0);
        this.confirmButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.confirmButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.north_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.north_button.tooltip");
        this.northButton = Button.builder(buttonName, button -> {
            int menuType2 = this.menu.getMenuType();
            if (menuType2 == 1)
                this.directionButtonOnPress(2);
        }).bounds(this.leftPos + 58, this.topPos + 6, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.north_button.narrate")).build();
        this.northButton.setAlpha(0);
        this.northButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.northButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.north_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.north_block_face_button.tooltip");
        this.northBlockFaceButton = Button.builder(buttonName, button -> {
                    int menuType2 = this.menu.getMenuType();
            if (menuType2 == 1)
                this.blockFaceButtonOnPress(2);
        }).bounds(this.leftPos + 59, this.topPos + 29, 14, 4)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.north_block_face_button.narrate")).build();
        this.northBlockFaceButton.setAlpha(0);
        this.northBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.northBlockFaceButton);

        this.placementOffsetBox = new EditBox(this.font, this.leftPos + 109, this.topPos + 40, 30, 18,
                Component.translatable("menu.marioverse.block_spawner.placement_offset_box.narrate"));
        this.placementOffsetBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.block_spawner.placement_offset_box.tooltip")));
        this.placementOffsetBox.setFilter(filter -> filter.matches("[0-9]\\d*") || filter.isEmpty());
        this.placementOffsetBox.setBordered(false);
        this.placementOffsetBox.setMaxLength(15);
        this.addRenderableWidget(this.placementOffsetBox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.replace_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.replace_button.tooltip");
        this.replaceButton = Button.builder(buttonName, button -> {
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            this.replaceButtonOnPress();
        }).bounds(this.leftPos + 149, this.topPos + 8, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.replace_button.narrate")).build();
        this.replaceButton.setAlpha(0);
        this.replaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.replaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.placement_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.placement_button.tooltip");
        this.placementButton = Button.builder(buttonName, button -> {
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            this.placementButtonOnPress();
        }).bounds(this.leftPos + 149, this.topPos + 33, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.placement_button.narrate")).build();
        this.placementButton.setAlpha(0);
        this.placementButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.placementButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.disguise_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.disguise_button.tooltip");
        this.disguiseButton = Button.builder(buttonName, button -> {
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            PacketHandler.sendToServer(new MenuTypePayload(this.menu.containerId, 2));
        }).bounds(this.leftPos + 149, this.topPos + 58, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.disguise_button.narrate")).build();
        this.disguiseButton.setAlpha(0);
        this.disguiseButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.disguiseButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int placementOffset = this.menu.getPlacementOffset();
        int refillCountdown = this.menu.getRefillCountdown();
        int menuType = this.menu.getMenuType();


        if (menuType != this.lastMenuType) {
            this.lastMenuType = menuType;

            if (menuType == 0)
                this.countdownBox.setValue(String.valueOf(this.menu.convertFromTicks(refillCountdown)));
            if (menuType == 1)
                this.placementOffsetBox.setValue(String.valueOf(placementOffset));
        }

        if (!this.countdownBox.isFocused() && menuType == 0)
            this.countdownBox.setValue(String.valueOf(this.menu.convertFromTicks(refillCountdown)));

        if (!this.placementOffsetBox.isFocused() && menuType == 1)
            this.placementOffsetBox.setValue(String.valueOf(placementOffset));

        this.clockButton.visible = menuType == 0;
        this.confirmButton.visible = menuType == 0;
        this.ticksButton.visible = menuType == 0;
        this.secondsButton.visible = menuType == 0;
        this.minuteButton.visible = menuType == 0;
        this.hourButton.visible = menuType == 0;
        this.countdownBox.setVisible(menuType == 0);

        this.placementOffsetBox.setVisible(menuType == 1);
        this.northBlockFaceButton.visible = menuType == 1;
        this.northButton.visible = menuType == 1;
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

        if (this.hoveredSlot instanceof GhostSlot ghostSlot && ghostSlot.getItem().isEmpty()) {
            if (ghostSlot.getContainerSlot() == 0) {
                tooltip = Component.translatable("menu.marioverse.block_spawner.disguise_slot.tooltip");
                graphics.renderTooltip(this.font, this.font.split(tooltip, 115), mouseX, mouseY);
                return;
            }
            if (ghostSlot.getContainerSlot() == 1) {
                tooltip = Component.translatable("menu.marioverse.block_spawner.replace_slot.tooltip");
                graphics.renderTooltip(this.font, this.font.split(tooltip, 115), mouseX, mouseY);
                return;
            }
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int b, final int c) {
        if (this.countdownBox.isFocused() || this.placementOffsetBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                    if (this.countdownBox.isFocused())
                        this.confirmButtonOnPress();
                    if (this.placementOffsetBox.isFocused())
                        this.placementOffsetOnPress();
                }
                if (this.countdownBox.isFocused())
                    this.countdownBox.setFocused(false);
                if (this.placementOffsetBox.isFocused())
                    this.placementOffsetBox.setFocused(false);
                return false;
            }
        }

        if (this.countdownBox.isFocused() || this.placementOffsetBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_E) {
                if (this.countdownBox.isFocused())
                    this.countdownBox.setFocused(true);
                if (this.placementOffsetBox.isFocused())
                    this.placementOffsetBox.setFocused(true);
                return true;
            }
        }
        return super.keyPressed(keyCode, b, c);
    }

    private void blockFaceButtonOnPress(int blockFace) {
        PacketHandler.sendToServer(new BlockFacePayload(this.menu.containerId, blockFace));
    }

    private void confirmButtonOnPress() {
        String value = this.countdownBox.getValue();
        int parsed = -1;

        if (!value.isEmpty() && !value.equals("-"))
            parsed = Integer.parseInt(value);

        if (this.minecraft != null && this.minecraft.getConnection() != null)
            PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, parsed));
    }

    private void directionButtonOnPress(int placementDirection) {
        PacketHandler.sendToServer(new PlacementDirectionPayload(this.menu.containerId, placementDirection));
    }

    private void placementOffsetOnPress() {
        String value = this.placementOffsetBox.getValue();
        int parsed = 1;

        if (!value.isEmpty())
            parsed = Integer.parseInt(value);

        if (this.minecraft != null && this.minecraft.getConnection() != null)
            PacketHandler.sendToServer(new PlacementOffsetPayload(this.menu.containerId, parsed));
    }

    private void replaceButtonOnPress() {
        PacketHandler.sendToServer(new MenuTypePayload(this.menu.containerId, 0));
    }

    private void placementButtonOnPress() {
        PacketHandler.sendToServer(new MenuTypePayload(this.menu.containerId, 1));
    }

    private void updateSlotPositions() {
        int type = this.menu.getMenuType();

        Slot blockSlot = this.menu.getBlockSlot();
        Slot disguiseSlot = this.menu.getDisguiseSlot();

        if (type == 0 || type == 2) {
            this.setSlotPos(blockSlot, 8, 22);
            this.setSlotPos(disguiseSlot, 8, 48);
        } else if (type == 1) {
            this.setSlotPos(blockSlot, 58, 35);
            this.setSlotPos(disguiseSlot, 8, 35);
        }
    }

    private void setSlotPos(Slot slot, int x, int y) {
        try {
            var xField = Slot.class.getDeclaredField("x");
            var yField = Slot.class.getDeclaredField("y");

            xField.setAccessible(true);
            yField.setAccessible(true);

            xField.setInt(slot, x);
            yField.setInt(slot, y);

        } catch (Exception e) {
            try {
                var xField = Slot.class.getDeclaredField("xPos");
                var yField = Slot.class.getDeclaredField("yPos");

                xField.setAccessible(true);
                yField.setAccessible(true);

                xField.setInt(slot, x);
                yField.setInt(slot, y);
            } catch (Exception ignored) {}
        }
    }
}