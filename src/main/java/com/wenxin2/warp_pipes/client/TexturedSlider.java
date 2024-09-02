package com.wenxin2.warp_pipes.client;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class TexturedSlider extends ExtendedSlider {
    public static final ResourceLocation SLIDER_LOCATION = ResourceLocation.fromNamespaceAndPath(WarpPipes.MODID, "textures/gui/slider.png");

    /**
     * @param x x position of upper left corner
     * @param y y position of upper left corner
     * @param width Width of the widget
     * @param height Height of the widget
     * @param prefix {@link Component} displayed before the value string
     * @param suffix {@link Component} displayed after the value string
     * @param minValue Minimum (left) value of slider
     * @param maxValue Maximum (right) value of slider
     * @param currentValue Starting value when widget is first displayed
     * @param stepSize Size of step used. Precision will automatically be calculated based on this value if this value is not 0.
     * @param precision Only used when {@code stepSize} is 0. Limited to a maximum of 4 (inclusive).
     * @param drawString Should text be displayed on the widget
     */
    public TexturedSlider(int x, int y, int width, int height, Component prefix, Component suffix,
                          double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString){
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        final Minecraft mc = Minecraft.getInstance();

        guiGraphics.blitWithBorder(SLIDER_LOCATION, this.getX(), this.getY(), 0, getTextureY(), this.width, this.height,
                200, 24, 2, 3, 2, 2);

        guiGraphics.blitWithBorder(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 12)), this.getY(),
                0, getHandleTextureY(), 12, this.height, 200, 24 , 2, 3, 3, 3);

        renderScrollingString(guiGraphics, mc.font, 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public int getHandleTextureY() {
        int i = !this.isHovered && !this.canChangeValue ? 2 : 3;
        return i * 24;
    }

    public int getTextureY() {
        int i = this.isFocused() && !this.canChangeValue ? 1 : 0;
        return i * 24;
    }
}
