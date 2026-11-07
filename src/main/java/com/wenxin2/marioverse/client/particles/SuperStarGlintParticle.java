package com.wenxin2.marioverse.client.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SuperStarGlintParticle extends GlowingSuspendedTownParticle {
    public static final ParticleRenderType SUPER_STAR_SHEET = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            ShaderInstance shader = SuperStarRenderType.SUPER_STAR_PARTICLE_SHADER;
            if (shader != null) {
                Minecraft minecraft = Minecraft.getInstance();
                float gameTime = minecraft.level != null ? minecraft.level.getGameTime() : 0.0F;
                shader.safeGetUniform("Time").set((gameTime + minecraft.getTimer().getGameTimeDeltaPartialTick(false)) * 0.2F);
                RenderSystem.setShader(() -> shader);
            }
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public boolean isTranslucent() {
            return true;
        }

        @Override
        public String toString() {
            return "MARIOVERSE_SUPER_STAR_SHEET";
        }
    };

    public SuperStarGlintParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return SUPER_STAR_SHEET;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider extends GlowParticle.WaxOffProvider {
        private final SpriteSet sprite;
        public Provider(SpriteSet spriteSet) {
            super(spriteSet);
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Particle particle = new SuperStarGlintParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);

            particle.setColor(1.0F, 1.0F, 1.0F);
            particle.setLifetime(world.random.nextInt(30) + 10);
            particle.setParticleSpeed(xSpeed * 0.01 / 2.0, ySpeed * 0.01, zSpeed * 0.01 / 2.0);
            return particle;
        }
    }
}
