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

  private final float roomWidth;
  private final float roomHeight;
  private final float roomDepth;
  private final float windowWidth;
  private final float windowHeight;
  private final float glassWidth;
  private final float glassHeight;
  private final float glassDepth;
  private final Mat4 glassScale;
  private final float FRAME_DIM = Cube.THICKNESS / 2;

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
  public Window(Vec3 roomDimension, Model windowFrame, Model glass) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.windowFrame = windowFrame;
    this.glass = glass;

    windowWidth = roomWidth * RATIO.x;
    windowHeight = roomHeight * RATIO.y - 2 * FRAME_DIM; // 2 * FRAME_DIM for top and bot bar

    glassWidth = windowWidth / 2 - FRAME_DIM;
    glassHeight = (windowHeight - FRAME_DIM) / 2;
    glassDepth = FRAME_DIM / 8;
    glassScale = Mat4Transform.scale(glassWidth, glassHeight, glassDepth);
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
    windowRoot.draw(gl);
  }

  /**
   * Creates three horizontal bars: bot, mid and top
   *
   * @param parent Parent node
   */
  private void createHorizontalBar(SGNode parent) {
    final Mat4 H_MAT = Mat4Transform.scale(windowWidth, FRAME_DIM, FRAME_DIM); // Horizontal mat

    // Bottom horizontal bar
    NameNode botH = new NameNode("Bottom horizontal bar");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(0, FRAME_DIM / 2, 0), H_MAT);
    TransformNode botHTransform = new TransformNode("botH Transform", m);
    ModelNode botHModel = new ModelNode("botH Model", windowFrame);

    // Mid horizontal bar
    NameNode midH = new NameNode("Mid horizontal bar");
    m = Mat4.multiply(Mat4Transform.translate(0, RATIO.y * roomHeight / 2, 0), H_MAT);
    TransformNode midHTransform = new TransformNode("midH Transform", m);
    ModelNode midHModel = new ModelNode("midH Model", windowFrame);

    // Top horizontal bar
    NameNode topH = new NameNode("Top horizontal bar");
    m = Mat4.multiply(
        Mat4Transform.translate(0, RATIO.y * roomHeight - FRAME_DIM / 2, 0), H_MAT);
    TransformNode topHTransform = new TransformNode("topH Transform", m);
    ModelNode topHModel = new ModelNode("topH Model", windowFrame);

    parent.addAllChildren(botH, botHTransform, botHModel);
      createVerticalBar(botH);               // 3 vertical bars as children
    parent.addAllChildren(midH, midHTransform, midHModel);
    parent.addAllChildren(topH, topHTransform, topHModel);

    // Glasses have to be added at last for transparency to fully work with window frames
    createBottomGlass(parent);               // 2 bottom glasses as children
    createTopGlass(parent);                  // 2 top glasses as children
  }

  /**
   * Creates three vertical bars: left, mid and right
   *
   * @param parent Parent node
   */
  private void createVerticalBar(SGNode parent) {
    final Mat4 V_MAT = Mat4Transform.scale(FRAME_DIM, windowHeight, FRAME_DIM);  // Vertical mat
    final float V_BAR_POS_X = (windowWidth - FRAME_DIM) / 2;
    final float V_BAR_POS_Y = FRAME_DIM + windowHeight / 2;

    TransformNode verticalTranslate = new TransformNode("Vertical translate",
        Mat4Transform.translate(0, V_BAR_POS_Y, 0));

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

    parent.addChild(verticalTranslate);
      verticalTranslate.addAllChildren(leftV, leftVTransform, leftVModel);
      verticalTranslate.addAllChildren(midV, midVTransform, midVModel);
      verticalTranslate.addAllChildren(rightV, rightVTransform, rightVModel);
  }

  /**
   * Creates the bottom left and bottom right glass
   *
   * @param parent Parent node
   */
  private void createBottomGlass(SGNode parent) {
    final float POS_X = -glassWidth / 2;
    final float POS_Y = glassHeight / 2 + FRAME_DIM;

    NameNode leftGlass = new NameNode("Bottom left glass");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(POS_X, POS_Y, 0), glassScale);
    TransformNode leftGlassTransform = new TransformNode("Bottom left glass transform", m);
    ModelNode leftGlassModel = new ModelNode("Bottom left glass model", glass);

    NameNode rightGlass = new NameNode("Bottom right glass");
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, POS_Y, 0), glassScale);
    TransformNode rightGlassTransform = new TransformNode("Bottom right glasss transform", m);
    ModelNode rightGlassModel = new ModelNode("Bottom right glass model", glass);

    parent.addAllChildren(leftGlass, leftGlassTransform, leftGlassModel);
    parent.addAllChildren(rightGlass, rightGlassTransform, rightGlassModel);
  }

  /**
   * Creates the top left and top right glass
   *
   * @param parent Parent node
   */
  private void createTopGlass(SGNode parent) {
    final float POS_X = -glassWidth / 2;
    final float POS_Y = glassHeight * 2 - FRAME_DIM - 0.05f; // 0.05 offset for floating point error

    NameNode leftGlass = new NameNode("Top left glass");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(POS_X, POS_Y, 0), glassScale);
    TransformNode leftGlassTransform = new TransformNode("Top left glass transform", m);
    ModelNode leftGlassModel = new ModelNode("Top left glass model", glass);

    NameNode rightGlass = new NameNode("Top right glass");
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, POS_Y, 0), glassScale);
    TransformNode rightGlassTransform = new TransformNode("Top right glasss transform", m);
    ModelNode rightGlassModel = new ModelNode("Top right glass model", glass);

    parent.addAllChildren(leftGlass, leftGlassTransform, leftGlassModel);
    parent.addAllChildren(rightGlass, rightGlassTransform, rightGlassModel);
  }
}
