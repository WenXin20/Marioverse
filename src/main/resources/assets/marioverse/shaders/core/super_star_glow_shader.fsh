#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float Time;
uniform float PositionScale;

in vec4 vertexColor;
in vec2 texCoord0;
in vec3 viewPos;
in vec3 viewNormal;

out vec4 fragColor;

vec3 palette(float t) {
    return 0.6 + 0.4 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + t));
}

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.1) {
        discard;
    }

    vec3 pos = viewPos * PositionScale;
    vec3 rainbow = palette(pos.y + Time * 0.5);
    float pulse = 0.85 + 0.15 * sin(Time * 1.5);

    fragColor = vec4(rainbow * pulse * 0.75 * ColorModulator.rgb * vertexColor.a, 1.0);
}
