package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.BubblesSlider;
import com.wenxin2.marioverse.client.StateImageButton;
import com.wenxin2.marioverse.client.WaterSpoutSlider;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.RenamePipePayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutButtonPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.lwjgl.glfw.GLFW;

public class WarpPipeScreen extends AbstractContainerScreen<WarpPipeMenu> {
    public static ResourceLocation WARP_PIPE_GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/warp_pipe.png");
    Button bubblesButton;
    Button closeButton;
    Button renameButton;
    Button waterSpoutButton;
    EditBox renameBox;
    Inventory inventory;

    public ExtendedSlider waterSpoutSlider;
    public ExtendedSlider bubblesSlider;
    private String pipeName = "";
    private int lastSpoutHeight;
    private int lastBubblesDistance;

    private static final WidgetSprites RENAME_SPRITES = sprites("rename");
    private static final WidgetSprites PIPE_OPEN_SPRITES = sprites("pipe_open");
    private static final WidgetSprites PIPE_CLOSED_SPRITES = sprites("pipe_closed");
    private static final WidgetSprites WATER_SPOUT_OFF_SPRITES = sprites("water_spout_off");
    private static final WidgetSprites WATER_SPOUT_ON_SPRITES = sprites("water_spout_on");
    private static final WidgetSprites BUBBLES_OFF_SPRITES = sprites("bubbles_off");
    private static final WidgetSprites BUBBLES_ON_SPRITES = sprites("bubbles_on");

