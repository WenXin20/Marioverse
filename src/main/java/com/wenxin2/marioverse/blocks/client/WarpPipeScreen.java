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
import com.wenxin2.marioverse.network.server_bound.data.PaymentsPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipePricePayload;
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
import com.wenxin2.marioverse.utils.WarpPrice;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;

public class WarpPipeScreen extends AbstractContainerScreen<WarpPipeMenu> {
    public static ResourceLocation WARP_PIPE_GUI = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/warp_pipe.png");
    private static final ResourceLocation GENERIC_SLOT = ResourceLocation.withDefaultNamespace("container/slot");
    private static final ResourceLocation TEXT_FIELD = ResourceLocation.withDefaultNamespace("widget/text_field");
    private static final ResourceLocation XP_PRICE_ICON = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "warp_pipe/xp_orb");

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
    private static final WidgetSprites CHECKBOX_FORCED_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_selected_forced"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox"),
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "checkbox_selected_forced"));

    private static final int TAB_X_OFFSET = -30;
    private static final int TAB_Y_OFFSET = 3;
    private static final int TAB_SPACING = 27;
    private static final int SLIDER_SYNC_DELAY = 10;
    private static final int XP_PRICE_SLIDER_MAX = 30;

    Button allowAutomationCheckbox;
    Button bubblesButton;
    Button closeButton;
    Button connectionCheckbox;
    Button renameButton;
    Button voidPaymentsCheckbox;
    Button waterSourceCheckbox;
    Button waterSpoutButton;
    EditBox renameBox;
    EditBox xpPriceBox;
    Inventory inventory;
    TabButton paymentsTab;
    TabButton warpPipeTab;
    TabButton waterworksTab;
    TexturedSlider itemPriceSlider;
    TexturedSlider xpPriceSlider;

    public ExtendedSlider waterSpoutSlider;
    public ExtendedSlider bubblesSlider;
    private String pipeName = "";
    private int lastBubblesDistance;
    private int lastPriceItemCount;
    private int lastMenuType = -1;
    private int lastSpoutHeight;
    private int lastXpPrice;
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
            graphics.blitSprite(GENERIC_SLOT, x + WarpPipeMenu.PRICE_SLOT_X - 1, y + WarpPipeMenu.PRICE_SLOT_Y - 1, 18, 18);
            graphics.blitSprite(TEXT_FIELD, x + 37, y + 51, 18, 12);
            graphics.blitSprite(XP_PRICE_ICON, x + 156, y + 51, 12, 12);
        }

        if (this.menu.getMenuType() == WarpPipeMenu.PAYMENTS_TAB)
            graphics.blitSprite(GENERIC_SLOT, x + WarpPipeMenu.PAYMENT_SLOT_X - 1,
                    y + WarpPipeMenu.PAYMENT_SLOT_Y - 1, 18, 18);

        if (this.renameBox.visible && !this.isWaxLocked(ConfigRegistry.WAX_DISABLES_RENAMING.get()))
            graphics.blit(WARP_PIPE_GUI, x + 7, y + 4, 0, 167, 162, 12);

        if (this.xpPriceBox.visible)
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
                () -> this.menu.isWaxed() || ConfigRegistry.WATER_SPOUTS_REQUIRE_WATER.get() ? CHECKBOX_FORCED_SPRITES : CHECKBOX_SPRITES,
                () -> !this.menu.requiresWaterSource() && !this.isWaterSourceForced(),
                button -> this.waterSourceCheckboxOnPress(), Component.translatable("menu.marioverse.warp_pipe.water_source_checkbox.tooltip")));
        this.waterSourceCheckbox.active = !this.isWaterSourceForced();

        this.connectionCheckbox = this.addRenderableWidget(new StateImageButton(x + 157, y + 26, 10, 8,
                () -> this.menu.isWaxed() || this.isConnectionForced() ? CHECKBOX_FORCED_SPRITES : CHECKBOX_SPRITES,
                () -> !this.menu.requiresConnection() && !this.isConnectionForced(),
                button -> this.connectionCheckboxOnPress(), Component.translatable("menu.marioverse.warp_pipe.connection_checkbox.tooltip")));
        this.connectionCheckbox.active = !this.isConnectionForced();

        this.lastPriceItemCount = this.getPriceItemCount();
        this.itemPriceSlider = this.addRenderableWidget(new TexturedSlider(x + 61, y + 21, 90, 18,
                Component.translatable("menu.marioverse.warp_pipe.item_price_slider"), Component.empty(),
                1D, WarpPipeBlockEntity.MAX_PRICE_ITEM_COUNT, Math.max(1, this.lastPriceItemCount), 1D, 0, true) {
            @Override
            public boolean isLocked() {
                return !WarpPipeScreen.this.menu.canEditPrice();
            }

            @NotNull
            @Override
            public String getValueString() {
                return WarpPrice.hasConfigPipePrice() ? String.valueOf(WarpPipeScreen.this.getPriceItemCount()) : super.getValueString();
            }
        });
        this.itemPriceSlider.setTooltip(this.getPriceTooltip("menu.marioverse.warp_pipe.item_price_slider"));

        this.lastXpPrice = this.getXpPrice();
        this.xpPriceBox = new EditBox(this.font, x + 34, y + 53, 24, 12,
                Component.translatable("menu.marioverse.warp_pipe.xp_price_box.narrate"));
        this.xpPriceBox.setBordered(false);
        this.xpPriceBox.setVisible(false);
        this.xpPriceBox.setMaxLength(6);
        this.xpPriceBox.setTextColorUneditable(ChatFormatting.RED.getColor());
        this.xpPriceBox.setFilter(value -> value.chars().allMatch(Character::isDigit));
        this.xpPriceBox.setValue(String.valueOf(this.lastXpPrice));
        this.xpPriceBox.setResponder(this::xpPriceBoxOnChange);
        this.xpPriceBox.setTooltip(this.getPriceTooltip("menu.marioverse.warp_pipe.xp_price_box"));
        this.addRenderableWidget(this.xpPriceBox);

        this.xpPriceSlider = this.addRenderableWidget(new TexturedSlider(x + 61, y + 48, 90, 18,
                Component.translatable("menu.marioverse.warp_pipe.xp_price_slider"), Component.empty(), 0D,
                XP_PRICE_SLIDER_MAX, Math.min(WarpPrice.getLevelForExperience(this.lastXpPrice), XP_PRICE_SLIDER_MAX),
                1D, 0, true) {
            @Override
            public boolean isLocked() {
                return !WarpPipeScreen.this.menu.canEditPrice();
            }

            @NotNull
            @Override
            public String getValueString() {
                String xpPrice = WarpPipeScreen.this.xpPriceBox.getValue();
                return String.valueOf(xpPrice.isEmpty() ? 0 : WarpPrice.getLevelForExperience(Integer.parseInt(xpPrice)));
            }

            @Override
            protected void applyValue() {
                if (!WarpPipeScreen.this.xpPriceBox.isFocused())
                    WarpPipeScreen.this.xpPriceBox.setValue(String.valueOf(WarpPrice.getExperienceForLevel(this.getValueInt())));
            }
        });
        this.xpPriceSlider.setTooltip(this.getPriceTooltip("menu.marioverse.warp_pipe.xp_price_slider"));

        this.voidPaymentsCheckbox = this.addRenderableWidget(new StateImageButton(x + 14, y + 49, 10, 8, CHECKBOX_SPRITES,
                () -> !this.isVoidPayments(), button -> this.sendPayments(PaymentsPayload.TOGGLE_VOID),
                Component.translatable("menu.marioverse.warp_pipe.void_payments_checkbox.narrate")));
        this.voidPaymentsCheckbox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe.void_payments_checkbox.tooltip")));

        this.allowAutomationCheckbox = this.addRenderableWidget(new StateImageButton(x + 14, y + 59, 10, 8, CHECKBOX_SPRITES,
                () -> !this.isAllowAutomation(), button -> this.sendPayments(PaymentsPayload.TOGGLE_AUTOMATION),
                Component.translatable("menu.marioverse.warp_pipe.allow_automation_checkbox.narrate")));
        this.allowAutomationCheckbox.setTooltip(Tooltip.create(Component.translatable("menu.marioverse.warp_pipe.allow_automation_checkbox.tooltip")));

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

        int priceItemCount = this.getPriceItemCount();
        if (priceItemCount != this.lastPriceItemCount) {
            this.lastPriceItemCount = priceItemCount;
            if (this.sliderSyncDelay == 0)
                this.itemPriceSlider.setValue(Math.max(1, priceItemCount));
        }

        int xpPrice = this.getXpPrice();
        if (xpPrice != this.lastXpPrice) {
            this.lastXpPrice = xpPrice;
            if (this.sliderSyncDelay == 0 && !this.xpPriceBox.isFocused()) {
                this.xpPriceSlider.setValue(Math.min(WarpPrice.getLevelForExperience(xpPrice), XP_PRICE_SLIDER_MAX));
                this.xpPriceBox.setValue(String.valueOf(xpPrice));
            }
        }
        this.xpPriceBox.setEditable(this.menu.canEditPrice());
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        this.updateTabWidgets();
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics graphics, Slot slot) {
        if (slot != this.menu.getPaymentSlot() || !slot.hasItem()) {
            super.renderSlot(graphics, slot);
            return;
        }

        ItemStack stack = slot.getItem();
        graphics.renderItem(stack, slot.x, slot.y);
        graphics.renderItemDecorations(this.font, stack, slot.x, slot.y, WarpPipeScreen.formatCount(stack.getCount()));
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        Component tooltip;
        Player player = this.inventory.player;

        if (this.hoveredSlot != null && this.hoveredSlot == this.menu.getPaymentSlot() && this.menu.getCarried().isEmpty())
            this.renderPaymentSlotTooltip(graphics, mouseX, mouseY);

        if (this.hoveredSlot == this.menu.getPriceSlot() && this.hoveredSlot != null && !this.hoveredSlot.hasItem())
            graphics.renderTooltip(this.font, this.font.split(Component
                    .translatable("menu.marioverse.warp_pipe.item_price_slot.tooltip"), 200), mouseX, mouseY);

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
        if (this.itemPriceSlider.isFocused() && !this.itemPriceSlider.isLocked())
            this.sendPipePrice(PipePricePayload.ITEM_COUNT, this.itemPriceSlider.getValueInt());
        if (this.xpPriceSlider.isFocused() && !this.xpPriceSlider.isLocked())
            this.sendPipePrice(PipePricePayload.XP_PRICE, WarpPrice.getExperienceForLevel(this.xpPriceSlider.getValueInt()));
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
        if (scrollY != 0 && this.hoveredSlot != null && this.hoveredSlot == this.menu.getPaymentSlot()
                && this.hoveredSlot.hasItem()) {
            this.sendPayments(PaymentsPayload.TAKE_ONE);
            return true;
        }

        if (this.waterSpoutSlider.isMouseOver(mouseX, mouseY)
                && this.waterSpoutSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.waterSpoutSliderOnPress();
            return true;
        }

        if (this.bubblesSlider.isMouseOver(mouseX, mouseY)
                && this.bubblesSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.bubblesSliderOnPress();
            return true;
        }

        if (this.itemPriceSlider.isMouseOver(mouseX, mouseY)
                && this.itemPriceSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.sendPipePrice(PipePricePayload.ITEM_COUNT, this.itemPriceSlider.getValueInt());
            return true;
        }

        if (this.xpPriceSlider.isMouseOver(mouseX, mouseY)
                && this.xpPriceSlider.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;
            this.sendPipePrice(PipePricePayload.XP_PRICE, WarpPrice.getExperienceForLevel(this.xpPriceSlider.getValueInt()));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int b, final int c) {
        if ((keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT)
                && this.getFocused() instanceof TexturedSlider slider && slider.visible) {
            if (slider.isLocked())
                return true;

            slider.keyPressed(keyCode, b, c);
            this.sliderSyncDelay = SLIDER_SYNC_DELAY;

            if (slider == this.itemPriceSlider)
                this.sendPipePrice(PipePricePayload.ITEM_COUNT, slider.getValueInt());
            else if (slider == this.xpPriceSlider)
                this.sendPipePrice(PipePricePayload.XP_PRICE, WarpPrice.getExperienceForLevel(slider.getValueInt()));
            else if (slider == this.waterSpoutSlider)
                this.waterSpoutSliderOnPress();
            else if (slider == this.bubblesSlider)
                this.bubblesSliderOnPress();
            return true;
        }

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

        if (this.xpPriceBox.canConsumeInput()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
                this.setFocused(null);
            else this.xpPriceBox.keyPressed(keyCode, b, c);
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
            this.itemPriceSlider.visible = isWarpPipeTab;
            this.xpPriceSlider.visible = isWarpPipeTab;
            this.xpPriceBox.setVisible(isWarpPipeTab);
            if (!isWarpPipeTab)
                this.renameBox.setVisible(false);

            this.waterSpoutButton.visible = isWaterworksTab;
            this.waterSpoutSlider.visible = isWaterworksTab;
            this.waterSourceCheckbox.visible = isWaterworksTab;
            this.bubblesButton.visible = isWaterworksTab;
            this.bubblesSlider.visible = isWaterworksTab;

            this.voidPaymentsCheckbox.visible = menuType == WarpPipeMenu.PAYMENTS_TAB;
            this.allowAutomationCheckbox.visible = menuType == WarpPipeMenu.PAYMENTS_TAB;
        }
    }

    private ItemStack getPriceItem() {
        return this.menu.getPriceSlot().getItem();
    }

    private int getPriceItemCount() {
        if (WarpPrice.hasConfigPipePrice())
            return Math.max(1, this.getPriceItem().getCount());

        WarpPipeBlockEntity pipeBE = this.getPipeBlockEntity();
        return pipeBE == null ? 1 : pipeBE.getPriceItemCount();
    }

    private void sendPipePrice(int action, int value) {
        PacketHandler.sendToServer(new PipePricePayload(this.menu.containerId, action, value));
    }

    private int getXpPrice() {
        if (WarpPrice.hasConfigPipePrice())
            return this.menu.getConfigXpPrice();

        WarpPipeBlockEntity pipeBE = this.getPipeBlockEntity();
        return pipeBE == null ? 0 : pipeBE.getXpPrice();
    }

    private Tooltip getPriceTooltip(String key) {
        return Tooltip.create(Component.translatable(WarpPrice.hasConfigPipePrice() ? key + "_config.tooltip" : key + ".tooltip"));
    }

    private void xpPriceBoxOnChange(String value) {
        if (value.isEmpty() || !this.xpPriceBox.isFocused())
            return;

        int xpPrice = Integer.parseInt(value);
        this.sliderSyncDelay = SLIDER_SYNC_DELAY;
        this.xpPriceSlider.setValue(Math.min(WarpPrice.getLevelForExperience(xpPrice), XP_PRICE_SLIDER_MAX));
        this.sendPipePrice(PipePricePayload.XP_PRICE, xpPrice);
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

    private boolean isVoidPayments() {
        WarpPipeBlockEntity pipeBE = this.getPipeBlockEntity();
        return pipeBE != null && pipeBE.isVoidPayments();
    }

    private boolean isAllowAutomation() {
        WarpPipeBlockEntity pipeBE = this.getPipeBlockEntity();
        return pipeBE != null && pipeBE.isAllowAutomation();
    }

    private void sendPayments(int action) {
        PacketHandler.sendToServer(new PaymentsPayload(this.menu.containerId, action));
    }

    private void renderPaymentSlotTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        ItemStack payment = this.menu.getPaymentSlot().getItem();
        List<FormattedCharSequence> lines = new ArrayList<>();

        if (payment.isEmpty())
            lines.add(Component.translatable("menu.marioverse.warp_pipe.payment_slot.tooltip").getVisualOrderText());
        else lines.add(Component.translatable("menu.marioverse.warp_pipe.payment_slot.count",
                String.format(Locale.ROOT, "%,d", payment.getCount()), payment.getHoverName(),
                WarpPrice.getItemPlural(payment, payment.getCount())).getVisualOrderText());

        lines.addAll(this.font.split(Component.translatable("menu.marioverse.warp_pipe.payment_slot.tooltip.info"), 200));
        if (payment.isEmpty())
            graphics.renderTooltip(this.font, lines, mouseX, mouseY);
        else graphics.renderTooltip(this.font, lines, WarpPipeScreen::positionAboveItemTooltip, mouseX, mouseY);
    }

    @Nullable
    private static String formatCount(int count) {
        if (count == 1)
            return null;
        if (count < 1000)
            return String.valueOf(count);
        if (count < 10000)
            return String.format(Locale.ROOT, "%.1fk", Math.floor(count / 100.0) / 10.0);
        if (count < 1000000)
            return count / 1000 + "k";
        return String.format(Locale.ROOT, "%.1fm", Math.floor(count / 100000.0) / 10.0);
    }

    private static Vector2ic positionAboveItemTooltip(int screenWidth, int screenHeight, int mouseX, int mouseY, int width, int height) {
        int tooltipX = mouseX + 12;
        if (tooltipX + width > screenWidth)
            tooltipX = Math.max(mouseX - 16 - width, 4);
        return new Vector2i(tooltipX, Math.max(mouseY - 22 - height, 4));
    }
}