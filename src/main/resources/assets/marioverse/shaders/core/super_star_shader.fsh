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

    float v = worldPos.y + Time * 0.5;
    vec3 rainbow = palette(v);
    float alpha = 0.8;
    vec3 finalColor = mix(texColor.rgb, texColor.rgb * rainbow, alpha);

    fragColor = vec4(texColor.rgb * rainbow, texColor.a) * ColorModulator * vertexColor;
}