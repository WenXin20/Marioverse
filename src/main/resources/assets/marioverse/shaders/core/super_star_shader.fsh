#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float Time;

in vec4 vertexColor;
in vec2 texCoord0;
in vec3 worldPos;

out vec4 fragColor;

vec3 palette(float t) {
    return 0.8 + 0.5 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + t));
}

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a == 0.0) {
        discard;
    }

    vec2 circleCenter = vec2(
        sin(Time * 0.05) * 5.0,
        cos(Time * 0.04) * 5.0
    );

    vec2 pos = worldPos.xz;
    float dist = length(pos - circleCenter);
    float v = dist * 0.9 + Time * 0.5;
    vec3 rainbow = palette(v);
    float alpha = 0.8;
    vec3 finalColor = mix(texColor.rgb, texColor.rgb * rainbow, alpha);

    fragColor = vec4(finalColor, texColor.a) * ColorModulator * vertexColor;
}