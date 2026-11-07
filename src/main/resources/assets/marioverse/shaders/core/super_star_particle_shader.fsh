#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float Time;

in vec2 texCoord0;
in vec4 vertexColor;
in vec3 viewPos;

out vec4 fragColor;

vec3 palette(float t) {
    return 0.6 + 0.4 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + t));
}

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.1) {
        discard;
    }

    vec3 rainbow = palette(viewPos.y + Time * 0.5);
    float luma = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    float pulse = 0.9 + 0.1 * sin(Time * 1.5);

    vec3 color = rainbow * (0.7 + 0.6 * luma) * pulse + vec3(pow(luma, 4.0) * 0.35);
    vec4 tint = ColorModulator * vertexColor;

    fragColor = vec4(min(color, vec3(1.0)), texColor.a * tint.a);
}
