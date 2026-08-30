package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistry {
    public static final WoodType MUSHROOT;

    // Custom marioverse-namespaced WoodTypes for each vanilla wood, reusing the vanilla BlockSetType
    // (so sounds/interactions match vanilla) but with our own name so arrow sign texture resolution
    // (ResourceLocation.parse(woodType.name()) in ArrowSignBlockModel) resolves to our own texture
    // pack layout under assets/marioverse/... instead of assets/minecraft/....
    public static final WoodType OAK;
    public static final WoodType SPRUCE;
    public static final WoodType BIRCH;
    public static final WoodType JUNGLE;
    public static final WoodType ACACIA;
    public static final WoodType DARK_OAK;
    public static final WoodType MANGROVE;
    public static final WoodType CHERRY;
    public static final WoodType BAMBOO;
    public static final WoodType CRIMSON;
    public static final WoodType WARPED;

    static {
        MUSHROOT = WoodType.register(new WoodType(Marioverse.MOD_ID + ":mushroot", BlockSetTypeRegistry.MUSHROOT));

        OAK = WoodType.register(new WoodType(Marioverse.MOD_ID + ":oak", BlockSetType.OAK));
        SPRUCE = WoodType.register(new WoodType(Marioverse.MOD_ID + ":spruce", BlockSetType.SPRUCE));
        BIRCH = WoodType.register(new WoodType(Marioverse.MOD_ID + ":birch", BlockSetType.BIRCH));
        JUNGLE = WoodType.register(new WoodType(Marioverse.MOD_ID + ":jungle", BlockSetType.JUNGLE));
        ACACIA = WoodType.register(new WoodType(Marioverse.MOD_ID + ":acacia", BlockSetType.ACACIA));
        DARK_OAK = WoodType.register(new WoodType(Marioverse.MOD_ID + ":dark_oak", BlockSetType.DARK_OAK));
        MANGROVE = WoodType.register(new WoodType(Marioverse.MOD_ID + ":mangrove", BlockSetType.MANGROVE));
        CHERRY = WoodType.register(new WoodType(Marioverse.MOD_ID + ":cherry", BlockSetType.CHERRY));
        BAMBOO = WoodType.register(new WoodType(Marioverse.MOD_ID + ":bamboo", BlockSetType.BAMBOO));
        CRIMSON = WoodType.register(new WoodType(Marioverse.MOD_ID + ":crimson", BlockSetType.CRIMSON));
        WARPED = WoodType.register(new WoodType(Marioverse.MOD_ID + ":warped", BlockSetType.WARPED));
    }
}