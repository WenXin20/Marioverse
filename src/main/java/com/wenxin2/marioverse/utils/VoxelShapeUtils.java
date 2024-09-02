package com.wenxin2.marioverse.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtils {
    public static VoxelShape rotateShape(VoxelShape shape, Direction fromDirection, Direction toDirection) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };

        // Calculate the number of 90-degree rotations needed around the specified axis
        int times = (toDirection.get2DDataValue() - fromDirection.get2DDataValue() + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ)
                    -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape rotateShapeAxis(VoxelShape shape, Direction.Axis rotationAxis, int numRotations) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        // Calculate the number of 90-degree rotations needed around the specified axis
        int times = numRotations % 4;
        if (times < 0) times += 4; // Ensure positive value for times

        for (int i = 0; i < numRotations; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                // Rotate the VoxelShape around the specified axis
                if (rotationAxis == Direction.Axis.X) {
                    // Rotate around the X-axis
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ));
                } else if (rotationAxis == Direction.Axis.Y) {
                    // Rotate around the Y-axis
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
                } else if (rotationAxis == Direction.Axis.Z) {
                    // Rotate around the Z-axis
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(minY, 1 - maxX, minZ, maxY, 1 - minX, maxZ));
                }
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
}
