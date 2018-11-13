import com.jogamp.opengl.*;
import gmaths.*;
import lib.*;

/**
 * I declare that this code is my own work
 * A class for rendering the window
 *
 * @author Zer Jun Eng
 */
class Window {

  private Model windowFrame;
  private SGNode frameRoot;

  private float roomWidth, roomHeight, roomDepth;
  private float horizontalWidth, verticalHeight;
  private final float FRAME_DIM = Cube.THICKNESS / 2;

  // Dimension ratio of window with respect to room dimension
  static final Vec3 RATIO = new Vec3(0.45f, 0.39f, 0);
  static final float Y_POS = RATIO.y + 0.1f;

  /**
   * Window constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   */
  Window(Vec3 roomDimension, Model windowFrame) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.windowFrame = windowFrame;

    horizontalWidth = roomWidth * RATIO.x;
    verticalHeight = roomHeight * RATIO.y - 2 * FRAME_DIM; // 2 * FRAME_DIM for top and bot bar

    sceneGraph();
  }

  /**
   * Construct the main scene graph
   */
  private void sceneGraph() {
    final float POS_Z = -(roomDepth + Cube.THICKNESS) / 2;

    // Root
    frameRoot = new NameNode("Window frame structure");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(0, roomHeight * Y_POS, POS_Z));

    // Scene graph
    frameRoot.addChild(rootTranslate);
    createHorizontalBar(rootTranslate);

    frameRoot.update();
  }

  /**
   * Create three horizontal bars: bot, mid and top
   *
   * @param parent Parent node
   */
  private void createHorizontalBar(SGNode parent) {
    final Mat4 H_MAT = Mat4Transform.scale(horizontalWidth, FRAME_DIM, FRAME_DIM); // Horizontal mat

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

    parent.addChild(botH);
      botH.addChild(botHTransform);
        botHTransform.addChild(botHModel);
      createVerticalBar(botH);               // 3 vertical bars as children
    parent.addChild(midH);
      midH.addChild(midHTransform);
        midHTransform.addChild(midHModel);
    parent.addChild(topH);
      topH.addChild(topHTransform);
        topHTransform.addChild(topHModel);
  }

  /**
   * Create three vertical bars: left, mid and right
   *
   * @param parent Parent node
   */
  private void createVerticalBar(SGNode parent) {
    final Mat4 V_MAT = Mat4Transform.scale(FRAME_DIM, verticalHeight, FRAME_DIM);  // Vertical mat
    final float V_BAR_POS_X = (horizontalWidth - FRAME_DIM) / 2;
    final float V_BAR_POS_Y = FRAME_DIM + verticalHeight / 2;

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
      verticalTranslate.addChild(leftV);
        leftV.addChild(leftVTransform);
          leftVTransform.addChild(leftVModel);
      verticalTranslate.addChild(midV);
        midV.addChild(midVTransform);
          midVTransform.addChild(midVModel);
      verticalTranslate.addChild(rightV);
        rightV.addChild(rightVTransform);
          rightVTransform.addChild(rightVModel);
  }

  /**
   * Renders window
   *
   * @param gl OpenGL object, for rendering
   */
  void render(GL3 gl) {
    frameRoot.draw(gl);
  }
}
