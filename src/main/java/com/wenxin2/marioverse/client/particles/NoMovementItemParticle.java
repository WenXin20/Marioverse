package com.wenxin2.marioverse.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class NoMovementItemParticle extends TextureSheetParticle {

    protected NoMovementItemParticle(ClientLevel world, double x, double y, double z,
                                     double dx, double dy, double dz, ItemStack p_105653_) {
        this(world, x, y, z, p_105653_);
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += dx;
        this.yd += dy;
        this.zd += dz;
    }

    protected NoMovementItemParticle(ClientLevel world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        var model = Minecraft.getInstance().getItemRenderer().getModel(stack, world, null, 0);
        this.setSprite(model.getOverrides().resolve(model, stack, world, null, 0).getParticleIcon(ModelData.EMPTY));
        this.gravity = 0.0F;
        this.lifetime = 80;
        this.hasPhysics = false;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return new ParticleRenderType() {
            @Nullable
            @Override
            public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.depthMask(true);
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public boolean isTranslucent() {
                return false;
            }
        };
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return 0.5F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ItemParticleOption> {
        public Particle createParticle(ItemParticleOption itemParticleOption, ClientLevel world,
                                       double x, double y, double z, double dx, double dy, double dz) {
            return new NoMovementItemParticle(world, x, y, z, itemParticleOption.getItem());
        }
    }
}
