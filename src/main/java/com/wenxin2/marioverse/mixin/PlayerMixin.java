package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import com.wenxin2.marioverse.utils.EntityWarpPlayerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements BlockWarpPlayerHandler, EntityWarpPlayerHandler {
    @Shadow protected abstract float getBlockSpeedFactor();

    @Shadow public abstract void displayClientMessage(Component component, boolean isAboveHotbar);

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean marioverse$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    public boolean marioverse$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Player player = (Player) (Object) this;
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(player.getBbHeight()));
        BlockPos posBelowEntity = BlockPos.containing(player.position().x, player.position().y - 0.3, player.position().z);
        BlockPos posInBlock = pos.above(Math.round(player.getBbHeight()) - 1);
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateInBlock = world.getBlockState(posInBlock);
        BlockState stateBelowEntity = world.getBlockState(posBelowEntity);

        int preventWarpCooldown = this.getPersistentData().getInt("marioverse:prevent_warp_cooldown");
        if (preventWarpCooldown > 0)
            player.getPersistentData().putInt("marioverse:prevent_warp_cooldown", preventWarpCooldown - 1);

        if (preventWarpCooldown == 0 && this.getPersistentData().getBoolean("marioverse:prevent_warp"))
            player.getPersistentData().putBoolean("marioverse:prevent_warp", false);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(player, world, offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(player, world, pos);
            }
        }
    }
}
