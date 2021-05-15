#version 330 core

out vec4 color;

in vec3 normal;
in vec2 texCoord;
in vec3 fragPos;

uniform sampler2D tex;
uniform vec3 lightPos;
uniform vec3 lightColor;

void main() {
    // Ambient
    float ambientStrength = 0.5;
    vec3 ambient = ambientStrength * lightColor;

    // Diffuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.3);
    vec3 diffuse = diff * lightColor;

    // Final Result
    vec3 result = (ambient + diffuse) * vec3(texture(tex, texCoord));
    color = vec4(result, 1.0);
}