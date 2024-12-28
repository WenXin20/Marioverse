package com.wenxin2.marioverse.utils;

public class EntityScale implements EntityScaleInterface {
    private float widthScale = 1.0f;
    private float heightScale = 1.0f;

    @Override
    public float getWidthScale() {
        return widthScale;
    }

    @Override
    public void setWidthScale(float scale) {
        this.widthScale = scale;
    }

    @Override
    public float getHeightScale() {
        return heightScale;
    }

    @Override
    public void setHeightScale(float scale) {
        this.heightScale = scale;
    }
}
