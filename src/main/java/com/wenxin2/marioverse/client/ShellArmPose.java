package com.wenxin2.marioverse.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class ShellArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> SHELL_HOLD = new EnumProxy<>(HumanoidModel.ArmPose.class,
            true, (IArmPoseTransformer) ShellArmPose::applyTransform);

    public static HumanoidModel.ArmPose getShellHoldPose() {
        return SHELL_HOLD.getValue();
    }

    private static void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        float armPitch = model.head.xRot - (float) (Math.PI / 2) + 0.15F;

        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot = armPitch;
            model.rightArm.yRot = model.head.yRot - 0.35F;
            model.rightArm.zRot = 0.0F;
        } else {
            model.leftArm.xRot = armPitch;
            model.leftArm.yRot = model.head.yRot + 0.35F;
            model.leftArm.zRot = 0.0F;
        }
    }
}
