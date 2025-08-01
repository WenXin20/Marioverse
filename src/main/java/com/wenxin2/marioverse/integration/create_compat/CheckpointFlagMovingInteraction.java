package com.wenxin2.marioverse.integration.create_compat;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.GameEventRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

// Crashes on player collision with trains. Disabled for now
public class CheckpointFlagMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public void handleEntityCollision(Entity entity, BlockPos pos, AbstractContraptionEntity contraptionEntity) {
        BlockState state = contraptionEntity.getInBlockState();
        BlockPos respawnPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

        BlockPos otherPos = switch (state.getValue(CheckpointFlagBlock.PART)) {
            case TOP -> pos.below(2);
            case MIDDLE -> pos.below();
            default -> pos;
        };
        StructureTemplate.StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(otherPos);

        if (entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS) && entity instanceof AbilitiesHandler handler
                && handler.mv$getCheckpointFlagCooldown() <= 0) {
            if (info != null && info.state().hasProperty(CheckpointFlagBlock.CLAIMED) && !info.state().getValue(CheckpointFlagBlock.CLAIMED)) {
                BlockState newState = info.state().setValue(CheckpointFlagBlock.CLAIMED, true);
                this.setContraptionBlockData(contraptionEntity.getContraption().entity, otherPos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
            }

            if (!state.getValue(CheckpointFlagBlock.CLAIMED))
                state = state.setValue(CheckpointFlagBlock.CLAIMED, true);

            if (state.hasProperty(CheckpointFlagBlock.CLAIMED) && !state.getValue(CheckpointFlagBlock.CLAIMED)) {
                if (entity.level().getBlockEntity(otherPos) instanceof CheckpointFlagBlockEntity checkpointFlagBE) {
                    checkpointFlagBE.markUpdated();

                    ParticleUtils.spawnParticlesOnBlockFaces(entity.level(), otherPos, ParticleRegistry.GLOWING_STAR.get(), UniformInt.of(1, 1));

                    if (!checkpointFlagBE.isAmericanFlag() && state.getBlock() != BlockRegistry.CLASSIC_GOAL_POLE.get())
                        checkpointFlagBE.triggerAnim("switch_controller", "switch");

                    entity.level().gameEvent(entity, GameEventRegistry.CHECKPOINT_ACTIVATED, otherPos);
                    checkpointFlagBE.triggerAnim("claim_controller", "claim");
                }

                if (entity.level().getBlockEntity(otherPos) instanceof CheckpointFlagBlockEntity flagBE
                        && ConfigRegistry.CHECKPOINT_FLAG_CLAIM_USES_ITEMS.get()) {
                    ItemStack storedItem = flagBE.getTheItem();

                    if (!storedItem.isEmpty()) {
                        CheckpointFlagBlock.spawnFromCheckpointFlag(entity.level(), otherPos, storedItem, entity, true);
                        if (state.getBlock() instanceof CheckpointFlagBlock)
                            QuestionBlock.playSounds(entity.level(), otherPos, storedItem);
                        flagBE.splitTheItem(1);
                    }
                }
            }

            if (entity instanceof ServerPlayer player && !pos.equals(player.getRespawnPosition())) {
                BlockPos playerRespawnPos = player.getRespawnPosition();
                BlockPos newRespawnPos = switch (state.getValue(CheckpointFlagBlock.PART)) {
                    case TOP -> respawnPos.below(2);
                    case MIDDLE -> respawnPos.below();
                    default -> respawnPos;
                };

                if (entity.level().getBlockEntity(newRespawnPos) instanceof CheckpointFlagBlockEntity checkpointFlagBE
                        && !(newRespawnPos.equals(playerRespawnPos))) {
                    checkpointFlagBE.triggerAnim("claim_controller", "claim");

                    entity.level().playSound(null, newRespawnPos, SoundRegistry.CHECKPOINT_FLAG_CLAIMED.get(), SoundSource.BLOCKS);
                    ParticleUtils.spawnParticlesOnBlockFaces(entity.level(), newRespawnPos, ParticleRegistry.GLOWING_STAR.get(), UniformInt.of(1, 1));
                    player.setRespawnPosition(entity.level().dimension(), newRespawnPos, player.getYRot(), false, true);
                    handler.mv$setCheckpointFlagCooldown(40);

                    if (entity.level() instanceof ServerLevel serverWorld)
                        serverWorld.sendParticles(ParticleRegistry.GLOWING_STAR.get(),
                                newRespawnPos.getX() + 0.5, newRespawnPos.getY() + 0.5, newRespawnPos.getZ() + 0.5,
                                10, 0.4, 0.5, 0.4, 0.6);
                }
            }
        }
        super.handleEntityCollision(entity, pos, contraptionEntity);
    }

    public static void setup() {
        try {
            MovingInteractionBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS, new CheckpointFlagMovingInteraction()));
        } catch (Exception e) {
            Marioverse.LOGGER.warn("failed to register supplementaries create behaviors: {}", String.valueOf(e));
        }
    }
}
