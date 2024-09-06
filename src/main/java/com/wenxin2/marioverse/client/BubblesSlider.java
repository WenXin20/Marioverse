package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BubblesSlider extends TexturedSlider {

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
    public BubblesSlider(int x, int y, int width, int height, Component prefix, Component suffix,
                         double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString){
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        guiGraphics.blitWithBorder(SLIDER_LOCATION, this.getX(), this.getY(), 0, getTextureY(), this.width, this.height,
                200, 24, 2, 3, 2, 2);

        if (player != null && requiresCreativeBubbles(player))
            guiGraphics.blitWithBorder(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 12)), this.getY(),
                    0, 96, 12, this.height, 200, 24 , 2, 3, 3, 3);
        else if (player != null && waxDisablesBubbles(minecraft))
            guiGraphics.blitWithBorder(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 12)), this.getY(),
                    0, 96, 12, this.height, 200, 24 , 2, 3, 3, 3);
        else guiGraphics.blitWithBorder(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 12)), this.getY(),
                0, getHandleTextureY(), 12, this.height, 200, 24 , 2, 3, 3, 3);

        renderScrollingString(guiGraphics, minecraft.font, 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public boolean requiresCreativeBubbles(LocalPlayer player) {
        return !player.isCreative() && ConfigRegistry.CREATIVE_BUBBLES.get();
    }

    public boolean waxDisablesBubbles(Minecraft minecraft) {
        if (minecraft.player != null && minecraft.hitResult != null && minecraft.level != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) minecraft.hitResult).getBlockPos();
            BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);

            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                return pipeBlockEntity.isWaxed() && ConfigRegistry.WAX_DISABLES_BUBBLES.get();
            }
        }
        return false;
    }
}
