package com.wenxin2.marioverse.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PowerUpCommand {

    private static final SuggestionProvider<CommandSourceStack> POWERUP_SUGGESTIONS = (context, builder) ->
            suggestPowerUps(builder);

    private static CompletableFuture<Suggestions> suggestPowerUps(SuggestionsBuilder builder) {
        return net.minecraft.commands.SharedSuggestionProvider.suggest(
                List.of("fire_flower", "ice_flower", "mushroom", "super_star"), builder);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("powerup")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.literal("fire_flower")
                                .executes(ctx -> hasPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), "fire_flower"))
                                .then(Commands.argument("enablePowerUp", BoolArgumentType.bool())
                                        .executes(ctx -> applyPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"),
                                                "fire_flower", BoolArgumentType.getBool(ctx, "enablePowerUp")))
                                )
                        )
                        .then(Commands.literal("ice_flower")
                                .executes(ctx -> hasPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), "ice_flower"))
                                .then(Commands.argument("enablePowerUp", BoolArgumentType.bool())
                                        .executes(ctx -> applyPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"),
                                                "ice_flower", BoolArgumentType.getBool(ctx, "enablePowerUp")))
                                )
                        )
                        .then(Commands.literal("mushroom")
                                .executes(ctx -> hasPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), "mushroom"))
                                .then(Commands.argument("enablePowerUp", BoolArgumentType.bool())
                                        .executes(ctx -> applyPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"),
                                                "mushroom", BoolArgumentType.getBool(ctx, "enablePowerUp")))
                                )
                        )
                        .then(Commands.literal("super_star")
                                .executes(ctx -> hasPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), "super_star"))
                                .then(Commands.argument("enablePowerUp", BoolArgumentType.bool())
                                        .then(Commands.argument("cooldown", IntegerArgumentType.integer(0))
                                                .executes(ctx -> applySuperStar(
                                                        ctx.getSource(),
                                                        EntityArgument.getEntities(ctx, "targets"),
                                                        BoolArgumentType.getBool(ctx, "enablePowerUp"),
                                                        IntegerArgumentType.getInteger(ctx, "cooldown")
                                                ))
                                        )
                                        .executes(ctx -> applySuperStar(
                                                ctx.getSource(),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                BoolArgumentType.getBool(ctx, "enablePowerUp"),
                                                -1
                                        ))
                                )
                        )
                )
        );
    }

    private static void applyPowerUpType(AbilitiesHandler handler, String powerUpName, boolean enablePowerUp) {
        switch (powerUpName) {
            case "fire_flower" -> handler.mv$setFireFlower(enablePowerUp);
            case "ice_flower" -> handler.mv$setIceFlower(enablePowerUp);
            case "mushroom" -> handler.mv$setMushroom(enablePowerUp);
        }
    }

    private static int applyPowerUp(CommandSourceStack source, Collection<? extends Entity> targets, String powerUpName, boolean enablePowerUp) {
        int count = 0;
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && entity instanceof AbilitiesHandler handler) {
                entity.level().playSound(null, entity.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                applyPowerUpType(handler, powerUpName, enablePowerUp);
                count++;
                if (count == 1) {
                    source.sendSuccess(() ->
                            Component.literal(powerUpName + " set to " + enablePowerUp + " for " + entity.getDisplayName() + "."), true);
                }
            }
        }

        int finalCount = count;

        if (finalCount > 1) {
            source.sendSuccess(() ->
                    Component.literal(powerUpName + " set to " + enablePowerUp + " for " + finalCount + " entities."), true);
        }

        return count;
    }

    private static int applySuperStar(CommandSourceStack source, Collection<? extends Entity> targets, boolean enablePowerUp, int cooldown) {
        int count = 0;
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler) {
                entity.level().playSound(null, entity.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                handler.mv$setSuperStar(enablePowerUp);
                count++;

                if (cooldown >= 0) {
                    handler.mv$setSuperStarCooldown(cooldown);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, cooldown, 4, true, false));
                } else {
                    handler.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));
                }

                if (count == 1)
                    source.sendSuccess(() ->
                            Component.literal("Super Star set to" + enablePowerUp + " for " + entity.getDisplayName() + " for " + cooldown + " ticks."), true);
            }
        }

        int finalCount = count;

        if (finalCount > 1)
            source.sendSuccess(() ->
                    Component.literal("Super Star set to" + enablePowerUp + " for " + finalCount + " entities for " + cooldown + " ticks."), true);

        return count;
    }

    private static int hasPowerUp(CommandSourceStack source, Collection<? extends Entity> targets, String powerup) {
        int count = 0;
        int falseCount = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && entity instanceof AbilitiesHandler handler) {
                boolean hasPowerUp = switch (powerup) {
                    case "fire_flower" -> handler.mv$hasFireFlower();
                    case "ice_flower" -> handler.mv$hasIceFlower();
                    case "mushroom" -> handler.mv$hasMushroom();
                    case "super_star" -> handler.mv$hasSuperStar();
                    default -> false;
                };

                if (hasPowerUp) count++;
                else falseCount++;
            }
        }

        int finalTrueCount = count;
        int finalFalseCount = falseCount;
        source.sendSuccess(() -> Component.literal(
                        powerup + " = true for " + finalTrueCount + " entities, false for " + finalFalseCount + " entities."),
                false
        );

        return count + falseCount;
    }
}
