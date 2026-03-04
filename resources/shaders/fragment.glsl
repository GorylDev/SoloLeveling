#version 330

in vec2 outTextCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main() {
    fragColor = texture(texture_sampler, outTextCoord);
}