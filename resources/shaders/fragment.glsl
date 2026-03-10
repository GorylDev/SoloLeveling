#version 330

in vec2 outTextCoord;
in vec3 fragNormal;
in float visibility;

out vec4 fragColor;
uniform sampler2D textureSampler;

void main() {
    //LIGHTING
    vec3 lightDir = normalize(vec3(-0.5, -1.0, -0.5));
    vec3 lightColor = vec3(1.0, 1.0, 1.0);

    vec3 norm = fragNormal;
    if (length(norm) < 0.01) {
        norm = vec3(0.0, 1.0, 0.0);
    } else {
        norm = normalize(norm);
    }

    float ambientStrength = 0.6;
    vec3 ambient = ambientStrength * lightColor;

    float diff = max(dot(norm, -lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec4 texColor = texture(textureSampler, outTextCoord);
    vec3 finalLight = ambient + diffuse;

    // FOG
    vec3 objectColor = texColor.rgb * min(finalLight, vec3(1.0));
    vec3 fogColor = vec3(0.7, 0.8, 0.9);
    vec3 finalColor = mix(fogColor, objectColor, visibility);

    fragColor = vec4(finalColor, texColor.a);
}