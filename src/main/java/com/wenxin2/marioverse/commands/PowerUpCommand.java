package com.wenxin2.marioverse.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.wenxin2.marioverse.items.MushroomItem;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;

public class PowerUpCommand {

    private static final SuggestionProvider<CommandSourceStack> POWERUP_SUGGESTIONS = (context, builder) ->
            suggestPowerUps(builder);

    private static CompletableFuture<Suggestions> suggestPowerUps(SuggestionsBuilder builder) {
        return net.minecraft.commands.SharedSuggestionProvider.suggest(
                List.of("fire_flower", "ice_flower", "super_mushroom", "super_star"), builder);
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
                        .then(Commands.literal("super_mushroom")
                                .executes(ctx -> hasPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), "super_mushroom"))
                                .then(Commands.argument("enablePowerUp", BoolArgumentType.bool())
                                        .then(Commands.argument("manualOverride", BoolArgumentType.bool())
                                                .executes(ctx -> applyMushroom(ctx.getSource(),
                                                        EntityArgument.getEntities(ctx, "targets"),
                                                        BoolArgumentType.getBool(ctx, "enablePowerUp"),
                                                        BoolArgumentType.getBool(ctx, "manualOverride")
                                                ))
                                        )
                                        .executes(ctx -> applyMushroom(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"),
                                                BoolArgumentType.getBool(ctx, "enablePowerUp"), true)
                                        )
                                )
                        )
                        .then(Commands.literal("mushroom_boost")
                                .then(Commands.argument("boostStrength", DoubleArgumentType.doubleArg(0.0, 50.0))
                                        .executes(ctx -> applyMushroomBoost(ctx.getSource(),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                DoubleArgumentType.getDouble(ctx, "boostStrength"))
                                        )
                                )
                                .executes(ctx -> applyMushroomBoost(ctx.getSource(),
                                        EntityArgument.getEntities(ctx, "targets"), 1.0)
                                )
                        )
                        .then(Commands.literal("super_star")
                                .executes(ctx -> hasPowerUp(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), "super_star"))
                                .then(Commands.argument("enablePowerUp", BoolArgumentType.bool())
                                        .then(Commands.argument("cooldownTicks", IntegerArgumentType.integer(0))
                                                .executes(ctx -> applySuperStar(ctx.getSource(),
                                                        EntityArgument.getEntities(ctx, "targets"),
                                                        BoolArgumentType.getBool(ctx, "enablePowerUp"),
                                                        IntegerArgumentType.getInteger(ctx, "cooldownTicks")
                                                ))
                                        )
                                        .executes(ctx -> applySuperStar(ctx.getSource(),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                BoolArgumentType.getBool(ctx, "enablePowerUp"), -1
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
        }
    }

    private static int applyPowerUp(CommandSourceStack source, Collection<? extends Entity> targets, String powerUpName, boolean enablePowerUp) {
        int count = 0;
        Component powerUpBoolean = Component.translatable(enablePowerUp
                ? "commands.marioverse.boolean.true" : "commands.marioverse.boolean.false");

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && entity instanceof AbilitiesHandler handler) {
                SoundSource soundSource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL;
                applyPowerUpType(handler, powerUpName, enablePowerUp);
                count++;

                if (enablePowerUp)
                    entity.level().playSound(null, entity.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), soundSource, 1.0F, 1.0F);

                if (count == 1) {
                    switch (powerUpName) {
                        case "fire_flower" -> source.sendSuccess(() ->
                                Component.translatable("commands.marioverse.power_up.fire_flower", powerUpBoolean, entity.getDisplayName()), true);
                        case "ice_flower" -> source.sendSuccess(() ->
                                Component.translatable("commands.marioverse.power_up.ice_flower", powerUpBoolean, entity.getDisplayName()), true);
                    }
                }
            } else source.sendSuccess(() ->
                    Component.translatable("commands.marioverse.power_up.fail"), true);
        }

        int finalCount = count;

        if (finalCount > 1) {
            switch (powerUpName) {
                case "fire_flower" -> source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up.fire_flower.count", powerUpBoolean, finalCount), true);
                case "ice_flower" -> source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up.ice_flower.count", powerUpBoolean, finalCount), true);
            }
        }

        return count;
    }

    private static int applyMushroom(CommandSourceStack source, Collection<? extends Entity> targets, boolean enablePowerUp, boolean manualOverride) {
        int count = 0;
        Component powerUpBoolean = Component.translatable(enablePowerUp
                ? "commands.marioverse.boolean.true" : "commands.marioverse.boolean.false");

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && entity instanceof AbilitiesHandler handler) {
                SoundSource soundSource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL;
                handler.mv$setSuperMushroom(enablePowerUp);
                handler.mv$setMushroomOverride(manualOverride);
                count++;

                if (enablePowerUp)
                    entity.level().playSound(null, entity.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), soundSource, 1.0F, 1.0F);
                else entity.level().playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(), soundSource, 1.0F, 1.0F);

                if (count == 1)
                    source.sendSuccess(() ->
                            Component.translatable("commands.marioverse.power_up.super_mushroom", powerUpBoolean, entity.getDisplayName()), true);
            } else source.sendSuccess(() ->
                    Component.translatable("commands.marioverse.power_up.fail"), true);
        }

        int finalCount = count;

        if (finalCount > 1)
            source.sendSuccess(() ->
                    Component.translatable("commands.marioverse.power_up.super_mushroom.count", powerUpBoolean, finalCount), true);

        return count;
    }

    private static int applyMushroomBoost(CommandSourceStack source, Collection<? extends Entity> targets, double boostStrength) {
        int count = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler) {
                SoundSource soundSource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL;
                handler.mv$setMushroomBoost(true);
                MushroomItem.mushroomAbilities(null, livingEntity.level(), livingEntity, boostStrength, false, true);
                count++;

                entity.level().playSound(null, entity.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), soundSource, 1.0F, 1.0F);

                if (count == 1) {
                    if (entity.getVehicle() != null)
                        source.sendSuccess(() ->
                                Component.translatable("commands.marioverse.power_up.super_mushroom_boost", boostStrength, entity.getVehicle().getDisplayName()), true);
                     else source.sendSuccess(() ->
                            Component.translatable("commands.marioverse.power_up.super_mushroom_boost", boostStrength, entity.getDisplayName()), true);
                }
            } else if (entity instanceof VehicleEntity) {
                source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up_boost.fail", entity.getDisplayName()).withStyle(ChatFormatting.RED), true);
            } else source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up.fail"), true);
        }

        int finalCount = count;

        if (finalCount > 1)
            source.sendSuccess(() ->
                    Component.translatable("commands.marioverse.power_up.super_mushroom_boost.count", boostStrength, finalCount), true);

        return count;
    }

    private static int applySuperStar(CommandSourceStack source, Collection<? extends Entity> targets, boolean enablePowerUp, int cooldownTicks) {
        int count = 0;
        Component powerUpBoolean = Component.translatable(enablePowerUp
                ? "commands.marioverse.boolean.true" : "commands.marioverse.boolean.false");

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler) {
                SoundSource soundSource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL;
                handler.mv$setSuperStar(enablePowerUp);
                count++;

                if (enablePowerUp)
                    entity.level().playSound(null, entity.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(), soundSource, 1.0F, 1.0F);

                if (cooldownTicks >= 0) {
                    handler.mv$setSuperStarCooldown(cooldownTicks);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, cooldownTicks, 4, true, false));
                } else {
                    handler.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));
                }

                if (count == 1) {
                    if (enablePowerUp)
                        source.sendSuccess(() ->
                                Component.translatable("commands.marioverse.power_up.super_star.ticks", powerUpBoolean, entity.getDisplayName(), cooldownTicks), true);
                    else source.sendSuccess(() ->
                                Component.translatable("commands.marioverse.power_up.super_star", powerUpBoolean, entity.getDisplayName()), true);
                }
            } else source.sendSuccess(() ->
                    Component.translatable("commands.marioverse.power_up.fail"), true);
        }

        int finalCount = count;

        if (finalCount > 1) {
            if (enablePowerUp)
                source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up.super_star.ticks.count", powerUpBoolean, finalCount, cooldownTicks), true);
            else source.sendSuccess(() ->
                    Component.translatable("commands.marioverse.power_up.super_star.count", powerUpBoolean, finalCount), true);
        }

        return count;
    }

    private static int hasPowerUp(CommandSourceStack source, Collection<? extends Entity> targets, String powerUpName) {
        int count = 0;
        int falseCount = 0;

        List<Component> singleResults = new ArrayList<>();

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && entity instanceof AbilitiesHandler handler) {
                boolean hasPowerUp = switch (powerUpName) {
                    case "fire_flower" -> handler.mv$hasFireFlower();
                    case "ice_flower" -> handler.mv$hasIceFlower();
                    case "super_mushroom" -> handler.mv$hasSuperMushroom();
                    case "super_star" -> handler.mv$hasSuperStar();
                    default -> false;
                };

                Component powerUpBoolean = Component.translatable(hasPowerUp
                        ? "commands.marioverse.boolean.true" : "commands.marioverse.boolean.false");

                if (hasPowerUp) count++;
                else falseCount++;

                if (targets.size() == 1)
                    singleResults.add(Component
                            .translatable("commands.marioverse.power_up." + powerUpName + ".value", powerUpBoolean, entity.getDisplayName()));
            }
        }

        if (singleResults.size() == 1) {
            source.sendSuccess(singleResults::getFirst, true);
        }

        if (targets.size() > 1) {
            if (count > 0) {
                Component powerUpBoolean = Component.translatable("commands.marioverse.boolean.true");
                int finalCount = count;

                source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up." + powerUpName + ".value.count", powerUpBoolean, finalCount), true);
            }

            if (falseCount > 0) {
                Component powerUpBoolean = Component.translatable("commands.marioverse.boolean.false");
                int finalFalseCount = falseCount;

                source.sendSuccess(() ->
                        Component.translatable("commands.marioverse.power_up." + powerUpName + ".value.count", powerUpBoolean, finalFalseCount), true);
            }
        }

        return count + falseCount;
    }

}
