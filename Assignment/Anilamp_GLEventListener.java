import codeprovided.Camera;
import codeprovided.Light;
import codeprovided.Material;
import codeprovided.Mesh;
import codeprovided.Model;
import codeprovided.SGNode;
import codeprovided.Shader;
import codeprovided.TextureLibrary;
import codeprovided.TransformNode;
import codeprovided.TwoTriangles;
import com.jogamp.opengl.*;
import gmaths.*;
import java.util.*;

/**
 * I declare that this code is my own work.
 *
 * @author Zer Jun Eng
 */

public class Anilamp_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;

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
   * This will be added to in later examples.
   */

  private Camera camera;
  private Light light;
  private Model floor, topWall, bottomWall, leftWall, rightWall;
  private List<Model> modelList = new ArrayList<>();

  private RoomFrame roomFrame;

  // Room dimension (width, height, depth)
  private final Vec3 ROOM_DIMENSION = new Vec3(20f, 20f, 20f);

  private void initialise(GL3 gl) {
    // Add all models to list for easier disposal management
    modelList.add(floor);
    modelList.add(leftWall);
    modelList.add(rightWall);
    modelList.add(topWall);
    modelList.add(bottomWall);

    light = new Light(gl);
    light.setCamera(camera);

    // Pass components into room frame for positioning and rendering
    modelFloor(gl);
    modelWall(gl);
    roomFrame = new RoomFrame(ROOM_DIMENSION, floor, topWall, bottomWall, leftWall, rightWall);
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    roomFrame.render(gl);
  }

  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = 5.0f * (float) (Math.sin(Math.toRadians(elapsedTime * 50)));
    float y = 2.7f;
    float z = 5.0f * (float) (Math.cos(Math.toRadians(elapsedTime * 50)));
    return new Vec3(x, y, z);
//    return new Vec3(5f,3.4f,5f);
  }

  /**
   * Model the floor
   *
   * @param gl OpenGL object, for drawing
   */
  private void modelFloor(GL3 gl) {
    final int[] TEXTURE = TextureLibrary.loadTexture(gl, "textures/floor.jpg");

    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
    Material material = new Material(
        new Vec3(1f, 1f, 1f),
        new Vec3(1f, 1f, 1f),
        new Vec3(0.0f, 0.0f, 0.0f), 32.0f);
    floor = new Model(gl, camera, light, shader, material, new Mat4(), mesh, TEXTURE);
  }

  /**
   * Model the floor
   *
   * @param gl OpenGL object, for drawing
   */
  private void modelWall(GL3 gl) {
    final int[] TEXTURE = TextureLibrary.loadTexture(gl, "textures/wall.jpg");

    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
    Material material = new Material(
        new Vec3(1f, 1f, 1f),
        new Vec3(1f, 1f, 1f),
        new Vec3(0.0f, 0.0f, 0.0f), 32.0f);
    topWall = new Model(gl, camera, light, shader, material, new Mat4(), mesh, TEXTURE);
    bottomWall = new Model(gl, camera, light, shader, material, new Mat4(), mesh, TEXTURE);
    leftWall = new Model(gl, camera, light, shader, material, new Mat4(), mesh, TEXTURE);
    rightWall = new Model(gl, camera, light, shader, material, new Mat4(), mesh, TEXTURE);
  }
//
//  private void drawWall(GL3 gl) {
//    final float SIZE = 16f;
//    final int[] TEXTURE = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
//
//    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
//    Shader shader = new Shader(gl, "shaders/vs_tt.txt", "shaders/fs_tt.txt");
//    Material material = new Material(
//        new Vec3(0.0f, 0.5f, 0.81f),
//        new Vec3(0.0f, 0.5f, 0.81f),
//        new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
//    Mat4 modelMatrix = Mat4Transform.scale(6, 1f, SIZE);
//    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
//    modelMatrix = Mat4
//        .multiply(Mat4Transform.translate(-8, SIZE * 0.5f, -SIZE * 0.5f), modelMatrix);
//    leftWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, TEXTURE);
//
//    modelMatrix = new Mat4(1);
//    modelMatrix = Mat4.multiply(Mat4Transform.scale(10, 1f, 1), modelMatrix);
//    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
//    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, SIZE * 1f, -SIZE * 0.5f), modelMatrix);
//    rightWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, TEXTURE);
//  }
}
