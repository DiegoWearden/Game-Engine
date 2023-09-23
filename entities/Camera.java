package entities;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private static float pitch;
	private static float yaw;
	private float roll;
	private boolean isFirstPersonView = false;

	private Player player;

	public Camera(Player player){
		this.player = player;
	}

	public void move(){
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			isFirstPersonView = !isFirstPersonView;  // flip the view mode
			switchView();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if(Display.isActive()){
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			if(!isFirstPersonView){
				calculateCameraPosition(horizontalDistance, verticalDistance);
			}else{
				calculateCameraPositionFP();
			}
			setMouseVisibility(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE));
			this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
			Mouse.setCursorPosition(DisplayManager.getWidth()/2, DisplayManager.getHeight()/2);
		}
	}

	private void calculateCameraPositionFP() {
		position.y = player.getPosition().y + 5;// Adjust camera height in first person view. Modify this value as needed.
		position.x = player.getPosition().x;
		position.z = player.getPosition().z;
	}

	public void switchView() {
		if (isFirstPersonView) {
			player.isInvisible = true;
			distanceFromPlayer = 0;  // for the first person view, the distance from the player should be zero. Modify this as needed.
		} else {
			distanceFromPlayer = 50;  // reset the distance for the third-person view.
			player.isInvisible = false;
		}
	}

	public void setMouseVisibility(boolean visible) {
		try {
			if (visible) {
				// Setting default system cursor
				Mouse.setNativeCursor(null);
			} else {
				// Create an invisible cursor (using a transparent 16x16 pixel image).
				Cursor invisibleCursor = new Cursor(16, 16, 0, 0, 1, BufferUtils.createIntBuffer(16 * 16), null);
				Mouse.setNativeCursor(invisibleCursor);
			}
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
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

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance;
	}

	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom(){
		float zoomSensitivity = 0.1f;
		float zoomLevel = Mouse.getDWheel() * zoomSensitivity;
		distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch(){
		float pitchSensitivity = 0.1f;
		int mouseY = Mouse.getY();
		int dy = mouseY - DisplayManager.getHeight() / 2;
		float pitchChange = dy * pitchSensitivity;
		pitch -= pitchChange;
		if (pitch < -90) {
			pitch = -90;
		} else if (pitch > 90) {
			pitch = 90;
		}
	}

	private void calculateAngleAroundPlayer() {
		float angleSensitivity = 0.3f;
		int mouseX = Mouse.getX();
		int dx = mouseX - DisplayManager.getWidth() / 2;
		float angleChange = dx * angleSensitivity; //0.3f is the sensitivity, you may want to adjust it
		this.player.setRotY(this.player.getRotY() - angleChange);
	}

	public void invertPitch(){
		this.pitch = -pitch;
	}

}
