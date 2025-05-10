package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.EntityWarpPlayerHandler;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements BlockWarpPlayerHandler, EntityWarpPlayerHandler {
    @Shadow protected abstract float getBlockSpeedFactor();

    @Shadow public abstract void displayClientMessage(Component component, boolean isAboveHotbar);

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void baseTick() {
        Player player = (Player) (Object) this;

        int preventWarpCooldown = this.getPersistentData().getInt("marioverse:prevent_warp_cooldown");
        if (preventWarpCooldown > 0)
            player.getPersistentData().putInt("marioverse:prevent_warp_cooldown", preventWarpCooldown - 1);

        if (preventWarpCooldown == 0 && this.getPersistentData().getBoolean("marioverse:prevent_warp"))
            player.getPersistentData().putBoolean("marioverse:prevent_warp", false);
        super.baseTick();
    }
}
