#version 330 core

in vec2 fragTextCoords;
out vec4 fragColor;

uniform float time;
uniform vec3 gateColor;

float noise(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

float lightning(vec2 uv, float t) {
    float n = noise(uv * 10.0 + t * 5.0);
    float l = pow(1.0 - abs(uv.x - n), 20.0);
    return l * step(0.9, noise(vec2(t, 0.0)));
}

void main() {
    vec2 uv = fragTextCoords * 2.0 - 1.0;

    float dist = length(uv);
    float angle = atan(uv.y, uv.x);
    float wave = sin(dist * 10.0 - time * 5.0 + angle * 2.0);
    float edge = smoothstep(0.8, 1.0, dist);

    vec3 baseColor = gateColor * (0.8 + 0.2 * wave);
    baseColor = mix(baseColor, vec3(1.0), edge);

    float light1 = lightning(uv + vec2(sin(time) * 0.5, cos(time) * 0.5), time);
    float light2 = lightning(uv * 1.5 - vec2(cos(time * 0.7) * 0.3, sin(time * 0.7) * 0.3), time * 1.2);

    vec3 lightningColor = vec3(0.8, 0.9, 1.0) * (light1 + light2) * 2.0;

    vec3 finalColor = baseColor + lightningColor;

    float alpha = 1.0 - smoothstep(0.9, 1.0, dist);

    if (alpha < 0.1) discard;

    fragColor = vec4(finalColor, alpha);
}