    public WarpPipeScreen(WarpPipeMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WARP_PIPE_GUI);

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture width, texture height
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        graphics.blit(WARP_PIPE_GUI, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.renameBox.visible && !this.isWaxLocked(ConfigRegistry.WAX_DISABLES_RENAMING.get()))
            graphics.blit(WARP_PIPE_GUI, x + 7, y + 4, 0, 167, 162, 12);
    }

    @Override
    public void init() {
        super.init();
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

        this.renameButton = this.addRenderableWidget(new StateImageButton(x + 7, y + 18, 24, 24,
                RENAME_SPRITES, () -> !this.isWaxLocked(ConfigRegistry.WAX_DISABLES_RENAMING.get()),
                button -> this.renameButtonOnPress(), Component.translatable("menu.marioverse.warp_pipe.rename_button.narrate")));

        this.closeButton = this.addRenderableWidget(new StateImageButton(x + 7, y + 45, 24, 24,
                () -> this.menu.isClosed() ? PIPE_CLOSED_SPRITES : PIPE_OPEN_SPRITES,
                () -> !this.isLocked(ConfigRegistry.CREATIVE_CLOSE_PIPES.get(), ConfigRegistry.WAX_DISABLES_CLOSING.get()),
                button -> this.closeButtonOnPress(), Component.translatable("menu.marioverse.warp_pipe.close_button.narrate")));

        this.waterSpoutButton = this.addRenderableWidget(new StateImageButton(x + 34, y + 18, 24, 24,
                () -> this.menu.hasWaterSpout() ? WATER_SPOUT_ON_SPRITES : WATER_SPOUT_OFF_SPRITES,
                () -> !this.isLocked(ConfigRegistry.CREATIVE_WATER_SPOUT.get(), ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()),
                button -> this.waterSpoutButtonOnPress(), Component.translatable("menu.marioverse.warp_pipe.water_spout_button.narrate")));

        this.lastSpoutHeight = this.menu.getSpoutHeight();
        final Component height = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider.height");
        this.waterSpoutSlider = this.addRenderableWidget(new WaterSpoutSlider(x + 61, y + 18, 108, 24,
                height, Component.literal(""), 0D, 16D, this.lastSpoutHeight, 1D, 0, true, this.menu::isWaxed));

        this.bubblesButton = this.addRenderableWidget(new StateImageButton(x + 34, y + 45, 24, 24,
                () -> this.menu.hasBubbles() ? BUBBLES_ON_SPRITES : BUBBLES_OFF_SPRITES,
                () -> !this.isLocked(ConfigRegistry.CREATIVE_BUBBLES.get(), ConfigRegistry.WAX_DISABLES_BUBBLES.get()),
                button -> this.bubblesButtonOnPress(), Component.translatable("menu.marioverse.warp_pipe.bubbles_button.narrate")));

        this.lastBubblesDistance = this.menu.getBubblesDistance();
        final Component distance = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider.distance");
        this.bubblesSlider = this.addRenderableWidget(new BubblesSlider(x + 61, y + 45, 108, 24,
                distance, Component.literal(""), 0D, 16D, this.lastBubblesDistance, 1D, 0, true, this.menu::isWaxed));
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (this.menu.getSpoutHeight() != this.lastSpoutHeight) {
            this.lastSpoutHeight = this.menu.getSpoutHeight();
            this.waterSpoutSlider.setValue(this.lastSpoutHeight);
        }

        if (this.menu.getBubblesDistance() != this.lastBubblesDistance) {
            this.lastBubblesDistance = this.menu.getBubblesDistance();
            this.bubblesSlider.setValue(this.lastBubblesDistance);
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

        Component tooltip;
        Player player = this.inventory.player;

        if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_RENAMING.get()))
            tooltip = Component.translatable("menu.marioverse.warp_pipe.rename_button_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.rename_button.tooltip");
        this.renameButton.setTooltip(Tooltip.create(tooltip));

        if (this.menu.isClosed()) {
            if (!player.isCreative() && ConfigRegistry.CREATIVE_CLOSE_PIPES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.open_button_creative.tooltip");
            else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_CLOSING.get()))
                tooltip = Component.translatable("menu.marioverse.warp_pipe.open_button_waxed.tooltip");
            else tooltip = Component.translatable("menu.marioverse.warp_pipe.open_button.tooltip");
        } else if (!player.isCreative() && ConfigRegistry.CREATIVE_CLOSE_PIPES.get())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.close_button_creative.tooltip");
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_CLOSING.get()))
            tooltip = Component.translatable("menu.marioverse.warp_pipe.close_button_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.close_button.tooltip");
        this.closeButton.setTooltip(Tooltip.create(tooltip));

        if (this.menu.hasWaterSpout()) {
            if (!player.isCreative() && ConfigRegistry.CREATIVE_WATER_SPOUT.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_off_button_creative.tooltip");
            else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()))
                tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_off_button_waxed.tooltip");
            else tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_off_button.tooltip");
        } else if (!player.isCreative() && ConfigRegistry.CREATIVE_WATER_SPOUT.get())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_on_button_creative.tooltip");
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()))
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_on_button_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_on_button.tooltip");
        this.waterSpoutButton.setTooltip(Tooltip.create(tooltip));

        if (!player.isCreative() && ConfigRegistry.CREATIVE_WATER_SPOUT.get())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider_creative.tooltip");
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()))
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider.tooltip");
        this.waterSpoutSlider.setTooltip(Tooltip.create(tooltip));

        if (this.menu.hasBubbles()) {
            if (!player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get())
                tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_off_button_creative.tooltip");
            else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_BUBBLES.get()))
                tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_off_button_waxed.tooltip");
            else tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_off_button.tooltip");
        } else if (!player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_on_button_creative.tooltip");
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_BUBBLES.get()))
            tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_on_button_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_on_button.tooltip");
        this.bubblesButton.setTooltip(Tooltip.create(tooltip));

        if (!player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider_creative.tooltip");
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_BUBBLES.get()))
            tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider.tooltip");
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
    public boolean keyPressed(final int keyCode, final int b, final int c) {
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
                if (!pipeRename.equals(this.pipeName) && this.renameBox.isVisible() && !this.renameBox.getValue().equals("")) {
                    PacketHandler.sendToServer(new RenamePipePayload(this.menu.containerId, this.renameBox.getValue()));
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

    public void renameButtonOnPress() {
        Player player = this.inventory.player;
        final String pipeRename = this.renameBox.getValue();
        boolean waxLocked = this.isWaxLocked(ConfigRegistry.WAX_DISABLES_RENAMING.get());

        if (waxLocked)
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else if (!pipeRename.equals(this.pipeName) && this.renameBox.visible && this.renameBox.isFocused()) {
            PacketHandler.sendToServer(new RenamePipePayload(this.menu.containerId, this.renameBox.getValue()));
            this.pipeName = pipeRename;
        }

        if (!waxLocked)
            this.renameBox.setVisible(!this.renameBox.visible);
    }

    public void closeButtonOnPress() {
        Player player = this.inventory.player;

        if (!player.isCreative() && ConfigRegistry.CREATIVE_CLOSE_PIPES.get())
            player.displayClientMessage(Component.translatable("display.marioverse.close_pipes.requires_creative").withStyle(ChatFormatting.RED), true);
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_CLOSING.get()))
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new ClosePipeButtonPayload(this.menu.containerId, Boolean.TRUE));
    }

    public void bubblesButtonOnPress() {
        Player player = this.inventory.player;

        if (!player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get())
            player.displayClientMessage(Component.translatable("display.marioverse.pipe_bubbles.requires_creative").withStyle(ChatFormatting.RED), true);
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_BUBBLES.get()))
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new PipeBubblesButtonPayload(this.menu.containerId, Boolean.TRUE));
    }

    public void bubblesSliderOnPress() {
        Player player = this.inventory.player;

        if (!player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get())
            player.displayClientMessage(Component.translatable("display.marioverse.pipe_bubbles.requires_creative").withStyle(ChatFormatting.RED), true);
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_BUBBLES.get()))
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else if (bubblesSlider.isFocused()) {
            int bubblesDistance = bubblesSlider.getValueInt();
            PacketHandler.sendToServer(new PipeBubblesSliderPayload(this.menu.containerId, bubblesDistance));
        }
    }

    public void waterSpoutButtonOnPress() {
        Player player = this.inventory.player;

        if (!player.isCreative() && ConfigRegistry.CREATIVE_WATER_SPOUT.get())
            player.displayClientMessage(Component.translatable("display.marioverse.water_spouts.requires_creative").withStyle(ChatFormatting.RED), true);
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()))
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new WaterSpoutButtonPayload(this.menu.containerId, Boolean.TRUE));

        if (!ConfigRegistry.CREATIVE_WATER_SPOUT.get() || ConfigRegistry.CREATIVE_WATER_SPOUT.get() && !player.isCreative()) {
            if (this.menu.hasWaterSpout() && this.menu.needsWaterlogging())
                player.displayClientMessage(Component.translatable("display.marioverse.water_spouts.requires_waterlogging").withStyle(ChatFormatting.RED), true);
        }
    }

    public void waterSpoutSliderOnPress() {
        Player player = this.inventory.player;

        if (!player.isCreative() && ConfigRegistry.CREATIVE_WATER_SPOUT.get())
            player.displayClientMessage(Component.translatable("display.marioverse.water_spouts.requires_creative").withStyle(ChatFormatting.RED), true);
        else if (this.isWaxLocked(ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()))
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else if (waterSpoutSlider.isFocused()) {
            int spoutHeight = waterSpoutSlider.getValueInt();
            PacketHandler.sendToServer(new WaterSpoutSliderPayload(this.menu.containerId, spoutHeight));
        }
    }

    private boolean isWaxLocked(boolean waxDisables) {
        return waxDisables && this.menu.isWaxed();
    }

    private boolean isLocked(boolean creativeOnly, boolean waxDisables) {
        return (creativeOnly && !this.inventory.player.isCreative()) || this.isWaxLocked(waxDisables);
    }

    private static WidgetSprites sprites(String name) {
        return new WidgetSprites(
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "warp_pipe/" + name),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "warp_pipe/" + name + "_disabled"),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "warp_pipe/" + name + "_highlighted"));
    }
}
