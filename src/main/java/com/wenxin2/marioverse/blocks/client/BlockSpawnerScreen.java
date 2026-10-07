package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.StateImageButton;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.slots.GhostSlot;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.BlockFacePayload;
import com.wenxin2.marioverse.network.server_bound.data.FacingDirectionPayload;
import com.wenxin2.marioverse.network.server_bound.data.HasCollisionPayload;
import com.wenxin2.marioverse.network.server_bound.data.HideItemRenderedPayload;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
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

    private static final WidgetSprites[] CLOCK_SPRITES = clockSprites();
    private static final WidgetSprites CHECKBOX_SPRITES = new WidgetSprites(sprite("checkbox"), sprite("checkbox_selected"),
            sprite("checkbox_highlighted"), sprite("checkbox_selected_highlighted"));
    private static final WidgetSprites CONFIRM_SPRITES = sprites("confirm");
    private static final WidgetSprites REPLACE_SPRITES = selectableSprites("replace");
    private static final WidgetSprites PLACEMENT_SPRITES = selectableSprites("placement");
    private static final WidgetSprites DISGUISE_SPRITES = selectableSprites("disguise");
    private static final WidgetSprites UNIT_LEFT_SPRITES = selectableSprites("unit_left");
    private static final WidgetSprites UNIT_MIDDLE_SPRITES = selectableSprites("unit_middle");
    private static final WidgetSprites UNIT_RIGHT_SPRITES = selectableSprites("unit_right");
    private static final WidgetSprites ARROW_UP_SPRITES = selectableSprites("arrow_up");
    private static final WidgetSprites ARROW_DOWN_SPRITES = selectableSprites("arrow_down");
    private static final WidgetSprites ARROW_LEFT_SPRITES = selectableSprites("arrow_left");
    private static final WidgetSprites ARROW_RIGHT_SPRITES = selectableSprites("arrow_right");
    private static final WidgetSprites FACE_HORIZONTAL_SPRITES = selectableSprites("face_horizontal");
    private static final WidgetSprites FACE_VERTICAL_SPRITES = selectableSprites("face_vertical");

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
    Button collisionCheckbox;
    Button interactableCheckbox;
    Button hideItemRenderedCheckbox;
    Button rightClickableCheckbox;
    Button sneakingCheckbox;
    Button unbreakableCheckbox;

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

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture width, texture height
        graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.countdownBox.visible && this.menu.getMenuType() == 0)
            graphics.blit(GUI, this.leftPos + 41, this.topPos + 32, 177, 0, 78, 14);

        if (this.placementOffsetBox.visible && this.menu.getMenuType() == 1)
            graphics.blit(GUI, this.leftPos + 107, this.topPos + 42, 177, 15, 26, 18);

        if (this.showLine)
            graphics.blit(GUI, this.leftPos + 26, this.topPos + 16, 240, 34, 2, 70);

        if (this.showDisguiseIcon) {
            graphics.blit(GUI, this.leftPos + 61, this.topPos + 42, 219, 97, 18, 18);
            graphics.blit(GUI, this.leftPos + 106, this.topPos + 42, 219, 97, 18, 18);
        }

        if (this.menu.getMenuType() == 0 || this.menu.getMenuType() == 2) {
            // Replace Slot
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 29, 204, 15, 18, 18);
            // Disguise Slot
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 55, 204, 15, 18, 18);
        } else {
            graphics.blit(GUI, this.leftPos + 57, this.topPos + 42, 204, 15, 18, 18);
            graphics.blit(GUI, this.leftPos + 7, this.topPos + 42, 204, 15, 18, 18);
        }
    }

    @Override
    public void init() { // Order buttons/widgets initialized is the order the 'tab' key will select it
        super.init();

        Component tooltip = Component.literal("");

        this.countdownBox = new EditBox(this.font, this.leftPos + 43, this.topPos + 35, 70, 16,
                Component.translatable("menu.marioverse.block_spawner.countdown_box.narrate"));
        this.countdownBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.block_spawner.countdown_box.tooltip")));
        this.countdownBox.setFilter(filter -> filter.matches("-?\\d*"));
        this.countdownBox.setBordered(false);
        this.countdownBox.setMaxLength(34);
        this.addRenderableWidget(this.countdownBox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.clock_button.tooltip");
        this.clockButton = new StateImageButton(this.leftPos + 126, this.topPos + 30, 16, 16,
                () -> CLOCK_SPRITES[this.clockFrame()], () -> true, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }, Component.translatable("menu.marioverse.block_spawner.clock_button.narrate"));
        this.clockButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.clockButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.ticks_button.tooltip");
        this.ticksButton = new StateImageButton(this.leftPos + 61, this.topPos + 56, 15, 16,
                UNIT_LEFT_SPRITES, () -> this.menu.getTimeUnit() != 0, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 0));
        }, Component.translatable("menu.marioverse.block_spawner.ticks_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.ticks_button"));
        this.ticksButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.ticksButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.seconds_button.tooltip");
        this.secondsButton = new StateImageButton(this.leftPos + 76, this.topPos + 56, 14, 16,
                UNIT_MIDDLE_SPRITES, () -> this.menu.getTimeUnit() != 1, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 1));
        }, Component.translatable("menu.marioverse.block_spawner.seconds_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.seconds_button"));
        this.secondsButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.secondsButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.minute_button.tooltip");
        this.minuteButton = new StateImageButton(this.leftPos + 90, this.topPos + 56, 14, 16,
                UNIT_MIDDLE_SPRITES, () -> this.menu.getTimeUnit() != 2, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
        }, Component.translatable("menu.marioverse.block_spawner.minute_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.minute_button"));
        this.minuteButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.minuteButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.hour_button.tooltip");
        this.hourButton = new StateImageButton(this.leftPos + 104, this.topPos + 56, 15, 16,
                UNIT_RIGHT_SPRITES, () -> this.menu.getTimeUnit() != 3, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0)
                this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 3));
        }, Component.translatable("menu.marioverse.block_spawner.hour_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.hour_button"));
        this.hourButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.hourButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.confirm_button.tooltip");
        this.confirmButton = new ImageButton(this.leftPos + 124, this.topPos + 54, 20, 20,
                CONFIRM_SPRITES, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }, tooltip);
        this.confirmButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.confirmButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.unbreakable_checkbox.tooltip");
        this.unbreakableCheckbox = new StateImageButton(this.leftPos + 6, this.topPos + 62, 10, 8,
                CHECKBOX_SPRITES, () -> this.menu.isUnbreakable() != 1, button ->
                PacketHandler.sendToServer(new IsUnbreakablePayload(this.menu.containerId, this.menu.isUnbreakable() == 1 ? 0 : 1)),
                tooltip);
        this.unbreakableCheckbox.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.unbreakableCheckbox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.right_clickable_checkbox.tooltip");
        this.rightClickableCheckbox = new StateImageButton(this.leftPos + 16, this.topPos + 62, 10, 8,
                CHECKBOX_SPRITES, () -> this.menu.isRightClickable() != 1, button ->
                PacketHandler.sendToServer(new IsRightClickablePayload(this.menu.containerId, this.menu.isRightClickable() == 1 ? 0 : 1)),
                tooltip);
        this.rightClickableCheckbox.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.rightClickableCheckbox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.interactable_checkbox.tooltip");
        this.interactableCheckbox = new StateImageButton(this.leftPos + 6, this.topPos + 72, 10, 8,
                CHECKBOX_SPRITES, () -> this.menu.isInteractable() != 1, button ->
                PacketHandler.sendToServer(new IsInteractablePayload(this.menu.containerId, this.menu.isInteractable() == 1 ? 0 : 1)),
                tooltip);
        this.interactableCheckbox.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.interactableCheckbox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.collision_checkbox.tooltip");
        this.collisionCheckbox = new StateImageButton(this.leftPos + 16, this.topPos + 72, 10, 8,
                CHECKBOX_SPRITES, () -> this.menu.hasCollision() != 1, button ->
                PacketHandler.sendToServer(new HasCollisionPayload(this.menu.containerId, this.menu.hasCollision() == 1 ? 0 : 1)),
                tooltip);
        this.collisionCheckbox.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.collisionCheckbox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.north_button.tooltip");
        this.northButton = new StateImageButton(this.leftPos + 58, this.topPos + 14, 16, 22,
                ARROW_UP_SPRITES, () -> this.menu.getPlacementDirection() != 2, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(2);
        }, Component.translatable("menu.marioverse.block_spawner.north_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.north_button"));
        this.northButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.northButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.north_block_face_button.tooltip");
        this.northBlockFaceButton = new StateImageButton(this.leftPos + 59, this.topPos + 37, 14, 4,
                FACE_HORIZONTAL_SPRITES, () -> this.menu.getBlockFace() != 2, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(2);
        }, Component.translatable("menu.marioverse.block_spawner.north_block_face_button.narrate"));
        this.northBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.northBlockFaceButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.east_button.tooltip");
        this.eastButton = new StateImageButton(this.leftPos + 81, this.topPos + 43, 22, 16,
                ARROW_RIGHT_SPRITES, () -> this.menu.getPlacementDirection() != 4, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(4);
        }, Component.translatable("menu.marioverse.block_spawner.east_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.east_button"));
        this.eastButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.eastButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.east_block_face_button.tooltip");
        this.eastBlockFaceButton = new StateImageButton(this.leftPos + 76, this.topPos + 44, 4, 14,
                FACE_VERTICAL_SPRITES, () -> this.menu.getBlockFace() != 4, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(4);
        }, Component.translatable("menu.marioverse.block_spawner.east_block_face_button.narrate"));
        this.eastBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.eastBlockFaceButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.south_button.tooltip");
        this.southButton = new StateImageButton(this.leftPos + 58, this.topPos + 66, 16, 22,
                ARROW_DOWN_SPRITES, () -> this.menu.getPlacementDirection() != 3, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(3);
        }, Component.translatable("menu.marioverse.block_spawner.south_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.south_button"));
        this.southButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.southButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.south_block_face_button.tooltip");
        this.southBlockFaceButton = new StateImageButton(this.leftPos + 59, this.topPos + 61, 14, 4,
                FACE_HORIZONTAL_SPRITES, () -> this.menu.getBlockFace() != 3, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(3);
        }, Component.translatable("menu.marioverse.block_spawner.south_block_face_button.narrate"));
        this.southBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.southBlockFaceButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.west_button.tooltip");
        this.westButton = new StateImageButton(this.leftPos + 29, this.topPos + 43, 22, 16,
                ARROW_LEFT_SPRITES, () -> this.menu.getPlacementDirection() != 5, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(5);
        }, Component.translatable("menu.marioverse.block_spawner.west_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.west_button"));
        this.westButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.westButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.west_block_face_button.tooltip");
        this.westBlockFaceButton = new StateImageButton(this.leftPos + 52, this.topPos + 44, 4, 14,
                FACE_VERTICAL_SPRITES, () -> this.menu.getBlockFace() != 5, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(5);
        }, Component.translatable("menu.marioverse.block_spawner.west_block_face_button.narrate"));
        this.westBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.westBlockFaceButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.up_button.tooltip");
        this.upButton = new StateImageButton(this.leftPos + 112, this.topPos + 14, 16, 22,
                ARROW_UP_SPRITES, () -> this.menu.getPlacementDirection() != 0, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(0);
        }, Component.translatable("menu.marioverse.block_spawner.up_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.up_button"));
        this.upButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.upButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.top_block_face_button.tooltip");
        this.topBlockFaceButton = new StateImageButton(this.leftPos + 113, this.topPos + 37, 14, 4,
                FACE_HORIZONTAL_SPRITES, () -> this.menu.getBlockFace() != 0, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(0);
        }, Component.translatable("menu.marioverse.block_spawner.top_block_face_button.narrate"));
        this.topBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.topBlockFaceButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.down_button.tooltip");
        this.downButton = new StateImageButton(this.leftPos + 112, this.topPos + 66, 16, 22,
                ARROW_DOWN_SPRITES, () -> this.menu.getPlacementDirection() != 1, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.placementDirectionButtonOnPress(1);
        }, Component.translatable("menu.marioverse.block_spawner.down_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.down_button"));
        this.downButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.downButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.bottom_block_face_button.tooltip");
        this.bottomBlockFaceButton = new StateImageButton(this.leftPos + 113, this.topPos + 61, 14, 4,
                FACE_HORIZONTAL_SPRITES, () -> this.menu.getBlockFace() != 1, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 1)
                this.blockFaceButtonOnPress(1);
        }, Component.translatable("menu.marioverse.block_spawner.bottom_block_face_button.narrate"));
        this.bottomBlockFaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.bottomBlockFaceButton);

        this.placementOffsetBox = new EditBox(this.font, this.leftPos + 109, this.topPos + 48, 20, 18,
                Component.translatable("menu.marioverse.block_spawner.placement_offset_box.narrate"));
        this.placementOffsetBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.block_spawner.placement_offset_box.tooltip")));
        this.placementOffsetBox.setFilter(filter -> filter.matches("[0-9]\\d*") || filter.isEmpty());
        this.placementOffsetBox.setBordered(false);
        this.placementOffsetBox.setMaxLength(15);
        this.addRenderableWidget(this.placementOffsetBox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.hide_item_rendered_checkbox.tooltip");
        this.hideItemRenderedCheckbox = new StateImageButton(this.leftPos + 136, this.topPos + 47, 10, 8,
                CHECKBOX_SPRITES, () -> this.menu.isItemRenderHidden() != 1, button ->
                PacketHandler.sendToServer(new HideItemRenderedPayload(this.menu.containerId, this.menu.isItemRenderHidden() == 1 ? 0 : 1)),
                tooltip);
        this.hideItemRenderedCheckbox.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.hideItemRenderedCheckbox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.face_north_button.tooltip");
        this.faceNorthButton = new StateImageButton(this.leftPos + 62, this.topPos + 18, 16, 22,
                ARROW_UP_SPRITES, () -> this.menu.getFacingDirection() != 2, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 2)
                this.facingDirectionButtonOnPress(2);
        }, Component.translatable("menu.marioverse.block_spawner.face_north_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.face_north_button"));
        this.faceNorthButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceNorthButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.face_east_button.tooltip");
        this.faceEastButton = new StateImageButton(this.leftPos + 81, this.topPos + 43, 22, 16,
                ARROW_RIGHT_SPRITES, () -> this.menu.getFacingDirection() != 4, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 2)
                this.facingDirectionButtonOnPress(4);
        }, Component.translatable("menu.marioverse.block_spawner.face_east_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.face_east_button"));
        this.faceEastButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceEastButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.face_south_button.tooltip");
        this.faceSouthButton = new StateImageButton(this.leftPos + 62, this.topPos + 62, 16, 22,
                ARROW_DOWN_SPRITES, () -> this.menu.getFacingDirection() != 3, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 2)
                this.facingDirectionButtonOnPress(3);
        }, Component.translatable("menu.marioverse.block_spawner.face_south_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.face_south_button"));
        this.faceSouthButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceSouthButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.face_west_button.tooltip");
        this.faceWestButton = new StateImageButton(this.leftPos + 37, this.topPos + 43, 22, 16,
                ARROW_LEFT_SPRITES, () -> this.menu.getFacingDirection() != 5, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 2)
                this.facingDirectionButtonOnPress(5);
        }, Component.translatable("menu.marioverse.block_spawner.face_west_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.face_west_button"));
        this.faceWestButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceWestButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.face_up_button.tooltip");
        this.faceUpButton = new StateImageButton(this.leftPos + 107, this.topPos + 18, 16, 22,
                ARROW_UP_SPRITES, () -> this.menu.getFacingDirection() != 0, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 2)
                this.facingDirectionButtonOnPress(0);
        }, Component.translatable("menu.marioverse.block_spawner.face_up_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.face_up_button"));
        this.faceUpButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceUpButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.face_down_button.tooltip");
        this.faceDownButton = new StateImageButton(this.leftPos + 107, this.topPos + 62, 16, 22,
                ARROW_DOWN_SPRITES, () -> this.menu.getFacingDirection() != 1, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 2)
                this.facingDirectionButtonOnPress(1);
        }, Component.translatable("menu.marioverse.block_spawner.face_down_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.block_spawner.face_down_button"));
        this.faceDownButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.faceDownButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.sneaking_checkbox.tooltip");
        this.sneakingCheckbox = new StateImageButton(this.leftPos + 136, this.topPos + 72, 10, 8,
                CHECKBOX_SPRITES, () -> this.menu.isSneaking() != 1, button ->
                PacketHandler.sendToServer(new IsSneakingPayload(this.menu.containerId, this.menu.isSneaking() == 1 ? 0 : 1)),
                tooltip);
        this.sneakingCheckbox.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.sneakingCheckbox);

        tooltip = Component.translatable("menu.marioverse.block_spawner.replace_button.tooltip");
        this.replaceButton = new StateImageButton(this.leftPos + 149, this.topPos + 16, 20, 20,
                REPLACE_SPRITES, () -> this.menu.getMenuType() != 0, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            this.replaceButtonOnPress();
        }, Component.translatable("menu.marioverse.block_spawner.replace_button.narrate"));
        this.replaceButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.replaceButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.placement_button.tooltip");
        this.placementButton = new StateImageButton(this.leftPos + 149, this.topPos + 41, 20, 20,
                PLACEMENT_SPRITES, () -> this.menu.getMenuType() != 1, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            this.placementButtonOnPress();
        }, Component.translatable("menu.marioverse.block_spawner.placement_button.narrate"));
        this.placementButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.placementButton);

        tooltip = Component.translatable("menu.marioverse.block_spawner.disguise_button.tooltip");
        this.disguiseButton = new StateImageButton(this.leftPos + 149, this.topPos + 66, 20, 20,
                DISGUISE_SPRITES, () -> this.menu.getMenuType() != 2, button -> {
            int menuType = this.menu.getMenuType();
            if (menuType == 0 && this.countdownBox.isFocused())
                this.confirmButtonOnPress();
            if (menuType == 1)
                this.placementOffsetOnPress();
            PacketHandler.sendToServer(new MenuTypePayload(this.menu.containerId, 2));
        }, Component.translatable("menu.marioverse.block_spawner.disguise_button.narrate"));
        this.disguiseButton.setTooltip(Tooltip.create(tooltip));
        this.addRenderableWidget(this.disguiseButton);

        this.lastMenuType = -1;
        this.containerTick();
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
            this.hideItemRenderedCheckbox.visible = menuType == 1;
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
                    if (this.countdownBox.isFocused()) {
                        this.confirmButtonOnPress();
                        this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
                    }
                    if (this.placementOffsetBox.isFocused()) {
                        this.placementOffsetOnPress();
                        this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
                    }
                }
                if (this.countdownBox.isFocused())
                    this.countdownBox.setFocused(false);
                if (this.placementOffsetBox.isFocused()) {
                    this.placementOffsetBox.setFocused(false);
                    this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
                    this.placementOffsetOnPress();
                }
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

    private int clockFrame() {
        int refillTicks = this.menu.getRefillCountdown();

        if (refillTicks > 0 && this.minecraft != null && this.minecraft.level != null) {
            long gameTime = this.minecraft.level.getGameTime();
            int speed = Math.max(1, (int) (Math.sqrt(refillTicks) / 2));

            return (int) ((gameTime / speed) % CLOCK_SPRITES.length);
        }
        return 0;
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

    private static ResourceLocation sprite(String name) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block_spawner/" + name);
    }

    private static WidgetSprites sprites(String name) {
        return new WidgetSprites(sprite(name), sprite(name + "_highlighted"));
    }

    private static WidgetSprites selectableSprites(String name) {
        return new WidgetSprites(sprite(name), sprite(name + "_selected"), sprite(name + "_highlighted"));
    }

    private static WidgetSprites[] clockSprites() {
        WidgetSprites[] clock = new WidgetSprites[8];
        for (int frame = 0; frame < clock.length; frame++)
            clock[frame] = sprites("clock_" + frame);
        return clock;
    }
}