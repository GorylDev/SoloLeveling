package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera{
    private final Vector3f position;
    private float pitch;
    private float yaw;

    private final Matrix4f projectionMatrix;


    public Camera(float x, float y, float z){
        this.position = new Vector3f(x, y, z);
        pitch = 0.0f;
        yaw = 0.0f;

        this.projectionMatrix = new Matrix4f();
    }

    public Matrix4f getViewMatrix(){

        Vector3f direction = new Vector3f(
                 (float) Math.cos(Math.toRadians(pitch)) * (float) Math.sin(Math.toRadians(yaw)),
                 (float) Math.sin(Math.toRadians(pitch)),
                 -(float) Math.cos(Math.toRadians(pitch)) * (float) Math.cos(Math.toRadians(yaw))
        );

        Vector3f target = new Vector3f(position).add(direction);

        Vector3f up = new Vector3f(0f, 1f, 0f);

        Matrix4f view = new Matrix4f();
        view.lookAt(position, target, up);
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

    public void move(float dx, float dy, float dz){
         position.add(dx, dy, dz);
     }

    public float getYaw(){
        return yaw;
    }

    public void setYaw(float yaw){
        this.yaw = yaw;
    }

    public void setPosition(float x, float y, float z){
        this.position.set(x, y, z);
    }

    public void setRotation(float pitch, float yaw){
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
