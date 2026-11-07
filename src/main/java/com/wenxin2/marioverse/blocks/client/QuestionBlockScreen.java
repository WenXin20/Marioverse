package com.wenxin2.marioverse.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.client.StateImageButton;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.network.PacketHandler;
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
import org.lwjgl.glfw.GLFW;

public class QuestionBlockScreen extends AbstractContainerScreen<QuestionBlockMenu> {
    public static ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/gui/question_block.png");
    private boolean showIcon = false;
    private boolean initializedFromServer = false;
    private String questionBlockName = "";

    private static final WidgetSprites[] CLOCK_SPRITES = clockSprites();
    private static final WidgetSprites CONFIRM_SPRITES = sprites("confirm");
    private static final WidgetSprites REFILL_OFF_SPRITES = sprites("refill_off");
    private static final WidgetSprites REFILL_ON_SPRITES = sprites("refill_on");
    private static final WidgetSprites UNIT_LEFT_SPRITES = selectableSprites("unit_left");
    private static final WidgetSprites UNIT_MIDDLE_SPRITES = selectableSprites("unit_middle");
    private static final WidgetSprites UNIT_RIGHT_SPRITES = selectableSprites("unit_right");
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

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture width, texture height
        graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.countdownBox.visible)
            graphics.blit(GUI, this.leftPos + 57, this.topPos + 24, 177, 43, 78, 14);

        if (this.showIcon)
            graphics.blit(GUI, this.leftPos + 83, this.topPos + 9, 177, 130, 60, 68);
    }

    @Override
    public void init() {
        super.init();

        this.refillOffButton = new ImageButton(this.leftPos + 14, this.topPos + 45, 37, 20, REFILL_OFF_SPRITES, button -> {
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
        }, Component.translatable("menu.marioverse.question_block.refill_off_button.tooltip"));
        this.addRenderableWidget(this.refillOffButton);

        this.refillOnButton = new ImageButton(this.leftPos + 14, this.topPos + 45, 37, 20, REFILL_ON_SPRITES, button -> {
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
        }, Component.translatable("menu.marioverse.question_block.refill_on_button.tooltip"));
        this.refillOnButton.visible = false;
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

        this.clockButton = new StateImageButton(this.leftPos + 143, this.topPos + 23, 16, 16,
                () -> CLOCK_SPRITES[this.clockFrame()], () -> true, button -> {
            this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }, Component.translatable("menu.marioverse.question_block.clock_button.narrate"));
        this.clockButton.visible = false;
        this.addRenderableWidget(this.clockButton);

        this.ticksButton = new StateImageButton(this.leftPos + 77, this.topPos + 48, 15, 16,
                UNIT_LEFT_SPRITES, () -> this.menu.getTimeUnit() != 0, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 0));
        }, Component.translatable("menu.marioverse.question_block.ticks_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.question_block.ticks_button"));
        this.ticksButton.visible = false;
        this.addRenderableWidget(this.ticksButton);

        this.secondsButton = new StateImageButton(this.leftPos + 92, this.topPos + 48, 14, 16,
                UNIT_MIDDLE_SPRITES, () -> this.menu.getTimeUnit() != 1, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 1));
        }, Component.translatable("menu.marioverse.question_block.seconds_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.question_block.seconds_button"));
        this.secondsButton.visible = false;
        this.addRenderableWidget(this.secondsButton);

        this.minuteButton = new StateImageButton(this.leftPos + 106, this.topPos + 48, 14, 16,
                UNIT_MIDDLE_SPRITES, () -> this.menu.getTimeUnit() != 2, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 2));
        }, Component.translatable("menu.marioverse.question_block.minute_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.question_block.minute_button"));
        this.minuteButton.visible = false;
        this.addRenderableWidget(this.minuteButton);

        this.hourButton = new StateImageButton(this.leftPos + 120, this.topPos + 48, 15, 16,
                UNIT_RIGHT_SPRITES, () -> this.menu.getTimeUnit() != 3, button -> {
            this.confirmButtonOnPress();
            PacketHandler.sendToServer(new TimeUnitPayload(this.menu.containerId, 3));
        }, Component.translatable("menu.marioverse.question_block.hour_button.narrate"))
                .withLabel(Component.translatable("menu.marioverse.question_block.hour_button"));
        this.hourButton.visible = false;
        this.addRenderableWidget(this.hourButton);

        this.confirmButton = new ImageButton(this.leftPos + 141, this.topPos + 45, 20, 20,
                CONFIRM_SPRITES, button -> {
            this.confirmButtonOnPress();
            this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
        }, Component.translatable("menu.marioverse.question_block.confirm_button.tooltip"));
        this.confirmButton.visible = false;
        this.addRenderableWidget(this.confirmButton);

        this.initializedFromServer = false;
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
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                this.confirmButtonOnPress();
                this.menu.playSound(SoundRegistry.REFILL_CONFIRMED.get());
            }
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

    private int clockFrame() {
        int refillTicks = this.menu.getRefillCountdown();

        if (refillTicks > 0 && this.minecraft != null && this.minecraft.level != null) {
            long gameTime = this.minecraft.level.getGameTime();
            int speed = Math.max(1, (int) (Math.sqrt(refillTicks) / 2));

            return (int) ((gameTime / speed) % CLOCK_SPRITES.length);
        }
        return 0;
    }

    private static ResourceLocation sprite(String name) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "question_block/" + name);
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