package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public class ContainersMixin {
    @Inject(method = "dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V", at = @At("HEAD"))
    private static void dropContents(Level world, double x, double y, double z, Container container, CallbackInfo ci) {
        int mv$stackCount;
        if (container instanceof DecoratedPotBlockEntity decoratedPotBE && !ConfigRegistry.DISABLE_DECORATED_POT_TWEAKS.get()) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                BlockPos pos = decoratedPotBE.getBlockPos();
                ItemStack stack = decoratedPotBE.getTheItem();
                mv$stackCount = stack.getCount();

                MarioverseSoundTypes.playSounds(world, pos, container.getItem(i), decoratedPotBE);

                if (stack.getItem() == CompatRegistry.MINECART_CONTRAPTION.get()
                        || stack.getItem() == CompatRegistry.CHEST_MINECART_CONTRAPTION.get()
                        || stack.getItem() == CompatRegistry.FURNACE_MINECART_CONTRAPTION.get())
                    pos = pos.below();

                for (int j = 0; j < mv$stackCount; j++) {
                    QuestionBlock.spawnFromContainer(world, pos, pos, container.getItem(i),
                            null, ConfigRegistry.DECORATED_POT_SPAWNS_MOBS.get(), ConfigRegistry.DECORATED_POT_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.DECORATED_POT_BUCKET_TWEAKS.get(), TagRegistry.DECORATED_POT_CANNOT_SPAWN);
                }
                decoratedPotBE.removeTheItem();
            }
        } else if (container instanceof QuestionBlockEntity questionBE) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                Entity breakingEntity = questionBE.getBreakingEntity();
                BlockPos pos = questionBE.getBlockPos();
                ItemStack stack = questionBE.getTheItem();
                mv$stackCount = stack.getCount();

                MarioverseSoundTypes.playSounds(world, questionBE.getBlockPos(), container.getItem(i), questionBE);

                if (stack.getItem() == CompatRegistry.MINECART_CONTRAPTION.get()
                        || stack.getItem() == CompatRegistry.CHEST_MINECART_CONTRAPTION.get()
                        || stack.getItem() == CompatRegistry.FURNACE_MINECART_CONTRAPTION.get())
                    pos = pos.below();

                for (int j = 0; j < mv$stackCount; j++) {
                    QuestionBlock.spawnFromContainer(world, pos, pos, container.getItem(i),
                            breakingEntity, ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.QUESTION_BUCKET_TWEAKS.get(), TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);
                }

                for (int j = 0; j < mv$stackCount; j++)
                    questionBE.removeTheItem();
            }
        }
    }
}