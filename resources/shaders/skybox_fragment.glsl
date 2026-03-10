#version 330

in vec3 texCoords;
out vec4 fragColor;

void main() {
    vec3 dir = normalize(texCoords);
    vec3 topColor = vec3(0.1, 0.4, 0.8);
    vec3 bottomColor = vec3(0.7, 0.8, 0.9);

    float factor = smoothstep(0.0, 1.0, dir.y);
    vec3 finalColor = mix(bottomColor, topColor, factor);

    if(dir.y < 0.0) {
        finalColor = mix(vec3(0.1, 0.1, 0.1), bottomColor, smoothstep(-0.2, 0.0, dir.y));
    }

    fragColor = vec4(finalColor, 1.0);
}