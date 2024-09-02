package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.client.BubblesSlider;
import com.wenxin2.marioverse.client.WaterSpoutSlider;
import com.wenxin2.marioverse.init.Config;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.RenamePipePayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutButtonPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.lwjgl.glfw.GLFW;

public class WarpPipeScreen extends AbstractContainerScreen<WarpPipeMenu> {
    public static ResourceLocation WARP_PIPE_GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "textures/gui/warp_pipe.png");
    Button bubblesButton;
    Button closeButton;
    Button renameButton;
    Button waterSpoutButton;
    EditBox renameBox;
    Inventory inventory;

    public static BlockPos lastClickedPos = null;
    public ExtendedSlider waterSpoutSlider;
    public ExtendedSlider bubblesSlider;
    private String pipeName = "";
    private Level world;

    public WarpPipeScreen(WarpPipeMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
        this.world = inventory.player.level();
    }

    @Override
    public void renderLabels(GuiGraphics graphics, int x, int y) {
        if (this.renameBox.visible)
            graphics.drawString(this.font, "", this.titleLabelX, this.titleLabelY, 4210752, false);
        else if (!this.pipeName.isEmpty())
            // Warp Pipe "Name"
            graphics.drawString(this.font, this.pipeName, this.titleLabelX, this.titleLabelY, 4210752, false);
        // "Warp Pipe"
        else graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        // Inventory
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        Player player = this.inventory.player;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WARP_PIPE_GUI);

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture width, texture height
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        graphics.blit(WARP_PIPE_GUI, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.getClickedPos() != null) {
            BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());
            BlockState state = world.getBlockState(this.getClickedPos());

            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                if (this.renameButton.isHoveredOrFocused() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_RENAMING.get())
                    graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 170, 24, 24);
                else if (this.renameButton.isHoveredOrFocused() && !Config.WAX_DISABLES_RENAMING.get())
                    graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 170, 24, 24);
                else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_RENAMING.get())
                    graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 194, 24, 24);
                else graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 146, 24, 24);

                if (this.renameBox.visible && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_RENAMING.get())
                    graphics.blit(WARP_PIPE_GUI, x + 7, y + 4, 0, 167, 162, 12);
                else if (this.renameBox.visible && !Config.WAX_DISABLES_RENAMING.get())
                    graphics.blit(WARP_PIPE_GUI, x + 7, y + 4, 0, 167, 162, 12);

                if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.CLOSED)) {
                    if ((this.closeButton.isHoveredOrFocused() && !Config.CREATIVE_CLOSE_PIPES.get() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                            || (this.closeButton.isHoveredOrFocused() && Config.CREATIVE_CLOSE_PIPES.get() && player.isCreative() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 202, 24, 24, 24);
                    else if ((this.closeButton.isHoveredOrFocused() && !Config.CREATIVE_CLOSE_PIPES.get() && !Config.WAX_DISABLES_CLOSING.get())
                            || (this.closeButton.isHoveredOrFocused() && Config.CREATIVE_CLOSE_PIPES.get() && player.isCreative() && !Config.WAX_DISABLES_CLOSING.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 202, 24, 24, 24);
                    else if (!player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get())
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 202, 48, 24, 24);
                    else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 202, 48, 24, 24);
                    else graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 202, 0, 24, 24);
                } else {
                    if ((this.closeButton.isHoveredOrFocused() && !Config.CREATIVE_CLOSE_PIPES.get() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                            || (this.closeButton.isHoveredOrFocused() && Config.CREATIVE_CLOSE_PIPES.get() && player.isCreative()) && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 177, 24, 24, 24);
                    else if ((this.closeButton.isHoveredOrFocused() && !Config.CREATIVE_CLOSE_PIPES.get() && !Config.WAX_DISABLES_CLOSING.get())
                            || (this.closeButton.isHoveredOrFocused() && Config.CREATIVE_CLOSE_PIPES.get() && player.isCreative()) && !Config.WAX_DISABLES_CLOSING.get())
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 177, 24, 24, 24);
                    else if (!player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get())
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 177, 48, 24, 24);
                    else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                        graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 177, 48, 24, 24);
                    else graphics.blit(WARP_PIPE_GUI, x + 7, y + 45, 177, 0, 24, 24);
                }

                if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.WATER_SPOUT)) {
                    if ((this.waterSpoutButton.isHoveredOrFocused() && !Config.CREATIVE_WATER_SPOUT.get() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                            || (this.waterSpoutButton.isHoveredOrFocused() && Config.CREATIVE_WATER_SPOUT.get() && player.isCreative() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 97, 24, 24);
                    else if ((this.waterSpoutButton.isHoveredOrFocused() && !Config.CREATIVE_WATER_SPOUT.get() && !Config.WAX_DISABLES_WATER_SPOUTS.get())
                            || (this.waterSpoutButton.isHoveredOrFocused() && Config.CREATIVE_WATER_SPOUT.get() && player.isCreative() && !Config.WAX_DISABLES_WATER_SPOUTS.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 97, 24, 24);
                    else if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 121, 24, 24);
                    else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 121, 24, 24);
                    else graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 73, 24, 24);
                } else {
                    if ((this.waterSpoutButton.isHoveredOrFocused() && !Config.CREATIVE_WATER_SPOUT.get() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                            || (this.waterSpoutButton.isHoveredOrFocused() && Config.CREATIVE_WATER_SPOUT.get() && player.isCreative()) && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 177, 97, 24, 24);
                    else if ((this.waterSpoutButton.isHoveredOrFocused() && !Config.CREATIVE_WATER_SPOUT.get() && !Config.WAX_DISABLES_WATER_SPOUTS.get())
                            || (this.waterSpoutButton.isHoveredOrFocused() && Config.CREATIVE_WATER_SPOUT.get() && player.isCreative()) && !Config.WAX_DISABLES_WATER_SPOUTS.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 177, 97, 24, 24);
                    else if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 177, 121, 24, 24);
                    else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 177, 121, 24, 24);
                    else graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 177, 73, 24, 24);
                }

                if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.BUBBLES)) {
                    if ((this.bubblesButton.isHoveredOrFocused() && !Config.CREATIVE_BUBBLES.get() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                            || (this.bubblesButton.isHoveredOrFocused() && Config.CREATIVE_BUBBLES.get() && player.isCreative() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 97, 24, 24);
                    else if ((this.bubblesButton.isHoveredOrFocused() && !Config.CREATIVE_BUBBLES.get() && !Config.WAX_DISABLES_BUBBLES.get())
                            || (this.bubblesButton.isHoveredOrFocused() && Config.CREATIVE_BUBBLES.get() && player.isCreative() && !Config.WAX_DISABLES_BUBBLES.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 97, 24, 24);
                    else if (!player.isCreative() && Config.CREATIVE_BUBBLES.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 121, 24, 24);
                    else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 121, 24, 24);
                    else graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 73, 24, 24);
                } else {
                    if ((this.bubblesButton.isHoveredOrFocused() && !Config.CREATIVE_BUBBLES.get() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                            || (this.bubblesButton.isHoveredOrFocused() && Config.CREATIVE_BUBBLES.get() && player.isCreative() && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 24, 24, 24);
                    else if ((this.bubblesButton.isHoveredOrFocused() && !Config.CREATIVE_BUBBLES.get() && !Config.WAX_DISABLES_BUBBLES.get())
                            || (this.bubblesButton.isHoveredOrFocused() && Config.CREATIVE_BUBBLES.get() && player.isCreative() && !Config.WAX_DISABLES_BUBBLES.get()))
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 24, 24, 24);
                    else if (!player.isCreative() && Config.CREATIVE_BUBBLES.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 48, 24, 24);
                    else if (pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                        graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 48, 24, 24);
                    else graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 0, 24, 24);
                }
            }
        }
    }

    @Override
    public void init() {
        super.init();
        lastClickedPos = this.getClickedPos();
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;

        this.renameBox = new EditBox(this.font, x + 8, y + 6, 160, 12,
                Component.translatable("menu.marioverse.warp_pipe.rename_box.narrate"));
        this.renameBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe.rename_box.tooltip")));
        this.renameBox.setValue(this.renameBox.getValue());
        this.renameBox.setBordered(false);
        this.renameBox.setVisible(false);
        this.renameBox.setMaxLength(27);
        this.addRenderableWidget(this.renameBox);

        final Component rename = Component.translatable("menu.marioverse.warp_pipe.rename_button");
        this.renameButton = this.addRenderableWidget(new Button.Builder(rename, (b) -> {
            this.renameButtonOnPress();
        }).bounds(x + 7, y + 18, 24, 24)
                .createNarration(supplier -> Component.translatable("menu.marioverse.warp_pipe.rename_button.narrate")).build());
        this.renameButton.setAlpha(0);

        final Component close = Component.translatable("menu.marioverse.warp_pipe.close_button");
        this.closeButton = this.addRenderableWidget(new Button.Builder(close, (b) -> {
            this.closeButtonOnPress();
        }).bounds(x + 7, y + 45, 24, 24)
                .createNarration(supplier -> Component.translatable("menu.marioverse.warp_pipe.close_button.narrate")).build());
        this.closeButton.setAlpha(0);

        final Component waterSpout = Component.translatable("menu.marioverse.warp_pipe.water_spout_button");
        this.waterSpoutButton = this.addRenderableWidget(new Button.Builder(waterSpout, (b) -> {
            this.waterSpoutButtonOnPress();
        }).bounds(x + 34, y + 18, 24, 24)
                .createNarration(supplier -> Component.translatable("menu.marioverse.warp_pipe.water_spout_button.narrate")).build());
        this.waterSpoutButton.setAlpha(0);

        // Only returning default of 4
        BlockPos clickedPos = getClickedPos();
        int spoutHeight = 4; // Default value
        if (clickedPos != null && Minecraft.getInstance().level != null) {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(clickedPos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                spoutHeight = ((WarpPipeBlockEntity) blockEntity).getSpoutHeight();
            }
        }

        final Component height = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider.height");
        this.waterSpoutSlider = this.addRenderableWidget(new WaterSpoutSlider(x + 61, y + 18, 108, 24,
                height, Component.literal(""), 0D, 16D, spoutHeight, 1D, 0, true));

        final Component bubbles = Component.translatable("menu.marioverse.warp_pipe.bubbles_button");
        this.bubblesButton = this.addRenderableWidget(new Button.Builder(bubbles, (b) -> {
            this.bubblesButtonOnPress();
        }).bounds(x + 34, y + 45, 24, 24)
                .createNarration(supplier -> Component.translatable("menu.marioverse.warp_pipe.bubbles_button.narrate")).build());
        this.bubblesButton.setAlpha(0);


        // Only returning default of 3
        clickedPos = getClickedPos();
        int bubblesDistance = 3; // Default value
        if (clickedPos != null && Minecraft.getInstance().level != null) {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(clickedPos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                bubblesDistance = ((WarpPipeBlockEntity) blockEntity).getBubblesDistance();
            }
        }

        final Component distance = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider.distance");
        this.bubblesSlider = this.addRenderableWidget(new BubblesSlider(x + 61, y + 45, 108, 24,
                distance, Component.literal(""), 0D, 16D, bubblesDistance, 1D, 0, true));
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
        BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());
        Player player = this.inventory.player;

        if (this.getClickedPos() != null && blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_RENAMING.get())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.rename_button_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.rename_button.tooltip");
        this.renameButton.setTooltip(Tooltip.create(tooltip));

        if (this.getClickedPos() != null) {
            BlockState state = world.getBlockState(this.getClickedPos());
            if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.CLOSED)) {
                if (!player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.open_button_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                    tooltip = Component.translatable("menu.marioverse.warp_pipe.open_button_waxed.tooltip");
                else tooltip = Component.translatable("menu.marioverse.warp_pipe.open_button.tooltip");
            } else if (!player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.close_button_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.close_button_waxed.tooltip");
            else tooltip = Component.translatable("menu.marioverse.warp_pipe.close_button.tooltip");
        }
        this.closeButton.setTooltip(Tooltip.create(tooltip));

        if (this.getClickedPos() != null) {
            BlockState state = world.getBlockState(this.getClickedPos());
            if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.WATER_SPOUT)) {
                if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_off_button_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                    tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_off_button_waxed.tooltip");
                else tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_off_button.tooltip");
            } else if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_on_button_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_on_button_waxed.tooltip");
            else  tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_on_button.tooltip");
        }
        this.waterSpoutButton.setTooltip(Tooltip.create(tooltip));

        if (this.getClickedPos() != null) {
            if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider_waxed.tooltip");
            else tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider.tooltip");
        }
        this.waterSpoutSlider.setTooltip(Tooltip.create(tooltip));

        if (this.getClickedPos() != null) {
            BlockState state = world.getBlockState(this.getClickedPos());
            if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.BUBBLES)) {
                if (!player.isCreative() && Config.CREATIVE_BUBBLES.get())
                    tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_off_button_creative.tooltip");
                else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                    tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_off_button_waxed.tooltip");
                else tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_off_button.tooltip");
            } else if (!player.isCreative() && Config.CREATIVE_BUBBLES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_on_button_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_on_button_waxed.tooltip");
            else  tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_on_button.tooltip");
        }
        this.bubblesButton.setTooltip(Tooltip.create(tooltip));

        if (this.getClickedPos() != null) {
            if (!player.isCreative() && Config.CREATIVE_BUBBLES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider_creative.tooltip");
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider_waxed.tooltip");
            else tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider.tooltip");
        }
        this.bubblesSlider.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (waterSpoutSlider.isFocused())
            waterSpoutSliderOnPress();
        if (bubblesSlider.isFocused())
            bubblesSliderOnPress();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int b, final int c)
    {
        if (waterSpoutSlider.isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            waterSpoutSliderOnPress();
            return false;
        }

        if (bubblesSlider.isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            bubblesSliderOnPress();
            return false;
        }

        if (this.renameBox.isFocused() && (keyCode == GLFW.GLFW_KEY_ESCAPE
                || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                final String pipeRename = this.renameBox.getValue();
                if (!pipeRename.equals(this.pipeName) && this.renameBox.isVisible() && !this.renameBox.getValue().equals("") && this.getClickedPos() != null) {
                    PacketHandler.sendToServer(new RenamePipePayload(this.getClickedPos(), this.renameBox.getValue()));
                    this.pipeName = pipeRename;
                }

                if (this.renameBox.visible) {
                    this.renameBox.setVisible(false);
                }
            }
            this.renameBox.setFocused(false);
            return false;
        }

        if (this.renameBox.isFocused() && keyCode == GLFW.GLFW_KEY_E) {
            this.renameBox.setFocused(true);
            return true;
        }

        return super.keyPressed(keyCode, b, c);
    }

    public BlockPos getClickedPos() {
        return lastClickedPos;
    }

    public void renameButtonOnPress() {
        Player player = this.inventory.player;
        final String pipeRename = this.renameBox.getValue();
        BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());

        if (this.getClickedPos() != null) {
            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_RENAMING.get())
                player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
            else if (!pipeRename.equals(this.pipeName) && this.renameBox.visible && this.renameBox.isFocused()) {
                PacketHandler.sendToServer(new RenamePipePayload(this.getClickedPos(), this.renameBox.getValue()));
                this.pipeName = pipeRename;
            }
        }

        if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && !pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_RENAMING.get())
            this.renameBox.setVisible(!this.renameBox.visible);
        else if (!Config.WAX_DISABLES_RENAMING.get())
            this.renameBox.setVisible(!this.renameBox.visible);
    }

    public void closeButtonOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null && this.getClickedPos() != null) {
            BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());

            if (!player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.close_pipes.requires_creative").withStyle(ChatFormatting.RED), true);
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_CLOSING.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
            else PacketHandler.sendToServer(new ClosePipeButtonPayload(this.getClickedPos(), Boolean.TRUE));
        }
    }

    public void bubblesButtonOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null && this.getClickedPos() != null) {
            BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());

            if (!player.isCreative() && Config.CREATIVE_BUBBLES.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.pipe_bubbles.requires_creative").withStyle(ChatFormatting.RED), true);
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
            else PacketHandler.sendToServer(new PipeBubblesButtonPayload(this.getClickedPos(), Boolean.TRUE));
        }
    }

    public void bubblesSliderOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null && this.getClickedPos() != null) {
            BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());

            if (!player.isCreative() && Config.CREATIVE_BUBBLES.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.pipe_bubbles.requires_creative").withStyle(ChatFormatting.RED), true);
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_BUBBLES.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
            else if (bubblesSlider.isFocused()) {
                int bubblesDistance = bubblesSlider.getValueInt();
                BlockPos clickedPos = this.getClickedPos();
                PacketHandler.sendToServer(new PipeBubblesSliderPayload(clickedPos, bubblesDistance));
            }
        }
    }

    public void waterSpoutButtonOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null && this.getClickedPos() != null) {
            BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());

            if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.water_spouts.requires_creative").withStyle(ChatFormatting.RED), true);
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
            else PacketHandler.sendToServer(new WaterSpoutButtonPayload(this.getClickedPos(), Boolean.TRUE));

            if (!Config.CREATIVE_WATER_SPOUT.get() || Config.CREATIVE_WATER_SPOUT.get() && !player.isCreative()) {
                BlockState state = world.getBlockState(this.getClickedPos());
                if (state.getBlock() instanceof ClearWarpPipeBlock && state.getValue(WarpPipeBlock.WATER_SPOUT) && !state.getValue(ClearWarpPipeBlock.WATERLOGGED)) {
                    player.displayClientMessage(Component.translatable("display.marioverse.water_spouts.requires_waterlogging").withStyle(ChatFormatting.RED), true);
                }
            }
        }
    }

    public void waterSpoutSliderOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null && this.getClickedPos() != null) {
            BlockEntity blockEntity = world.getBlockEntity(this.getClickedPos());

            if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.water_spouts.requires_creative").withStyle(ChatFormatting.RED), true);
            else if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity && pipeBlockEntity.isWaxed() && Config.WAX_DISABLES_WATER_SPOUTS.get() && world.isClientSide())
                player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
            else if (waterSpoutSlider.isFocused()) {
                int spoutHeight = waterSpoutSlider.getValueInt();
                BlockPos clickedPos = this.getClickedPos();
                PacketHandler.sendToServer(new WaterSpoutSliderPayload(clickedPos, spoutHeight));
            }
        }
    }
}
