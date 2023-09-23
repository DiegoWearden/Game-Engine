package skybox;

import entities.Camera;
import models.RawModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;

import java.util.Calendar;

public class SkyboxRenderer {
    private static final float SIZE = 500f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };
    private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
    private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
    private int nightTexture;

    private RawModel cube;
    private int texture;
    private SkyboxShader shader;
    private float time = 0;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
        cube = loader.loadToVAO(VERTICES, 3);
        texture = loader.loadCubeMap(TEXTURE_FILES);
        nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, float r, float g, float b){
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColor(r, g, b);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures() {
        Calendar calendar = Calendar.getInstance();
        long hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        long minute = calendar.get(Calendar.MINUTE);

        // Scales the time up to a 0-24000 system rather than 0-24.
        time = (hourOfDay * 1000) + ((minute * 1000) / 60);
        int texture1;
        int texture2;
        float blendFactor;
        if (time >= 0 && time < 5000) {
            texture1 = nightTexture;
            texture2 = nightTexture;
            blendFactor = (time - 0) / (5000);
        } else if (time >= 5000 && time < 8000) {
            texture1 = nightTexture;
            texture2 = texture;
            blendFactor = (time - 5000) / (8000 - 5000);
        } else if (time >= 8000 && time < 21000) {
            texture1 = texture;
            texture2 = texture;
            blendFactor = (time - 8000) / (21000 - 8000);
        } else {
            texture1 = texture;
            texture2 = nightTexture;
            blendFactor = (time - 21000) / (24000 - 21000);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(blendFactor);
    }
}
