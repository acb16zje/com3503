import com.jogamp.opengl.*;
import lib.gmaths.*;
import java.util.*;
import lib.*;
import models.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 *
 * @author Zer Jun Eng
 */
public class Anilamp_GLEventListener implements GLEventListener {
  Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
  }

  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);    // default is 'back', assuming CCW
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnable( GL.GL_BLEND );
    initialise(gl);
    startTime = getSeconds();
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

    // Dispose without typing one by one
    for (Light light : lightList) {
      light.dispose(gl);
    }

    for (Model model : modelList) {
      model.dispose(gl);
    }
  }

  // ***************************************************
  /* TIME */

  private double startTime;

  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }

  // ***************************************************
  /* INTERACTION */

  private boolean animation = true;
  private double savedTime = 0;

  public void startAnimation() {
    animation = true;
    startTime = getSeconds() - savedTime;
  }

  public void stopAnimation() {
    animation = false;
    savedTime = getSeconds() - startTime;
  }

  // ***************************************************
  /* THE SCENE */

  private Camera camera;
  private Light lampLight, innerWorldLight, outerWorldLight;
  private Model floor;                                                         // Floor
  private Model topWall, bottomWall, leftWall, rightWall;                      // Wall
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper;  // Wallpaper
  private Model windowFrame, glass;                                            // Window frame
  private Model tableFrame, drawerGaps, drawerHandle;                          // Table
  private Model cylinder, sphere, cone;                                        // Lamp

  private List<Light> lightList = new ArrayList<>();
  private List<Light> worldLightList;
  private List<Model> modelList;

  private Room room;
  private Window window;
  private Table table;
  private Lamp lamp;

  // Room dimension (width, height, depth)
  private final Vec3 ROOM_DIMENSION = new Vec3(20f, 20f, 20f);

  // Turn light off and on
  boolean lightIsOn = true;
  private final Vec3 LIGHT_ON = new Vec3(0.8f, 0.8f, 0.8f);
  private final Vec3 LIGHT_OFF = new Vec3(0, 0, 0);

  // Constant mesh, shaders
  private Mesh cubeMesh, cylinderMesh, sphereMesh;
  private Shader cubeShader, sphereShader, twoTriangleShader;

  private void initialise(GL3 gl) {
    // Create constant mesh, shaders
    createConstants(gl);

    // Create world lights
    innerWorldLight = new Light(gl, camera, Sphere.vertices.clone(), Sphere.indices.clone());
    innerWorldLight.setPosition(0, ROOM_DIMENSION.y, 0);

    outerWorldLight = new Light(gl, camera, Sphere.vertices.clone(), Sphere.indices.clone());
    outerWorldLight.setPosition(0, ROOM_DIMENSION.y, -ROOM_DIMENSION.z);

    // Create lamp light (spotlight)
    lampLight = new Spotlight(gl, camera, Sphere.vertices.clone(), Sphere.indices.clone());

    // Create the required models first
    modelFloor(gl);
    modelWall(gl);
    modelWallpaper(gl);
    modelWindow(gl);
    modelTable(gl);
    modelLamp(gl);

    // Room
    room = new Room(ROOM_DIMENSION, floor);
    room.new Wall(topWall, bottomWall, leftWall, rightWall);
    room.new Wallpaper(topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper);

    // Window
    window = new Window(ROOM_DIMENSION, windowFrame, glass);

    // Table
    table = new Table(ROOM_DIMENSION, tableFrame, drawerGaps, drawerHandle);

    // Add all lights to list for disposal management
    worldLightList = List.of(innerWorldLight, outerWorldLight);
    lightList.addAll(worldLightList);
    // lightList.add(lampLight);

    // Add all models to list for disposal management
    modelList = List.of(floor, leftWallpaper, rightWallpaper, topWallpaper, bottomWallpaper,
        windowFrame, tableFrame);
  }

  /**
   * Render all scenes
   *
   * @param gl OpenGL object, for rendering
   */
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    for (Light light : lightList) {
      light.render(gl);
    }

    room.render(gl);
    table.render(gl);
    window.render(gl);
  }

  /**
   * Creates constant meshes, shaders
   *
   * @param gl OpenGL object
   */
  private void createConstants(GL3 gl) {
    cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    cylinderMesh = new Mesh(gl, Cylinder.vertices.clone(), Cylinder.indices.clone());
    sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

    cubeShader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    // sphereShader = new Shader(gl, "shaders/vs_sphere.txt", "shaders/fs_sphere.txt");
    twoTriangleShader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
  }

  // ***************************************************
  /* LIGHTS */

  /**
   * Sets the world light on or off
   */
  void setOnOff() {
    for (Light worldLight : worldLightList) {
      Material m = worldLight.getMaterial();

      if (lightIsOn) {
        worldLight.setLightColor(0.5f);
        m.setDiffuse(LIGHT_OFF);
        m.setSpecular(LIGHT_OFF);
      } else {
        worldLight.setLightColor(1);
        m.setDiffuse(LIGHT_ON);
        m.setSpecular(LIGHT_ON);
      }
    }

    lightIsOn = !lightIsOn;
  }

  /**
   * Sets the intensity of the world lights
   *
   * @param intensity The intensity spinner value
   */
  void setIntensity(float intensity) {
    for (Light worldLight : worldLightList) {
      Material m = worldLight.getMaterial();

      m.setDiffuse(Vec3.multiply(new Vec3(1, 1, 1), intensity));
      m.setSpecular(Vec3.multiply(new Vec3(1, 1, 1), intensity));
      worldLight.setLightColor(1 - ((1 - intensity) / 2));
    }
  }

  // ***************************************************
  /* MODELS */

  /**
   * Creates the model of floor
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelFloor(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/floor_specular.jpg");

    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    floor = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  /**
   * Creates the model of wall
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWall(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wall_specular.jpg");

    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    topWall = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    bottomWall = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    leftWall = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
    rightWall = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);
  }

  /**
   * Creates the model of wallpaper
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWallpaper(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wallpaper.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wallpaper_specular.jpg");

    float[] vertices = TwoTriangles.vertices.clone();
    int[] indices = TwoTriangles.indices.clone();

    // Texture coordinates for wallpaper
    final float[] topTexCoords = {
        Window.RATIO.x, 1, Window.RATIO.x,                    // top left
        Window.Y_POS + Window.RATIO.y,                        // bottom left
        1 - Window.RATIO.x, Window.Y_POS + Window.RATIO.y,    // bottom right
        1 - Window.RATIO.x, 1                                 // top right
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

    topWallpaper = new Model(camera, lightList, twoTriangleShader, material, topMesh, DIFFUSE, SPECULAR);
    bottomWallpaper = new Model(camera, lightList, twoTriangleShader, material, bottomMesh, DIFFUSE, SPECULAR);
    leftWallpaper = new Model(camera, lightList, twoTriangleShader, material, leftMesh, DIFFUSE, SPECULAR);
    rightWallpaper = new Model(camera, lightList, twoTriangleShader, material, rightMesh, DIFFUSE, SPECULAR);
  }

  /**
   * Creates the model of window
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWindow(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/window_frame.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/window_frame_specular.jpg");
    final int[] GLASS_DIFFUSE = TextureLibrary.loadTexture(gl, "textures/glass.jpg");
    final int[] GLASS_SPECULAR = TextureLibrary.loadTexture(gl, "textures/glass_specular.jpg");

    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    windowFrame = new Model(camera, lightList, cubeShader, material, cubeMesh, DIFFUSE, SPECULAR);

    // Transparency glass
    Shader shader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_glass.txt");
    glass = new Model(camera, lightList, shader, material, cubeMesh, GLASS_DIFFUSE, GLASS_SPECULAR);
  }

  /**
   * Creates the model of table
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
   * Creates the model of lamp
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelLamp(GL3 gl) {
    // final int
  }
}
