import org.joml.Vector3f;

public class Camera {
        private final Vector3f position;
            private float pitch;
            private float yaw;

    public Camera(float x, float y, float z) {
         this.position = new Vector3f(x, y, z);
         pitch = 0.0f;
         yaw = 0.0f;
     }

     public void rotate(float dpitch, float dyaw){
         pitch += dpitch;
         pitch = Math.min(Math.max(pitch, -89.0f), 89.0f);
         yaw += dyaw;
         yaw %= 360.0f;
     }

     public void move(float dx, float dy, float dz) {
         position.add(dx, dy, dz);
     }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z); // JOML's way of overwriting the vector
    }

    public void setRotation(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
