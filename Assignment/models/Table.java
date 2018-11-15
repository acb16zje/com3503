package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work
 * A class for rendering the table
 *
 * @author Zer Jun Eng
 */
public class Table {

  private Model tableFrame, drawerGaps, drawerHandle;

  private float roomWidth, roomHeight, roomDepth;
  public float tableWidth, tableHeight, tableDepth;
  private float drawerWidth, drawerHeight, drawerDepth, topDrawerHeight, bottomDrawerHeight;
  private float handleWidth, handleHeight, handleDepth, supportHeight;
  private float gapsWidth, gapsDepth;

  // Dimension ratio of table with respect to room dimension
  public static final Vec3 RATIO = new Vec3(0.679f, 0.39f, 0.286f);

  // Dimension ratio of drawers with respect to table dimension
  private final Vec3 DRAWER_RATIO = new Vec3(0.331f, 0.6f, 1);
  private final float TOP_DRAWER_HEIGHT_RATIO = 0.4f;

  // Width ratio of drawer handles width
  private final float HANDLE_HEIGHT_RATIO = 2f;      // with respect to draweer
  private final float SUPPORT_HEIGHT_RATIO = 0.15f;  // with respect to handle

  private final float GAPS_HEIGHT = 0.03f;
  private final float FRAME_DIM = Cube.THICKNESS / 4;

  public Table(Vec3 roomDimension, Model tableFrame, Model drawerGaps, Model drawerHandle) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.tableFrame = tableFrame;
    this.drawerGaps = drawerGaps;
    this.drawerHandle = drawerHandle;

    // Table
    tableWidth = roomWidth * RATIO.x;
    tableHeight = roomHeight * RATIO.y;
    tableDepth = roomDepth * RATIO.z;

    // Drawer
    drawerWidth = tableWidth * DRAWER_RATIO.x;
    drawerHeight = tableHeight * DRAWER_RATIO.y;
    drawerDepth = tableDepth * DRAWER_RATIO.z - FRAME_DIM;
    topDrawerHeight = drawerHeight * TOP_DRAWER_HEIGHT_RATIO;
    bottomDrawerHeight = drawerHeight - topDrawerHeight;

    // Drawer handle
    handleWidth = FRAME_DIM / 2;
    handleHeight = drawerHeight * HANDLE_HEIGHT_RATIO;
    handleDepth = handleWidth;
    supportHeight = handleHeight * SUPPORT_HEIGHT_RATIO;

