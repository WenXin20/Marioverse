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
flat in vec3 rippleData;

out vec4 fragColor;

const int SWIRL_TAPS = 12;

void main() {
    float progress = rippleData.x;
    bool translucent = rippleData.y > 0.5;
    float aspect = rippleData.z;

    mat2 localToScreen = mat2(dFdx(localPos), dFdy(localPos));
    mat2 texToScreen = mat2(dFdx(texCoord0), dFdy(texCoord0));
    float determinantValue = determinant(localToScreen);

    vec4 texColor;
    float depth = 0.0;

    if (abs(determinantValue) > 1.0e-12) {
        mat2 localToTex = texToScreen * inverse(localToScreen);
        vec2 uvMin = min(texCoord0 - localToTex * localPos, texCoord0 + localToTex * (vec2(1.0) - localPos));
        vec2 uvMax = max(texCoord0 - localToTex * localPos, texCoord0 + localToTex * (vec2(1.0) - localPos));

        vec2 scale = vec2(aspect, 1.0) / max(aspect, 1.0);
        vec2 centered = (localPos - 0.5) * scale;
        float radius = length(centered);
        float maxRadius = length(0.5 * scale);
        float closeness = 1.0 - clamp(radius / maxRadius, 0.0, 1.0);

        float envelope = sin(3.14159265 * progress);
        envelope *= envelope;
        vec2 edges = smoothstep(0.0, 0.12, localPos) * smoothstep(0.0, 0.12, 1.0 - localPos);
        float strength = envelope * edges.x * edges.y;

        float twist = strength * (1.0 + 3.5 * closeness * closeness);
        float pull = strength * 0.55;
        depth = strength * closeness;

        texColor = vec4(0.0);
        for (int i = 0; i < SWIRL_TAPS; i++) {
            float tapStep = float(i) / float(SWIRL_TAPS - 1);
            float angle = twist * (0.55 + 0.45 * tapStep);
            float c = cos(angle);
            float s = sin(angle);
            vec2 rotated = mat2(c, s, -s, c) * centered * (1.0 + pull * (0.4 + 0.6 * tapStep));
            vec2 sampleLocal = clamp(rotated / scale + 0.5, vec2(0.002), vec2(0.998));
            vec2 sampleUv = clamp(texCoord0 + localToTex * (sampleLocal - localPos), uvMin, uvMax);
            texColor += texture(Sampler0, sampleUv);
        }
        texColor /= float(SWIRL_TAPS);
    } else {
        texColor = texture(Sampler0, texCoord0);
    }

    vec4 color = texColor * vertexColor * ColorModulator;
    if (translucent && color.a < 0.1)
        discard;
    if (!translucent)
        color.a = 1.0;

    color.rgb *= 1.0 - depth * 0.35;
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
