package engine.core;

import engine.graphics.Animation;
import org.joml.Vector3f;

public class Enemy {
    private final AnimatedGameObject entity;
    private final Animation idle;
    private final Animation run;
    private final Animation attack;

    private final float agroRange = 15.0f;
    private final float attackRange = 2.5f;
    private final float speed = 3.5f;

    private int health = 100;
    private boolean isDead = false;

    public Enemy(AnimatedGameObject entity, Animation idle, Animation run, Animation attack) {
        this.entity = entity;
        this.idle = idle;
        this.run = run;
        this.attack = attack;
        this.entity.getAnimator().play(idle);
    }

    public void update(double deltaTime, Vector3f playerPos) {
        if (isDead) return;

        Vector3f pos = entity.getTransform().position;
        float dx = playerPos.x - pos.x;
        float dz = playerPos.z - pos.z;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);

        if (distance < attackRange) {
            handleAttack();
        } else if (distance < agroRange) {
            handleChase(deltaTime, dx, dz);
        } else {
            handleIdle();
        }

        entity.update(deltaTime);
    }

    private void handleIdle() {
        if (entity.getAnimator().getCurrentAnimation() != idle) {
            entity.getAnimator().play(idle);
        }
    }

    private void handleChase(double deltaTime, float dx, float dz) {
        if (entity.getAnimator().getCurrentAnimation() != run) {
            entity.getAnimator().play(run);
        }

        float angle = (float) Math.toDegrees(Math.atan2(dx, dz));
        entity.getTransform().rotation.y = angle;

        float length = (float) Math.sqrt(dx * dx + dz * dz);
        entity.getTransform().position.x += (dx / length) * speed * (float) deltaTime;
        entity.getTransform().position.z += (dz / length) * speed * (float) deltaTime;

        float terrainY = TerrainGenerator.getProceduralHeight(entity.getTransform().position.x, entity.getTransform().position.z);
        entity.getTransform().position.y = terrainY;
    }

    private void handleAttack() {
        if (entity.getAnimator().getCurrentAnimation() != attack) {
            entity.getAnimator().play(attack);
        }
    }

    public void takeDamage(int damage) {
        if (isDead) return;
        health -= damage;
        System.out.println("SYSTEM: Wróg otrzymał " + damage + " obrażeń. Pozostałe HP: " + health);
        if (health <= 0) {
            isDead = true;
            System.out.println("SYSTEM: Wróg został zgładzony!");
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public AnimatedGameObject getEntity() {
        return entity;
    }
}