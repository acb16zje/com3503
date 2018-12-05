import com.jogamp.opengl.*;
import java.util.*;
import lib.*;
import lib.gmaths.*;
import models.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * OpenGL event listener class
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
class Anilamp_GLEventListener implements GLEventListener {
  Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
  }

  // ***************************************************
  /* METHODS DEFINED BY GLEventListener */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LEQUAL);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);    // default is 'back', assuming CCW
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    initialise(gl);
  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float) width / (float) height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();

    for (Light light : lightList) {
      light.dispose(gl);
    }

    for (Model model : modelList) {
      model.dispose(gl);
    }
  }

  // ***************************************************
  /* THE SCENE */

  private final Camera camera;
  private Light lampLight;
  private Model floor;                                                         // Floor
  private Model wall;                                                          // Wall
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper;  // Wallpaper
  private Model tableFrame, drawerGaps, drawerHandle;                          // Table
  private Model frame, holder, picture;                                        // Picture frame
  private Model pot, cactus, flower;                                           // Cactus plant pot
  private Model body, nose, ear, leg, cubeDeco, sphereDeco;                    // Piggy bank
  private Model cube, cylinder, sphere, frustumCone;                           // Lamp
  private Model lampEar, lowerTail;                                            // Lamp decorations
  private Model windowFrame, glass, outsideScene;                         // Window and scene

  private List<Light> lightList;
  private List<Model> modelList;

  private Room room;
  private Window window;
  private Table table;
  private PictureFrame pictureFrame;
  private CactusPot cactusPot;
  private PiggyBank piggyBank;
  Lamp lamp;

  // Room dimension (width, height, depth)
  private final Vec3 ROOM_DIMENSION = new Vec3(20f, 20f, 20f);

  private void initialise(GL3 gl) {
    // Create constant mesh, shaders
    createConstants(gl);

    // Create world lights
    Light innerWorldLight = new Light(gl, camera);
    innerWorldLight.setPosition(0, ROOM_DIMENSION.y, 0);

    Light outerWorldLight = new Light(gl, camera);
    outerWorldLight.setPosition(0, ROOM_DIMENSION.y, -ROOM_DIMENSION.z);

    // Create lamp light (spotlight)
    lampLight = new Spotlight(gl, camera);

    // Add all lights to list for disposal management
    lightList = Arrays.asList(innerWorldLight, outerWorldLight, lampLight);

    // Create the required models first
    modelFloor(gl);
    modelWall(gl);
    modelWallpaper(gl);
    modelWindow(gl);
    modelTable(gl);
    modelPictureFrame(gl);
    modelCactusPot(gl);
    modelPiggyBank(gl);
    modelLamp(gl);

    // Add all models to list for disposal management
    modelList = Arrays.asList(floor, wall, topWallpaper, bottomWallpaper, leftWallpaper,
        rightWallpaper, tableFrame, drawerGaps, drawerHandle, frame, holder, picture, pot, cactus,
        flower, body, nose, ear, leg, cubeDeco, sphereDeco, cube, cylinder, sphere, frustumCone,
        lampEar, lowerTail, windowFrame, glass, outsideScene);

    // Room
    room = new Room(ROOM_DIMENSION, floor, wall);
    room.new Wallpaper(topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper);
    room.initialise();

    // Window
    window = new Window(ROOM_DIMENSION, windowFrame, glass, outsideScene);
    window.initialise();

    // Table
    table = new Table(ROOM_DIMENSION, tableFrame, drawerGaps, drawerHandle);
    table.initialise();

    // 3 Table accessories
    pictureFrame = new PictureFrame(frame, holder, picture);
    pictureFrame.initialise();
    cactusPot = new CactusPot(pot, cactus, flower);
    cactusPot.initialise();
    piggyBank = new PiggyBank(body, nose, ear, leg, cubeDeco, sphereDeco);
    piggyBank.initialise();

    // Desk lamp
    lamp = new Lamp(cube, cylinder, sphere, frustumCone, lampLight, lampEar, lowerTail);
    lamp.initialise();
  }

  /**
   * Render all scenes.
   *
   * @param gl OpenGL object, for rendering
   */
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    for (Light light : lightList) {
      light.render(gl);
    }

    // Animation controls
    // No further animations available until the current one is finished
    if (lamp.isAnimatingRandom || lamp.isAnimatingReset || lamp.isAnimatingJump) {
      Anilamp.random.setEnabled(false);
      Anilamp.reset.setEnabled(false);
      Anilamp.jump.setEnabled(false);
    } else {
      Anilamp.random.setEnabled(true);
      Anilamp.reset.setEnabled(true);
      Anilamp.jump.setEnabled(true);
    }

    room.render(gl);
    table.render(gl);
    pictureFrame.render(gl);
    cactusPot.render(gl);
    piggyBank.render(gl);
    lamp.render(gl);
    window.render(gl);
  }

  // ***************************************************
  /* CONSTANTS */

  private Mesh cubeMesh, cylinderMesh, frustumConeMesh, sphereMesh, twoTrianglesMesh;
  private Shader cubeShader, twoTrianglesShader;

  /**
   * Creates constant meshes, shaders.
   *
   * @param gl OpenGL object
   */
  private void createConstants(GL3 gl) {
    cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    cylinderMesh = new Mesh(gl, Cylinder.vertices.clone(), Cylinder.indices.clone());
    frustumConeMesh = new Mesh(gl, FrustumCone.vertices.clone(), FrustumCone.indices.clone());
    sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    twoTrianglesMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());

    cubeShader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    twoTrianglesShader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
  }

  // ***************************************************
  /* LIGHTS */

  // Turn light off and on
  boolean lightIsOn = true;
  boolean spotlightIsOn = true;

  /**
   * Sets the intensity of the world lights.
   *
   * @param intensity The intensity spinner value
   */
  void setIntensity(float intensity) {
    for (Light worldLight : lightList) {
      if (!worldLight.getClass().equals(Spotlight.class)) {
        Material m = worldLight.getMaterial();

        m.setDiffuse(Vec3.multiply(new Vec3(1, 1, 1), intensity));
        m.setSpecular(Vec3.multiply(new Vec3(1, 1, 1), intensity));
        worldLight.setLightColor(1 - (1 - intensity) / 2);
      }
    }
  }

  /**
   * Sets the spotlight on or off.
   */
  void setSpotlightOnOrOff() {
    if (spotlightIsOn) {
      lampLight.setSpotlightIntensity(0);
      lampLight.setLightColor(0.5f);
    } else {
      lampLight.setSpotlightIntensity(1);
      lampLight.setLightColor(1);
    }

    spotlightIsOn = !spotlightIsOn;
  }

  // ***************************************************
  /* MODELS */

  /**
   * Creates the model of floor.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelFloor(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/floor_specular.jpg");

    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 25f);
    floor = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  /**
   * Creates the model of wall.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWall(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wall_specular.jpg");

    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 25f);
    wall = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  /**
   * Creates the model of wallpaper.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWallpaper(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wallpaper.jpg");

    float[] vertices = TwoTriangles.vertices.clone();
    int[] indices = TwoTriangles.indices.clone();

    // Texture coordinates for wallpaper
    final float[] topTexCoords = {
        Window.RATIO.x, 1,                                                           // top left
        Window.RATIO.x, Window.Y_POS + Window.RATIO.y,                               // bottom left
        (1 - Window.RATIO.x) / 2 + Window.RATIO.x, Window.Y_POS + Window.RATIO.y,    // bottom right
        (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 1                                 // top right
    };

    final float[] bottomTexCoords = {
        (1 - Window.RATIO.x) / 2, Window.Y_POS,                     // top left
        (1 - Window.RATIO.x) / 2, 0,                                // bottom left
        (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 0,               // bottom right
        (1 - Window.RATIO.x) / 2 + Window.RATIO.x, Window.Y_POS     // top right
    };

    final float[] leftTexCoords = {
        0, 1,                                                 // top left
        0, 0,                                                 // bottom left
        (1 - Window.RATIO.x) / 2, 0,                          // bottom right
        (1 - Window.RATIO.x) / 2, 1                           // top right
    };

    final float[] rightTexCoords = {
        (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 1,         // top left
        (1 - Window.RATIO.x) / 2 + Window.RATIO.x, 0,         // bottom left
        1, 0,                                                 // bottom right
        1, 1                                                  // top right
    };

    Mesh topMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, topTexCoords), indices);
    Mesh bottomMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, bottomTexCoords), indices);
    Mesh leftMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, leftTexCoords), indices);
    Mesh rightMesh = new Mesh(gl, TwoTriangles.setTexCoords(vertices, rightTexCoords), indices);

    Material material = new Material(
        new Vec3(1f, 1f, 1f),
        new Vec3(1f, 1f, 1f),
        new Vec3(0.0f, 0.0f, 0.0f), 32f);
    topWallpaper = new Model(camera, lightList, twoTrianglesShader, material, topMesh, DIFFUSE);
    bottomWallpaper = new Model(camera, lightList, twoTrianglesShader, material, bottomMesh, DIFFUSE);
    leftWallpaper = new Model(camera, lightList, twoTrianglesShader, material, leftMesh, DIFFUSE);
    rightWallpaper = new Model(camera, lightList, twoTrianglesShader, material, rightMesh, DIFFUSE);
  }

  /**
   * Creates the model of window.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWindow(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/window_frame.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/window_frame_specular.jpg");
    final int[] GLASS_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/glass.jpg");
    final int[] GLASS_SPECULAR = TextureLibrary.loadTexture(gl, "textures/glass_specular.jpg");
    final int[] SCENE = TextureLibrary.loadTexture(gl, "textures/fuji.jpg");
    final int[] SNOW = TextureLibrary.loadTexture(gl, "textures/snow.jpg");

    Material material = new Material(
        new Vec3(1, 1, 1),
        new Vec3(1, 1, 1),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    windowFrame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);

    // Transparency glass
    Shader shader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_glass.txt");
    glass = new Model(camera, lightList, shader, material, cubeMesh, GLASS_DIFFUSE, GLASS_SPECULAR);

    shader = new Shader(gl, "shaders/vs_scene.txt", "shaders/fs_scene.txt");
    outsideScene = new Model(camera, lightList, shader, material, twoTrianglesMesh, SCENE, SNOW);
  }

  /**
   * Creates the model of table.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelTable(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/table.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/table_specular.jpg");
    final int[] GAPS = TextureLibrary.loadTexture(gl, "textures/gaps.jpg");
    final int[] HANDLE_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/drawer_handle.jpg");
    final int[] HANDLE_SPECULAR = TextureLibrary.loadTexture(gl, "textures/drawer_handle_specular.jpg");

    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    tableFrame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    drawerGaps = new Model(camera, lightList, cubeShader, material, cubeMesh, GAPS);
    drawerHandle = new Model(camera, lightList, cubeShader, material, cylinderMesh, HANDLE_DIFFUSE, HANDLE_SPECULAR);
  }

  /**
   * Creates a picture frame.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelPictureFrame(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/frame.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/frame_specular.jpg");
    final int[] PICTURE = TextureLibrary.loadTexture(gl, "textures/dog.jpg");
    final int[] HOLDER_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/window_frame.jpg");
    final int[] HOLDER_SPECULAR = TextureLibrary.loadTexture(gl, "textures/window_frame_specular.jpg");

    Material material = new Material(
        new Vec3(1, 1, 1),
        new Vec3(1, 1, 1),
        new Vec3(0, 0, 0), 32f);
    frame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    picture = new Model(camera, lightList, twoTrianglesShader, material, twoTrianglesMesh, PICTURE);
    holder = new Model(camera, lightList, cubeShader, material, cubeMesh, HOLDER_DIFFUSE, HOLDER_SPECULAR);
  }

  /**
   * Creates a cactus plant pot.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelCactusPot(GL3 gl) {
    final int[] POT_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/pot.jpg");
    final int[] POT_SPECULAR = TextureLibrary.loadTexture(gl, "textures/pot_specular.jpg");
    final int[] CACTUS_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/cactus.jpg");
    final int[] CACTUS_SPECULAR = TextureLibrary.loadTexture(gl, "textures/cactus_specular.jpg");
    final int[] FLOWER_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/flower.jpg");
    final int[] FLOWER_SPECULAR = TextureLibrary.loadTexture(gl, "textures/flower_specular.jpg");

    Material material = new Material(
        new Vec3(1, 1, 1),
        new Vec3(1, 1, 1),
        new Vec3(0, 0, 0), 32f);
    pot = new Model(camera, lightList, cubeShader, material, frustumConeMesh, POT_DIFFUSE, POT_SPECULAR);
    cactus = new Model(camera, lightList, cubeShader, material, sphereMesh, CACTUS_DIFFUSE, CACTUS_SPECULAR);
    flower = new Model(camera, lightList, cubeShader, material, sphereMesh, FLOWER_DIFFUSE, FLOWER_SPECULAR);
  }

  /**
   * Creates a piggy bank.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelPiggyBank(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/piggybank.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/piggybank_specular.jpg");
    final int[] DECO = TextureLibrary.loadTexture(gl, "textures/gaps.jpg");

    Material material = new Material(
        new Vec3(1, 1, 1),
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0), 32f);
    body = new Model(camera, lightList, cubeShader, material, sphereMesh, DIFFUSE, SPECULAR);
    nose = new Model(camera, lightList, cubeShader, material, cylinderMesh, DIFFUSE, SPECULAR);
    ear = new Model(camera, lightList, cubeShader, material, sphereMesh, DIFFUSE, SPECULAR);
    leg = new Model(camera, lightList, cubeShader, material, cylinderMesh, DIFFUSE, SPECULAR);

    // Black decorations
    cubeDeco = new Model(camera, lightList, cubeShader, material, cubeMesh, DECO);
    sphereDeco = new Model(camera, lightList, cubeShader, material, sphereMesh, DECO);
  }

  /**
   * Creates the model of lamp.
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelLamp(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/lamp.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/lamp_specular.jpg");
    final int[] JOINT = TextureLibrary.loadTexture(gl, "textures/lamp_joint.jpg");
    final int[] EAR = TextureLibrary.loadTexture(gl, "textures/lamp_ear.jpg");

    Material material = new Material(
        new Vec3(1, 1, 1),
        new Vec3(0, 0, 0),
        new Vec3(1, 1, 1), 32f);

    Mesh mesh = new Mesh(gl, FrustumCone.createVertices(true), FrustumCone.createIndices(false));

    cube = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    cylinder = new Model(camera, lightList, cubeShader, material, cylinderMesh, DIFFUSE, SPECULAR);
    sphere = new Model(camera, lightList, cubeShader, material, sphereMesh, JOINT, SPECULAR);
    frustumCone = new Model(camera, lightList, cubeShader, material, mesh, DIFFUSE, SPECULAR);

    // Decorations
    lampEar = new Model(camera, lightList, cubeShader, material, sphereMesh, EAR, SPECULAR);
    lowerTail = new Model(camera, lightList, cubeShader, material, cubeMesh, JOINT, SPECULAR);
  }
}
