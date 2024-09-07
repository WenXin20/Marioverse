package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class WarpPipeBlock extends DirectionalBlock implements EntityBlock {
    public static final MapCodec<WarpPipeBlock> CODEC = simpleCodec(WarpPipeBlock::new);
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final BooleanProperty BUBBLES = BooleanProperty.create("bubbles");
    public static final BooleanProperty WATER_SPOUT = BooleanProperty.create("water_spout");

    public WarpPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATER_SPOUT, Boolean.FALSE)
                .setValue(BUBBLES, Boolean.TRUE).setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE));
    }

    @Override
    public MapCodec<WarpPipeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(BUBBLES, CLOSED, ENTRANCE, FACING, WATER_SPOUT);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state)
    {
        return new WarpPipeBlockEntity(pos, state);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Item item = stack.getItem();

        if (state.getValue(ENTRANCE) && player.getItemInHand(hand).getItem() == ItemRegistry.PIPE_WRENCH.get()) {
            if (blockEntity instanceof WarpPipeBlockEntity) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> new WarpPipeMenu(id,
                        playerInventory, ContainerLevelAccess.create(world, pos), pos), ((WarpPipeBlockEntity) blockEntity).getDisplayName()));
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
            boolean isSuccesful = false;
            boolean isSuccesfulTool = false;

            if (!pipeBlockEntity.isWaxed()) {
                if (item == Items.INK_SAC) {
                    if (pipeBlockEntity.updateText((pipeText) -> pipeText.setHasGlowingText(Boolean.FALSE))) {
                        world.playSound(player, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        coloredDustParticles(world, pos, new Vector3f(0, 0, 0), UniformInt.of(8, 12));
                        pipeBlockEntity.markUpdated();
                        isSuccesful = true;
                    }
                } else if (item == Items.GLOW_INK_SAC) {
                    if (pipeBlockEntity.updateText((pipeText) -> pipeText.setHasGlowingText(Boolean.TRUE))) {
                        world.playSound(player, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        spawnParticlesOnBlockFaces(world, pos, ParticleTypes.GLOW, new Vec3(0, 0, 0), UniformInt.of(3, 5));
                        pipeBlockEntity.markUpdated();
                        isSuccesful = true;
                    }
                } else if (item == Items.HONEYCOMB && (ConfigRegistry.WAX_DISABLES_BUBBLES.get() || ConfigRegistry.WAX_DISABLES_CLOSING.get()
                        || ConfigRegistry.WAX_DISABLES_RENAMING.get() || ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get())) {
                    pipeBlockEntity.setWaxed(Boolean.TRUE);
                    world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                    ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                    pipeBlockEntity.markUpdated();
                    isSuccesful = true;
                } else if (stack.is(Items.BRUSH)) {
                    if (hit.getDirection() == Direction.NORTH) {
                        pipeBlockEntity.setTextNorth(!pipeBlockEntity.hasTextNorth());
                        this.dyedDustParticles(pipeBlockEntity, world, pos, Direction.NORTH);
                    } else if (hit.getDirection() == Direction.SOUTH) {
                        pipeBlockEntity.setTextSouth(!pipeBlockEntity.hasTextSouth());
                        this.dyedDustParticles(pipeBlockEntity, world, pos, Direction.SOUTH);
                    } else if (hit.getDirection() == Direction.EAST) {
                        pipeBlockEntity.setTextEast(!pipeBlockEntity.hasTextEast());
                        this.dyedDustParticles(pipeBlockEntity, world, pos, Direction.EAST);
                    } else if (hit.getDirection() == Direction.WEST) {
                        pipeBlockEntity.setTextWest(!pipeBlockEntity.hasTextWest());
                        this.dyedDustParticles(pipeBlockEntity, world, pos, Direction.WEST);
                    } else if (hit.getDirection() == Direction.UP) {
                        pipeBlockEntity.setTextAbove(!pipeBlockEntity.hasTextAbove());
                        this.dyedDustParticles(pipeBlockEntity, world, pos, Direction.UP);
                    } else if (hit.getDirection() == Direction.DOWN) {
                        pipeBlockEntity.setTextBelow(!pipeBlockEntity.hasTextBelow());
                        this.dyedDustParticles(pipeBlockEntity, world, pos, Direction.DOWN);
                    }
                    world.playSound(player, pos, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pipeBlockEntity.markUpdated();
                    isSuccesfulTool = true;
                } else if (stack.is(CompatRegistry.BUBBLE_BLOWER.get()) || stack.is(CompatRegistry.SOAP.get())) {
                    if (hit.getDirection() == Direction.NORTH && pipeBlockEntity.hasTextNorth()) {
                        pipeBlockEntity.setTextNorth(Boolean.FALSE);
                        world.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.sudParticles(world, pos, Direction.NORTH);
                        pipeBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    } else if (hit.getDirection() == Direction.SOUTH && pipeBlockEntity.hasTextSouth()) {
                        pipeBlockEntity.setTextSouth(Boolean.FALSE);
                        world.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.sudParticles(world, pos, Direction.SOUTH);
                        pipeBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    } else if (hit.getDirection() == Direction.EAST && pipeBlockEntity.hasTextEast()) {
                        pipeBlockEntity.setTextEast(Boolean.FALSE);
                        world.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.sudParticles(world, pos, Direction.EAST);
                        pipeBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    } else if (hit.getDirection() == Direction.WEST && pipeBlockEntity.hasTextWest()) {
                        pipeBlockEntity.setTextWest(Boolean.FALSE);
                        world.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.sudParticles(world, pos, Direction.WEST);
                        pipeBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    } else if (hit.getDirection() == Direction.UP && pipeBlockEntity.hasTextAbove()) {
                        pipeBlockEntity.setTextAbove(Boolean.FALSE);
                        world.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.sudParticles(world, pos, Direction.UP);
                        pipeBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    } else if (hit.getDirection() == Direction.DOWN && pipeBlockEntity.hasTextBelow()) {
                        pipeBlockEntity.setTextBelow(Boolean.FALSE);
                        world.playSound(player, pos, CompatRegistry.BUBBLE_BLOWER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.sudParticles(world, pos, Direction.DOWN);
                        pipeBlockEntity.markUpdated();
                        isSuccesfulTool = true;
                    }
                } else {
                    if (item instanceof DyeItem dyeItem
                            && pipeBlockEntity.updateText((pipeText) -> pipeText.setColor(dyeItem.getDyeColor()))) {
                        int textColor = dyeItem.getDyeColor().getTextColor();
                        float red = (float)(textColor >> 16 & 255) / 255.0F;
                        float green = (float)(textColor >> 8 & 255) / 255.0F;
                        float blue = (float)(textColor & 255) / 255.0F;
                        Vector3f colorVec = new Vector3f(red, green, blue);

                        world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ParticleUtils.spawnParticlesOnBlockFaces(world, pos, new DustParticleOptions(colorVec, 1.0F), UniformInt.of(8, 12));
                        pipeBlockEntity.markUpdated();
                        isSuccesful = true;
                    }
                }
            } else if (ConfigRegistry.ALLOW_PIPE_UNWAXING.get() && (stack.is(ItemTags.AXES) || item instanceof AxeItem)) {
                pipeBlockEntity.setWaxed(Boolean.FALSE);
                world.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                pipeBlockEntity.markUpdated();
                isSuccesfulTool = true;
            }

            if (isSuccesfulTool) {
                if (!player.isCreative()) {
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }

            if (isSuccesful) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                pipeBlockEntity.setCustomName(stack.getHoverName());
                pipeBlockEntity.setChanged();
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Direction direction = placeContext.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction).setValue(CLOSED, placeContext.getLevel().hasNeighborSignal(placeContext.getClickedPos()));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos posNeighbor, boolean b) {
        boolean isClosed = state.getValue(CLOSED);
        boolean isPowered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());

        if (isClosed != isPowered) {
            if (isClosed) {
                world.scheduleTick(pos, this, 4);
                world.setBlock(pos, state.cycle(CLOSED), 2);

            } else {
                world.setBlock(pos, state.cycle(CLOSED), 2);
            }

            if (isClosed) {
                this.playSound(world, pos, SoundRegistry.PIPE_CLOSES.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
            } else this.playSound(world, pos, SoundRegistry.PIPE_OPENS.get(), SoundSource.BLOCKS, 1.0F, 0.15F);
        }

        super.neighborChanged(state, world, pos, block, posNeighbor, b);
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos posNeighbor) {
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();
        Block blockBelow = worldAccessor.getBlockState(pos.below()).getBlock();
        Block blockNorth = worldAccessor.getBlockState(pos.north()).getBlock();
        Block blockSouth = worldAccessor.getBlockState(pos.south()).getBlock();
        Block blockEast = worldAccessor.getBlockState(pos.east()).getBlock();
        Block blockWest = worldAccessor.getBlockState(pos.west()).getBlock();

        boolean facingUp = state.getValue(FACING) == Direction.UP;
        boolean facingDown = state.getValue(FACING) == Direction.DOWN;
        boolean facingNorth = state.getValue(FACING) == Direction.NORTH;
        boolean facingSouth = state.getValue(FACING) == Direction.SOUTH;
        boolean facingEast = state.getValue(FACING) == Direction.EAST;
        boolean facingWest = state.getValue(FACING) == Direction.WEST;

        if (facingUp) {
            if (blockAbove == this) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            else return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingDown) {
            if (blockBelow == this) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            else return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingNorth) {
            if (blockNorth == this) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            else return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingSouth) {
            if (blockSouth == this) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            else return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingEast) {
            if (blockEast == this) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            else return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingWest) {
            if (blockWest == this) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            else return state.setValue(ENTRANCE, Boolean.TRUE);
        }
        return state.setValue(ENTRANCE, Boolean.FALSE);
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WarpPipeBlockEntity pipeBlockEntity = (WarpPipeBlockEntity) serverWorld.getBlockEntity(pos);

        if (!serverWorld.isClientSide && pipeBlockEntity != null && pipeBlockEntity.getUuid() == null) {
            UUID uuid = UUID.randomUUID();
            pipeBlockEntity.setUuid(uuid);
            pipeBlockEntity.setChanged();
        }

        if (state.getValue(WATER_SPOUT) && state.getValue(FACING) == Direction.UP && pipeBlockEntity != null
                && serverWorld.dimension() != Level.NETHER) {
            WaterSpoutBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBlockEntity.spoutHeight);
            serverWorld.scheduleTick(pos, this, 3);
        }

        if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.UP && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.DOWN && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnDown(serverWorld, pos.below(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.NORTH && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnNorth(serverWorld, pos.north(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.SOUTH && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnSouth(serverWorld, pos.south(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.EAST && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnEast(serverWorld, pos.east(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.WEST && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnWest(serverWorld, pos.west(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState neighborState, boolean b) {

        Block blockAbove = world.getBlockState(pos.above()).getBlock();
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        Block blockNorth = world.getBlockState(pos.north()).getBlock();
        Block blockSouth = world.getBlockState(pos.south()).getBlock();
        Block blockEast = world.getBlockState(pos.east()).getBlock();
        Block blockWest = world.getBlockState(pos.west()).getBlock();

        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos destinationPos = null;

        if (!state.getValue(CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE
                && warpPipeBE.destinationPos != null) {
            destinationPos = warpPipeBE.destinationPos;
            world.scheduleTick(pos, this, 3);
        }

        if (!world.isClientSide && blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
            UUID uuid = UUID.randomUUID();
            pipeBlockEntity.setPreventWarp(Boolean.FALSE);
            pipeBlockEntity.setUuid(uuid);
            pipeBlockEntity.setChanged();
        }

        if (state.getValue(FACING) == Direction.UP) {
            if (blockAbove == this) {
                world.setBlock(pos, state.setValue(ENTRANCE, Boolean.FALSE), 3);
            }
            else world.setBlock(pos, state.setValue(ENTRANCE, Boolean.TRUE), 3);

            if (blockAbove == Blocks.WATER) {
                world.scheduleTick(pos, this, 3);
            }
        }

        if (state.getValue(FACING) == Direction.DOWN) {
            if (blockBelow == this) {
                world.setBlock(pos, state.setValue(ENTRANCE, Boolean.FALSE), 3);
            }
            else world.setBlock(pos, state.setValue(ENTRANCE, Boolean.TRUE), 3);

            if (blockBelow == Blocks.WATER) {
                world.scheduleTick(pos, this, 3);
            }
        }

        if (state.getValue(FACING) == Direction.NORTH) {
            if (blockNorth == this) {
                world.setBlock(pos, state.setValue(ENTRANCE, Boolean.FALSE), 3);
            }
            else world.setBlock(pos, state.setValue(ENTRANCE, Boolean.TRUE), 3);

            if (blockNorth == Blocks.WATER) {
                world.scheduleTick(pos, this, 3);
            }
        }

        if (state.getValue(FACING) == Direction.SOUTH) {
            if (blockSouth == this) {
                world.setBlock(pos, state.setValue(ENTRANCE, Boolean.FALSE), 3);
            }
            else world.setBlock(pos, state.setValue(ENTRANCE, Boolean.TRUE), 3);

            if (blockSouth == Blocks.WATER) {
                world.scheduleTick(pos, this, 3);
            }
        }

        if (state.getValue(FACING) == Direction.EAST) {
            if (blockEast == this) {
                world.setBlock(pos, state.setValue(ENTRANCE, Boolean.FALSE), 3);
            }
            else world.setBlock(pos, state.setValue(ENTRANCE, Boolean.TRUE), 3);

            if (blockEast == Blocks.WATER) {
                world.scheduleTick(pos, this, 3);
            }
        }

        if (state.getValue(FACING) == Direction.WEST) {
            if (blockWest == this) {
                world.setBlock(pos, state.setValue(ENTRANCE, Boolean.FALSE), 3);
            }
            else world.setBlock(pos, state.setValue(ENTRANCE, Boolean.TRUE), 3);

            if (blockWest == Blocks.WATER) {
                world.scheduleTick(pos, this, 3);
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

    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    // Store a map to track whether entities have teleported or not
    public static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null) {
            teleportedEntities.put(entity.getId(), true);
        }
    }

    public static void warp(Entity entity, BlockPos warpPos, Level world, BlockState state) {
        if (world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock && !state.getValue(CLOSED)) {
            Entity passengerEntity = entity.getControllingPassenger();

            if (state.getBlock() instanceof ClearWarpPipeBlock && !state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - 1.0, warpPos.getZ() + 0.5);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1, 0));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - 1.0, warpPos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1, 0));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(FACING) == Direction.UP && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() + 1.0, warpPos.getZ() + 0.5);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() + 1.0, warpPos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(FACING) == Direction.DOWN && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - entity.getBbHeight(), warpPos.getZ() + 0.5);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY() - entity.getBbHeight(), warpPos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(FACING) == Direction.NORTH && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() - entity.getBbWidth());
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() - entity.getBbWidth());
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(FACING) == Direction.SOUTH && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + entity.getBbWidth() + 1.0);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + entity.getBbWidth() + 1.0);
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(FACING) == Direction.EAST && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() + entity.getBbWidth() + 1.0, warpPos.getY(), warpPos.getZ() + 0.5);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() + entity.getBbWidth() + 1.0, warpPos.getY(), warpPos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(warpPos).getValue(FACING) == Direction.WEST && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(warpPos.getX() - entity.getBbWidth(), warpPos.getY(), warpPos.getZ() + 0.5);
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                } else {
                    entity.teleportTo(warpPos.getX() - entity.getBbWidth(), warpPos.getY(), warpPos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        if (ConfigRegistry.BLINDNESS_EFFECT.get())
                            ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                        entity.unRide();
                    }
                }
            }
            WarpPipeBlock.markEntityTeleported(entity);
        }
        world.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
        world.playSound(null, warpPos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS, 1.0F, 0.1F);
    }

    public static BlockPos findMatchingUUID(UUID uuid, Level world, BlockPos pos) {
        BlockPos closestPos = null;
        double closestDistanceSq = Double.MAX_VALUE;
        int maxDistance = 64; // How far it searches for warp pipes with a matching UUID

        for (int x = -maxDistance; x <= maxDistance; x++) {
            for (int y = Math.max(-maxDistance, world.getMinBuildHeight() - pos.getY()); y <= Math.min(maxDistance, world.getMaxBuildHeight() - pos.getY()); y++) {
                for (int z = -maxDistance; z <= maxDistance; z++) {
                    BlockPos checkingPos = pos.offset(x, y, z);
                    BlockState blockState = world.getBlockState(checkingPos);
                    Block block = blockState.getBlock();

                    if (block instanceof WarpPipeBlock) {
                        BlockEntity blockEntity = world.getBlockEntity(checkingPos);

                        if (blockEntity instanceof WarpPipeBlockEntity pipeTileEntity) {
                            UUID warpUUID = pipeTileEntity.getWarpUuid();

                            if (uuid.equals(warpUUID)) {
                                double distanceSq = pos.distToCenterSqr(checkingPos.getX(), checkingPos.getY(), checkingPos.getZ());
                                if (distanceSq < closestDistanceSq) {
                                    closestPos = checkingPos.immutable();
                                    closestDistanceSq = distanceSq;
                                }
                            }
                        }
                    }
                }
            }
        }
        return closestPos;
    }

    public void teleportEntityIfConnected(Level world, BlockPos currentPos, Entity entity) {
        // Check if the current pipe is a valid entrance
        BlockState currentState = world.getBlockState(currentPos);
        if (currentState.getBlock() instanceof WarpPipeBlock && currentState.getValue(WarpPipeBlock.ENTRANCE)) {

            // Search for the connected pipe
            BlockPos targetPos = findConnectedPipe(currentPos, world);

            if (targetPos != null) {
                // Teleport the player to the target pipe's position
                teleportEntityToPos(entity, targetPos);
            }
        }
    }

    // Finds the other pipe with Entrance = true
    private BlockPos findConnectedPipe(BlockPos pos, Level world) {
        Set<BlockPos> visited = new HashSet<>();
        return searchForTargetPipe(pos, world, visited);
    }

    // Recursively search for the target pipe (Entrance = true)
    private BlockPos searchForTargetPipe(BlockPos pos, Level world, Set<BlockPos> visited) {
        if (visited.contains(pos)) {
            return null;
        }
        visited.add(pos);

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof WarpPipeBlock)) {
            return null;
        }

        // Check if this pipe is an entrance
        if (state.getValue(WarpPipeBlock.ENTRANCE)) {
            return pos;
        }

        // Continue searching adjacent pipes
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = world.getBlockState(adjacentPos);

            // Only continue through pipes with Entrance = false
            if (adjacentState.getBlock() instanceof WarpPipeBlock && !adjacentState.getValue(WarpPipeBlock.ENTRANCE)) {
                BlockPos result = searchForTargetPipe(adjacentPos, world, visited);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    // Teleport the player to the given BlockPos
    private void teleportEntityToPos(Entity entity, BlockPos targetPos) {
        double x = targetPos.getX() + 0.5; // Center of the block
        double y = targetPos.getY() + 1;   // Slightly above the block
        double z = targetPos.getZ() + 0.5; // Center of the block

        // Set the player's position and teleport
        entity.teleportTo(x, y, z);
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
