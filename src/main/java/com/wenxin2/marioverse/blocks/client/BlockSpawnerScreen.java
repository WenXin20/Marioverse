package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.ResizableCheckbox;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.slots.GhostSlot;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.BlockFacePayload;
import com.wenxin2.marioverse.network.server_bound.data.FacingDirectionPayload;
import com.wenxin2.marioverse.network.server_bound.data.HasCollisionPayload;
import com.wenxin2.marioverse.network.server_bound.data.IsInteractablePayload;
import com.wenxin2.marioverse.network.server_bound.data.IsRightClickablePayload;
import com.wenxin2.marioverse.network.server_bound.data.IsSneakingPayload;
import com.wenxin2.marioverse.network.server_bound.data.IsUnbreakablePayload;
import com.wenxin2.marioverse.network.server_bound.data.MenuTypePayload;
import com.wenxin2.marioverse.network.server_bound.data.PlacementDirectionPayload;
import com.wenxin2.marioverse.network.server_bound.data.PlacementOffsetPayload;
import com.wenxin2.marioverse.network.server_bound.data.RefillCountdownPayload;
import com.wenxin2.marioverse.network.server_bound.data.TimeUnitPayload;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
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
    private boolean showLine;
    private boolean showDisguiseIcon;
    private int lastMenuType = -1;
    private String blockSpawnerName = "";

    Button topBlockFaceButton;
    Button bottomBlockFaceButton;
    Button northBlockFaceButton;
    Button southBlockFaceButton;
    Button eastBlockFaceButton;
    Button westBlockFaceButton;

    Button faceUpButton;
    Button faceDownButton;
    Button faceNorthButton;
    Button faceSouthButton;
    Button faceEastButton;
    Button faceWestButton;

    Button upButton;
    Button downButton;
    Button northButton;
    Button southButton;
    Button eastButton;
    Button westButton;

    Button ticksButton;
    Button secondsButton;
    Button hourButton;
    Button minuteButton;

    Button clockButton;
    Button confirmButton;
    Button disguiseButton;
    Button placementButton;
    Button replaceButton;

    EditBox countdownBox;
    EditBox placementOffsetBox;
    Inventory inventory;
    ResizableCheckbox collisionCheckbox;
    ResizableCheckbox interactableCheckbox;
    ResizableCheckbox rightClickableCheckbox;
    ResizableCheckbox sneakingCheckbox;
    ResizableCheckbox unbreakableCheckbox;

    public BlockSpawnerScreen(BlockSpawnerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
        this.imageWidth = 176;
        this.imageHeight = 182;
    }

    @Override
    public void renderLabels(GuiGraphics graphics, int x, int y) {
        if (!this.blockSpawnerName.isEmpty()) // Block Spawner "Name"
            graphics.drawString(this.font, this.blockSpawnerName, this.titleLabelX, this.titleLabelY - 1, 4210752, false);
        else // "Block Spawner"
            graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY - 1, 4210752, false);

        // Inventory
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 17, 4210752, false);
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
                graphics.blit(GUI, this.leftPos + 126, this.topPos + 30, uOffset, 200, 16, 16);
            else graphics.blit(GUI, this.leftPos + 126, this.topPos + 30, uOffset, 183, 16, 16);
        }

        if (this.countdownBox.visible && this.menu.getMenuType() == 0)
            graphics.blit(GUI, this.leftPos + 41, this.topPos + 32, 177, 0, 78, 14);

        if (this.placementOffsetBox.visible && this.menu.getMenuType() == 1)
            graphics.blit(GUI, this.leftPos + 107, this.topPos + 42, 177, 15, 38, 18);

        if (this.confirmButton.visible) {
            if (this.confirmButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 124, this.topPos + 54, 198, 97, 20, 20);
            else graphics.blit(GUI, this.leftPos + 124, this.topPos + 54, 177, 97, 20, 20);
        }

        if (this.replaceButton.visible) {
            if (this.menu.getMenuType() == 0)
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 16, 219, 34, 20, 20);
            else if (this.replaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 16, 198, 34, 20, 20);
            else graphics.blit(GUI, this.leftPos + 149, this.topPos + 16, 177, 34, 20, 20);
        }

        if (this.placementButton.visible) {
            if (this.menu.getMenuType() == 1)
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 41, 219, 55, 20, 20);
            else if (this.placementButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 41, 198, 55, 20, 20);
            else graphics.blit(GUI, this.leftPos + 149, this.topPos + 41, 177, 55, 20, 20);
        }

        if (this.disguiseButton.visible) {
            if (this.menu.getMenuType() == 2)
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 66, 219, 76, 20, 20);
            else if (this.disguiseButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 149, this.topPos + 66, 198, 76, 20, 20);
            else graphics.blit(GUI, this.leftPos + 149, this.topPos + 66, 177, 76, 20, 20);
        }

        if (this.ticksButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 0)
                graphics.blit(GUI, this.leftPos + 61, this.topPos + 56, 209, 139, 15, 16);
            else if (this.ticksButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 61, this.topPos + 56, 193, 139, 15, 16);
            else graphics.blit(GUI, this.leftPos + 61, this.topPos + 56, 177, 139, 15, 16);
        }

        if (this.secondsButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 1)
                graphics.blit(GUI, this.leftPos + 76, this.topPos + 56, 207, 156, 14, 16);
            else if (this.secondsButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 76, this.topPos + 56, 192, 156, 14, 16);
            else graphics.blit(GUI, this.leftPos + 76, this.topPos + 56, 177, 156, 14, 16);
        }

        if (this.minuteButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 2)
                graphics.blit(GUI, this.leftPos + 90, this.topPos + 56, 207, 156, 14, 16);
            else if (this.minuteButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 90, this.topPos + 56, 192, 156, 14, 16);
            else graphics.blit(GUI, this.leftPos + 90, this.topPos + 56, 177, 156, 14, 16);
        }

        if (this.hourButton.visible && this.menu.getMenuType() == 0) {
            if (this.menu.getTimeUnit() == 3)
                graphics.blit(GUI, this.leftPos + 104, this.topPos + 56, 209, 173, 15, 16);
            else if (this.hourButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 104, this.topPos + 56, 193, 173, 15, 16);
            else graphics.blit(GUI, this.leftPos + 104, this.topPos + 56, 177, 173, 15, 16);
        }

        if (this.northButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 2)
                graphics.blit(GUI, this.leftPos + 58, this.topPos + 14, 34, 217, 16, 22);
            else if (this.northButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 58, this.topPos + 14, 17, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 58, this.topPos + 14, 0, 217, 16, 22);
        }

        if (this.northBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 2)
                graphics.blit(GUI, this.leftPos + 59, this.topPos + 37, 102, 227, 14, 4);
            else if (this.northBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 59, this.topPos + 37, 102, 222, 14, 4);
            else graphics.blit(GUI, this.leftPos + 59, this.topPos + 37, 102, 217, 14, 4);
        }

        if (this.southButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 3)
                graphics.blit(GUI, this.leftPos + 58, this.topPos + 66, 85, 217, 16, 22);
            else if (this.southButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 58, this.topPos + 66, 68, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 58, this.topPos + 66, 51, 217, 16, 22);
        }

        if (this.southBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 3)
                graphics.blit(GUI, this.leftPos + 59, this.topPos + 61, 102, 227, 14, 4);
            else if (this.southBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 59, this.topPos + 61, 102, 222, 14, 4);
            else graphics.blit(GUI, this.leftPos + 59, this.topPos + 61, 102, 217, 14, 4);
        }

        if (this.eastButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 4)
                graphics.blit(GUI, this.leftPos + 81, this.topPos + 43, 115, 240, 22, 16);
            else if (this.eastButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 81, this.topPos + 43, 92, 240, 22, 16);
            else graphics.blit(GUI, this.leftPos + 81, this.topPos + 43, 69, 240, 22, 16);
        }

        if (this.eastBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 4)
                graphics.blit(GUI, this.leftPos + 76, this.topPos + 44, 127, 217, 4, 14);
            else if (this.eastBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 76, this.topPos + 44, 122, 217, 4, 14);
            else graphics.blit(GUI, this.leftPos + 76, this.topPos + 44, 117, 217, 4, 14);
        }

        if (this.westButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 5)
                graphics.blit(GUI, this.leftPos + 29, this.topPos + 43, 46, 240, 22, 16);
            else if (this.westButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 29, this.topPos + 43, 23, 240, 22, 16);
            else graphics.blit(GUI, this.leftPos + 29, this.topPos + 43, 0, 240, 22, 16);
        }

        if (this.westBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 5)
                graphics.blit(GUI, this.leftPos + 52, this.topPos + 44, 127, 217, 4, 14);
            else if (this.westBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 52, this.topPos + 44, 122, 217, 4, 14);
            else graphics.blit(GUI, this.leftPos + 52, this.topPos + 44, 117, 217, 4, 14);
        }

        if (this.upButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 0)
                graphics.blit(GUI, this.leftPos + 118, this.topPos + 14, 34, 217, 16, 22);
            else if (this.upButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 118, this.topPos + 14, 17, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 118, this.topPos + 14, 0, 217, 16, 22);
        }

        if (this.topBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 0)
                graphics.blit(GUI, this.leftPos + 119, this.topPos + 37, 102, 227, 14, 4);
            else if (this.topBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 119, this.topPos + 37, 102, 222, 14, 4);
            else graphics.blit(GUI, this.leftPos + 119, this.topPos + 37, 102, 217, 14, 4);
        }

        if (this.downButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getPlacementDirection() == 1)
                graphics.blit(GUI, this.leftPos + 118, this.topPos + 66, 85, 217, 16, 22);
            else if (this.downButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 118, this.topPos + 66, 68, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 118, this.topPos + 66, 51, 217, 16, 22);
        }

        if (this.bottomBlockFaceButton.visible && this.menu.getMenuType() == 1) {
            if (this.menu.getBlockFace() == 1)
                graphics.blit(GUI, this.leftPos + 119, this.topPos + 61, 102, 227, 14, 4);
            else if (this.bottomBlockFaceButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 119, this.topPos + 61, 102, 222, 14, 4);
            else graphics.blit(GUI, this.leftPos + 119, this.topPos + 61, 102, 217, 14, 4);
        }

        if (this.faceNorthButton.visible && this.menu.getMenuType() == 2) {
            if (this.menu.getFacingDirection() == 2)
                graphics.blit(GUI, this.leftPos + 62, this.topPos + 18, 34, 217, 16, 22);
            else if (this.faceNorthButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 62, this.topPos + 18, 17, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 62, this.topPos + 18, 0, 217, 16, 22);
        }

        if (this.faceSouthButton.visible && this.menu.getMenuType() == 2) {
            if (this.menu.getFacingDirection() == 3)
                graphics.blit(GUI, this.leftPos + 62, this.topPos + 62, 85, 217, 16, 22);
            else if (this.faceSouthButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 62, this.topPos + 62, 68, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 62, this.topPos + 62, 51, 217, 16, 22);
        }

        if (this.faceEastButton.visible && this.menu.getMenuType() == 2) {
            if (this.menu.getFacingDirection() == 4)
                graphics.blit(GUI, this.leftPos + 81, this.topPos + 43, 115, 240, 22, 16);
            else if (this.faceEastButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 81, this.topPos + 43, 92, 240, 22, 16);
            else graphics.blit(GUI, this.leftPos + 81, this.topPos + 43, 69, 240, 22, 16);
        }

        if (this.faceWestButton.visible && this.menu.getMenuType() == 2) {
            if (this.menu.getFacingDirection() == 5)
                graphics.blit(GUI, this.leftPos + 37, this.topPos + 43, 46, 240, 22, 16);
            else if (this.faceWestButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 37, this.topPos + 43, 23, 240, 22, 16);
            else graphics.blit(GUI, this.leftPos + 37, this.topPos + 43, 0, 240, 22, 16);
        }

        if (this.faceUpButton.visible && this.menu.getMenuType() == 2) {
            if (this.menu.getFacingDirection() == 0)
                graphics.blit(GUI, this.leftPos + 107, this.topPos + 18, 34, 217, 16, 22);
            else if (this.faceUpButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 107, this.topPos + 18, 17, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 107, this.topPos + 18, 0, 217, 16, 22);
        }

        if (this.faceDownButton.visible && this.menu.getMenuType() == 2) {
            if (this.menu.getFacingDirection() == 1)
                graphics.blit(GUI, this.leftPos + 107, this.topPos + 62, 85, 217, 16, 22);
            else if (this.faceDownButton.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 107, this.topPos + 62, 68, 217, 16, 22);
            else graphics.blit(GUI, this.leftPos + 107, this.topPos + 62, 51, 217, 16, 22);
        }

        if (this.unbreakableCheckbox.visible && this.menu.getMenuType() == 1) {
            if (this.menu.isUnbreakable() == 1 && this.unbreakableCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 6, this.topPos + 62, 244, 24, 10, 8);
            else if (this.menu.isUnbreakable() == 1)
                graphics.blit(GUI, this.leftPos + 6, this.topPos + 62, 244, 15, 10, 8);
            else if (this.unbreakableCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 7, this.topPos + 62, 235, 24, 8, 8);
            else graphics.blit(GUI, this.leftPos + 7, this.topPos + 62, 235, 15, 8, 8);
        }

        if (this.rightClickableCheckbox.visible && this.menu.getMenuType() == 1) {
            if (this.menu.isRightClickable() == 1 && this.rightClickableCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 16, this.topPos + 62, 244, 24, 10, 8);
            else if (this.menu.isRightClickable() == 1)
                graphics.blit(GUI, this.leftPos + 16, this.topPos + 62, 244, 15, 10, 8);
            else if (this.rightClickableCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 17, this.topPos + 62, 235, 24, 8, 8);
            else graphics.blit(GUI, this.leftPos + 17, this.topPos + 62, 235, 15, 8, 8);
        }

        if (this.interactableCheckbox.visible && this.menu.getMenuType() == 1) {
            if (this.menu.isInteractable() == 1 && this.interactableCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 6, this.topPos + 72, 244, 24, 10, 8);
            else if (this.menu.isInteractable() == 1)
                graphics.blit(GUI, this.leftPos + 6, this.topPos + 72, 244, 15, 10, 8);
            else if (this.interactableCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 7, this.topPos + 72, 235, 24, 8, 8);
            else graphics.blit(GUI, this.leftPos + 7, this.topPos + 72, 235, 15, 8, 8);
        }

        if (this.collisionCheckbox.visible && this.menu.getMenuType() == 1) {
            if (this.menu.hasCollision() == 1 && this.collisionCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 16, this.topPos + 72, 244, 24, 10, 8);
            else if (this.menu.hasCollision() == 1)
                graphics.blit(GUI, this.leftPos + 16, this.topPos + 72, 244, 15, 10, 8);
            else if (this.collisionCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 17, this.topPos + 72, 235, 24, 8, 8);
            else graphics.blit(GUI, this.leftPos + 17, this.topPos + 72, 235, 15, 8, 8);
        }

        if (this.sneakingCheckbox.visible && this.menu.getMenuType() == 2) {
            if (this.menu.isSneaking() == 1 && this.sneakingCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 136, this.topPos + 72, 244, 24, 10, 8);
            else if (this.menu.isSneaking() == 1)
                graphics.blit(GUI, this.leftPos + 136, this.topPos + 72, 244, 15, 10, 8);
            else if (this.sneakingCheckbox.isHoveredOrFocused())
                graphics.blit(GUI, this.leftPos + 137, this.topPos + 72, 235, 24, 8, 8);
            else graphics.blit(GUI, this.leftPos + 137, this.topPos + 72, 235, 15, 8, 8);
        }

        if (this.showLine)
            graphics.blit(GUI, this.leftPos + 26, this.topPos + 16, 240, 34, 2, 70);

        if (this.showDisguiseIcon) {
            graphics.blit(GUI, this.leftPos + 61, this.topPos + 42, 219, 97, 18, 18);
            graphics.blit(GUI, this.leftPos + 106, this.topPos + 42, 219, 97, 18, 18);
        }

        if (this.menu.getMenuType() == 0 || this.menu.getMenuType() == 2) {
            // Replace Slot
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 29, 216, 15, 18, 18);
            // Disguise Slot
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 55, 216, 15, 18, 18);
        } else {
            graphics.blit(GUI, this.leftPos + 57, this.topPos + 42, 216, 15, 18, 18);
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 42, 216, 15, 18, 18);
        }
    }

    @Override
    public void init() { // Order buttons/widgets initialized is the order the 'tab' key will select it
        super.init();

        Component tooltip = Component.literal("");
        Component buttonName = Component.literal("");

        this.countdownBox = new EditBox(this.font, this.leftPos + 43, this.topPos + 35, 70, 16,
                Component.translatable("menu.marioverse.block_spawner.countdown_box.narrate"));
        this.countdownBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.block_spawner.countdown_box.tooltip")));
        this.countdownBox.setFilter(filter -> filter.matches("-?\\d*"));
        this.countdownBox.setBordered(false);
        this.countdownBox.setMaxLength(34);
        this.addRenderableWidget(this.countdownBox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.clock_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.clock_button.tooltip");
        this.clockButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 126, this.topPos + 30, 16, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.clock_button.narrate")).build();
        this.clockButton.setAlpha(0);
        this.clockButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.clockButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.ticks_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.ticks_button.tooltip");
        this.ticksButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 0));
        }).bounds(this.leftPos + 61, this.topPos + 56, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.ticks_button.narrate")).build();
        this.ticksButton.setAlpha(0);
        this.ticksButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.ticksButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.seconds_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.seconds_button.tooltip");
        this.secondsButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 1));
        }).bounds(this.leftPos + 76, this.topPos + 56, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.seconds_button.narrate")).build();
        this.secondsButton.setAlpha(0);
        this.secondsButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.secondsButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.minute_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.minute_button.tooltip");
        this.minuteButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
        }).bounds(this.leftPos + 90, this.topPos + 56, 14, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.minute_button.narrate")).build();
        this.minuteButton.setAlpha(0);
        this.minuteButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.minuteButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.hour_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.hour_button.tooltip");
        this.hourButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 3));
        }).bounds(this.leftPos + 104, this.topPos + 56, 15, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.hour_button.narrate")).build();
        this.hourButton.setAlpha(0);
        this.hourButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.hourButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.confirm_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.confirm_button.tooltip");
        this.confirmButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }).bounds(this.leftPos + 124, this.topPos + 54, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.confirm_button.narrate")).build();
        this.confirmButton.setAlpha(0);
        this.confirmButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.confirmButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.unbreakable_checkbox");
        tooltip = Component.translatable("menu.marioverse.block_spawner.unbreakable_checkbox.tooltip");
        this.unbreakableCheckbox = ResizableCheckbox.builder(buttonName, this.font).onValueChange(this::isUnbreakableCheckbox)
                .pos(this.leftPos + 7, this.topPos + 62).setSize(8)
                .tooltip(Tooltip.create(tooltip)).build();
        this.unbreakableCheckbox.setAlpha(0);
        this.addRenderableWidget(this.unbreakableCheckbox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.right_clickable_checkbox");
        tooltip = Component.translatable("menu.marioverse.block_spawner.right_clickable_checkbox.tooltip");
        this.rightClickableCheckbox = ResizableCheckbox.builder(buttonName, this.font).onValueChange(this::isRightClickableCheckbox)
                .pos(this.leftPos + 17, this.topPos + 62).setSize(8)
                .tooltip(Tooltip.create(tooltip)).build();
        this.rightClickableCheckbox.setAlpha(0);
        this.addRenderableWidget(this.rightClickableCheckbox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.interactable_checkbox");
        tooltip = Component.translatable("menu.marioverse.block_spawner.interactable_checkbox.tooltip");
        this.interactableCheckbox = ResizableCheckbox.builder(buttonName, this.font).onValueChange(this::isInteractableCheckbox)
                .pos(this.leftPos + 7, this.topPos + 72).setSize(8)
                .tooltip(Tooltip.create(tooltip)).build();
        this.interactableCheckbox.setAlpha(0);
        this.addRenderableWidget(this.interactableCheckbox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.collision_checkbox");
        tooltip = Component.translatable("menu.marioverse.block_spawner.collision_checkbox.tooltip");
        this.collisionCheckbox = ResizableCheckbox.builder(buttonName, this.font).onValueChange(this::hasCollisionCheckbox)
                .pos(this.leftPos + 17, this.topPos + 72).setSize(8)
                .tooltip(Tooltip.create(tooltip)).build();
        this.collisionCheckbox.setAlpha(0);
        this.addRenderableWidget(this.collisionCheckbox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.north_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.north_button.tooltip");
        this.northButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(2);
        }).bounds(this.leftPos + 58, this.topPos + 14, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.north_button.narrate")).build();
        this.northButton.setAlpha(0);
        this.northButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.northButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.north_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.north_block_face_button.tooltip");
        this.northBlockFaceButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(2);
        }).bounds(this.leftPos + 59, this.topPos + 37, 14, 4)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.north_block_face_button.narrate")).build();
        this.northBlockFaceButton.setAlpha(0);
        this.northBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.northBlockFaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.east_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.east_button.tooltip");
        this.eastButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(4);
        }).bounds(this.leftPos + 81, this.topPos + 43, 22, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.east_button.narrate")).build();
        this.eastButton.setAlpha(0);
        this.eastButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.eastButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.east_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.east_block_face_button.tooltip");
        this.eastBlockFaceButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(4);
        }).bounds(this.leftPos + 76, this.topPos + 44, 4, 14)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.east_block_face_button.narrate")).build();
        this.eastBlockFaceButton.setAlpha(0);
        this.eastBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.eastBlockFaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.south_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.south_button.tooltip");
        this.southButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(3);
        }).bounds(this.leftPos + 58, this.topPos + 66, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.south_button.narrate")).build();
        this.southButton.setAlpha(0);
        this.southButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.southButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.south_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.south_block_face_button.tooltip");
        this.southBlockFaceButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(3);
        }).bounds(this.leftPos + 59, this.topPos + 61, 14, 4)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.south_block_face_button.narrate")).build();
        this.southBlockFaceButton.setAlpha(0);
        this.southBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.southBlockFaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.west_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.west_button.tooltip");
        this.westButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 1)
                        this.placementDirectionButtonOnPress(5);
                }).bounds(this.leftPos + 29, this.topPos + 43, 22, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.west_button.narrate")).build();
        this.westButton.setAlpha(0);
        this.westButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.westButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.west_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.west_block_face_button.tooltip");
        this.westBlockFaceButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 1)
                        this.blockFaceButtonOnPress(5);
                }).bounds(this.leftPos + 52, this.topPos + 44, 4, 14)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.west_block_face_button.narrate")).build();
        this.westBlockFaceButton.setAlpha(0);
        this.westBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.westBlockFaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.up_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.up_button.tooltip");
        this.upButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(0);
        }).bounds(this.leftPos + 118, this.topPos + 14, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.up_button.narrate")).build();
        this.upButton.setAlpha(0);
        this.upButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.upButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.top_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.top_block_face_button.tooltip");
        this.topBlockFaceButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(0);
        }).bounds(this.leftPos + 119, this.topPos + 37, 14, 4)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.top_block_face_button.narrate")).build();
        this.topBlockFaceButton.setAlpha(0);
        this.topBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.topBlockFaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.down_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.down_button.tooltip");
        this.downButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(1);
        }).bounds(this.leftPos + 118, this.topPos + 66, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.down_button.narrate")).build();
        this.downButton.setAlpha(0);
        this.downButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.downButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.bottom_block_face_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.bottom_block_face_button.tooltip");
        this.bottomBlockFaceButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(1);
        }).bounds(this.leftPos + 119, this.topPos + 61, 14, 4)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.bottom_block_face_button.narrate")).build();
        this.bottomBlockFaceButton.setAlpha(0);
        this.bottomBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.bottomBlockFaceButton);

        this.placementOffsetBox = new EditBox(this.font, this.leftPos + 109, this.topPos + 48, 30, 18,
                Component.translatable("menu.marioverse.block_spawner.placement_offset_box.narrate"));
        this.placementOffsetBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.block_spawner.placement_offset_box.tooltip")));
        this.placementOffsetBox.setFilter(filter -> filter.matches("[0-9]\\d*") || filter.isEmpty());
        this.placementOffsetBox.setBordered(false);
        this.placementOffsetBox.setMaxLength(15);
        this.addRenderableWidget(this.placementOffsetBox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.face_north_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.face_north_button.tooltip");
        this.faceNorthButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 2)
                        this.facingDirectionButtonOnPress(2);
                }).bounds(this.leftPos + 62, this.topPos + 18, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.face_north_button.narrate")).build();
        this.faceNorthButton.setAlpha(0);
        this.faceNorthButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceNorthButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.face_east_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.face_east_button.tooltip");
        this.faceEastButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 2)
                        this.facingDirectionButtonOnPress(4);
                }).bounds(this.leftPos + 81, this.topPos + 43, 22, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.face_east_button.narrate")).build();
        this.faceEastButton.setAlpha(0);
        this.faceEastButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceEastButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.face_south_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.face_south_button.tooltip");
        this.faceSouthButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 2)
                        this.facingDirectionButtonOnPress(3);
                }).bounds(this.leftPos + 62, this.topPos + 62, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.face_south_button.narrate")).build();
        this.faceSouthButton.setAlpha(0);
        this.faceSouthButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceSouthButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.face_west_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.face_west_button.tooltip");
        this.faceWestButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 2)
                        this.facingDirectionButtonOnPress(5);
                }).bounds(this.leftPos + 37, this.topPos + 43, 22, 16)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.face_west_button.narrate")).build();
        this.faceWestButton.setAlpha(0);
        this.faceWestButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceWestButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.face_up_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.face_up_button.tooltip");
        this.faceUpButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 2)
                        this.facingDirectionButtonOnPress(0);
                }).bounds(this.leftPos + 107, this.topPos + 18, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.face_up_button.narrate")).build();
        this.faceUpButton.setAlpha(0);
        this.faceUpButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceUpButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.face_down_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.face_down_button.tooltip");
        this.faceDownButton = Button.builder(buttonName, button -> {
                    int menuType = this.menu.getMenuType();
                    if (menuType == 2)
                        this.facingDirectionButtonOnPress(1);
                }).bounds(this.leftPos + 107, this.topPos + 62, 16, 22)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.face_down_button.narrate")).build();
        this.faceDownButton.setAlpha(0);
        this.faceDownButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceDownButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.sneaking_checkbox");
        tooltip = Component.translatable("menu.marioverse.block_spawner.sneaking_checkbox.tooltip");
        this.sneakingCheckbox = ResizableCheckbox.builder(buttonName, this.font).onValueChange(this::isSneakingCheckbox)
                .pos(this.leftPos + 137, this.topPos + 72).setSize(8)
                .tooltip(Tooltip.create(tooltip)).build();
        this.sneakingCheckbox.setAlpha(0);
        this.addRenderableWidget(this.sneakingCheckbox);

        buttonName = Component.translatable("menu.marioverse.block_spawner.replace_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.replace_button.tooltip");
        this.replaceButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            this.replaceButtonOnPress();
        }).bounds(this.leftPos + 149, this.topPos + 16, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.replace_button.narrate")).build();
        this.replaceButton.setAlpha(0);
        this.replaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.replaceButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.placement_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.placement_button.tooltip");
        this.placementButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            this.placementButtonOnPress();
        }).bounds(this.leftPos + 149, this.topPos + 41, 20, 20)
                .createNarration(supplier -> Component.translatable("menu.marioverse.block_spawner.placement_button.narrate")).build();
        this.placementButton.setAlpha(0);
        this.placementButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.placementButton);

        buttonName = Component.translatable("menu.marioverse.block_spawner.disguise_button");
        tooltip = Component.translatable("menu.marioverse.block_spawner.disguise_button.tooltip");
        this.disguiseButton = Button.builder(buttonName, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            PacketHandler.sendToServer(new MenuTypePayload(this.menu.containerId, 2));
        }).bounds(this.leftPos + 149, this.topPos + 66, 20, 20)
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
            this.updateSlotPositions();

            this.clockButton.visible = menuType == 0;
            this.confirmButton.visible = menuType == 0;
            this.ticksButton.visible = menuType == 0;
            this.secondsButton.visible = menuType == 0;
            this.minuteButton.visible = menuType == 0;
            this.hourButton.visible = menuType == 0;
            this.countdownBox.setVisible(menuType == 0);

            this.placementOffsetBox.setVisible(menuType == 1);
            this.northBlockFaceButton.visible = menuType == 1;
            this.southBlockFaceButton.visible = menuType == 1;
            this.eastBlockFaceButton.visible = menuType == 1;
            this.westBlockFaceButton.visible = menuType == 1;
            this.topBlockFaceButton.visible = menuType == 1;
            this.bottomBlockFaceButton.visible = menuType == 1;
            this.northButton.visible = menuType == 1;
            this.southButton.visible = menuType == 1;
            this.eastButton.visible = menuType == 1;
            this.westButton.visible = menuType == 1;
            this.upButton.visible = menuType == 1;
            this.downButton.visible = menuType == 1;
            this.collisionCheckbox.visible = menuType == 1;
            this.interactableCheckbox.visible = menuType == 1;
            this.rightClickableCheckbox.visible = menuType == 1;
            this.unbreakableCheckbox.visible = menuType == 1;
            this.showLine = menuType == 1;

            this.faceNorthButton.visible = menuType == 2;
            this.faceSouthButton.visible = menuType == 2;
            this.faceEastButton.visible = menuType == 2;
            this.faceWestButton.visible = menuType == 2;
            this.faceUpButton.visible = menuType == 2;
            this.faceDownButton.visible = menuType == 2;
            this.sneakingCheckbox.visible = menuType == 2;
            this.showDisguiseIcon = menuType == 2;

            if (!this.countdownBox.isFocused() && menuType == 0)
                this.countdownBox.setValue(String.valueOf(this.menu.convertFromTicks(refillCountdown)));

            if (!this.placementOffsetBox.isFocused() && menuType == 1)
                this.placementOffsetBox.setValue(String.valueOf(placementOffset));
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

    private void facingDirectionButtonOnPress(int facingDirection) {
        PacketHandler.sendToServer(new FacingDirectionPayload(this.menu.containerId, facingDirection));
    }

    private void hasCollisionCheckbox(AbstractWidget widget, boolean hasCollision) {
        PacketHandler.sendToServer(new HasCollisionPayload(this.menu.containerId, hasCollision ? 1 : 0));
    }

    private void isInteractableCheckbox(AbstractWidget widget, boolean isInteractable) {
        PacketHandler.sendToServer(new IsInteractablePayload(this.menu.containerId, isInteractable ? 1 : 0));
    }

    private void isRightClickableCheckbox(AbstractWidget widget, boolean isRightClickable) {
        PacketHandler.sendToServer(new IsRightClickablePayload(this.menu.containerId, isRightClickable ? 1 : 0));
    }

    private void isSneakingCheckbox(AbstractWidget widget, boolean isSneaking) {
        PacketHandler.sendToServer(new IsSneakingPayload(this.menu.containerId, isSneaking ? 1 : 0));
    }

    private void isUnbreakableCheckbox(AbstractWidget widget, boolean isUnbreakable) {
        PacketHandler.sendToServer(new IsUnbreakablePayload(this.menu.containerId, isUnbreakable ? 1 : 0));
    }

    private void placementDirectionButtonOnPress(int placementDirection) {
        PacketHandler.sendToServer(new PlacementDirectionPayload(this.menu.containerId, placementDirection));
    }

    private void confirmButtonOnPress() {
        String value = this.countdownBox.getValue();
        int parsed = -1;

        if (!value.isEmpty() && !value.equals("-"))
            parsed = Integer.parseInt(value);

        if (this.minecraft != null && this.minecraft.getConnection() != null)
            PacketHandler.sendToServer(new RefillCountdownPayload(this.menu.containerId, parsed));
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
            this.setSlotPos(blockSlot, 8, 30);
            this.setSlotPos(disguiseSlot, 8, 56);
        } else if (type == 1) {
            this.setSlotPos(blockSlot, 58, 43);
            this.setSlotPos(disguiseSlot, 8, 43);
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