    // Gaps
    gapsWidth = drawerWidth - GAPS_HEIGHT;
    gapsDepth = drawerDepth - GAPS_HEIGHT;
  }

  /**
   * Constructs the scene graph of table and renders it
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    final float POS_Z = -(roomDepth - tableDepth) / 2;

    // Root
    SGNode tableRoot = new NameNode("Table structure");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(0, (tableHeight + Cube.THICKNESS) / 2 , POS_Z));

    // Scene graph
    tableRoot.addChild(rootTranslate);
    createLegs(rootTranslate);

    tableRoot.update();

    tableRoot.draw(gl);
  }

  /**
   * Creates left and right legs of table
   *
   * @param parent Parent node
   */
  private void createLegs(SGNode parent) {
    final Mat4 LEG_MAT = Mat4Transform.scale(FRAME_DIM, tableHeight, tableDepth); // Legs matrix

    // Left leg
    NameNode leftLeg = new NameNode("Left leg");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(-tableWidth / 2, 0, 0), LEG_MAT);
    TransformNode leftLegTransform = new TransformNode("Left leg transform", m);
    ModelNode leftLegModel = new ModelNode("Left leg model", tableFrame);

    // Right leg
    NameNode rightLeg = new NameNode("Right leg");
    m = Mat4.multiply(Mat4Transform.translate(tableWidth / 2, 0, 0), LEG_MAT);
    TransformNode rightLegTransform = new TransformNode("Right leg transform", m);
    ModelNode rightLegModel = new ModelNode("Right leg model", tableFrame);

    parent.addChild(leftLeg);
      leftLeg.addChild(leftLegTransform);
        leftLegTransform.addChild(leftLegModel);
      createTableTop(leftLeg);                         // Table top
    parent.addChild(rightLeg);
      rightLeg.addChild(rightLegTransform);
        rightLegTransform.addChild(rightLegModel);
  }

  /**
   * Creates a table top
   *
   * @param parent Parent node
   */
  private void createTableTop(SGNode parent) {
    // Table top
    NameNode tableTop = new NameNode("Table top");
    Mat4 m = Mat4Transform.scale(tableWidth + FRAME_DIM, FRAME_DIM, tableDepth);
    m = Mat4.multiply(Mat4Transform.translate(0, tableHeight / 2, 0), m);
    TransformNode tableTopTransform = new TransformNode("Table top transform", m);
    ModelNode tableTopModel = new ModelNode("Table top model", tableFrame);

    parent.addChild(tableTop);
      tableTop.addChild(tableTopTransform);
        tableTopTransform.addChild(tableTopModel);
      createBackSupport(tableTop);                     // Back support
      createTopDrawer(tableTop);                       // Top drawer
  }

  /**
   * Creates a back support behind the drawers
   *
   * @param parent Parent node
   */
  private void createBackSupport(SGNode parent) {
    // Back support - child of table top
    NameNode backSupport = new NameNode("Back drawer support");
    Mat4 m = Mat4Transform.scale(tableWidth - FRAME_DIM, drawerHeight + GAPS_HEIGHT, FRAME_DIM);
    m = Mat4.multiply(Mat4Transform.translate(
        0,
        (drawerHeight - Cube.THICKNESS) / 2 - FRAME_DIM - GAPS_HEIGHT,
        -(tableDepth - FRAME_DIM) / 2), m);
    TransformNode backSupportTransform = new TransformNode("Back support transform", m);
    ModelNode backSupportModel = new ModelNode("Back support model", tableFrame);

    parent.addChild(backSupport);
      backSupport.addChild(backSupportTransform);
        backSupportTransform.addChild(backSupportModel);
  }

  /**
   * Creates a top drawer
   *
   * @param parent Parent node
   */
  private void createTopDrawer(SGNode parent) {
    final float POS_X = (tableWidth - drawerWidth - FRAME_DIM) / 2;

    TransformNode topDrawerTranslate = new TransformNode("Drawer translate",
        Mat4Transform.translate(POS_X, (tableHeight - topDrawerHeight) / 2, FRAME_DIM / 2));

    // Top drawer - child of table top
    NameNode topDrawer = new NameNode("Top drawer");
    Mat4 m = Mat4Transform.scale(drawerWidth, topDrawerHeight, drawerDepth);

    TransformNode topDrawerTransform = new TransformNode("Top drawer transform", m);
    ModelNode topDrawerModel = new ModelNode("Top drawer model", tableFrame);

    parent.addChild(topDrawerTranslate);
      topDrawerTranslate.addChild(topDrawer);
        topDrawer.addChild(topDrawerTransform);
          topDrawerTransform.addChild(topDrawerModel);
        createTopHandleSupport(topDrawer);                  // Top handle support
        createGaps(topDrawer);                              // Gaps
  }

  /**
   * Creates a left and right support for top drawer handle
   *
   * @param parent Parent node
   */
  private void createTopHandleSupport(SGNode parent) {
    final float POS_X_TO_LEFT = handleHeight * 0.1f;
    final float POS_Z = tableDepth / 2;

    TransformNode topTranslate = new TransformNode("Top handle support translate",
        Mat4Transform.translate(0, 0, POS_Z));

    // Left support - child of top drawer
    NameNode leftSupport = new NameNode("Top handle left support");
    Mat4 m = Mat4Transform.scale(handleWidth, supportHeight, handleDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-POS_X_TO_LEFT, 0, 0), m);
    TransformNode leftSupportTransform = new TransformNode("Top handle left support", m);
    ModelNode leftSupportModel = new ModelNode("Top handle left support model", drawerHandle);

    // Right support - child of top drawer
    NameNode rightSupport = new NameNode("Top handle right support");
    m = Mat4Transform.scale(handleWidth, supportHeight, handleDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(POS_X_TO_LEFT, 0, 0), m);
    TransformNode rightSupportTransform = new TransformNode("Top handle right support transform", m);
    ModelNode rightSupportModel = new ModelNode("Top handle right support model", drawerHandle);

    parent.addChild(topTranslate);
      topTranslate.addChild(leftSupport);
        leftSupport.addChild(leftSupportTransform);
        leftSupportTransform.addChild(leftSupportModel);
        createTopHandle(leftSupport);                       // Top handle
      topTranslate.addChild(rightSupport);
        rightSupport.addChild(rightSupportTransform);
        rightSupportTransform.addChild(rightSupportModel);
  }

  /**
   * Creates a top drawer handle
   *
   * @param parent Parent node
   */
  private void createTopHandle(SGNode parent) {
    final float POS_Z = handleDepth * 1.43f; // Make the top handle "merge" into the supports

    // Top handle - child of top drawer
    NameNode topHandle = new NameNode("Top handle");
    Mat4 m = Mat4Transform.scale(handleWidth, handleHeight, handleDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, POS_Z), m);
    TransformNode topHandleTransform = new TransformNode("Top handle transform", m);
    ModelNode topHandleModel = new ModelNode("Top handle model", drawerHandle);

    parent.addChild(topHandle);
      topHandle.addChild(topHandleTransform);
        topHandleTransform.addChild(topHandleModel);
  }

  /**
   * Creates a gaps between top and bottom drawer
   *
   * @param parent Parent node
   */
  private void createGaps(SGNode parent) {
    // Gaps between top and bottom drawer - child of top drawer
    NameNode gaps = new NameNode("Gaps between top and bot drawer");
    Mat4 m = Mat4Transform.scale(gapsWidth, GAPS_HEIGHT, gapsDepth);
    m = Mat4.multiply(
        Mat4Transform.translate(0, -(topDrawerHeight + GAPS_HEIGHT) / 2, 0), m);
    TransformNode gapsTransform = new TransformNode("Gaps transform", m);
    ModelNode gapsModel = new ModelNode("Gaps model", drawerGaps);

    parent.addChild(gaps);
      gaps.addChild(gapsTransform);
        gapsTransform.addChild(gapsModel);
      createBottomDrawer(gaps);                        // Bottom drawer
  }

  /**
   * Creates a bottom drawer
   *
   * @param parent Parent node
   */
  private void createBottomDrawer(SGNode parent) {
    TransformNode botDrawerTranslate = new TransformNode("Bottom drawer translate",
        Mat4Transform.translate(0, -(bottomDrawerHeight + GAPS_HEIGHT + topDrawerHeight) / 2, 0));

    // Bottom drawer - child of gaps
    NameNode botDrawer = new NameNode("Bottom drawer");
    Mat4 m = Mat4Transform.scale(drawerWidth, bottomDrawerHeight, drawerDepth);
    TransformNode botDrawerTransform = new TransformNode("Bottom drawer transform", m);
    ModelNode botDrawerModel = new ModelNode("Bottom drawer model", tableFrame);

    parent.addChild(botDrawerTranslate);
      botDrawerTranslate.addChild(botDrawer);
        botDrawer.addChild(botDrawerTransform);
          botDrawerTransform.addChild(botDrawerModel);
        createBotHandleSupport(botDrawer);               // Bottom handle support
  }

  /**
   * Creates a left and right support for bottom drawer handle
   *
   * @param parent Parent node
   */
  private void createBotHandleSupport(SGNode parent) {
    // Manual adjustments to position
    final float POS_X = -drawerWidth / 2.5f;
    final float POS_Y_TO_TOP = handleHeight * 0.1f;
    final float POS_Z = tableDepth / 2;

    TransformNode botTranslate = new TransformNode("Handle translate",
        Mat4Transform.translate(POS_X, 0, POS_Z));

    // Top support - child of bottom drawer
    NameNode topSupport = new NameNode("Bot handle top support");
    Mat4 m = Mat4Transform.scale(handleWidth, supportHeight, handleDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, POS_Y_TO_TOP, 0), m);
    TransformNode topSupportTransform = new TransformNode("Bot handle top support transform", m);
    ModelNode topSupportModel = new ModelNode("Bot handle top support model", drawerHandle);

    // Bottom support - child of bottom drawer
    NameNode botSupport = new NameNode("Bot handle bot support");
    m = Mat4Transform.scale(handleWidth, supportHeight, handleDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, -POS_Y_TO_TOP, 0), m);
    TransformNode botSupportTransform = new TransformNode("Bot handle bot support transform", m);
    ModelNode botSupportModel = new ModelNode("Bot handle bot support model", drawerHandle);

    parent.addChild(botTranslate);
      botTranslate.addChild(topSupport);
        topSupport.addChild(topSupportTransform);
          topSupportTransform.addChild(topSupportModel);
        createBotHandle(topSupport);                       // Bottom handle
      botTranslate.addChild(botSupport);
        botSupport.addChild(botSupportTransform);
          botSupportTransform.addChild(botSupportModel);
  }

  /**
   * Creates a top drawer handle
   *
   * @param parent Parent node
   */
  private void createBotHandle(SGNode parent) {
    final float POS_Z = handleDepth * 1.43f; // Make the top handle "merge" into the supports

    // Bot handle - child of top support
    NameNode botHandle = new NameNode("Bot handle");
    Mat4 m = Mat4Transform.scale(handleWidth, handleHeight, handleDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, POS_Z), m);
    TransformNode botHandleTransform = new TransformNode("Bot handle transform", m);
    ModelNode botHandleModel = new ModelNode("Bot handle model", drawerHandle);

    parent.addChild(botHandle);
      botHandle.addChild(botHandleTransform);
        botHandleTransform.addChild(botHandleModel);
  }
}
