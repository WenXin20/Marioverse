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

    float luma = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    float facing = abs(dot(normalize(viewNormal), normalize(-viewPos)));
    float rim = pow(1.0 - facing, 2.0);
    float shimmer = pow(max(sin((pos.x + pos.y) * 4.0 - Time * 2.0), 0.0), 24.0);

    vec3 color = rainbow * (0.55 + 0.65 * luma) + rainbow * rim * 0.35 + vec3(shimmer * 0.3);
    vec4 tint = ColorModulator * vertexColor;

    fragColor = vec4(min(color, vec3(1.0)) * tint.rgb, 0.6 * texColor.a * tint.a);
}
