package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import java.util.function.Supplier;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GameEventRegistry {
    public static final DeferredHolder<GameEvent, GameEvent> CHECKPOINT_ACTIVATED = register("checkpoint_activate", () -> new GameEvent(16));

    private static <T extends GameEvent> DeferredHolder<GameEvent, T> register(String name, Supplier<T> gameEvent) {
        return Marioverse.GAME_EVENTS.register(name, gameEvent);
    }

    public static void init() {}
}
