package com.wenxin2.marioverse.client.particles;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wenxin2.marioverse.particles.EntityShatterParticleOption;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

@OnlyIn(Dist.CLIENT)
public class EntityShatterParticle extends SingleQuadParticle {
    private static final Map<ResourceLocation, ParticleRenderType> RENDER_TYPES = new HashMap<>();
    private static final Map<String, List<float[]>> MODEL_FACES = new HashMap<>();
    private static final Map<ResourceLocation, Optional<TextureMask>> TEXTURE_MASKS = new HashMap<>();
    private static final int FRAGMENT_PIXELS = 4;
    private final ParticleRenderType renderType;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;

    public EntityShatterParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz,
                                 EntityShatterParticleOption option) {
        super(level, x, y, z, dx, dy, dz);
        this.renderType = EntityShatterParticle.getRenderType(option.texture());
        this.gravity = 1.0F;
        this.quadSize /= 2.0F;
        this.setParticleSpeed(dx + this.random.nextGaussian() / 40.0, dy + this.random.nextGaussian() / 40.0,
                dz + this.random.nextGaussian() / 40.0);
        this.setLifetime(this.random.nextInt(20) + 20);

        float[] fragment = EntityShatterParticle.pickFragment(option, this.random);
        this.u0 = fragment[0];
        this.v0 = fragment[1];
        this.u1 = fragment[2];
        this.v1 = fragment[3];
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return this.renderType;
    }

    @Override
    protected float getU0() {
        return this.u0;
    }

    @Override
    protected float getU1() {
        return this.u1;
    }

    @Override
    protected float getV0() {
        return this.v0;
    }

    @Override
    protected float getV1() {
        return this.v1;
    }

    private static ParticleRenderType getRenderType(ResourceLocation texture) {
        return RENDER_TYPES.computeIfAbsent(texture, location -> new ParticleRenderType() {
            @Override
            public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
                RenderSystem.depthMask(true);
                RenderSystem.setShaderTexture(0, location);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public String toString() {
                return "ENTITY_SHATTER[" + location + "]";
            }
        });
    }

    private static float[] pickFragment(EntityShatterParticleOption option, RandomSource random) {
        TextureMask mask = TEXTURE_MASKS.computeIfAbsent(option.texture(), EntityShatterParticle::loadMask).orElse(null);
        List<float[]> faces = MODEL_FACES.computeIfAbsent(option.model() + "#" + option.bone(),
                key -> EntityShatterParticle.collectFaces(option.model(), option.bone()));
        int width = mask != null ? mask.width() : 64;
        int height = mask != null ? mask.height() : 64;
        float[] fallback = null;

        for (int attempt = 0; attempt < 12; attempt++) {
            float[] face = faces.isEmpty() ? new float[]{0.0F, 0.0F, 1.0F, 1.0F} : faces.get(random.nextInt(faces.size()));
            int minX = (int) Math.floor(face[0] * width + 1.0E-4);
            int minY = (int) Math.floor(face[1] * height + 1.0E-4);
            int maxX = Math.max(minX + 1, (int) Math.ceil(face[2] * width - 1.0E-4));
            int maxY = Math.max(minY + 1, (int) Math.ceil(face[3] * height - 1.0E-4));
            int sizeX = Math.min(FRAGMENT_PIXELS, maxX - minX);
            int sizeY = Math.min(FRAGMENT_PIXELS, maxY - minY);
            int pixelX = minX + random.nextInt(maxX - minX - sizeX + 1);
            int pixelY = minY + random.nextInt(maxY - minY - sizeY + 1);
            float[] fragment = new float[]{(float) pixelX / width, (float) pixelY / height,
                    (float) (pixelX + sizeX) / width, (float) (pixelY + sizeY) / height};

            if (fallback == null)
                fallback = fragment;
            if (mask == null || mask.isOpaque(pixelX + sizeX / 2, pixelY + sizeY / 2))
                return fragment;
        }
        return fallback;
    }

    private static List<float[]> collectFaces(ResourceLocation model, String boneName) {
        List<float[]> faces = new ArrayList<>();
        BakedGeoModel bakedModel = GeckoLibCache.getBakedModels().get(model);

        if (bakedModel == null)
            return faces;

        if (boneName.isEmpty()) {
            for (GeoBone bone : bakedModel.topLevelBones())
                EntityShatterParticle.collectFaces(bone, faces, true);
        } else bakedModel.getBone(boneName).ifPresent(bone -> EntityShatterParticle.collectFaces(bone, faces, false));
        return faces;
    }

    private static void collectFaces(GeoBone bone, List<float[]> faces, boolean includeChildren) {
        for (GeoCube cube : bone.getCubes()) {
            for (GeoQuad quad : cube.quads()) {
                if (quad == null)
                    continue;

                float minU = Float.MAX_VALUE;
                float minV = Float.MAX_VALUE;
                float maxU = -Float.MAX_VALUE;
                float maxV = -Float.MAX_VALUE;

                for (GeoVertex vertex : quad.vertices()) {
                    minU = Math.min(minU, vertex.texU());
                    minV = Math.min(minV, vertex.texV());
                    maxU = Math.max(maxU, vertex.texU());
                    maxV = Math.max(maxV, vertex.texV());
                }

                if (maxU - minU > 1.0E-6 && maxV - minV > 1.0E-6)
                    faces.add(new float[]{minU, minV, maxU, maxV});
            }
        }

        if (includeChildren)
            for (GeoBone child : bone.getChildBones())
                EntityShatterParticle.collectFaces(child, faces, true);
    }

    private static Optional<TextureMask> loadMask(ResourceLocation texture) {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture);

        if (resource.isEmpty())
            return Optional.empty();

        try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
            BitSet opaque = new BitSet(image.getWidth() * image.getHeight());

            for (int y = 0; y < image.getHeight(); y++)
                for (int x = 0; x < image.getWidth(); x++)
                    if ((image.getPixelRGBA(x, y) >>> 24) != 0)
                        opaque.set(y * image.getWidth() + x);
            return Optional.of(new TextureMask(image.getWidth(), image.getHeight(), opaque));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private record TextureMask(int width, int height, BitSet opaque) {
        private boolean isOpaque(int x, int y) {
            return x >= 0 && y >= 0 && x < this.width && y < this.height && this.opaque.get(y * this.width + x);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<EntityShatterParticleOption> {
        @Nullable
        @Override
        public Particle createParticle(EntityShatterParticleOption option, ClientLevel level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new EntityShatterParticle(level, x, y, z, dx, dy, dz, option);
        }
    }
}
