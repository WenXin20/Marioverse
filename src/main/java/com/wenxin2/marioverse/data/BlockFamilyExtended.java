package com.wenxin2.marioverse.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;

public class BlockFamilyExtended {
    private final Block baseBlock;
    final Map<Variant, Block> variants = Maps.newHashMap();
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

    public Map<Variant, Block> getVariants() {
        return this.variants;
    }

    public Block get(Variant variant) {
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
            this.family.variants.put(Variant.BUTTON, block);
            return this;
        }

        public BlockFamilyExtended.Builder bricks(Block block) {
            this.family.variants.put(Variant.BRICKS, block);
            return this;
        }

        public BlockFamilyExtended.Builder bridge(Block block) {
            this.family.variants.put(Variant.BRIDGE, block);
            return this;
        }

        public BlockFamilyExtended.Builder bridgeStairs(Block block) {
            this.family.variants.put(Variant.BRIDGE_STAIRS, block);
            return this;
        }

        public BlockFamilyExtended.Builder chiseled(Block block) {
            this.family.variants.put(Variant.CHISELED, block);
            return this;
        }

        public BlockFamilyExtended.Builder cobble(Block block) {
            this.family.variants.put(Variant.COBBLE, block);
            return this;
        }

        public BlockFamilyExtended.Builder cracked(Block block) {
            this.family.variants.put(Variant.CRACKED, block);
            return this;
        }

        public BlockFamilyExtended.Builder customFence(Block block) {
            this.family.variants.put(Variant.CUSTOM_FENCE, block);
            return this;
        }

        public BlockFamilyExtended.Builder customFenceGate(Block block) {
            this.family.variants.put(Variant.CUSTOM_FENCE_GATE, block);
            return this;
        }

        public BlockFamilyExtended.Builder cut(Block block) {
            this.family.variants.put(Variant.CUT, block);
            return this;
        }

        public BlockFamilyExtended.Builder door(Block block) {
            this.family.variants.put(Variant.DOOR, block);
            return this;
        }

        public BlockFamilyExtended.Builder fence(Block block) {
            this.family.variants.put(Variant.FENCE, block);
            return this;
        }

        public BlockFamilyExtended.Builder fenceGate(Block block) {
            this.family.variants.put(Variant.FENCE_GATE, block);
            return this;
        }

        public BlockFamilyExtended.Builder hardBlock(Block block) {
            this.family.variants.put(Variant.HARD_BLOCK, block);
            return this;
        }

        public BlockFamilyExtended.Builder invisibleQuestionBlock(Block block) {
            this.family.variants.put(Variant.INVISIBLE_QUESTION_BLOCK, block);
            return this;
        }

        public BlockFamilyExtended.Builder mosaic(Block block) {
            this.family.variants.put(Variant.MOSAIC, block);
            return this;
        }

        public BlockFamilyExtended.Builder pedestal(Block block) {
            this.family.variants.put(Variant.PEDESTAL, block);
            return this;
        }

        public BlockFamilyExtended.Builder picketFence(Block block) {
            this.family.variants.put(Variant.PICKET_FENCE, block);
            return this;
        }

        public BlockFamilyExtended.Builder polished(Block block) {
            this.family.variants.put(Variant.POLISHED, block);
            return this;
        }

        public BlockFamilyExtended.Builder pressurePlate(Block block) {
            this.family.variants.put(Variant.PRESSURE_PLATE, block);
            return this;
        }

        public BlockFamilyExtended.Builder questionBlock(Block block) {
            this.family.variants.put(Variant.QUESTION_BLOCK, block);
            return this;
        }

        public BlockFamilyExtended.Builder questionBlockTag(Block block) {
            this.family.variants.put(Variant.QUESTION_BLOCK_TAG, block);
            return this;
        }

        public BlockFamilyExtended.Builder questionPanel(Block block) {
            this.family.variants.put(Variant.QUESTION_PANEL, block);
            return this;
        }

        public BlockFamilyExtended.Builder quicksand(Block block) {
            this.family.variants.put(Variant.QUICKSAND, block);
            return this;
        }

        public BlockFamilyExtended.Builder rocky(Block block) {
            this.family.variants.put(Variant.ROCKY, block);
            return this;
        }

        public BlockFamilyExtended.Builder sign(Block block, Block blockWall) {
            this.family.variants.put(Variant.SIGN, block);
            this.family.variants.put(Variant.WALL_SIGN, blockWall);
            return this;
        }

        public BlockFamilyExtended.Builder slab(Block block) {
            this.family.variants.put(Variant.SLAB, block);
            return this;
        }

        public BlockFamilyExtended.Builder smashableBlock(Block block) {
            this.family.variants.put(Variant.SMASHABLE_BLOCKS, block);
            return this;
        }

        public BlockFamilyExtended.Builder stairs(Block block) {
            this.family.variants.put(Variant.STAIRS, block);
            return this;
        }

        public BlockFamilyExtended.Builder storageBricks(Block block) {
            this.family.variants.put(Variant.STORAGE_BRICKS, block);
            return this;
        }

        public BlockFamilyExtended.Builder trapdoor(Block block) {
            this.family.variants.put(Variant.TRAPDOOR, block);
            return this;
        }

        public BlockFamilyExtended.Builder wall(Block block) {
            this.family.variants.put(Variant.WALL, block);
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
        BRICKS("bricks"),
        BRIDGE("bridge"),
        BRIDGE_STAIRS("bridge_stairs"),
        CHISELED("chiseled"),
        COBBLE("cobble"),
        CRACKED("cracked"),
        CUSTOM_FENCE("fence"),
        CUSTOM_FENCE_GATE("fence_gate"),
        CUT("cut"),
        DOOR("door"),
        FENCE("fence"),
        FENCE_GATE("fence_gate"),
        HARD_BLOCK("hard_block"),
        INVISIBLE_QUESTION_BLOCK("invisible_question_block"),
        MOSAIC("mosaic"),
        PEDESTAL("pedestal"),
        PICKET_FENCE("picket_fence"),
        POLISHED("polished"),
        PRESSURE_PLATE("pressure_plate"),
        QUESTION_BLOCK("question_block"),
        QUESTION_BLOCK_TAG("question_block_tag"),
        QUESTION_PANEL("question_panel"),
        QUICKSAND("quicksand"),
        ROCKY("rocky"),
        SIGN("sign"),
        SLAB("slab"),
        SMASHABLE_BLOCKS("smashable_blocks"),
        STAIRS("stairs"),
        STORAGE_BRICKS("storage_bricks"),
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
