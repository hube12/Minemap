#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec3 inNormal;

out vec2 texCoord;
out vec3 normal;
out vec3 fragPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    fragPos = vec3(model * vec4(pos, 1.0));
    normal = inNormal;
    texCoord = inTexCoord;
    gl_Position = projection * view * vec4(fragPos, 1.0);
}