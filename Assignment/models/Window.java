package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work
 * A class for rendering the window
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class Window {

  private SGNode windowRoot;

  private final Model windowFrame;
  private final Model glass;
  private final Model outsideScene;

  private final double startTime;

  private final float roomHeight;
  private final float roomDepth;
  private final float windowWidth;
  private final float windowHeight;
  private final float glassWidth;
  private final float glassHeight;
  private final Mat4 glassScale;
  private final float FRAME_DIM = Cube.THICKNESS / 2.5f;

  // Dimension ratio of window with respect to room dimension
  public static final Vec2 RATIO = new Vec2(0.45f, 0.39f);
  public static final float Y_POS = RATIO.y + 0.1f;

  /**
   * Window constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   * @param windowFrame Cube window frame model
   * @param glass Cube glass model
   */
  public Window(Vec3 roomDimension, Model windowFrame, Model glass, Model outsideScene) {
    float roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.windowFrame = windowFrame;
    this.glass = glass;
    this.outsideScene = outsideScene;

    windowWidth = roomWidth * RATIO.x;
    windowHeight = roomHeight * RATIO.y - 2 * FRAME_DIM; // 2 * FRAME_DIM for top and bot bar

    glassWidth = windowWidth / 2 - FRAME_DIM;
    glassHeight = (windowHeight - FRAME_DIM) / 2;
    float glassDepth = FRAME_DIM / 8;
    glassScale = Mat4Transform.scale(glassWidth, glassHeight, glassDepth);
    startTime = getSeconds();
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    final float POS_Z = -(roomDepth + Cube.THICKNESS) / 2;

    windowRoot = new NameNode("Window frame structure");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(0, roomHeight * Y_POS, POS_Z));

    windowRoot.addChild(rootTranslate);
      createHorizontalBar(rootTranslate);

    windowRoot.update();
  }

  /**
   * Renders the window frame and glass
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    double elapsedTime = getSeconds() - startTime;
    double wavelength = elapsedTime * 0.5;
    double t = wavelength * 0.1;
    float cosine = (float) ((Math.cos(wavelength) + 1) * 0.5);
    float offset = (float) (t - Math.floor(t));

    outsideScene.setDayNightCycle(cosine);
    outsideScene.setMovingTexture(offset, offset * 2);
    windowRoot.draw(gl);
  }

  /**
   * Creates three horizontal bars: bot, mid and top
   *
   * @param parent Parent node
   */
  private void createHorizontalBar(SGNode parent) {
    final Mat4 H_MAT = Mat4Transform.scale(windowWidth, FRAME_DIM, FRAME_DIM); // Horizontal mat
    final float H_BAR_POS_Y = (windowHeight + FRAME_DIM) / 2;

    // Mid horizontal bar
    TransformNode midHTranslate = new TransformNode("Mid horizontal bars translate",
        Mat4Transform.translate(0, windowHeight / 2 + FRAME_DIM, 0));
    NameNode midH = new NameNode("Mid horizontal bar");
    TransformNode midHTransform = new TransformNode("midH Transform", H_MAT);
    ModelNode midHModel = new ModelNode("midH Model", windowFrame);

    // Bottom horizontal bar
    NameNode botH = new NameNode("Bottom horizontal bar");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(0, -H_BAR_POS_Y, 0), H_MAT);
    TransformNode botHTransform = new TransformNode("botH Transform", m);
    ModelNode botHModel = new ModelNode("botH Model", windowFrame);

    // Top horizontal bar
    NameNode topH = new NameNode("Top horizontal bar");
    m = Mat4.multiply(Mat4Transform.translate(0, H_BAR_POS_Y, 0), H_MAT);
    TransformNode topHTransform = new TransformNode("topH Transform", m);
    ModelNode topHModel = new ModelNode("topH Model", windowFrame);

    // Glasses have to be added at last otherwise transparency will override other models
    parent.addChild(midHTranslate);
      midHTranslate.addAllChildren(botH, botHTransform, botHModel);
      midHTranslate.addAllChildren(topH, topHTransform, topHModel);
      midHTranslate.addAllChildren(midH, midHTransform, midHModel);
        createVerticalBar(midH);                // 3 vertical bars as children
        createOutsideScene(midH);
        createGlasses(midH);                    // 4 glasses as children
  }

  /**
   * Creates three vertical bars: left, mid and right
   *
   * @param parent Parent node
   */
  private void createVerticalBar(SGNode parent) {
    final Mat4 V_MAT = Mat4Transform.scale(FRAME_DIM, windowHeight, FRAME_DIM);  // Vertical mat
    final float V_BAR_POS_X = (windowWidth - FRAME_DIM) / 2;

    // Left vertical bar - child of bottom horizontal bar
    NameNode leftV = new NameNode("Left vertical bar");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(-V_BAR_POS_X, 0, 0), V_MAT);
    TransformNode leftVTransform = new TransformNode("leftV Transform", m);
    ModelNode leftVModel = new ModelNode("leftV Model", windowFrame);

    // Mid vertical bar - child of bottom horizontal bar
    NameNode midV = new NameNode("Mid vertical bar");
    m = Mat4.multiply(Mat4Transform.translate(0, 0, 0), V_MAT);
    TransformNode midVTransform = new TransformNode("midV Transform", m);
    ModelNode midVModel = new ModelNode("midV Model", windowFrame);

    // Right vertical bar - child of bottom horizontal bar
    NameNode rightV = new NameNode("Right vertical bar");
    m = Mat4.multiply(Mat4Transform.translate(V_BAR_POS_X, 0, 0), V_MAT);
    TransformNode rightVTransform = new TransformNode("rightV Transform", m);
    ModelNode rightVModel = new ModelNode("rightV Model", windowFrame);

    parent.addAllChildren(leftV, leftVTransform, leftVModel);
    parent.addAllChildren(midV, midVTransform, midVModel);
    parent.addAllChildren(rightV, rightVTransform, rightVModel);
  }

  /**
   * Creates the bottom left, bottom right, top left, and top right glasses
   *
   * @param parent Parent node
   */
  private void createGlasses(SGNode parent) {
    final float POS_X = -glassWidth / 2;
    final float POS_Y = glassHeight / 2 + FRAME_DIM / 2;

    NameNode botLeftGlass = new NameNode("Bottom left glass");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(POS_X, -POS_Y, 0), glassScale);
    TransformNode botLeftGlassTransform = new TransformNode("Bottom left glass transform", m);
    ModelNode botLeftGlassModel = new ModelNode("Bottom left glass model", glass);

    NameNode botRightGlass = new NameNode("Bottom right glass");
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, -POS_Y, 0), glassScale);
    TransformNode botRightGlassTransform = new TransformNode("Bottom right glasss transform", m);
    ModelNode botRightGlassModel = new ModelNode("Bottom right glass model", glass);

    NameNode topLeftGlass = new NameNode("Top left glass");
    m = Mat4.multiply(Mat4Transform.translate(POS_X, POS_Y, 0), glassScale);
    TransformNode topLeftGlassTransform = new TransformNode("Top left glass transform", m);
    ModelNode topLeftGlassModel = new ModelNode("Top left glass model", glass);

    NameNode topRightGlass = new NameNode("Top right glass");
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, POS_Y, 0), glassScale);
    TransformNode topRightGlassTransform = new TransformNode("Top right glasss transform", m);
    ModelNode topRightGlassModel = new ModelNode("Top right glass model", glass);

    parent.addAllChildren(botLeftGlass, botLeftGlassTransform, botLeftGlassModel);
    parent.addAllChildren(botRightGlass, botRightGlassTransform, botRightGlassModel);
    parent.addAllChildren(topLeftGlass, topLeftGlassTransform, topLeftGlassModel);
    parent.addAllChildren(topRightGlass, topRightGlassTransform, topRightGlassModel);
  }

  /**
   * Outside scene: fuji mountain with snow
   *
   * @param parent Parent node
   */
  private void createOutsideScene(SGNode parent) {
    NameNode scene = new NameNode("Outside scene");
    Mat4 m = Mat4Transform.scale(windowWidth, 1, windowHeight + 2 * FRAME_DIM);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, -FRAME_DIM), m);
    TransformNode sceneTransform = new TransformNode("Outside scene transform", m);
    ModelNode sceneModel = new ModelNode("Outside scene model", outsideScene);

    parent.addAllChildren(scene, sceneTransform, sceneModel);
  }

  /**
   * Get the elapsed time in seconds
   *
   * @return The elapsed time in seconds
   */
  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }
}
