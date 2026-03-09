package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera{
    private final Vector3f position;
    private float pitch = 20.0f;
    private float yaw = 0.0f;

    private final Matrix4f projectionMatrix;


    public Camera(float x, float y, float z){
        this.position = new Vector3f(x, y, z);
        pitch = 0.0f;
        yaw = 0.0f;

        this.projectionMatrix = new Matrix4f();
    }

    public void setPosition(float x, float y, float z){
        this.position.set(x, y, z);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        position.x += offsetX;
        position.y += offsetY;
        position.z += offsetZ;
    }

    public Matrix4f getViewMatrix(Vector3f playerPosition){
        Vector3f up = new Vector3f(0f, 1f, 0f);
        float targetHeightOffset = 2.5f;
        Vector3f target = new Vector3f(playerPosition.x, playerPosition.y + targetHeightOffset, playerPosition.z);

        Matrix4f view = new Matrix4f();
        view.lookAt(this.position, target, up);
        return view;
    }

    public Matrix4f getProjectionMatrix(float width, float height){
        float aspectRatio = width / height;
        projectionMatrix.identity().perspective((float) Math.toRadians(60.0f), aspectRatio, 0.1f, 100.0f);
        return projectionMatrix;
    }

    public void rotate(float dpitch, float dyaw){
        pitch += dpitch;
        pitch = Math.min(Math.max(pitch, -89.0f), 89.0f);
        yaw += dyaw;
        yaw %= 360.0f;
    }

    public void updateOrbit(Vector3f playerPosition) {
        float pitchRad = (float) Math.toRadians(pitch);
        float yawRad = (float) Math.toRadians(yaw);

        float distanceFromPlayer = 10.0f;
        float horizontalDistance = (float) (distanceFromPlayer * Math.cos(pitchRad));
        float verticalDistance = (float) (distanceFromPlayer * Math.sin(pitchRad));

        float offsetX = (float) (horizontalDistance * Math.sin(yawRad));
        float offsetZ = (float) (horizontalDistance * Math.cos(yawRad));

        this.position.x = playerPosition.x - offsetX;
        this.position.y = playerPosition.y + verticalDistance;
        this.position.z = playerPosition.z - offsetZ;
    }

    public void move(float dx, float dy, float dz){
         position.add(dx, dy, dz);
     }

    public float getYaw(){
        return yaw;
    }

    public void setYaw(float yaw){
        this.yaw = yaw;
    }

    public void setRotation(float pitch, float yaw){
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
