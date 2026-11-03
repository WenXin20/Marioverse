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
    return 0.8 + 0.5 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + t));
}

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.1) {
        discard;
    }

    vec3 pos = viewPos * PositionScale;
    vec3 rainbow = palette(pos.y + Time * 0.5);

    float facing = abs(dot(normalize(viewNormal), normalize(-viewPos)));
    float rim = pow(1.0 - facing, 2.0);
    float shimmer = pow(max(sin((pos.x + pos.y) * 4.0 - Time * 2.0), 0.0), 24.0);
    float pulse = 0.9 + 0.1 * sin(Time * 1.5);

    vec3 color = texColor.rgb * rainbow * pulse + rainbow * rim * 0.6 + vec3(shimmer * 0.35);
    vec4 tint = ColorModulator * vertexColor;

    fragColor = vec4(color * tint.rgb * tint.a * texColor.a, 1.0);
}
