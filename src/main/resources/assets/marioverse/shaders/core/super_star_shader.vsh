#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord0;
out vec3 viewPos;
out vec3 viewNormal;

void main() {
    vec4 pos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * pos;

    vertexColor = Color;
    texCoord0 = UV0;
    viewPos = pos.xyz;
    viewNormal = mat3(ModelViewMat) * Normal;
}
