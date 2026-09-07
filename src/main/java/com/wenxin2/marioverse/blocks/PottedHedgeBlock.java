package com.wenxin2.marioverse.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PottedHedgeBlock extends FlowerPotBlock implements BonemealableBlock {
    private final boolean alwaysSnowy;
    private final boolean isRose;

    public PottedHedgeBlock(Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> content,
                            Properties properties, boolean alwaysSnowy, boolean isRose) {
        super(emptyPot, content, properties);
        this.alwaysSnowy = alwaysSnowy;
        this.isRose = isRose;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HedgeBlock.SNOWY, alwaysSnowy).setValue(RoseHedgeBlock.FLOWERS, isRose));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HedgeBlock.SNOWY, RoseHedgeBlock.FLOWERS);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean snowy = state.getValue(HedgeBlock.SNOWY);
        Biome biome = level.getBiome(pos).value();
        boolean snowyBiome = biome.coldEnoughToSnow(pos);
        boolean isSnowing = level.isRaining() && biome.getPrecipitationAt(pos) == Biome.Precipitation.SNOW
                && HedgeBlock.isExposedToSky(level, pos);

        if (!snowy && isSnowing && random.nextInt(16) == 0)
            level.setBlockAndUpdate(pos, state.setValue(HedgeBlock.SNOWY, true));
        else if (snowy && !this.alwaysSnowy && !isSnowing && !snowyBiome)
            level.setBlockAndUpdate(pos, state.setValue(HedgeBlock.SNOWY, false));
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (state.getValue(HedgeBlock.SNOWY) && stack.is(ItemTags.SHOVELS)) {
            level.setBlockAndUpdate(pos, state.setValue(HedgeBlock.SNOWY, false));
            level.playSound(player, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 1.0F, pitch);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            if (stack.isDamageableItem())
                stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

            if (level instanceof ServerLevel serverLevel) {
                LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(HedgeBlock.SNOW_LOOT_TABLE);
                LootParams lootParams = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .create(LootContextParamSets.BLOCK_USE);

                lootTable.getRandomItems(lootParams).forEach(drop -> Block.popResource(level, pos, drop));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if (!state.getValue(HedgeBlock.SNOWY) && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SnowLayerBlock) {
            level.setBlockAndUpdate(pos, state.setValue(HedgeBlock.SNOWY, true));
            level.playSound(player, pos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0F, pitch);
            stack.consume(1, player);

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if (this.isRose && state.getValue(RoseHedgeBlock.FLOWERS) && stack.is(Tags.Items.TOOLS_SHEAR)) {
            level.setBlockAndUpdate(pos, state.setValue(RoseHedgeBlock.FLOWERS, false));
            level.playSound(player, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, pitch);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            if (stack.isDamageableItem())
                stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if (stack.is(Items.BONE_MEAL)) {
            if (!this.isValidBonemealTarget(level, pos, state))
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

            if (level instanceof ServerLevel serverLevel) {
                if (this.isBonemealSuccess(level, serverLevel.random, pos, state))
                    this.performBonemeal(serverLevel, serverLevel.random, pos, state);
                stack.consume(1, player);

                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                level.levelEvent(1505, pos, 15);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return this.isRose && !state.getValue(RoseHedgeBlock.FLOWERS);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return this.isRose && !state.getValue(RoseHedgeBlock.FLOWERS);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        serverLevel.setBlockAndUpdate(pos, state.setValue(RoseHedgeBlock.FLOWERS, true));
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.getPotted() == Blocks.AIR)
            return InteractionResult.CONSUME;

        ItemStack itemStack = this.createPottedItemStack(state);
        if (!player.addItem(itemStack))
            player.drop(itemStack, false);

        level.setBlock(pos, this.getEmptyPot().defaultBlockState(), 3);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @NotNull
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.getPotted() == Blocks.AIR ? super.getCloneItemStack(level, pos, state) : this.createPottedItemStack(state);
    }

    private ItemStack createPottedItemStack(BlockState state) {
        ItemStack itemStack = new ItemStack(this.getPotted());
        if (this.alwaysSnowy && !state.getValue(HedgeBlock.SNOWY))
            itemStack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(HedgeBlock.SNOWY, false));
        return itemStack;
    }
}
