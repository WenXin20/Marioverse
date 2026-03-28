package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

public class DeathBlock extends Block {
    public static final MapCodec<DeathBlock> CODEC = simpleCodec(DeathBlock::new);

    protected static final VoxelShape SHAPE =
            Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9).optimize();

    @NotNull
    @Override
    protected MapCodec<? extends DeathBlock> codec() {
        return CODEC;
    }

    public DeathBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.getType().is(TagRegistry.DEATH_BLOCKS_IMMUNE) && entity.isAlive()) {
            if (level instanceof ServerLevel serverLevel && !(entity instanceof Player))
                ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleRegistry.GLOWING_STAR.get(), serverLevel, entity, 2.0, 10);
            else ParticleUtils.spawnParticleInBlock(level, entity.blockPosition(), 5, ParticleRegistry.GLOWING_STAR.get());

            if (entity instanceof Player player && !player.isCreative() && !player.isSpectator())
                entity.hurt(DamageSourceRegistry.instakill(entity), Float.MAX_VALUE);
            else if (!(entity instanceof Player)) entity.remove(Entity.RemovalReason.KILLED);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.ability"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.unbreakable"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.description"));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.no_loot"));

            list.add(Component.literal(""));
        } else list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
