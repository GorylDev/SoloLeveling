#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textCoord;
layout (location=2) in vec3 normal;
layout (location=3) in ivec4 boneIds;
layout (location=4) in vec4 weights;

out vec2 outTextCoord;
out vec3 fragNormal;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

const int MAX_BONES = 150;
uniform mat4 boneMatrices[MAX_BONES];

const float density = 0.025;
const float gradient = 1.5;

void main() {
    mat4 boneTransform = boneMatrices[boneIds[0]] * weights[0];
    boneTransform += boneMatrices[boneIds[1]] * weights[1];
    boneTransform += boneMatrices[boneIds[2]] * weights[2];
    boneTransform += boneMatrices[boneIds[3]] * weights[3];

    vec4 localPosition = boneTransform * vec4(position, 1.0);
    vec4 worldPosition = modelMatrix * localPosition;
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;

    outTextCoord = textCoord;
    fragNormal = mat3(modelMatrix) * mat3(boneTransform) * normal;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}