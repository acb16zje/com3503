import codeprovided.*;
import com.jogamp.opengl.*;
import gmaths.*;
import java.util.*;

/**
 * I declare that this code is my own work.
 *
 * @author Zer Jun Eng
 */
public class Anilamp_GLEventListener implements GLEventListener {
  Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0, 10f, 22f));
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
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
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
    light.dispose(gl);

    // Dispose without typing one by one
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
  /* THE SCENE
   * Now define all the methods to handle the scene.
   */

  private Camera camera;
  private Light light;
  private Model floor;                                                         // Floor
  private Model topWall, bottomWall, leftWall, rightWall;                      // Wall
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper;  // Wallpaper
  private Model windowFrame;                                                   // Window frame
  private Model tableFrame;                                                    // Table
  private List<Model> modelList = new ArrayList<>();

  private Room room;
  private Window window;
  private Table table;

  // Empty matrix
  private final Mat4 M = new Mat4();

  // Room dimension (width, height, depth)
  private final Vec3 ROOM_DIMENSION = new Vec3(20f, 20f, 20f);

  private void initialise(GL3 gl) {
    light = new Light(gl);
    light.setCamera(camera);

    // Create the required models first
    modelFloor(gl);
    modelWall(gl);
    modelWallpaper(gl);
    modelWindow(gl);
    modelTable(gl);

    // Room
    room = new Room(ROOM_DIMENSION, floor);
    room.new Wall(topWall, bottomWall, leftWall, rightWall);
    room.new Wallpaper(topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper);

    // Window
    window = new Window(ROOM_DIMENSION, windowFrame);
    window.sceneGraph(gl);

    // Table
    table = new Table(ROOM_DIMENSION, tableFrame);
    table.sceneGraph(gl);

    // Add all models to list for disposal management
    modelList.add(floor);
    modelList.add(leftWallpaper);
    modelList.add(rightWallpaper);
    modelList.add(topWallpaper);
    modelList.add(bottomWallpaper);
    modelList.add(windowFrame);
    modelList.add(tableFrame);
  }

  /**
   * Render all scenes
   *
   * @param gl OpenGL object, for rendering
   */
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    room.render(gl);
    window.render(gl);
  }

  /**
   * Calculate the light's position each frame
   *
   * @return A 3D vector representing the light position
   */
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = 5.0f * (float) (Math.sin(Math.toRadians(elapsedTime * 50)));
    float y = 12.7f;
    float z = 5.0f * (float) (Math.cos(Math.toRadians(elapsedTime * 50)));
    return new Vec3(x, y, z);
    // return new Vec3(5f, 3.4f, 5f);
  }

  /**
   * Model the floor with diffuse and specular map
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelFloor(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/floor_specular.jpg");

    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    floor = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
  }

  /**
   * Model the wall
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWall(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wall_specular.jpg");

    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    topWall = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
    bottomWall = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
    leftWall = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
    rightWall = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
  }

  /**
   * Model the wallpaper
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWallpaper(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/wallpaper.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/wallpaper_specular.jpg");

    // Texture coordinates for wall and wallpaper
    final float[] topTexCoords = {
        Window.RATIO.x, 1, Window.RATIO.x,                    // top left
        Window.Y_POS + Window.RATIO.y,                        // bottom left
        1 - Window.RATIO.x, Window.Y_POS + Window.RATIO.y,    // bottom right
        1 - Window.RATIO.x, 1                                 // top right
    };

    final float[] bottomTexCoords = {
        Window.RATIO.x, Window.Y_POS,                         // top left
        Window.RATIO.x, 0,                                    // bottom left
        1 - Window.RATIO.x, 0,                                // bottom right
        1 - Window.RATIO.x, Window.Y_POS                      // top right
    };

    final float[] leftTexCoords = {
        0, 1,                                                 // top left
        0, 0,                                                 // bottom left
        Window.RATIO.x, 0,                                    // bottom right
        Window.RATIO.x, 1                                     // top right
    };

    final float[] rightTexCoords = {
        1 - Window.RATIO.x, 1,                                // top left
        1 - Window.RATIO.x, 0,                                // bottom left
        1, 0,                                                 // bottom right
        1, 1                                                  // top right
    };

    TwoTriangles top = new TwoTriangles(topTexCoords);
    TwoTriangles bottom = new TwoTriangles(bottomTexCoords);
    TwoTriangles left = new TwoTriangles(leftTexCoords);
    TwoTriangles right = new TwoTriangles(rightTexCoords);

    Mesh topMesh = new Mesh(gl, top.vertices.clone(), top.indices.clone());
    Mesh bottomMesh = new Mesh(gl, bottom.vertices.clone(), bottom.indices.clone());
    Mesh leftMesh = new Mesh(gl, left.vertices.clone(), left.indices.clone());
    Mesh rightMesh = new Mesh(gl, right.vertices.clone(), right.indices.clone());

    Shader shader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
    Material material = new Material(
        new Vec3(1f, 1f, 1f),
        new Vec3(1f, 1f, 1f),
        new Vec3(0.0f, 0.0f, 0.0f), 32f);

    topWallpaper = new Model(gl, camera, light, shader, material, M, topMesh, DIFFUSE, SPECULAR);
    bottomWallpaper = new Model(gl, camera, light, shader, material, M, bottomMesh, DIFFUSE, SPECULAR);
    leftWallpaper = new Model(gl, camera, light, shader, material, M, leftMesh, DIFFUSE, SPECULAR);
    rightWallpaper = new Model(gl, camera, light, shader, material, M, rightMesh, DIFFUSE, SPECULAR);
  }

  /**
   * Model the window
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelWindow(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/window_frame.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/window_frame_specular.jpg");

    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    windowFrame = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
  }

  /**
   * Model the table
   *
   * @param gl OpenGL object, for modelling
   */
  private void modelTable(GL3 gl) {
    final int[] DIFFUSE = TextureLibrary.loadTexture(gl, "textures/table.jpg");
    final int[] SPECULAR = TextureLibrary.loadTexture(gl, "textures/table_specular.jpg");

    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_cube.txt", "shaders/fs_cube.txt");
    Material material = new Material(
        new Vec3(0, 0, 0),
        new Vec3(0, 0, 0),
        new Vec3(0.3f, 0.3f, 0.3f), 30f);
    tableFrame = new Model(gl, camera, light, shader, material, M, mesh, DIFFUSE, SPECULAR);
  }
}
