package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.client.BubblesSlider;
import com.wenxin2.marioverse.client.StateImageButton;
import com.wenxin2.marioverse.client.TabButton;
import com.wenxin2.marioverse.client.TexturedSlider;
import com.wenxin2.marioverse.client.WaterSpoutSlider;
import com.wenxin2.marioverse.network.server_bound.data.ConnectedPipesCheckboxPayload;
import com.wenxin2.marioverse.network.server_bound.data.MenuTypePayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeCostPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSourceCheckboxPayload;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.RenamePipePayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutButtonPayload;
import com.wenxin2.marioverse.utils.WarpCost;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class WarpPipeScreen extends AbstractContainerScreen<WarpPipeMenu> {
    public static ResourceLocation WARP_PIPE_GUI = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/warp_pipe.png");
    private static final ResourceLocation GENERIC_SLOT = ResourceLocation.withDefaultNamespace("container/slot");
    private static final ResourceLocation TEXT_FIELD = ResourceLocation.withDefaultNamespace("widget/text_field");
    private static final ResourceLocation XP_COST_ICON = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "warp_pipe/xp_orb");

    private static final WidgetSprites RENAME_SPRITES = sprites("rename");
    private static final WidgetSprites PIPE_OPEN_SPRITES = sprites("pipe_open");
    private static final WidgetSprites PIPE_CLOSED_SPRITES = sprites("pipe_closed");
    private static final WidgetSprites WATER_SPOUT_OFF_SPRITES = sprites("water_spout_off");
    private static final WidgetSprites WATER_SPOUT_ON_SPRITES = sprites("water_spout_on");
    private static final WidgetSprites BUBBLES_OFF_SPRITES = sprites("bubbles_off");
    private static final WidgetSprites BUBBLES_ON_SPRITES = sprites("bubbles_on");
    private static final WidgetSprites CHECKBOX_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_selected"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_highlighted"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_selected_highlighted"));
    private static final WidgetSprites CHECKBOX_WAXED_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_selected_waxed"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_selected_waxed"));

    private static final int TAB_X_OFFSET = -30;
    private static final int TAB_Y_OFFSET = 3;
    private static final int TAB_SPACING = 27;
    private static final int SLIDER_SYNC_DELAY = 10;
    private static final int XP_COST_SLIDER_MAX = 30;

    Button bubblesButton;
    Button closeButton;
    Button connectionCheckbox;
    Button renameButton;
    Button waterSourceCheckbox;
    Button waterSpoutButton;
    EditBox renameBox;
    EditBox xpCostBox;
    Inventory inventory;
    TabButton paymentsTab;
    TabButton warpPipeTab;
    TabButton waterworksTab;
    TexturedSlider itemCostSlider;
    TexturedSlider xpCostSlider;

    public ExtendedSlider waterSpoutSlider;
    public ExtendedSlider bubblesSlider;
    private String pipeName = "";
    private int lastBubblesDistance;
    private int lastCostItemCount;
    private int lastMenuType = -1;
    private int lastSpoutHeight;
    private int lastXpCost;
    private int sliderSyncDelay;

    public WarpPipeScreen(WarpPipeMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
    }

    @Override
    public void renderLabels(GuiGraphics graphics, int x, int y) {
        Component title = switch (this.menu.getMenuType()) {
            case WarpPipeMenu.WATERWORKS_TAB -> Component.translatable("menu.marioverse.warp_pipe.waterworks");
            case WarpPipeMenu.PAYMENTS_TAB -> Component.translatable("menu.marioverse.warp_pipe.payments");
            // Warp Pipe "Name"
            default -> this.pipeName.isEmpty() ? this.title : Component.literal(this.pipeName);
        };

        if (!this.renameBox.visible)
            graphics.drawString(this.font, title, this.titleLabelX, this.titleLabelY, 4210752, false);
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

        if (this.menu.getMenuType() == WarpPipeMenu.WARP_PIPE_TAB) {
            graphics.blitSprite(GENERIC_SLOT, x + WarpPipeMenu.COST_SLOT_X - 1, y + WarpPipeMenu.COST_SLOT_Y - 1, 18, 18);
            graphics.blitSprite(TEXT_FIELD, x + 37, y + 51, 18, 12);
            graphics.blitSprite(XP_COST_ICON, x + 156, y + 51, 12, 12);
        }

        if (this.renameBox.visible && !this.isWaxLocked(ConfigRegistry.WAX_DISABLES_RENAMING.get()))
            graphics.blit(WARP_PIPE_GUI, x + 7, y + 4, 0, 167, 162, 12);

        if (this.xpCostBox.visible)
            graphics.blit(WARP_PIPE_GUI, x + 33, y + 51, 0, 180, 26, 12);
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

        this.waterSpoutButton = this.addRenderableWidget(new StateImageButton(x + 7, y + 18, 24, 24,
                () -> this.menu.hasWaterSpout() ? WATER_SPOUT_ON_SPRITES : WATER_SPOUT_OFF_SPRITES,
                () -> !this.isLocked(ConfigRegistry.CREATIVE_WATER_SPOUT.get(), ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()),
                button -> this.waterSpoutButtonOnPress(), Component.translatable("menu.marioverse.warp_pipe.water_spout_button.narrate")));

        this.lastSpoutHeight = this.menu.getSpoutHeight();
        final Component height = Component.translatable("menu.marioverse.warp_pipe.water_spout_slider.height");
        this.waterSpoutSlider = this.addRenderableWidget(new WaterSpoutSlider(x + 37, y + 21, 114, 18,
                height, Component.literal(""), 0D, 16D, this.lastSpoutHeight, 1D, 0, true, this.menu::isWaxed));

        this.bubblesButton = this.addRenderableWidget(new StateImageButton(x + 7, y + 45, 24, 24,
                () -> this.menu.hasBubbles() ? BUBBLES_ON_SPRITES : BUBBLES_OFF_SPRITES,
                () -> !this.isLocked(ConfigRegistry.CREATIVE_BUBBLES.get(), ConfigRegistry.WAX_DISABLES_BUBBLES.get()),
                button -> this.bubblesButtonOnPress(), Component.translatable("menu.marioverse.warp_pipe.bubbles_button.narrate")));

        this.lastBubblesDistance = this.menu.getBubblesDistance();
        final Component distance = Component.translatable("menu.marioverse.warp_pipe.bubbles_slider.distance");
        this.bubblesSlider = this.addRenderableWidget(new BubblesSlider(x + 37, y + 48, 114, 18,
                distance, Component.literal(""), 0D, 16D, this.lastBubblesDistance, 1D, 0, true, this.menu::isWaxed));

        this.waterSourceCheckbox = this.addRenderableWidget(new StateImageButton(x + 157, y + 26, 10, 8,
                () -> this.menu.isWaxed() ? CHECKBOX_WAXED_SPRITES : CHECKBOX_SPRITES, () -> !this.menu.requiresWaterSource() && !this.isWaterSourceForced(),
                button -> this.waterSourceCheckboxOnPress(), Component.translatable("menu.marioverse.warp_pipe.water_source_checkbox.tooltip")));
        this.waterSourceCheckbox.active = !this.isWaterSourceForced();

        this.connectionCheckbox = this.addRenderableWidget(new StateImageButton(x + 157, y + 26, 10, 8,
                () -> this.menu.isWaxed() ? CHECKBOX_WAXED_SPRITES : CHECKBOX_SPRITES, () -> !this.menu.requiresConnection() && !this.isConnectionForced(),
                button -> this.connectionCheckboxOnPress(), Component.translatable("menu.marioverse.warp_pipe.connection_checkbox.tooltip")));
        this.connectionCheckbox.active = !this.isConnectionForced();

        this.lastCostItemCount = this.getCostItemCount();
        this.itemCostSlider = this.addRenderableWidget(new TexturedSlider(x + 61, y + 21, 90, 18,
                Component.translatable("menu.marioverse.warp_pipe.item_cost_slider"), Component.empty(),
                1D, WarpPipeBlockEntity.MAX_COST_ITEM_COUNT, Math.max(1, this.lastCostItemCount), 1D, 0, true) {
            @Override
            public boolean isLocked() {
                return !WarpPipeScreen.this.menu.canEditCost();
            }
        });
        this.itemCostSlider.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe.item_cost_slider.tooltip")));

        this.lastXpCost = this.getXpCost();
        this.xpCostBox = new EditBox(this.font, x + 34, y + 53, 24, 12,
                Component.translatable("menu.marioverse.warp_pipe.xp_cost_box.narrate"));
        this.xpCostBox.setBordered(false);
        this.xpCostBox.setVisible(false);
        this.xpCostBox.setMaxLength(6);
        this.xpCostBox.setFilter(value -> value.chars().allMatch(Character::isDigit));
        this.xpCostBox.setValue(String.valueOf(this.lastXpCost));
        this.xpCostBox.setResponder(this::xpCostBoxOnChange);
        this.xpCostBox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe.xp_cost_box.tooltip")));
        this.addRenderableWidget(this.xpCostBox);

        this.xpCostSlider = this.addRenderableWidget(new TexturedSlider(x + 61, y + 48, 90, 18,
                Component.translatable("menu.marioverse.warp_pipe.xp_cost_slider"), Component.empty(), 0D,
                XP_COST_SLIDER_MAX, Math.min(WarpCost.getLevelForExperience(this.lastXpCost), XP_COST_SLIDER_MAX),
                1D, 0, true) {
            @Override
            public boolean isLocked() {
                return !WarpPipeScreen.this.menu.canEditCost();
            }

            @NotNull
            @Override
            public String getValueString() {
                String xpCost = WarpPipeScreen.this.xpCostBox.getValue();
                return String.valueOf(xpCost.isEmpty() ? 0 : WarpCost.getLevelForExperience(Integer.parseInt(xpCost)));
            }

            @Override
            protected void applyValue() {
                if (!WarpPipeScreen.this.xpCostBox.isFocused())
                    WarpPipeScreen.this.xpCostBox.setValue(String.valueOf(WarpCost.getExperienceForLevel(this.getValueInt())));
            }
        });
        this.xpCostSlider.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe.xp_cost_slider.tooltip")));

        this.warpPipeTab = this.addTab(WarpPipeMenu.WARP_PIPE_TAB, "warp_pipe");
        this.waterworksTab = this.addTab(WarpPipeMenu.WATERWORKS_TAB, "waterworks");
        this.paymentsTab = this.addTab(WarpPipeMenu.PAYMENTS_TAB, "payments");

        this.lastMenuType = -1;
        this.containerTick();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.sliderSyncDelay > 0)
            this.sliderSyncDelay--;

        if (this.menu.getSpoutHeight() != this.lastSpoutHeight) {
            this.lastSpoutHeight = this.menu.getSpoutHeight();
            if (this.sliderSyncDelay == 0)
                this.waterSpoutSlider.setValue(this.lastSpoutHeight);
        }

        if (this.menu.getBubblesDistance() != this.lastBubblesDistance) {
            this.lastBubblesDistance = this.menu.getBubblesDistance();
            if (this.sliderSyncDelay == 0)
                this.bubblesSlider.setValue(this.lastBubblesDistance);
        }

        int costItemCount = this.getCostItemCount();
        if (costItemCount != this.lastCostItemCount) {
            this.lastCostItemCount = costItemCount;
            if (this.sliderSyncDelay == 0)
                this.itemCostSlider.setValue(Math.max(1, costItemCount));
        }

        int xpCost = this.getXpCost();
        if (xpCost != this.lastXpCost) {
            this.lastXpCost = xpCost;
            if (this.sliderSyncDelay == 0 && !this.xpCostBox.isFocused()) {
                this.xpCostSlider.setValue(Math.min(WarpCost.getLevelForExperience(xpCost), XP_COST_SLIDER_MAX));
                this.xpCostBox.setValue(String.valueOf(xpCost));
            }
        }
        this.xpCostBox.setEditable(this.menu.canEditCost());
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        this.updateTabWidgets();
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        Component tooltip;
        Player player = this.inventory.player;

        if (this.hoveredSlot == this.menu.getCostSlot() && this.hoveredSlot != null && !this.hoveredSlot.hasItem())
            graphics.renderTooltip(this.font, this.font.split(Component
                    .translatable("menu.marioverse.warp_pipe.item_cost_slot.tooltip"), 200), mouseX, mouseY);

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

        if (this.menu.isClearPipe())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_source_checkbox_clear.tooltip");
        else if (this.isWaterSourceForced())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_source_checkbox_config.tooltip");
        else if (this.menu.isWaxed())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.water_source_checkbox_waxed.tooltip");
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.water_source_checkbox.tooltip");
        this.waterSourceCheckbox.setTooltip(Tooltip.create(tooltip));

        if (this.isConnectionForced())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.connection_checkbox_config.tooltip",
                    this.getDestinationText());
        else if (this.menu.isWaxed())
            tooltip = Component.translatable("menu.marioverse.warp_pipe.connection_checkbox_waxed.tooltip",
                    this.getDestinationText());
        else tooltip = Component.translatable("menu.marioverse.warp_pipe.connection_checkbox.tooltip",
                    this.getDestinationText());
        this.connectionCheckbox.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (waterSpoutSlider.isFocused())
            this.waterSpoutSliderOnPress();
        if (bubblesSlider.isFocused())
            this.bubblesSliderOnPress();
        if (this.itemCostSlider.isFocused() && !this.itemCostSlider.isLocked())
            this.sendPipeCost(PipeCostPayload.ITEM_COUNT, this.itemCostSlider.getValueInt());
        if (this.xpCostSlider.isFocused() && !this.xpCostSlider.isLocked())
            this.sendPipeCost(PipeCostPayload.XP_COST, WarpCost.getExperienceForLevel(this.xpCostSlider.getValueInt()));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.getFocused() instanceof ExtendedSlider slider) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            return slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.waterSpoutSlider.isMouseOver(mouseX, mouseY) && this.waterSpoutSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.waterSpoutSliderOnPress();
            return true;
        }

        if (this.bubblesSlider.isMouseOver(mouseX, mouseY) && this.bubblesSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.bubblesSliderOnPress();
            return true;
        }

        if (this.itemCostSlider.isMouseOver(mouseX, mouseY) && this.itemCostSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.sendPipeCost(PipeCostPayload.ITEM_COUNT, this.itemCostSlider.getValueInt());
            return true;
        }

        if (this.xpCostSlider.isMouseOver(mouseX, mouseY) && this.xpCostSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.sendPipeCost(PipeCostPayload.XP_COST, WarpCost.getExperienceForLevel(this.xpCostSlider.getValueInt()));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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

        if (this.xpCostBox.canConsumeInput()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
                this.setFocused(null);
            else this.xpCostBox.keyPressed(keyCode, b, c);
            return true;
        }

        if (this.renameBox.canConsumeInput()) {
            this.renameBox.keyPressed(keyCode, b, c);
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
        else {
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
        else {
            int spoutHeight = waterSpoutSlider.getValueInt();
            PacketHandler.sendToServer(new WaterSpoutSliderPayload(this.menu.containerId, spoutHeight));
        }
    }

    public void waterSourceCheckboxOnPress() {
        Player player = this.inventory.player;

        if (this.menu.isWaxed())
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new WaterSourceCheckboxPayload(this.menu.containerId, !this.menu.requiresWaterSource()));
    }

    public void connectionCheckboxOnPress() {
        Player player = this.inventory.player;

        if (this.menu.isWaxed())
            player.displayClientMessage(Component.translatable("display.marioverse.rename_pipes.pipe_waxed").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new ConnectedPipesCheckboxPayload(this.menu.containerId, !this.menu.requiresConnection()));
    }

    private boolean isWaterSourceForced() {
        return this.menu.isClearPipe() || ConfigRegistry.WATER_SPOUTS_REQUIRE_WATER.get();
    }

    private boolean isConnectionForced() {
        return this.menu.isClearPipe() ? ConfigRegistry.REQUIRE_CONNECTED_CLEAR_PIPES.get() : ConfigRegistry.REQUIRE_CONNECTED_PIPES.get();
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

    private TabButton addTab(int tab, String name) {
        TabButton tabButton = this.addRenderableWidget(
                new TabButton(this.leftPos + TAB_X_OFFSET, this.topPos + TAB_Y_OFFSET + tab * TAB_SPACING,
                        ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "warp_pipe/" + name),
                () -> this.menu.getMenuType() == tab, () -> this.tabOnPress(tab),
                        Component.translatable("menu.marioverse.warp_pipe." + name + "_tab.narrate")));

        tabButton.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe." + name + "_tab.tooltip")));
        return tabButton;
    }

    private void tabOnPress(int tab) {
        if (this.menu.getMenuType() == tab)
            return;
        this.renameBox.setVisible(false);
        PacketHandler.sendToServer(new MenuTypePayload(this.menu.containerId, tab));
    }

    private void updateTabWidgets() {
        this.paymentsTab.visible = this.menu.canManagePayments();
        int menuType = this.menu.getMenuType();

        if (menuType != this.lastMenuType) {
            this.lastMenuType = menuType;
            boolean isWarpPipeTab = menuType == WarpPipeMenu.WARP_PIPE_TAB;
            boolean isWaterworksTab = menuType == WarpPipeMenu.WATERWORKS_TAB;

            this.renameButton.visible = isWarpPipeTab;
            this.closeButton.visible = isWarpPipeTab;
            this.connectionCheckbox.visible = isWarpPipeTab;
            this.itemCostSlider.visible = isWarpPipeTab;
            this.xpCostSlider.visible = isWarpPipeTab;
            this.xpCostBox.setVisible(isWarpPipeTab);
            if (!isWarpPipeTab)
                this.renameBox.setVisible(false);

            this.waterSpoutButton.visible = isWaterworksTab;
            this.waterSpoutSlider.visible = isWaterworksTab;
            this.waterSourceCheckbox.visible = isWaterworksTab;
            this.bubblesButton.visible = isWaterworksTab;
            this.bubblesSlider.visible = isWaterworksTab;
        }
    }

    private ItemStack getCostItem() {
        return this.menu.getCostSlot().getItem();
    }

    private int getCostItemCount() {
        WarpPipeBlockEntity pipeBE = this.getPipeBlockEntity();
        return pipeBE == null ? 1 : pipeBE.getCostItemCount();
    }

    private void sendPipeCost(int action, int value) {
        PacketHandler.sendToServer(new PipeCostPayload(this.menu.containerId, action, value));
    }

    private int getXpCost() {
        WarpPipeBlockEntity pipeBE = this.getPipeBlockEntity();
        return pipeBE == null ? 0 : pipeBE.getXpCost();
    }

    private void xpCostBoxOnChange(String value) {
        if (value.isEmpty() || !this.xpCostBox.isFocused())
            return;

        int xpCost = Integer.parseInt(value);
        this.sliderSyncDelay = SLIDER_SYNC_DELAY;
        this.xpCostSlider.setValue(Math.min(WarpCost.getLevelForExperience(xpCost), XP_COST_SLIDER_MAX));
        this.sendPipeCost(PipeCostPayload.XP_COST, xpCost);
    }

    @Nullable
    private WarpPipeBlockEntity getPipeBlockEntity() {
        if (this.minecraft == null || this.minecraft.level == null)
            return null;
        return this.minecraft.level.getBlockEntity(this.menu.getBlockPos()) instanceof WarpPipeBlockEntity pipeBE ? pipeBE : null;
    }

    private Component getDestinationText() {
        BlockPos destination = this.menu.getDestinationPos();
        if (destination == null)
            return Component.empty();

        Component coordinates = Component.translatable("menu.marioverse.warp_pipe.connection_checkbox.coordinates",
                destination.getX(), destination.getY(), destination.getZ()).withStyle(ChatFormatting.YELLOW);
        return Component.translatable("menu.marioverse.warp_pipe.connection_checkbox.destination", coordinates)
                .withStyle(ChatFormatting.GRAY);
    }
}