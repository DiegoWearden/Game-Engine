package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

    public boolean isInvisible = false;

    private static final float RUN_SPEED = 50;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;
    private static final float STRAFE_SPEED = 50;
    private static final float FLY_SPEED = 30;  // New constant: flight vertical speed

    private float currentSpeed = 0;
    private float currentStrafeSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;
    private boolean isFlying = false;         // New variable: indicates if the player is flying
    private boolean fKeyPressedLastFrame = false; // To toggle flight mode only once per key press

    // Reference to camera if needed (not used in this snippet)
    // Camera camera;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain, Camera camera) {
        checkInputs();
        float dt = DisplayManager.getFrameTimeSeconds();

        // Calculate horizontal movement
        float distance = currentSpeed * dt;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));

        float strafeDistance = currentStrafeSpeed * dt;
        float strafeX = (float) (strafeDistance * Math.sin(Math.toRadians(super.getRotY() + 90)));
        float strafeZ = (float) (strafeDistance * Math.cos(Math.toRadians(super.getRotY() + 90)));

        super.increasePosition(dx + strafeX, 0, dz + strafeZ);

        if (isFlying) {
            // In flying mode, allow vertical movement via SPACE (up) and LSHIFT (down)
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                super.increasePosition(0, FLY_SPEED * dt, 0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                super.increasePosition(0, -FLY_SPEED * dt, 0);
            }
            // Reset any vertical momentum when flying
            upwardsSpeed = 0;
        } else {
            // Not flying: apply gravity and jump mechanics
            upwardsSpeed += GRAVITY * dt;
            super.increasePosition(0, upwardsSpeed * dt, 0);
            float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
            if (super.getPosition().y < terrainHeight) {
                upwardsSpeed = 0;
                isInAir = false;
                super.getPosition().y = terrainHeight;
            }
        }
    }

    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs() {
        // Forward/backward movement
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        // Strafing left/right
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            this.currentStrafeSpeed = -STRAFE_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentStrafeSpeed = STRAFE_SPEED;
        } else {
            this.currentStrafeSpeed = 0;
        }

        // Jump only when not flying
        if (!isFlying && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump();
        }

        // Toggle flight mode with the F key
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
            if (!fKeyPressedLastFrame) {
                isFlying = !isFlying; // Toggle flight mode
                if (isFlying) {
                    // When starting flight, reset vertical speed
                    upwardsSpeed = 0;
                }
            }
            fKeyPressedLastFrame = true;
        } else {
            fKeyPressedLastFrame = false;
        }
    }
}
