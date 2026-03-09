#version 330

in vec2 outTextCoord;
in vec3 fragNormal;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec3 lightDir = normalize(vec3(-0.5, -1.0, -0.5));
    vec3 lightColor = vec3(1.0, 1.0, 1.0);

    vec3 norm = normalize(fragNormal);

    float ambientStrength = 0.3;
    vec3 ambient = ambientStrength * lightColor;

    float diff = max(dot(norm, -lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec4 texColor = texture(textureSampler, outTextCoord);
    vec3 finalLight = ambient + diffuse;

    fragColor = vec4(texColor.rgb * finalLight, texColor.a);
}