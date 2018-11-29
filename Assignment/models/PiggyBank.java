package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;

/**
 * I declare that this code is my own work.
 * A class for rendering a a piggy bank
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class PiggyBank {

  private SGNode piggyRoot;

  private Model body, nose, ear, leg, cubeDeco, sphereDeco;

  private float bodyWidth, bodyHeight, bodyDepth;
  private float eyeRadius, holeWidth, holeDepth;
  private float legHeight, legScale;
  private float noseScale, noseDecoHeight, noseDecoDepth;
  private float earWidth, earHeight, earDepth;

  // The Z position of the piggy bank
  private float piggyZ;

  /**
   * Piggy bank constructor
   *
   * @param body Sphere shaped body
   * @param nose Cylinder shaped nose
   * @param ear Sphere shaped ear
   * @param leg Cylinder shaped leg
   * @param cubeDeco Cube shaped decoration
   * @param sphereDeco Sphere shaped decoration
   */
  public PiggyBank(Model body, Model nose, Model ear, Model leg, Model cubeDeco, Model sphereDeco) {
    this.body = body;
    this.nose = nose;
    this.ear = ear;
    this.leg = leg;
    this.cubeDeco = cubeDeco;
    this.sphereDeco = sphereDeco;

    // Dimension ratio of piggy bank with respect to table width
    bodyWidth = Table.tableWidth * 0.12f;
    bodyHeight = Table.tableWidth * 0.1f;
    bodyDepth = bodyHeight;

    // Eye and coin hole
    eyeRadius = bodyWidth * 0.05f;
    holeWidth = bodyWidth * 0.15f;
    holeDepth = holeWidth * 0.15f;

    // Leg
    legHeight = bodyHeight;
    legScale = bodyWidth * 0.25f;

    // Nose
    noseScale = bodyWidth * 0.3f;
    noseDecoDepth = noseScale * 0.07f;
    noseDecoHeight = noseScale * 0.43f;

    // Ear
    earWidth = bodyWidth * 0.1f;
    earHeight = bodyWidth * 0.4f;
    earDepth = bodyWidth * 0.25f;

    // Middle of the table
    piggyZ = -Table.tableDepth / 2 + 0.8f;
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    piggyRoot = new NameNode("Piggy bank root");

    // 0.15f offset because the leg rotated, hence it will intersect with table top
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(0, bodyHeight / 2 + 0.15f, piggyZ));

    Table.tableTop.addChild(piggyRoot);
      piggyRoot.addChild(rootTranslate);
        createBody(rootTranslate);

    Table.tableTop.update();
    piggyRoot.update();
  }

  /**
   * Renders a piggy bank
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    piggyRoot.draw(gl);
  }

  /**
   * Creates a piggy bank body
   *
   * @param parent Parent node
   */
  private void createBody(SGNode parent) {
    final float EYES_POS_X = -bodyWidth / 2.5f;
    final float EYES_POS_Y = bodyHeight / 4;
    final float EYES_POS_Z = bodyDepth / 6.6f;

    NameNode body = new NameNode("Piggy bank body");
    Mat4 m = Mat4Transform.scale(bodyWidth, bodyHeight, bodyDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(180), m);
    TransformNode bodyTransform = new TransformNode("Piggy bank body transform", m);
    ModelNode bodyModel = new ModelNode("Piggy bank body model", this.body);

    // Eyes
    TransformNode eyesTranslate = new TransformNode("Eyes translate",
        Mat4Transform.translate(EYES_POS_X, EYES_POS_Y, 0));

    NameNode leftEye = new NameNode("Left eye");
    m = Mat4Transform.scale(eyeRadius, eyeRadius, eyeRadius);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, EYES_POS_Z), m);
    TransformNode leftEyeTransform = new TransformNode("Left eye transform", m);
    ModelNode leftEyeModel = new ModelNode("Left eye model", sphereDeco);

    NameNode rightEye = new NameNode("Right eye");
    m = Mat4.multiply(Mat4Transform.translate(0, 0, -EYES_POS_Z * 2), m);
    TransformNode rightEyeTransform = new TransformNode("Right eye transform", m);
    ModelNode rightEyeModel = new ModelNode("Right eye model", sphereDeco);

    // Coin hole
    NameNode coinHole = new NameNode("Coin hole");
    m = Mat4Transform.scale(holeWidth, 0, holeDepth);
    m = Mat4.multiply(Mat4Transform.translate(0, bodyHeight / 2, 0), m);
    TransformNode coinHoleTransform = new TransformNode("Coin hole transform", m);
    ModelNode coinHoleModel = new ModelNode("Coin hole model", cubeDeco);

    parent.addAllChildren(body, bodyTransform, bodyModel);
      createLegs(body);
      createNose(body);
      createEars(body);
      body.addChild(eyesTranslate);
        eyesTranslate.addAllChildren(leftEye, leftEyeTransform, leftEyeModel);
        eyesTranslate.addAllChildren(rightEye, rightEyeTransform, rightEyeModel);
      body.addAllChildren(coinHole, coinHoleTransform, coinHoleModel);
  }

  /**
   * Creates four legs for the piggy bank
   *
   * @param parent Parent node
   */
  private void createLegs(SGNode parent) {
    final Mat4 LEG_M = Mat4Transform.scale(legScale, legHeight, legScale);

    // Position do not match the edge of the body to "merge" the legs with the body
    final float POS_X = bodyWidth / 4;
    final float POS_Y = -bodyHeight / 2.5f;
    final float POS_Z = bodyDepth / 4.5f;
    final int ANGLE = 5;

    TransformNode legYTranslate = new TransformNode("Legs Y translate",
        Mat4Transform.translate(0, POS_Y, 0));

    NameNode frontLeft = new NameNode("Front left leg");
    Mat4 m = Mat4.multiply(Mat4Transform.translate(-POS_X, 0, POS_Z), LEG_M);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-ANGLE), m);
    TransformNode frontLeftTransform = new TransformNode("Front left leg transform", m);
    ModelNode frontLeftModel = new ModelNode("Front left leg model", leg);

    NameNode backLeft = new NameNode("Back left leg");
    m = Mat4.multiply(Mat4Transform.translate(POS_X, 0, POS_Z), LEG_M);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(ANGLE), m);
    TransformNode backLeftTransform = new TransformNode("Back left leg transform", m);
    ModelNode backLeftModel = new ModelNode("Back left leg model", leg);

    NameNode frontRight = new NameNode("Front right leg");
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, 0, -POS_Z), LEG_M);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-ANGLE), m);
    TransformNode frontRightTransform = new TransformNode("Front right leg transform", m);
    ModelNode frontRightModel = new ModelNode("Front right leg model", leg);

    NameNode backRight = new NameNode("Back right leg");
    m = Mat4.multiply(Mat4Transform.translate(POS_X, 0, -POS_Z), LEG_M);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(ANGLE), m);
    TransformNode backRightTransform = new TransformNode("Back right leg transform", m);
    ModelNode backRightModel = new ModelNode("Back right leg model", leg);

    parent.addChild(legYTranslate);
      legYTranslate.addAllChildren(frontLeft, frontLeftTransform, frontLeftModel);
      legYTranslate.addAllChildren(backLeft, backLeftTransform, backLeftModel);
      legYTranslate.addAllChildren(frontRight, frontRightTransform, frontRightModel);
      legYTranslate.addAllChildren(backRight, backRightTransform, backRightModel);
  }

  /**
   * Creates a nose on the body
   *
   * @param parent Parent node
   */
  private void createNose(SGNode parent) {
    final float POS_X = bodyWidth / 2;
    final float DECO_POS_X = -noseScale / 4;
    final float DECO_POS_Z = -noseScale / 8;

    TransformNode noseTranslate = new TransformNode("Nose translate",
        Mat4Transform.translate(-POS_X, 0, 0));

    NameNode nose = new NameNode("Nose");
    Mat4 m = Mat4Transform.scale(noseScale, noseScale, noseScale);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(90), m);
    TransformNode noseTransform = new TransformNode("Nose transform", m);
    ModelNode noseModel = new ModelNode("Nose model", this.nose);

    TransformNode decoTranslate = new TransformNode("Nose decorations translate",
        Mat4Transform.translate(DECO_POS_X, 0, 0));

    NameNode leftStrip = new NameNode("Nose left strip");
    m = Mat4Transform.scale(0.01f, noseDecoHeight, noseDecoDepth);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, -DECO_POS_Z), m);
    TransformNode leftStripTransform = new TransformNode("Nose left strip transform", m);
    ModelNode leftStripModel = new ModelNode("Nose left strip model", cubeDeco);

    NameNode rightStrip = new NameNode("Nose right strip");
    m = Mat4.multiply(Mat4Transform.translate(0, 0, DECO_POS_Z * 2), m);
    TransformNode rightStripTransform = new TransformNode("Nose right strip transform", m);
    ModelNode rightStripModel = new ModelNode("Nose right strip model", cubeDeco);

    parent.addChild(noseTranslate);
      noseTranslate.addAllChildren(nose, noseTransform, noseModel);
        nose.addChild(decoTranslate);
          decoTranslate.addAllChildren(leftStrip, leftStripTransform, leftStripModel);
          decoTranslate.addAllChildren(rightStrip, rightStripTransform, rightStripModel);
  }

  /**
   * Creates two ear on the piggy bank body
   *
   * @param parent Parent node
   */
  private void createEars(SGNode parent) {
    final Mat4 EAR_M = Mat4Transform.scale(earWidth, earHeight, earDepth);

    // Only translate half of the sphere shaped ears on the body
    final float POS_X = -bodyWidth / 4;
    final float POS_Y = bodyHeight / 2.5f;
    final float POS_Z = bodyDepth / 4.5f;
    final int ANGLE = 30;

    TransformNode earTranslate = new TransformNode("Ears translate",
        Mat4Transform.translate(POS_X, POS_Y, 0));

    NameNode leftEar = new NameNode("Left ear");
    Mat4 m = Mat4.multiply(Mat4Transform.rotateAroundX(-ANGLE), EAR_M);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, -POS_Z), m);
    TransformNode leftEarTransform = new TransformNode("Left ear transform", m);
    ModelNode leftEarModel = new ModelNode("Left ear model", ear);

    NameNode rightEar = new NameNode("Right ear");
    m = Mat4.multiply(Mat4Transform.rotateAroundX(ANGLE), EAR_M);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, POS_Z), m);
    TransformNode rightEarTransform = new TransformNode("Right ear transfor", m);
    ModelNode rightEarModel = new ModelNode("Right ear model", ear);

    parent.addChild(earTranslate);
      earTranslate.addAllChildren(leftEar, leftEarTransform, leftEarModel);
      earTranslate.addAllChildren(rightEar, rightEarTransform, rightEarModel);
  }
}
