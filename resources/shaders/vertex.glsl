#version 330 core

layout (location = 0) in vec3 position;

// UNIFORMS: Math matrices passed from Java
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix; //move and rotate the specific 3D object

void main() {
    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCamera = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCamera;
}