package engine.core;

import engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    private final List<Enemy> enemies = new ArrayList<>();

    public void spawnEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void update(double deltaTime, Vector3f playerPos) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, playerPos);
        }
    }

    public void render(ShaderProgram shader, Matrix4f modelMatrix) {
        for (Enemy enemy : enemies) {
            enemy.getEntity().render(shader, modelMatrix);
        }
    }

    public void clear() {
        enemies.clear();
    }
}