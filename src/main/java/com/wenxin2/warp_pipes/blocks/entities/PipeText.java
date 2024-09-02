package com.wenxin2.warp_pipes.blocks.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class PipeText {
    private static final Codec<Component[]> LINES_CODEC = ComponentSerialization.FLAT_CODEC.listOf()
            .comapFlatMap((list) -> Util.fixedSize(list, 1)
                    .map((components) -> new Component[]{components.getFirst()}),
                    (components) -> List.of(components[0]));
    public static final Codec<PipeText> DIRECT_CODEC =
            RecordCodecBuilder.create((instance) -> instance.group(LINES_CODEC.fieldOf("pipe_name").forGetter((pipeText) -> pipeText.messages),
                    LINES_CODEC.optionalFieldOf("filtered_pipe_name").forGetter(PipeText::getOnlyFilteredMessages),
            DyeColor.CODEC.fieldOf("color").orElse(DyeColor.BLACK).forGetter((pipeText) -> pipeText.color),
                    Codec.BOOL.fieldOf("has_glowing_text").orElse(false).forGetter((pipeText) -> pipeText.hasGlowingText)).apply(instance, PipeText::load));
    public static final int LINES = 1;
    private final Component[] messages;
    private final Component[] filteredMessages;
    private final DyeColor color;
    private final boolean hasGlowingText;
    @Nullable
    private FormattedCharSequence[] renderMessages;
    private boolean renderMessagedFiltered;

    public PipeText() {
        this(emptyMessages(), emptyMessages(), DyeColor.BLACK, false);
    }

    public PipeText(Component[] messages, Component[] filteredMessages, DyeColor color, boolean hasGlowingText) {
        this.messages = messages;
        this.filteredMessages = filteredMessages;
        this.color = color;
        this.hasGlowingText = hasGlowingText;
    }

    private static Component[] emptyMessages() {
        return new Component[]{CommonComponents.EMPTY};
    }

    private static PipeText load(Component[] messages, Optional<Component[]> filteredMessages, DyeColor color, boolean hasGlowingText) {
        Component[] filteredMessagesArray = filteredMessages.orElseGet(PipeText::emptyMessages);
        populateFilteredMessagesWithRawMessages(messages, filteredMessagesArray);
        return new PipeText(messages, filteredMessagesArray, color, hasGlowingText);
    }

    private static void populateFilteredMessagesWithRawMessages(Component[] messages, Component[] filteredMessages) {
        for(int i = 0; i < 1; ++i) {
            if (filteredMessages[i].equals(CommonComponents.EMPTY)) {
                filteredMessages[i] = messages[i];
            }
        }
    }

    public boolean hasGlowingText() {
        return this.hasGlowingText;
    }

    public PipeText setHasGlowingText(boolean hasGlowingText) {
        return hasGlowingText == this.hasGlowingText ? this : new PipeText(this.messages, this.filteredMessages, this.color, hasGlowingText);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public PipeText setColor(DyeColor color) {
        return color == this.getColor() ? this : new PipeText(this.messages, this.filteredMessages, color, this.hasGlowingText);
    }

    public Component getMessage(int index, boolean isFiltered) {
        return this.getMessages(isFiltered)[index];
    }

    public PipeText setMessage(int index, Component text) {
        return this.setMessage(index, text, text);
    }

    public PipeText setMessage(int index, Component text, Component filteredText) {
        Component[] messagesArray = Arrays.copyOf(this.messages, this.messages.length);
        Component[] filteredMessagesArray = Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
        messagesArray[index] = text;
        filteredMessagesArray[index] = filteredText;
        return new PipeText(messagesArray, filteredMessagesArray, this.color, this.hasGlowingText);
    }

    public boolean hasMessage(Player player) {
        return Arrays.stream(this.getMessages(player.isTextFilteringEnabled())).anyMatch((message) -> {
            return !message.getString().isEmpty();
        });
    }

    public Component[] getMessages(boolean isFiltered) {
        return isFiltered ? this.filteredMessages : this.messages;
    }

    public FormattedCharSequence[] getRenderMessages(boolean renderMessagesFiltered, Function<Component, FormattedCharSequence> formatter) {
        if (this.renderMessages == null || this.renderMessagedFiltered != renderMessagesFiltered) {
            this.renderMessagedFiltered = renderMessagesFiltered;
            this.renderMessages = new FormattedCharSequence[1];

            for(int i = 0; i < 1; ++i) {
                this.renderMessages[i] = formatter.apply(this.getMessage(i, renderMessagesFiltered));
            }
        }
        return this.renderMessages;
    }

    private Optional<Component[]> getOnlyFilteredMessages() {
        Component[] filteredMessagesArray = new Component[1];
        boolean flag = false;

        for(int i = 0; i < 1; ++i) {
            Component filteredMessages = this.filteredMessages[i];
            if (!filteredMessages.equals(this.messages[i])) {
                return Optional.of(this.filteredMessages);
            }
        }
        return Optional.empty();
    }

    public boolean hasAnyClickCommands(Player player) {
        for(Component component : this.getMessages(player.isTextFilteringEnabled())) {
            Style style = component.getStyle();
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent != null && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                return true;
            }
        }
        return false;
    }
}