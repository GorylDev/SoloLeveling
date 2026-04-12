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
        enemies.removeIf(Enemy::isDead);

        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, playerPos);
        }
    }

    public void render(ShaderProgram shader, Matrix4f modelMatrix) {
        for (Enemy enemy : enemies) {
            enemy.getEntity().render(shader, modelMatrix);
        }
    }

    public boolean processPlayerAttack(Vector3f playerPos, float playerRotationY, float attackReach, float attackRadius, int damage) {
        float rotRad = (float) Math.toRadians(playerRotationY);
        float attackCenterX = playerPos.x + (float) Math.sin(rotRad) * attackReach;
        float attackCenterZ = playerPos.z + (float) Math.cos(rotRad) * attackReach;
        boolean hitAnything = false;

        for (Enemy enemy : enemies) {
            Vector3f enemyPos = enemy.getEntity().getTransform().position;
            float dx = enemyPos.x - attackCenterX;
            float dz = enemyPos.z - attackCenterZ;
            float distanceSquared = (dx * dx) + (dz * dz);

            if (distanceSquared <= (attackRadius * attackRadius)) {
                enemy.takeDamage(damage);
                hitAnything = true;

                // knockback
                float pushStrength = 2.5f;
                float dist = (float) Math.sqrt(distanceSquared);
                if (dist > 0.0f) {
                    enemyPos.x += (dx / dist) * pushStrength;
                    enemyPos.z += (dz / dist) * pushStrength;
                }
            }
        }
        return hitAnything;
    }

    public void clear() {
        enemies.clear();
    }
}