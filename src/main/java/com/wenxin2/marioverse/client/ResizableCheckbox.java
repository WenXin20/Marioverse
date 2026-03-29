package com.wenxin2.marioverse.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ResizableCheckbox extends AbstractButton {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox");
    private static final int TEXT_COLOR = 14737632;
    private static final int SPACING = 4;
    private final int size;
    private boolean selected;
    private final OnValueChange onValueChange;
    protected final MultiLineTextWidget textWidget;

    protected ResizableCheckbox(int x, int y, int maxWidth, Component message, Font font,
                                boolean selected, OnValueChange onValueChange, int size) {
        super(x, y, 0, 0, message);

        this.size = size;
        this.width = Math.min(getDefaultWidth(message, font, size), maxWidth);
        this.textWidget = new MultiLineTextWidget(message, font)
                .setMaxWidth(this.width)
                .setColor(TEXT_COLOR);

        this.height = Math.max(size, this.textWidget.getHeight());
        this.selected = selected;
        this.onValueChange = onValueChange;
    }

    public static int getDefaultWidth(Component text, Font font, int boxSize) {
        return boxSize + SPACING + font.width(text);
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.onValueChange.onValueChange(this, this.selected);
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.createNarrationMessage());

        if (this.active) {
            if (this.isFocused())
                output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            else output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableDepthTest();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();

        ResourceLocation sprite;
        if (this.selected)
            sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
        else sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;

        int size = this.size;

        graphics.blitSprite(sprite, this.getX(), this.getY(), size, size);

        int textX = this.getX() + size + SPACING;
        int textY = this.getY() + size / 2 - this.textWidget.getHeight() / 2;

        this.textWidget.setPosition(textX, textY);
        this.textWidget.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    public static Builder builder(Component message, Font font) {
        return new Builder(message, font);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Component message;
        private final Font font;
        private int maxWidth;
        private int x = 0;
        private int y = 0;
        private int size = 17;
        private OnValueChange onValueChange = OnValueChange.NOP;
        private boolean selected = false;
        @Nullable private OptionInstance<Boolean> option = null;
        @Nullable private Tooltip tooltip = null;

        Builder(Component message, Font font) {
            this.message = message;
            this.font = font;
            this.maxWidth = getDefaultWidth(message, font, size);
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder onValueChange(OnValueChange callback) {
            this.onValueChange = callback;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            this.option = null;
            return this;
        }

        public Builder selected(OptionInstance<Boolean> option) {
            this.option = option;
            this.selected = option.get();
            return this;
        }

        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder maxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public ResizableCheckbox build() {
            OnValueChange callback = this.option == null ? this.onValueChange : (cb, value) -> {
                this.option.set(value);
                this.onValueChange.onValueChange(cb, value);
            };

            ResizableCheckbox checkbox = new ResizableCheckbox(this.x, this.y, this.maxWidth,
                    this.message, this.font, this.selected, callback, this.size);

            checkbox.setTooltip(this.tooltip);
            return checkbox;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnValueChange {
        OnValueChange NOP = (checkbox, value) -> {};

        void onValueChange(ResizableCheckbox checkbox, boolean value);
    }
}