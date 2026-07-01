package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class WarpPipeBlock extends BaseEntityDirectionalBlock {
    public static final MapCodec<WarpPipeBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(DyeColor.CODEC.optionalFieldOf("color")
                            .forGetter(flagBlock -> Optional.ofNullable(flagBlock.color)), propertiesCodec())
                    .apply(instance, (dyeColor, properties) -> new WarpPipeBlock(dyeColor.orElse(null), properties))
    );
    public static final BooleanProperty BUBBLES = BooleanProperty.create("bubbles");
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATER_SPOUT = BooleanProperty.create("water_spout");
    @Nullable private final DyeColor color;

    public WarpPipeBlock(@Nullable DyeColor color, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(BUBBLES, Boolean.TRUE)
                .setValue(CLOSED, Boolean.FALSE).setValue(ENTRANCE, Boolean.TRUE)
                .setValue(POWERED, Boolean.FALSE).setValue(WATER_SPOUT, Boolean.FALSE));
        this.color = color;
    }

    @Override
    public MapCodec<WarpPipeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(BUBBLES, CLOSED, ENTRANCE, FACING, POWERED, WATER_SPOUT);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new WarpPipeBlockEntity(pos, state);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            if (state.getValue(WATER_SPOUT))
                return Math.min(warpPipeBE.getSpoutHeight(), 15);
            else if (state.getValue(BUBBLES))
                return Math.min(warpPipeBE.getBubblesDistance(), 15);
        }
        return 0;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.WARP_PIPE_BLOCK_ENTITY.get(), world.isClientSide ? WarpPipeBlockEntity::clientTick : WarpPipeBlockEntity::serverTick);
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag options) {
        super.appendHoverText(stack, context, list, options);
        Spawner.appendHoverText(stack, list, "SpawnData");
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getColoredItemStack(@Nullable DyeColor color) {
        return new ItemStack(BlockRegistry.WARP_PIPES.get(color));
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
        BlockEntity blockEntity = level.getBlockEntity(pos);
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (state.hasProperty(ENTRANCE) && state.getValue(ENTRANCE) && player.isCreative()
                && blockEntity instanceof WarpPipeBlockEntity pipeBE
                && !pipeBE.getTheItem().isEmpty() && heldItem.isEmpty()) {
            level.sendBlockUpdated(pos, state, state, 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            level.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(),
                    SoundSource.BLOCKS, 1.0F, pitch);
            pipeBE.splitTheItem(1);
            pipeBE.markUpdated();

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @NotNull
    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Item item = stack.getItem();

        if (state.getValue(ENTRANCE) && player.getItemInHand(hand).is(TagRegistry.WRENCHES)) {
            if (blockEntity instanceof WarpPipeBlockEntity) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> new WarpPipeMenu(id,
                        playerInventory, ContainerLevelAccess.create(level, pos), pos), ((WarpPipeBlockEntity) blockEntity).getDisplayName()));
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (state.getValue(ENTRANCE) && player.isCreative()
                && !heldItem.is(TagRegistry.WARP_PIPE_CANNOT_SPAWN_ITEMS)
                && !(heldItem.getItem() instanceof SpawnEggItem)
                && blockEntity instanceof WarpPipeBlockEntity pipeBE
                && !ItemStack.isSameItemSameComponents(heldItem, pipeBE.getTheItem())
                && !pipeBE.isWaxed()) {

            if (!heldItem.isEmpty())
                pipeBE.setTheItem(player.getItemInHand(hand).copyWithCount(1));
            else if (!pipeBE.getTheItem().isEmpty()) pipeBE.splitTheItem(1);

            pipeBE.markUpdated();
            level.sendBlockUpdated(pos, state, state, 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (!heldItem.isEmpty())
                player.getItemInHand(hand).consume(1, player);

            return ItemInteractionResult.SUCCESS;
        }

        if (blockEntity instanceof BaseWarpBlockEntity warpBlockEntity) {
            boolean isSuccesful = false;
            boolean isSuccesfulTool = false;

            if (blockEntity instanceof WarpPipeBlockEntity pipeBE) {
                if (!warpBlockEntity.isWaxed()) {
                    if (item == Items.INK_SAC) {
                        if (pipeBE.updateText((pipeText) -> pipeText.setHasGlowingText(Boolean.FALSE))) {
                            level.playSound(player, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, pitch);
                            coloredDustParticles(level, pos, new Vector3f(0, 0, 0), UniformInt.of(8, 12));
                            warpBlockEntity.markUpdated();
                            isSuccesful = true;
                        }
                    } else if (item == Items.GLOW_INK_SAC) {
                        if (pipeBE.updateText((pipeText) -> pipeText.setHasGlowingText(Boolean.TRUE))) {
                            level.playSound(player, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, pitch);
                            spawnParticlesOnBlockFaces(level, pos, ParticleTypes.GLOW, new Vec3(0, 0, 0), UniformInt.of(3, 5));
                            warpBlockEntity.markUpdated();
                            isSuccesful = true;
                        }
                    } else if (stack.is(Items.BRUSH)) {
                        if (hit.getDirection() == Direction.NORTH) {
                            pipeBE.setTextNorth(!pipeBE.hasTextNorth());
                            this.dyedDustParticles(pipeBE, level, pos, Direction.NORTH);
                        } else if (hit.getDirection() == Direction.SOUTH) {
                            pipeBE.setTextSouth(!pipeBE.hasTextSouth());
                            this.dyedDustParticles(pipeBE, level, pos, Direction.SOUTH);
                        } else if (hit.getDirection() == Direction.EAST) {
                            pipeBE.setTextEast(!pipeBE.hasTextEast());
                            this.dyedDustParticles(pipeBE, level, pos, Direction.EAST);
                        } else if (hit.getDirection() == Direction.WEST) {
                            pipeBE.setTextWest(!pipeBE.hasTextWest());
                            this.dyedDustParticles(pipeBE, level, pos, Direction.WEST);
                        } else if (hit.getDirection() == Direction.UP) {
                            pipeBE.setTextAbove(!pipeBE.hasTextAbove());
                            this.dyedDustParticles(pipeBE, level, pos, Direction.UP);
                        } else if (hit.getDirection() == Direction.DOWN) {
                            pipeBE.setTextBelow(!pipeBE.hasTextBelow());
                            this.dyedDustParticles(pipeBE, level, pos, Direction.DOWN);
                        }
                        level.playSound(player, pos, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.BLOCKS, 1.0F, pitch);
                        warpBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    } else if (stack.is(CompatRegistry.BUBBLE_BLOWER.get()) || stack.is(CompatRegistry.SOAP.get())) {
                        if (hit.getDirection() == Direction.NORTH && pipeBE.hasTextNorth()) {
                            pipeBE.setTextNorth(Boolean.FALSE);
                            level.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            this.sudParticles(level, pos, Direction.NORTH);
                            warpBlockEntity.markUpdated();
                            isSuccesfulTool = true;
                        } else if (hit.getDirection() == Direction.SOUTH && pipeBE.hasTextSouth()) {
                            pipeBE.setTextSouth(Boolean.FALSE);
                            level.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            this.sudParticles(level, pos, Direction.SOUTH);
                            warpBlockEntity.markUpdated();
                            isSuccesfulTool = true;
                        } else if (hit.getDirection() == Direction.EAST && pipeBE.hasTextEast()) {
                            pipeBE.setTextEast(Boolean.FALSE);
                            level.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            this.sudParticles(level, pos, Direction.EAST);
                            warpBlockEntity.markUpdated();
                            isSuccesfulTool = true;
                        } else if (hit.getDirection() == Direction.WEST && pipeBE.hasTextWest()) {
                            pipeBE.setTextWest(Boolean.FALSE);
                            level.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            this.sudParticles(level, pos, Direction.WEST);
                            warpBlockEntity.markUpdated();
                            isSuccesfulTool = true;
                        } else if (hit.getDirection() == Direction.UP && pipeBE.hasTextAbove()) {
                            pipeBE.setTextAbove(Boolean.FALSE);
                            level.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            this.sudParticles(level, pos, Direction.UP);
                            warpBlockEntity.markUpdated();
                            isSuccesfulTool = true;
                        } else if (hit.getDirection() == Direction.DOWN && pipeBE.hasTextBelow()) {
                            pipeBE.setTextBelow(Boolean.FALSE);
                            level.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            this.sudParticles(level, pos, Direction.DOWN);
                            warpBlockEntity.markUpdated();
                            isSuccesfulTool = true;
                        }
                    } else {
                        if (item instanceof DyeItem dyeItem
                                && pipeBE.updateText((pipeText) -> pipeText.setColor(dyeItem.getDyeColor()))) {
                            int textColor = dyeItem.getDyeColor().getTextColor();
                            float red = (float) (textColor >> 16 & 255) / 255.0F;
                            float green = (float) (textColor >> 8 & 255) / 255.0F;
                            float blue = (float) (textColor & 255) / 255.0F;
                            Vector3f colorVec = new Vector3f(red, green, blue);

                            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, pitch);
                            ParticleUtils.spawnParticlesOnBlockFaces(level, pos, new DustParticleOptions(colorVec, 1.0F),
                                    UniformInt.of(8, 12));
                            warpBlockEntity.markUpdated();
                            isSuccesful = true;
                        }
                    }
                }
            }

            if (isSuccesfulTool) {
                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            if (isSuccesful) {
                if (!player.isCreative())
                    stack.shrink(1);

                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WarpPipeBlockEntity pipeBE) {
            if (stack.has(DataComponents.CUSTOM_NAME) || stack.has(DataComponentRegistry.PIPE_NAME)) {
                pipeBE.setCustomName(stack.getHoverName());
                pipeBE.setPipeName(stack.getHoverName());
                pipeBE.setChanged();
            }

            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && data.copyTag().hasUUID("UUID")) {
                pipeBE.setUUID(data.copyTag().getUUID("UUID"));
                pipeBE.setChanged();
            } else {
                UUID uuid = UUID.randomUUID();
                pipeBE.setUUID(uuid);
                pipeBE.setChanged();
            }

            pipeBE.onLoad();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Direction direction = placeContext.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @NotNull
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos posNeighbor, boolean b) {
        if (world instanceof ServerLevel serverWorld) {
            this.checkAndFlip(state, serverWorld, pos);
//            this.flipNeighborPipeState(serverWorld, state, pos, world.getBlockState(posNeighbor));
        }

        super.neighborChanged(state, world, pos, block, posNeighbor, b);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                           LevelAccessor levelAccessor, BlockPos pos, BlockPos posNeighbor) {
        if (direction == state.getValue(FACING))
            return this.calculateEntrance(state, levelAccessor, pos);
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        BlockState newState = this.calculateEntrance(state, level, pos);
        Direction direction = state.getValue(FACING);
        BlockState stateRelative = level.getBlockState(pos.relative(direction));

        if (oldState.getBlock() == state.getBlock())
            return;
        if (level instanceof ServerLevel serverLevel)
            this.checkAndFlip(state, serverLevel, pos);

        if (newState != state)
            level.setBlock(pos, newState, 3);

        if (stateRelative.is(Blocks.WATER))
            level.scheduleTick(pos, this, 3);
    }

    public BlockState calculateEntrance(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockState stateRelative = level.getBlockState(pos.relative(facing));

        boolean connects = stateRelative.is(TagRegistry.PIPE_JUNCTION_BLOCKS)
                || stateRelative.getBlock() instanceof ClearWarpPipeBlock
                || (stateRelative.getBlock() instanceof WarpPipeBlock
                    && !(stateRelative.getBlock() instanceof ClearWarpPipeBlock));
        boolean shouldBeEntrance = !connects;

        if (state.getValue(ENTRANCE) != shouldBeEntrance)
            return state.setValue(ENTRANCE, shouldBeEntrance);
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (serverWorld.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE) {
            if (!serverWorld.isClientSide && pipeBE.getUUID() == null) {
                UUID uuid = UUID.randomUUID();
                pipeBE.setUUID(uuid);
                pipeBE.setChanged();
                BaseWarpBlockEntity.WARP_LOCATIONS.put(uuid, pos);
            }

            if (state.getValue(WATER_SPOUT) && state.getValue(FACING) == Direction.UP && serverWorld.dimension() != Level.NETHER) {
                WaterSpoutBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBE.spoutHeight);
                serverWorld.scheduleTick(pos, this, 3);
            }

            if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.UP) {
                PipeBubblesBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBE.bubblesDistance);
                serverWorld.scheduleTick(pos, this, 3);
            } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.DOWN) {
                PipeBubblesBlock.repeatColumnDown(serverWorld, pos.below(), state, pipeBE.bubblesDistance);
                serverWorld.scheduleTick(pos, this, 3);
            } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.NORTH) {
                PipeBubblesBlock.repeatColumnNorth(serverWorld, pos.north(), state, pipeBE.bubblesDistance);
                serverWorld.scheduleTick(pos, this, 3);
            } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.SOUTH) {
                PipeBubblesBlock.repeatColumnSouth(serverWorld, pos.south(), state, pipeBE.bubblesDistance);
                serverWorld.scheduleTick(pos, this, 3);
            } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.EAST) {
                PipeBubblesBlock.repeatColumnEast(serverWorld, pos.east(), state, pipeBE.bubblesDistance);
                serverWorld.scheduleTick(pos, this, 3);
            } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.WEST) {
                PipeBubblesBlock.repeatColumnWest(serverWorld, pos.west(), state, pipeBE.bubblesDistance);
                serverWorld.scheduleTick(pos, this, 3);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        double dx = pos.getX();
        double dy = pos.getY();
        double dz = pos.getZ();

        Fluid fluidAbove = world.getFluidState(pos.above()).getType();
        Fluid fluidBelow = world.getFluidState(pos.below()).getType();
        Fluid fluidNorth = world.getFluidState(pos.north()).getType();
        Fluid fluidSouth = world.getFluidState(pos.south()).getType();
        Fluid fluidEast = world.getFluidState(pos.east()).getType();
        Fluid fluidWest = world.getFluidState(pos.west()).getType();

        Block blockAbove = world.getBlockState(pos.above()).getBlock();
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        Block blockNorth = world.getBlockState(pos.north()).getBlock();
        Block blockSouth = world.getBlockState(pos.south()).getBlock();
        Block blockEast = world.getBlockState(pos.east()).getBlock();
        Block blockWest = world.getBlockState(pos.west()).getBlock();

        if (!state.getValue(CLOSED) && (state.getValue(BUBBLES) || state.getValue(WATER_SPOUT)) && state.getValue(ENTRANCE)
                && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {

            if (warpPipeBE.getPersistentData().isEmpty()) {

                if (state.getValue(FACING) == Direction.UP) {
                    if (fluidAbove instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 1.0D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidAbove instanceof WaterFluid || blockAbove instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 0.5D, dy + 1.15D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat() + 1.15D, dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.DOWN) {
                    if (fluidBelow instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy - 0.5D, dz + 0.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidBelow instanceof WaterFluid || blockBelow instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 0.5D, dy - 1.15D, dz + 0.5D, 0.0D, -0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat(),
                                dy - (double) random.nextFloat() - 1.15D, dz + (double) random.nextFloat(), 0.0D, -0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.NORTH) {
                    if (fluidNorth instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 0.5D, dz - 0.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidNorth instanceof WaterFluid || blockNorth instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 0.5D, dy + 0.5D, dz - 1.15D, 0.0D, 0.4D, -1.5D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat() - 1.15D, 0.0D, 0.4D, -1.5D);
                    }
                }
                if (state.getValue(FACING) == Direction.SOUTH) {
                    if (fluidSouth instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 0.5D, dz + 1.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidSouth instanceof WaterFluid || blockSouth instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 0.5D, dy + 0.5D, dz + 1.15D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat() + 1.15D, 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.EAST) {
                    if (fluidEast instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 1.05D, dy + 0.5D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidEast instanceof WaterFluid || blockEast instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 1.15D, dy + 0.5D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat() + 1.15D,
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.WEST) {
                    if (fluidWest instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx - 0.05D, dy + 0.5D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidWest instanceof WaterFluid || blockWest instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx - 1.15D, dy + 0.5D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat() - 1.15D,
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
            }
        }
        super.animateTick(state, world, pos, random);
    }

    public void checkAndFlip(BlockState state, ServerLevel serverWorld, BlockPos pos) {
        boolean hasNeighborSignal = serverWorld.hasNeighborSignal(pos);
        BlockState newState = state;
        if (hasNeighborSignal != state.getValue(POWERED)) {
            if (!state.getValue(POWERED)) {
                newState = state.cycle(CLOSED);
                serverWorld.playSound(null, pos, newState.getValue(CLOSED)
                        ? SoundRegistry.PIPE_OPENS.get() : SoundRegistry.PIPE_CLOSES.get(), SoundSource.BLOCKS);
            }

            serverWorld.setBlock(pos, newState.setValue(POWERED, hasNeighborSignal), 3);
        }
    }

    private void flipNeighborPipeState(ServerLevel world, BlockState state, BlockPos pos, BlockState stateNeighbor) {
        // TODO: Fix this
        Direction facing = stateNeighbor.getValue(FACING);
        BlockPos checkPos = pos.relative(facing);

        if (state.getBlock() == this &&
                state.getValue(ENTRANCE) && state.getValue(FACING) == stateNeighbor.getValue(FACING)) {
            world.setBlock(pos, state.setValue(CLOSED, stateNeighbor.getValue(CLOSED)), 3);
        }
    }

    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    public void dyedDustParticles(WarpPipeBlockEntity pipeBlockEntity, Level world, BlockPos pos, Direction direction) {
        RandomSource random = world.getRandom();
        int textColor = pipeBlockEntity.getPipeText().getColor().getTextColor();
        float red = (float)(textColor >> 16 & 255) / 255.0F;
        float green = (float)(textColor >> 8 & 255) / 255.0F;
        float blue = (float)(textColor & 255) / 255.0F;
        Vector3f colorVec = new Vector3f(red, green, blue);

        ParticleUtils.spawnParticlesOnBlockFace(world, pos, new DustParticleOptions(colorVec, 0.5F),
                UniformInt.of(8, 12), direction,
                () -> new Vec3(Mth.nextDouble(random, -0.005F, 0.005F),
                        Mth.nextDouble(random, -0.005F, 0.005F),
                        Mth.nextDouble(random, -0.005F, 0.005F)),  0.55);
    }

    public void coloredDustParticles(Level world, BlockPos pos, Vector3f colorVec, UniformInt amount) {
        ParticleUtils.spawnParticlesOnBlockFaces(world, pos, new DustParticleOptions(colorVec, 0.5F), amount);
    }

    public void sudParticles(Level world, BlockPos pos, Direction direction) {
        RandomSource random = world.getRandom();

        ParticleUtils.spawnParticlesOnBlockFace(world, pos, (ParticleOptions) CompatRegistry.SUDS_PARTICLE.get(),
                UniformInt.of(5, 8), direction,
                () -> new Vec3(Mth.nextDouble(random, -0.005F, 0.005F),
                        Mth.nextDouble(random, -0.005F, 0.005F),
                        Mth.nextDouble(random, -0.005F, 0.005F)),  0.55);
    }

    public static void spawnParticlesOnBlockFaces(Level world, BlockPos pos, ParticleOptions particles, Vec3 speedRange, IntProvider amountRange) {
        for(Direction direction : Direction.values()) {
            ParticleUtils.spawnParticlesOnBlockFace(world, pos, particles, amountRange, direction, () -> speedRange, 0.55D);
        }
    }
}