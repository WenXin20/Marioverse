package com.wenxin2.marioverse.client;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StateImageButton extends ImageButton {
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int LABEL_MARGIN = 2;
    private final Supplier<WidgetSprites> stateSprites;
    private final BooleanSupplier enabled;
    private Component label = Component.empty();

    public StateImageButton(int x, int y, int width, int height, WidgetSprites sprites,
                            BooleanSupplier enabled, OnPress onPress, Component message) {
        this(x, y, width, height, () -> sprites, enabled, onPress, message);
    }

    public StateImageButton(int x, int y, int width, int height, Supplier<WidgetSprites> stateSprites,
                            BooleanSupplier enabled, OnPress onPress, Component message) {
        super(x, y, width, height, stateSprites.get(), onPress, message);
        this.stateSprites = stateSprites;
        this.enabled = enabled;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = this.stateSprites.get().get(this.enabled.getAsBoolean(), this.isHoveredOrFocused());
        graphics.blitSprite(sprite, this.getX(), this.getY(), this.width, this.height);

        if (!this.label.getString().isEmpty())
            renderScrollingString(graphics, Minecraft.getInstance().font, this.label, this.getX() + LABEL_MARGIN, this.getY(),
                    this.getX() + this.width - LABEL_MARGIN, this.getY() + this.height, LABEL_COLOR);
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return this.getMessage().copy();
    }

    public StateImageButton withLabel(Component label) {
        this.label = label;
        return this;
    }
}
