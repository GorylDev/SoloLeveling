#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textCoord;
layout (location=2) in vec3 normal;

out vec2 outTextCoord;
out vec3 fragNormal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec2 textureOffset;
uniform vec2 textureScale;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    outTextCoord = (textCoord * textureScale) + textureOffset;

    fragNormal = mat3(modelMatrix) * normal;
}