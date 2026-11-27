package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class BubblesSlider extends TexturedSlider {
    private final BooleanSupplier waxed;

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
     * @param waxed Whether the pipe this slider controls is waxed
     */
    public BubblesSlider(int x, int y, int width, int height, Component prefix, Component suffix,
                         double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, BooleanSupplier waxed){
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
        this.waxed = waxed;
    }

    @Override
    public boolean isLocked() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && (this.requiresCreativeBubbles(player) || this.waxDisablesBubbles());
    }

    public boolean requiresCreativeBubbles(LocalPlayer player) {
        return !player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get();
    }

    public boolean waxDisablesBubbles() {
        return this.waxed.getAsBoolean() && ConfigRegistry.WAX_DISABLES_BUBBLES.get();
    }
}
