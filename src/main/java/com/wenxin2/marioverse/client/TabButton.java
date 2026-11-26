package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.Marioverse;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends AbstractButton {
    private static final ResourceLocation TAB_SELECTED = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "tab_selected");
    private static final ResourceLocation TAB_UNSELECTED = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "tab_unselected");
    private static final int TAB_WIDTH = 30;
    private static final int SELECTED_TAB_WIDTH = 35;
    private static final int TAB_HEIGHT = 26;
    private static final int ICON_SIZE = 24;
    private static final int SELECTED_TAB_OFFSET = -2;
    private static final int ICON_X = 5;
    private static final int SELECTED_ICON_X = 4;
    private static final int ICON_Y = 1;
    private final ResourceLocation icon;
    private final BooleanSupplier selected;
    private final Runnable onPress;

    public TabButton(int x, int y, ResourceLocation icon, BooleanSupplier selected, Runnable onPress, Component message) {
        super(x, y, TAB_WIDTH, TAB_HEIGHT, message);
        this.icon = icon;
        this.selected = selected;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean isSelected = this.selected.getAsBoolean();

        if (isSelected)
            graphics.blitSprite(TAB_SELECTED, this.getX() + SELECTED_TAB_OFFSET, this.getY(), SELECTED_TAB_WIDTH, this.height);
        else graphics.blitSprite(TAB_UNSELECTED, this.getX(), this.getY(), this.width, this.height);

        graphics.blitSprite(this.icon, this.getX() + (isSelected ? SELECTED_ICON_X : ICON_X), this.getY() + ICON_Y, ICON_SIZE, ICON_SIZE);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}