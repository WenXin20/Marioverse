package com.wenxin2.marioverse.client;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
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
    private final Supplier<WidgetSprites> stateSprites;
    private final BooleanSupplier enabled;

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
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return this.getMessage().copy();
    }
}
