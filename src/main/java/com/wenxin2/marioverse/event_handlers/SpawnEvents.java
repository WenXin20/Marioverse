package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.init.EntityRegistry;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SpawnEvents {
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(EntityRegistry.GOOMBA.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoombaEntity::checkGoombaSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }
}
