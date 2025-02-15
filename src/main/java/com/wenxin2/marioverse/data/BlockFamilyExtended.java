package com.wenxin2.marioverse.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;

public class BlockFamilyExtended {
    private final Block baseBlock;
    final Map<BlockFamilyExtended.Variant, Block> variants = Maps.newHashMap();
    boolean generateModel = true;
    boolean generateRecipe = true;
    @Nullable
    String recipeGroupPrefix;
    @Nullable
    String recipeUnlockedBy;

    BlockFamilyExtended(Block block) {
        this.baseBlock = block;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public Map<BlockFamilyExtended.Variant, Block> getVariants() {
        return this.variants;
    }

    public Block get(BlockFamilyExtended.Variant variant) {
        return this.variants.get(variant);
    }

    public boolean shouldGenerateModel() {
        return this.generateModel;
    }

    public boolean shouldGenerateRecipe() {
        return this.generateRecipe;
    }

    public Optional<String> getRecipeGroupPrefix() {
        return StringUtil.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
    }

    public Optional<String> getRecipeUnlockedBy() {
        return StringUtil.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
    }

    public static class Builder {
        private final BlockFamilyExtended family;

        public Builder(Block block) {
            this.family = new BlockFamilyExtended(block);
        }

        public BlockFamilyExtended getFamily() {
            return this.family;
        }

        public BlockFamilyExtended.Builder button(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.BUTTON, block);
            return this;
        }

        public BlockFamilyExtended.Builder chiseled(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.CHISELED, block);
            return this;
        }

        public BlockFamilyExtended.Builder cracked(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.CRACKED, block);
            return this;
        }

        public BlockFamilyExtended.Builder customFence(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.CUSTOM_FENCE, block);
            return this;
        }

        public BlockFamilyExtended.Builder customFenceGate(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.CUSTOM_FENCE_GATE, block);
            return this;
        }

        public BlockFamilyExtended.Builder cut(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.CUT, block);
            return this;
        }

        public BlockFamilyExtended.Builder door(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.DOOR, block);
            return this;
        }

        public BlockFamilyExtended.Builder fence(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.FENCE, block);
            return this;
        }

        public BlockFamilyExtended.Builder fenceGate(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.FENCE_GATE, block);
            return this;
        }

        public BlockFamilyExtended.Builder mosaic(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.MOSAIC, block);
            return this;
        }

        public BlockFamilyExtended.Builder questionBlock(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.QUESTION_BLOCK, block);
            return this;
        }

        public BlockFamilyExtended.Builder pedestal(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.PEDESTAL, block);
            return this;
        }

        public BlockFamilyExtended.Builder polished(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.POLISHED, block);
            return this;
        }

        public BlockFamilyExtended.Builder pressurePlate(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.PRESSURE_PLATE, block);
            return this;
        }

        public BlockFamilyExtended.Builder sign(Block block, Block blockWall) {
            this.family.variants.put(BlockFamilyExtended.Variant.SIGN, block);
            this.family.variants.put(BlockFamilyExtended.Variant.WALL_SIGN, blockWall);
            return this;
        }

        public BlockFamilyExtended.Builder slab(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.SLAB, block);
            return this;
        }

        public BlockFamilyExtended.Builder stairs(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.STAIRS, block);
            return this;
        }

        public BlockFamilyExtended.Builder trapdoor(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.TRAPDOOR, block);
            return this;
        }

        public BlockFamilyExtended.Builder wall(Block block) {
            this.family.variants.put(BlockFamilyExtended.Variant.WALL, block);
            return this;
        }

        public BlockFamilyExtended.Builder dontGenerateModel() {
            this.family.generateModel = false;
            return this;
        }

        public BlockFamilyExtended.Builder dontGenerateRecipe() {
            this.family.generateRecipe = false;
            return this;
        }

        public BlockFamilyExtended.Builder recipeGroupPrefix(String groupName) {
            this.family.recipeGroupPrefix = groupName;
            return this;
        }

        public BlockFamilyExtended.Builder recipeUnlockedBy(String recipeUnlockedBy) {
            this.family.recipeUnlockedBy = recipeUnlockedBy;
            return this;
        }
    }

    public static enum Variant {
        BUTTON("button"),
        CHISELED("chiseled"),
        CRACKED("cracked"),
        CUT("cut"),
        DOOR("door"),
        CUSTOM_FENCE("fence"),
        FENCE("fence"),
        CUSTOM_FENCE_GATE("fence_gate"),
        FENCE_GATE("fence_gate"),
        MOSAIC("mosaic"),
        SIGN("sign"),
        SLAB("slab"),
        STAIRS("stairs"),
        PEDESTAL("pedestal"),
        PRESSURE_PLATE("pressure_plate"),
        POLISHED("polished"),
        QUESTION_BLOCK("question_block"),
        TRAPDOOR("trapdoor"),
        WALL("wall"),
        WALL_SIGN("wall_sign");

        private final String recipeGroup;

        private Variant(String groupName) {
            this.recipeGroup = groupName;
        }

        public String getRecipeGroup() {
            return this.recipeGroup;
        }
    }
}
