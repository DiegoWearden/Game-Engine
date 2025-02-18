package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Camera {

	private float distanceFromPlayer = 50;
	// Removed angleAroundPlayer variable since we'll update player's rotation directly

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20; // starting pitch (vertical angle)
	private float yaw = 0;
	private float roll = 0;

	private boolean isFirstPersonView = false;
	private Player player;

	public Camera(Player player) {
		this.player = player;
		Mouse.setGrabbed(true); // Start with the mouse locked for smooth relative movement
	}

	public void move() {
		// Toggle view mode (first-person / third-person) with the M key
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			isFirstPersonView = !isFirstPersonView;
			switchView();
			try {
				Thread.sleep(100); // Small delay to avoid rapid toggling
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (Display.isActive()) {
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();

			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();

			if (!isFirstPersonView) {
				calculateCameraPosition(horizontalDistance, verticalDistance);
			} else {
				calculateCameraPositionFP();
			}

			// Toggle mouse visibility based on whether ESC is held (ESC releases mouse grab)
			setMouseVisibility(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE));

			// Update yaw using the player's rotation.
			yaw = 180 - player.getRotY();
		}
	}

	private void calculateCameraPositionFP() {
		// First-person view: position is directly at the player's location (with a vertical offset)
		position.x = player.getPosition().x;
		position.y = player.getPosition().y + 5; // Adjust for eye-level height
		position.z = player.getPosition().z;
	}

	public void switchView() {
		if (isFirstPersonView) {
			player.isInvisible = true;
			distanceFromPlayer = 0;
		} else {
			distanceFromPlayer = 50;
			player.isInvisible = false;
		}
	}

	public void setMouseVisibility(boolean visible) {
		// When visible is true, we want to release the mouse; otherwise, lock it.
		Mouse.setGrabbed(!visible);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		// Use player's rotation directly.
		float theta = player.getRotY();
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		// Clamp zoom range (adjust values as needed)
		distanceFromPlayer = Math.max(5, Math.min(100, distanceFromPlayer));
	}

	private void calculatePitch() {
		if (Mouse.isGrabbed()) {
			float pitchSensitivity = 10.0f; // Sensitivity can be tweaked
			float deltaTime = DisplayManager.getDeltaTime(); // Delta time in seconds
			float pitchChange = Mouse.getDY() * pitchSensitivity * deltaTime;
			pitch -= pitchChange;
			// Clamp pitch to avoid flipping the camera
			pitch = Math.max(-90, Math.min(90, pitch));
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Mouse.isGrabbed()) {
			float angleSensitivity = 10.0f; // Increase sensitivity as needed
			float deltaTime = DisplayManager.getDeltaTime();
			float angleChange = Mouse.getDX() * angleSensitivity * deltaTime;
			// Invert the horizontal movement: subtract angleChange instead of adding
			player.setRotY(player.getRotY() - angleChange);
		}
	}

	public void invertPitch() {
		pitch = -pitch;
	}
}
