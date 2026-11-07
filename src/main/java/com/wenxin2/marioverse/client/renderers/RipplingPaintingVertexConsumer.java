package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RipplingPaintingVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final PaintingRippleRenderer.RippleContext context;
    private final boolean translucent;
    private final Vector3f localPos = new Vector3f();

    public RipplingPaintingVertexConsumer(VertexConsumer delegate, PaintingRippleRenderer.RippleContext context, boolean translucent) {
        this.delegate = delegate;
        this.context = context;
        this.translucent = translucent;
    }

    @NotNull
    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.context.inverseOrigin().transformPosition(x, y, z, this.localPos);
        this.delegate.addVertex(x, y, z);
        this.delegate.setUv1(this.context.encodeU(this.localPos), this.context.encodeV(this.localPos));
        return this;
    }

    @NotNull
    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        this.delegate.setColor(red, green, blue, alpha);
        return this;
    }

    @NotNull
    @Override
    public VertexConsumer setUv(float u, float v) {
        this.delegate.setUv(u, v);
        return this;
    }

    @NotNull
    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    @NotNull
    @Override
    public VertexConsumer setUv2(int u, int v) {
        this.delegate.setUv2(this.context.encodeBlockLight(u), this.context.encodeSkyLight(v, this.translucent));
        return this;
    }

    @NotNull
    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        this.delegate.setNormal(x, y, z);
        return this;
    }
}
