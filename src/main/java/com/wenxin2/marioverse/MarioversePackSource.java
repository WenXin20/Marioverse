package com.wenxin2.marioverse;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MarioversePackSource extends BuiltInPackSource {
    private static final PackMetadataSection CUSTOM_METADATA_SECTION = new PackMetadataSection(
            Component.translatable("resource_pack.marioverse.truly_invisible.description"),
            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
            Optional.empty()
    );
    private static final PackSelectionConfig BUILT_IN_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
    private static final BuiltInMetadata CUSTOM_BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, CUSTOM_METADATA_SECTION);
    private static final ResourceLocation PACKS_DIR = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "resourcepacks");
    private static final PackLocationInfo CUSTOM_PACK_INFO = new PackLocationInfo(
            "truly_invisible", Component.translatable("resource_pack.marioverse.truly_invisible"), PackSource.BUILT_IN, Optional.of(CORE_PACK_INFO)
    );
    private final Path customResourcePath;

    public MarioversePackSource(Path resourcePath, DirectoryValidator validator) {
        super(PackType.CLIENT_RESOURCES, createCustomPackSource(resourcePath), PACKS_DIR, validator);
        this.customResourcePath = resourcePath;
        System.out.println("Custom resource path initialized: " + this.customResourcePath);
    }

    public static PackLocationInfo createBuiltInPackLocation(String id, Component title) {
        return new PackLocationInfo(id, title, PackSource.BUILT_IN, Optional.empty());
    }

    public static VanillaPackResources createCustomPackSource(Path resourcePath) {
        VanillaPackResourcesBuilder builder = new VanillaPackResourcesBuilder()
                .setMetadata(CUSTOM_BUILT_IN_METADATA)
                .exposeNamespace("marioverse")
                .pushAssetPath(PackType.CLIENT_RESOURCES, resourcePath);
        System.out.println("Registering custom resource path: " + resourcePath);
        return builder.build(CUSTOM_PACK_INFO);
    }

    @Nullable
    @Override
    public Pack createVanillaPack(PackResources resources) {
        return Pack.readMetaAndCreate(CUSTOM_PACK_INFO, fixedResources(resources), PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
    }

    @Nullable
    @Override
    public Pack createBuiltinPack(String id, Pack.ResourcesSupplier supplier, Component title) {
        return Pack.readMetaAndCreate(createBuiltInPackLocation(id, title), supplier, PackType.CLIENT_RESOURCES, BUILT_IN_SELECTION_CONFIG);
    }

    @NotNull
    @Override
    protected Component getPackTitle(String packName) {
        return Component.translatable("resource_pack.marioverse." + packName);
    }

    @Override
    protected void populatePackList(BiConsumer<String, Function<String, Pack>> packConsumer) {
        super.populatePackList(packConsumer);
        discoverPacksInPath(this.customResourcePath, packConsumer);
    }
}
