package com.wenxin2.marioverse.integration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinCompatPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_PACKAGE = "com.wenxin2.marioverse.mixin.";
    private static final Map<String, List<String>> COMPAT_PACKAGES = Map.of(
            "iris.", List.of("iris", "oculus"),
            "immersive_paintings.", List.of("immersive_paintings"),
            "twilightforest.", List.of("twilightforest"));

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(MIXIN_PACKAGE))
            return true;

        String mixinPath = mixinClassName.substring(MIXIN_PACKAGE.length());
        for (Map.Entry<String, List<String>> entry : COMPAT_PACKAGES.entrySet()) {
            if (mixinPath.startsWith(entry.getKey()))
                return entry.getValue().stream().anyMatch(MixinCompatPlugin::isModLoaded);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isModLoaded(String modId) {
        LoadingModList modList = LoadingModList.get();
        return modList != null && modList.getModFileById(modId) != null;
    }
}
