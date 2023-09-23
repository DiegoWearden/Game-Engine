package engineTester;

import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import org.lwjgl.util.vector.Vector4f;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) throws LWJGLException {

		DisplayManager.createDisplay();
		Loader loader = new Loader();


		//**********TERRAIN TEXTURE STUFF************

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		//************************************************************

		RawModel model = OBJLoader.loadObjModel("grassModel", loader);
		
		TexturedModel grass = new TexturedModel(model,new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("tree", loader), new ModelTexture(loader.loadTexture("tree")));
		tree.getTexture().setShineDamper(100);
		tree.getTexture().setReflectivity(1);

		Terrain terrain = new Terrain(0,-1,loader,texturePack, blendMap, "heightmap");
		List<Terrain> terrains = new ArrayList<>();
		terrains.add(terrain);
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
		List<Entity> entities = new ArrayList<>();
		Random random = new Random();
		for(int i=0;i<500;i++){
			float x = random.nextFloat()*800 - 400;
			float z = random.nextFloat() * -600;
//			entities.add(new Entity(grass, new Vector3f(x,terrain.getHeightOfTerrain(x, z),z),0,0,0,3));
			if(terrain.isBackgroundColor(x, z)){
				entities.add(new Entity(tree, new Vector3f(x,terrain.getHeightOfTerrain(x, z),z),0,0,0,3));
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, terrain.getHeightOfTerrain(x, z), z), 0, random.nextFloat(), 0, 0.9f));
			}
		}
		
		Entity entity = new Entity(grass, new Vector3f(0,0,-25),0,0,0,1);
		List<Light> lights = new ArrayList<Light>();

		// OpenGL 3D Game Tutorial 25: Multiple Lights
		Light sun = new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f));
		lights.add(sun);
		TexturedModel lampModel = new TexturedModel(OBJLoader.loadObjModel("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
		lampModel.getTexture().setUseFakeLighting(true);
		float ex = -85;
		float ez = 93;
		float ey = terrain.getHeightOfTerrain(ex, ez);
		entities.add(new Entity(lampModel, new Vector3f(ex, ey, ez), 0, 0, 0, 1f));
		lights.add(new Light(new Vector3f(ex, ey+14, ez), new Vector3f(2, 1, 1), new Vector3f(1, 0.01f, 0.002f)));

		ex = -70;
		ez = 200;
		ey = terrain.getHeightOfTerrain(ex, ez);
		entities.add(new Entity(lampModel, new Vector3f(ex, ey, ez), 0, 0, 0, 1f));
		lights.add(new Light(new Vector3f(ex, ey+14, ez), new Vector3f(1, 2, 0), new Vector3f(1, 0.01f, 0.002f)));

		ex = 395;
		ez = -372;
		ey = terrain.getHeightOfTerrain(ex, ez);
		entities.add(new Entity(lampModel, new Vector3f(ex, ey, ez), 0, 0, 0, 1f));
		lights.add(new Light(new Vector3f(ex, ey+14, ez), new Vector3f(0, 1, 2), new Vector3f(1, 0.01f, 0.002f)));


		MasterRenderer renderer = new MasterRenderer(loader);

		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel playerTexturedModel = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
		playerTexturedModel.getTexture().setShineDamper(10);
		playerTexturedModel.getTexture().setReflectivity(10);
		List<GuiTexture> guis = new ArrayList<>();
//		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.05f, 0.05f), new Vector2f(0.25f, 0.25f));
//		guis.add(gui);
//		GuiRenderer guiRenderer = new GuiRenderer(loader);
//		lights.add(new Light(new Vector3f(200, 10, 3), new Vector3f(10, 0, 0)));
//		lights.add(new Light(new Vector3f(200, 10, 300), new Vector3f(0, 0, 10)));
		Player player = new Player(playerTexturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 0.5f);
		Camera camera = new Camera(player);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		WaterFrameBuffers waterFrameBuffers = new WaterFrameBuffers();

		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFrameBuffers);
		List<WaterTile> waters = new ArrayList<>();
		WaterTile water = new WaterTile(75, -75, 0);
		waters.add(water);
//		GuiRenderer guiRenderer = new GuiRenderer(loader);
//		GuiTexture refraction = new GuiTexture(waterFrameBuffers.getRefractionTexture(), new Vector2f(-0.8f, 0.8f), new Vector2f(0.2f, 0.2f));
//		GuiTexture reflection = new GuiTexture(waterFrameBuffers.getReflectionTexture(), new Vector2f(0.8f, 0.8f), new Vector2f(0.2f, 0.2f));
//		guis.add(refraction);
//		guis.add(reflection);
		while(!Display.isCloseRequested()){
			entity.increaseRotation(0, 0, 0);
			camera.move();
			picker.update();

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			player.move(terrain, camera);

			//render reflection texture
			waterFrameBuffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1));
			if(!player.isInvisible){
				renderer.processEntity(player);
			}
			camera.getPosition().y +=distance;
			camera.invertPitch();

			//render refraction texture
			waterFrameBuffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
			if(!player.isInvisible){
				renderer.processEntity(player);
			}

			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			waterFrameBuffers.unbindCurrentFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, 1000000));
			if(!player.isInvisible){
				renderer.processEntity(player);
			}
			waterRenderer.render(waters, camera, sun);
//			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
//		guiRenderer.cleanUp();
		waterFrameBuffers.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
