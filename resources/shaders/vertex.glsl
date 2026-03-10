#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textCoord;
layout (location=2) in vec3 normal;

out vec2 outTextCoord;
out vec3 fragNormal;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

//FOG PARAMETERS
const float density = 0.025; //START FOG
const float gradient = 1.5; //SHARPNESS

void main() {
    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;

    outTextCoord = textCoord;
    fragNormal = mat3(modelMatrix) * normal;

    //FOG
    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}