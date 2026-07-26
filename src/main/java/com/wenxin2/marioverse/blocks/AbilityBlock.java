package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.event_handlers.TickEventHandlers;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AbilityBlock extends Block {

    public AbilityBlock(Properties properties) {
        super(properties);
    }

    public double getNormalJumpBoost() {
        return 0.0;
    }

    public double getRunningJumpBoost() {
        return 0.0;
    }

    public double getSafeFallDistance() {
        return 0.0;
    }

    public double getVerticalMotionMultiplier() {
        return 1.0;
    }

    public double getHeightScale() {
        return 1.0;
    }

    public double getWidthScale() {
        return 1.0;
    }

    public boolean hasDoubleJump() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag options) {
        super.appendHoverText(stack, tooltipContext, list, options);
        list.add(Component.translatable(this.getDescriptionId() + ".tooltip.character"));

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable("block.marioverse.ability_block.tooltip.instructions"));
            list.add(Component.translatable("block.marioverse.ability_block.tooltip.instructions.jump"));
            list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability"));

            if (this.getNormalJumpBoost() != 0.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.jump_height",
                        this.getNormalJumpBoost() * 10).withStyle(ChatFormatting.GRAY));

            if (this.getRunningJumpBoost() != 0.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.running_jump_height",
                        this.getRunningJumpBoost() * 10).withStyle(ChatFormatting.GRAY));

            if (this.getSafeFallDistance() != 0.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.safe_fall_distance",
                        this.getSafeFallDistance()).withStyle(ChatFormatting.GRAY));

            if (this.getVerticalMotionMultiplier() != 1.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.gravity",
                        this.getVerticalMotionMultiplier() * 100, "%").withStyle(ChatFormatting.GRAY));

            if (this.getHeightScale() != 1.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.height_scale",
                        this.getHeightScale()).withStyle(ChatFormatting.GRAY));

            if (this.getWidthScale() != 1.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.width_scale",
                        this.getWidthScale()).withStyle(ChatFormatting.GRAY));

            if (this.hasDoubleJump())
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.double_jump")
                        .withStyle(ChatFormatting.GRAY));

            if (this.getNormalJumpBoost() == 0.0 && this.getRunningJumpBoost() == 0.0
                    && this.getSafeFallDistance() == 0.0 && this.getVerticalMotionMultiplier() == 1.0)
                list.add(Component.translatable("block.marioverse.ability_block.tooltip.ability.resets"));

            list.add(Component.literal(""));
        } else list.add(Component.translatable("block.marioverse.ability_block.tooltip"));
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        if (projectile.getOwner() instanceof LivingEntity livingEntity
                && projectile.getType().is(TagRegistry.CAN_HIT_ABILITY_BLOCKS)
                && projectile.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0
                && !(projectile instanceof WindCharge))
            AbilityBlock.hitAbilityBlock(level, hitResult.getBlockPos(), state, livingEntity);

        projectile.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 20);
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> consumer) {
        LivingEntity entity = explosion.getIndirectSourceEntity();

        if (explosion.canTriggerBlocks() && entity != null) {
            if (entity.getType().is(TagRegistry.CAN_HIT_ABILITY_BLOCKS)
                    && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0)
                AbilityBlock.hitAbilityBlock(level, pos, state, entity);
            else AbilityBlock.hitAbilityBlock(level, pos, state, entity);
        }

        super.onExplosionHit(state, level, pos, explosion, consumer);
    }

    public static void hitAbilityBlock(Level level, BlockPos pos, BlockState state, LivingEntity entity) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        boolean isDaisyBlock = state.is(BlockRegistry.DAISY_ABILITY_BLOCK.get());
        boolean isLuigiBlock = state.is(BlockRegistry.LUIGI_ABILITY_BLOCK.get());
        boolean isMarioBlock = state.is(BlockRegistry.MARIO_ABILITY_BLOCK.get());
        boolean isPeachBlock = state.is(BlockRegistry.PEACH_ABILITY_BLOCK.get());
        boolean isRosalinaBlock = state.is(BlockRegistry.ROSALINA_ABILITY_BLOCK.get());
        boolean isSteveBlock = state.is(BlockRegistry.STEVE_ABILITY_BLOCK.get());
        boolean isWaluigiBlock = state.is(BlockRegistry.WALUIGI_ABILITY_BLOCK.get());
        boolean isWarioBlock = state.is(BlockRegistry.WARIO_ABILITY_BLOCK.get());

        entity.setData(DataAttachmentRegistry.HAS_DAISY_ABILITY.get(), isDaisyBlock && !isSteveBlock);
        entity.setData(DataAttachmentRegistry.HAS_LUIGI_ABILITY.get(), isLuigiBlock && !isSteveBlock);
        entity.setData(DataAttachmentRegistry.HAS_MARIO_ABILITY.get(), isMarioBlock && !isSteveBlock);
        entity.setData(DataAttachmentRegistry.HAS_PEACH_ABILITY.get(), isPeachBlock && !isSteveBlock);
        entity.setData(DataAttachmentRegistry.HAS_ROSALINA_ABILITY.get(), isRosalinaBlock && !isSteveBlock);
        entity.setData(DataAttachmentRegistry.HAS_WALUIGI_ABILITY.get(), isWaluigiBlock && !isSteveBlock);
        entity.setData(DataAttachmentRegistry.HAS_WARIO_ABILITY.get(), isWarioBlock && !isSteveBlock);

        if (state.getBlock() instanceof AbilityBlock block && block.hasDoubleJump())
            entity.setData(DataAttachmentRegistry.HAS_DOUBLE_JUMP.get(), true);

        level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, pitch);

        if (isSteveBlock) {
            if (entity.hasData(DataAttachmentRegistry.HAS_DAISY_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_DAISY_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_LUIGI_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_LUIGI_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_MARIO_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_MARIO_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_PEACH_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_PEACH_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_ROSALINA_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_ROSALINA_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_WALUIGI_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_WALUIGI_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_WARIO_ABILITY.get()))
                entity.removeData(DataAttachmentRegistry.HAS_WARIO_ABILITY.get());
            if (entity.hasData(DataAttachmentRegistry.HAS_DOUBLE_JUMP.get()))
                entity.removeData(DataAttachmentRegistry.HAS_DOUBLE_JUMP.get());
        }
    }

    public static void hitAbilityBlockFromSide(Level level, BlockPos pos, BlockState state, LivingEntity entity) {
        if (state.getBlock() instanceof AbilityBlock)
            AbilityBlock.hitAbilityBlock(level, pos, state, entity);
    }

    public static void characterAbility(LivingEntity entity) {
        Block block;

        if (entity.getData(DataAttachmentRegistry.HAS_MARIO_ABILITY))
            block = BlockRegistry.MARIO_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_LUIGI_ABILITY))
            block = BlockRegistry.LUIGI_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_DAISY_ABILITY))
            block = BlockRegistry.DAISY_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_PEACH_ABILITY))
            block = BlockRegistry.PEACH_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_ROSALINA_ABILITY))
            block = BlockRegistry.ROSALINA_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_WALUIGI_ABILITY))
            block = BlockRegistry.WALUIGI_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_WARIO_ABILITY))
            block = BlockRegistry.WARIO_ABILITY_BLOCK.get();
        else block = BlockRegistry.STEVE_ABILITY_BLOCK.get();

        AttributeInstance jumpAttribute = entity.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance safeFallAttribute = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);

        AbilityBlock.characterAbilityScale(entity);

        if (block instanceof AbilityBlock abilityBlock) {
            AbilityBlock.applyJumpBoost(jumpAttribute, AttributesRegistry.CHARACTER_JUMP_BOOST, AttributesRegistry.CHARACTER_RUNNING_JUMP_BOOST,
                    true, entity.isShiftKeyDown(), entity.isSprinting(),
                    abilityBlock.getNormalJumpBoost(), abilityBlock.getRunningJumpBoost());

            if (safeFallAttribute != null)
                AbilityBlock.setModifier(safeFallAttribute, AttributesRegistry.CHARACTER_SAFE_FALL_DISTANCE,
                        abilityBlock.getSafeFallDistance());

            double verticalMultiplier = abilityBlock.getVerticalMotionMultiplier();
            Vec3 motion = entity.getDeltaMovement();

            if (motion.y < 0 && verticalMultiplier != 1.0 && !entity.isShiftKeyDown())
                entity.setDeltaMovement(motion.x, motion.y * verticalMultiplier, motion.z);

            entity.setData(DataAttachmentRegistry.HAS_DOUBLE_JUMP.get(), abilityBlock.hasDoubleJump());
        }
    }

    public static void characterAbilityScale(LivingEntity entity) {
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        float scalingSpeed = 0.1F;

        Block block;

        if (entity.getData(DataAttachmentRegistry.HAS_MARIO_ABILITY))
            block = BlockRegistry.MARIO_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_LUIGI_ABILITY))
            block = BlockRegistry.LUIGI_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_PEACH_ABILITY))
            block = BlockRegistry.PEACH_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_WARIO_ABILITY))
            block = BlockRegistry.WARIO_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_WALUIGI_ABILITY))
            block = BlockRegistry.WALUIGI_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_DAISY_ABILITY))
            block = BlockRegistry.DAISY_ABILITY_BLOCK.get();
        else if (entity.getData(DataAttachmentRegistry.HAS_ROSALINA_ABILITY))
            block = BlockRegistry.ROSALINA_ABILITY_BLOCK.get();
        else block = BlockRegistry.STEVE_ABILITY_BLOCK.get();

        if (block instanceof AbilityBlock abilityBlock) {
            double targetHeightScale = abilityBlock.getHeightScale();
            double targetWidthScale = abilityBlock.getWidthScale();
            double targetEyeHeightScale = targetHeightScale;

            boolean isNeutral = targetHeightScale == 1.0D && targetWidthScale == 1.0D;

            TickEventHandlers.updateScale(entity, AttributesRegistry.CHARACTER_SCALE, !isNeutral, targetHeightScale, targetWidthScale, eyeHeightScale,
                    targetEyeHeightScale, scalingSpeed, heightScale, widthScale, isNeutral);
        }
    }

    public static void applyJumpBoost(AttributeInstance jumpAttribute, ResourceLocation normalId, ResourceLocation runningId,
                                      boolean isActive, boolean isCrouching, boolean isRunning,
                                      double normalBoost, double runningBoost) {
        if (jumpAttribute == null)
            return;

        if (isActive && !isCrouching) {
            if (isRunning) {
                setModifier(jumpAttribute, runningId, runningBoost);
                setModifier(jumpAttribute, normalId, 0);
            } else {
                setModifier(jumpAttribute, normalId, normalBoost);
                setModifier(jumpAttribute, runningId, 0);
            }
        } else {
            setModifier(jumpAttribute, normalId, 0);
            setModifier(jumpAttribute, runningId, 0);
        }
    }

    public static void setModifier(AttributeInstance attribute, ResourceLocation id, double amount) {
        AttributeModifier modifier = attribute.getModifier(id);

        if (amount == 0.0) {
            if (modifier != null)
                attribute.removeModifier(id);
            return;
        }

        if (modifier != null) {
            if (modifier.amount() == amount)
                return;
            attribute.removeModifier(id);
        }
        attribute.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
    }

    public static void setAirborneDuration(LivingEntity entity) {
        if (entity.onGround())
            entity.setData(DataAttachmentRegistry.AIRBORNE_DURATION.get(), 0);
        else {
            int duration = entity.getData(DataAttachmentRegistry.AIRBORNE_DURATION);
            entity.setData(DataAttachmentRegistry.AIRBORNE_DURATION.get(), duration + 1);
        }
    }
}