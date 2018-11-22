package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;

/**
 * I declare that this code is my own work.
 * A class for rendering a cactus pot on the left side of the picture frame
 *
 * @author Zer Jun Eng
 */
public class CactusPot {

  private Model pot, cactus, flower;

  private float potWidth, potHeight, potDepth;
  private float potX, potZ;                        // The X and Z position of the cactus plant pot

  private final float CACTUS_BODY_WIDTH = 0.65f;
  private final float CACTUS_BODY_HEIGHT = 0.8f;
  private final float CACTUS_DEPTH = 0.1f;

  private final float BRANCH_WIDTH = CACTUS_BODY_WIDTH / 1.5f;
  private final float BRANCH_HEIGHT = CACTUS_BODY_HEIGHT / 1.5f;

  /**
   * Cactus plant pot constructor
   *
   * @param pot Frustum cone shaped plant pot
   * @param cactus Sphere shaped cactus
   * @param flower Sphere shaped flower
   */
  public CactusPot(Model pot, Model cactus, Model flower) {
    this.pot = pot;
    this.cactus = cactus;
    this.flower = flower;

    // Dimension ratio of cactus plant pot with respect to table width
    potWidth = Table.tableWidth * 0.1f;
    potHeight = Table.tableWidth * 0.15f;
    potDepth = potWidth;

    // Below the right side on window, and on the left side of picture frame
    potX = Table.tableWidth / 2 - 3;
    potZ = -Table.tableDepth / 2 + 0.8f;
  }

  /**
   * Renders a cactus plant pot
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    // Root
    SGNode potRoot = new NameNode("Cactus plant pot root");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(potX, potHeight / 2, potZ));

    Table.tableTop.addChild(potRoot);
      potRoot.addChild(rootTranslate);
        createPot(rootTranslate);

    Table.tableRoot.update();
    potRoot.update();
    potRoot.draw(gl);
  }

  /**
   * Creates a plant pot
   *
   * @param parent Parent node
   */
  private void createPot(SGNode parent) {
    NameNode pot = new NameNode("Pot");
    Mat4 m = Mat4Transform.scale(potWidth, potHeight, potDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(180), m);
    TransformNode potTransform = new TransformNode("Pot transform", m);
    ModelNode potModel = new ModelNode("Pot model", this.pot);

    parent.addAllChildren(pot, potTransform, potModel);
      createCactusBody(pot);
  }


  /**
   * Creates a body of the cactus
   *
   * @param parent Parent Node
   */
  private void createCactusBody(SGNode parent) {
    // To make cactus branches "merge" with the body
    final float POS_Y = 0.2f;

    NameNode body = new NameNode("Cactus lower branch");
    Mat4 m = Mat4Transform.scale(CACTUS_BODY_WIDTH, CACTUS_BODY_HEIGHT, CACTUS_DEPTH);
    m = Mat4.multiply(Mat4Transform.translate(0, POS_Y, 0), m);
    TransformNode bodyBranchTransform = new TransformNode("Cactus lower branch transform", m);
    ModelNode bodyBranchNode = new ModelNode("Cactus lower branch model", cactus);

    parent.addAllChildren(body, bodyBranchTransform, bodyBranchNode);
      createCactusBranches(body);
  }

  /**
   * Creates a left branch and a right branch of the cactus
   *
   * @param parent Parent node
   */
  private void createCactusBranches(SGNode parent) {
    final float ARM_POS_Y = (CACTUS_BODY_HEIGHT + BRANCH_HEIGHT) / 2;
    final int ANGLE = 30;

    TransformNode leftBranchTranslateAndRotate =
        new TransformNode("Left branch translate and rotate",
            Mat4.multiply(Mat4Transform.rotateAroundZ(ANGLE), Mat4Transform.translate(0, ARM_POS_Y, 0)));

    NameNode leftBranch = new NameNode("Cactus left branch");
    Mat4 m = Mat4Transform.scale(BRANCH_WIDTH, BRANCH_HEIGHT, CACTUS_DEPTH);
    TransformNode leftBranchTransform = new TransformNode("Cactus left branch transform", m);
    ModelNode leftBranchModel = new ModelNode("Cactus left branch model", cactus);

    NameNode rightBranch = new NameNode("Cactus right branch");
    m = Mat4.multiply(Mat4Transform.translate(0, ARM_POS_Y, 0), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-ANGLE), m);
    TransformNode rightBranchTransform = new TransformNode("Cactus right branch transform", m);
    ModelNode rightBranchModel = new ModelNode("Cactus right branch model", cactus);

    parent.addChild(leftBranchTranslateAndRotate);
      leftBranchTranslateAndRotate.addAllChildren(leftBranch, leftBranchTransform, leftBranchModel);
        createFlower(leftBranch);
    parent.addAllChildren(rightBranch, rightBranchTransform, rightBranchModel);
  }

  /**
   * Creates a flower on top of left branch
   *
   * @param parent Parent node
   */
  private void createFlower(SGNode parent) {
    final float RADIUS = BRANCH_WIDTH / 2;
    final float POS_Y = (BRANCH_HEIGHT + RADIUS) / 2 - 0.05f; // 0.2f for "merge" effect
    final int ANGLE = -30;

    NameNode flower = new NameNode("Flower");
    Mat4 m = Mat4Transform.scale(RADIUS, RADIUS, RADIUS);
    m = Mat4.multiply(Mat4Transform.translate(0, POS_Y, 0), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(ANGLE), m);
    TransformNode flowerTransform = new TransformNode("Flower transform", m);
    ModelNode flowerModel = new ModelNode("Flower model", this.flower);

    parent.addAllChildren(flower, flowerTransform, flowerModel);
  }
}
