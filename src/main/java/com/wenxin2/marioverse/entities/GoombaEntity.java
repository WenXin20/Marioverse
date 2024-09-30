package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.ItemRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoombaEntity extends Monster implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.goomba.idle");
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.goomba.run");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.goomba.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GoombaEntity(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.5D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkAnimController));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (this.isRunning()) {
            event.setAndContinue(RUN_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isWalking()) {
            event.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private boolean isWalking() {
        return (this.getDeltaMovement().horizontalDistance() >= 0.01
                && this.getDeltaMovement().horizontalDistance() < 0.5)
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof RandomStrollGoal);
    }

    private boolean isRunning() {
        return this.getDeltaMovement().horizontalDistance() >= 0.5
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof MeleeAttackGoal)
                || this.targetSelector.getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal<?>);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public EquipmentSlot getEquipmentSlotForItem(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getEquipmentSlot();
        }
        return super.getEquipmentSlotForItem(stack);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = random.nextInt(6);
            if (i == 0) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.FIRE_HAT.get()));
            } else {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && player.getItemInHand(hand).getItem() instanceof ArmorItem) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            player.swing(hand);
        }
        return super.mobInteract(player, hand);
    }
}
