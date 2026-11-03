#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec2 texCoord0;
out vec2 localPos;
flat out vec3 rippleData;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexDistance = fog_distance(Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    texCoord0 = UV0;

    localPos = vec2(float(UV1.x & 255), float(UV1.y & 255)) / 255.0;
    rippleData = vec3(float(UV1.x >> 8) / 127.0, float((UV1.y >> 8) & 1), exp2(float(UV1.y >> 9) / 7.875 - 4.0));
}
