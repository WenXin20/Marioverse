package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.AbilityBlock;
import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MegaMushroomItem extends PowerUpItem implements PowerUpSource {

    public MegaMushroomItem(Properties properties) {
        super(properties);
    }

    public MegaMushroomItem(int tooltipLineAmt, Properties properties) {
        super(tooltipLineAmt, properties);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.MEGA_MUSHROOM;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof AbilitiesHandler handler) {
            handler.applyMegaMushroomPowerUp(level, player, null);
            stack.consume(1, player);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static void megaMushroomAbility(LivingEntity entity) {
        boolean isActive = entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM);
        AttributeInstance jumpAttribute = entity.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance safeFallAttribute = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        AttributeInstance blockReachAttribute = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityReachAttribute = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        double reachDistance = isActive ? ConfigRegistry.MEGA_MUSHROOM_REACH_DISTANCE.get() : 0;

        AbilityBlock.applyJumpBoost(entity, jumpAttribute, AttributesRegistry.MEGA_JUMP_BOOST,
                AttributesRegistry.MEGA_RUNNING_JUMP_BOOST, isActive, 0.3, 0.4);

        if (safeFallAttribute != null)
            AbilityBlock.setModifier(safeFallAttribute, AttributesRegistry.MEGA_SAFE_FALL_DISTANCE, isActive ? 4 : 0);

        if (blockReachAttribute != null)
            AbilityBlock.setModifier(blockReachAttribute, AttributesRegistry.MEGA_BLOCK_REACH_DISTANCE, reachDistance);

        if (entityReachAttribute != null)
            AbilityBlock.setModifier(entityReachAttribute, AttributesRegistry.MEGA_ENTITY_REACH_DISTANCE, reachDistance);
    }
}
