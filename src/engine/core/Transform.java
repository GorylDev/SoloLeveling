package engine.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

    Vector3f position = new Vector3f(0, 0, 0);
    Vector3f rotation = new Vector3f(0, 0, 0);
    Vector3f scale = new Vector3f(1, 1, 1);

    public void getModelMatrix(Matrix4f dest){
        dest.identity();

        dest.translate(position);
        dest.rotateX((float) Math.toRadians(rotation.x));
        dest.rotateY((float) Math.toRadians(rotation.y));
        dest.rotateZ((float) Math.toRadians(rotation.z));
        dest.scale(scale);
    }
}
