package com.wenxin2.marioverse.blocks;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class LogPlatformBlock extends PlatformBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public Block logBlock;

    public LogPlatformBlock(Block logBlock, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, false));
        this.logBlock = logBlock;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.AXE_STRIP)) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            String path = id.getPath();
            String logName = BuiltInRegistries.BLOCK.getKey(logBlock).getPath();

            if (!path.contains("stripped_")
                    && (path.contains("bamboo_platform") || path.contains("log_platform") || path.contains("stem_platform"))) {
                String removeBlockName = logName.replace("_block", "");
                String strippedPath = path.replace(removeBlockName, "stripped_" + removeBlockName);
                ResourceLocation strippedId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), strippedPath);

                Block strippedBlock = BuiltInRegistries.BLOCK.get(strippedId);

                if (strippedBlock != Blocks.AIR && strippedId.getPath().contains("stripped_")) {
                    return strippedBlock.defaultBlockState()
                            .setValue(AXIS, state.getValue(AXIS))
                            .setValue(TYPE, state.getValue(TYPE))
                            .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                }
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
