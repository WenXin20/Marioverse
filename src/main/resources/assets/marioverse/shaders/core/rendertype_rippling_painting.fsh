#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec2 texCoord0;
in vec2 localPos;
flat in vec3 rippleProgress;
flat in vec2 rippleData;

out vec4 fragColor;

const float RING_FREQUENCY = 42.0;
const float RING_DECAY = 3.5;
const float RING_STRENGTH = 0.045;
const float RING_SHADING = 0.22;

void addRipple(float encoded, vec2 centered, float radius, float maxRadius, float edgeMask, inout vec2 offset, inout float shading) {
    if (encoded < 0.5)
        return;

    float progress = (encoded - 1.0) / 254.0;
    float front = progress * (maxRadius + 0.35) * 1.15;
    float behindFront = front - radius;
    float packet = smoothstep(-0.02, 0.04, behindFront) * exp(-max(behindFront, 0.0) * RING_DECAY);
    float amplitude = smoothstep(0.0, 0.08, progress) * pow(1.0 - progress, 1.5);
    float strength = packet * amplitude * edgeMask;
    float phase = behindFront * RING_FREQUENCY;
    vec2 direction = radius > 1.0e-4 ? centered / radius : vec2(0.0);

    offset += direction * sin(phase) * strength * RING_STRENGTH;
    shading += cos(phase) * strength;
}

void main() {
    bool translucent = rippleData.x > 0.5;
    float aspect = rippleData.y;

    mat2 localToScreen = mat2(dFdx(localPos), dFdy(localPos));
    mat2 texToScreen = mat2(dFdx(texCoord0), dFdy(texCoord0));
    float determinantValue = determinant(localToScreen);

    vec4 texColor;
    float shading = 0.0;

    if (abs(determinantValue) > 1.0e-12) {
        mat2 localToTex = texToScreen * inverse(localToScreen);
        vec2 uvMin = min(texCoord0 - localToTex * localPos, texCoord0 + localToTex * (vec2(1.0) - localPos));
        vec2 uvMax = max(texCoord0 - localToTex * localPos, texCoord0 + localToTex * (vec2(1.0) - localPos));

        vec2 scale = vec2(aspect, 1.0) / max(aspect, 1.0);
        vec2 centered = (localPos - 0.5) * scale;
        float radius = length(centered);
        float maxRadius = length(0.5 * scale);

        vec2 edges = smoothstep(0.0, 0.12, localPos) * smoothstep(0.0, 0.12, 1.0 - localPos);
        float edgeMask = edges.x * edges.y;
        vec2 offset = vec2(0.0);

        addRipple(rippleProgress.x, centered, radius, maxRadius, edgeMask, offset, shading);
        addRipple(rippleProgress.y, centered, radius, maxRadius, edgeMask, offset, shading);
        addRipple(rippleProgress.z, centered, radius, maxRadius, edgeMask, offset, shading);

        vec2 sampleLocal = clamp((centered + offset) / scale + 0.5, vec2(0.002), vec2(0.998));
        vec2 sampleUv = clamp(texCoord0 + localToTex * (sampleLocal - localPos), uvMin, uvMax);
        texColor = texture(Sampler0, sampleUv);
    } else {
        texColor = texture(Sampler0, texCoord0);
    }

    vec4 color = texColor * vertexColor * ColorModulator;
    if (translucent && color.a < 0.1)
        discard;
    if (!translucent)
        color.a = 1.0;

    color.rgb *= 1.0 + shading * RING_SHADING;
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
