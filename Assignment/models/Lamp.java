package models;

import com.jogamp.opengl.*;
import java.util.*;
import javax.swing.text.*;
import lib.*;
import lib.gmaths.*;
import main.*;

/**
 * A class for rendering a lamp with spotlight and animation
 *
 * @author Zer Jun Eng
 */
public class Lamp {

  private SGNode lampRoot;
  private TransformNode baseRotateY;
  private TransformNode lowerJointRotateZ;
  private TransformNode upperJointRotateZ;
  private TransformNode headJointRotateY, headJointRotateZ;

  private Model cube, cylinder, sphere, frustumCone, lampEar, lowerTail;
  private Light lampLight;

  private float lampRadius, baseHeight, jointRadius, bodyRadius, lowerBodyHeight, upperBodyHeight;
  private float lampX;

  private double startTime;

  public boolean isRandom = false;
  private boolean isAnimatingRandom = false;

  public boolean isReset = false;
  private boolean isAnimatingReset = false;

  public boolean isJump = false;
  private boolean isAnimatingJump = false;

  private final int DEFAULT_BASE_ANGLE_Y = 0;

  private final int DEFAULT_LOWER_JOINT_ANGLE_Z = 30;
  private final int MIN_LOWER_JOINT_ANGLE_Z = -70;
  private final int MAX_LOWER_JOINT_ANGLE_Z = 70;
  private int initialLowerJointAngle = DEFAULT_LOWER_JOINT_ANGLE_Z;
  private int targetLowerJointAngle;

  private final int DEFAULT_UPPER_JOINT_ANGLE_Z = -90;
  private final int MIN_UPPER_JOINT_ANGLE_Z = -100;
  private final int MAX_UPPER_JOINT_ANGLE_Z = 0;
  private int initialUpperJointAngle = DEFAULT_UPPER_JOINT_ANGLE_Z;
  private int targetUpperJointAngle;

  private final int DEFAULT_HEAD_JOINT_ANGLE_Y = 0;
  private final int MIN_HEAD_JOINT_ANGLE_Y = -90;
  private final int MAX_HEAD_JOINT_ANGLE_Y = 90;
  private int initialHeadJointAngleY = DEFAULT_HEAD_JOINT_ANGLE_Y;
  private int targetHeadJointAngleY;

  private final int DEFAULT_HEAD_JOINT_ANGLE_Z = -10;
  private final int MIN_HEAD_JOINT_ANGLE_Z = -30;
  private final int MAX_HEAD_JOINT_ANGLE_Z = 80;
  private int initialHeadJointAngleZ = DEFAULT_HEAD_JOINT_ANGLE_Z;
  private int targetHeadJointAngleZ;

  private final Random r = new Random();

  /**
   * Lamp constructor
   *
   * @param cube Cube shape
   * @param cylinder Cylinder shape
   * @param sphere Sphere shape
   * @param frustumCone Frustum cone shape
   * @param lampLight Light bulb
   * @param lampEar Sphere shaped ear
   * @param lowerTail Cube shaped tail
   */
  public Lamp(Model cube, Model cylinder, Model sphere, Model frustumCone, Light lampLight,
      Model lampEar, Model lowerTail) {
    this.cube = cube;
    this.cylinder = cylinder;
    this.sphere = sphere;
    this.frustumCone = frustumCone;
    this.lampLight = lampLight;
    this.lampEar = lampEar;
    this.lowerTail = lowerTail;

    lampRadius = Table.tableWidth * 0.1f;
    baseHeight = Table.tableHeight * 0.06f;
    jointRadius = lampRadius * 0.2f;
    bodyRadius = jointRadius / 2;
    lowerBodyHeight = Table.tableHeight * 0.45f;
    upperBodyHeight = Table.tableHeight * 0.4f;

    lampX = -Table.tableWidth / 2 + 2;
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    lampRoot = new NameNode("Lamp root");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(lampX, (Table.FRAME_DIM + baseHeight / 2) / 2, 0));

    Table.tableTop.addChild(lampRoot);
      lampRoot.addChild(rootTranslate);
        createBase(rootTranslate);

    Table.tableRoot.update();
    lampRoot.update();
  }

  /**
   * Renders a lamp
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    // No further animations available until the current is finished
    if (isAnimatingRandom || isAnimatingReset || isAnimatingJump) {
      Anilamp.random.setEnabled(false);
      Anilamp.reset.setEnabled(false);
      Anilamp.jump.setEnabled(false);
    } else {
      Anilamp.random.setEnabled(true);
      Anilamp.reset.setEnabled(true);
      Anilamp.jump.setEnabled(true);
    }

    if (isRandom || isAnimatingRandom) {
      if (isRandom && !isAnimatingRandom) {
        // Random pose clicked, starting animation
        calculateRandomPose();
      } else {
        // Animation started
        float elapsedTime = (float) Math.sin(getSeconds() - startTime);
        randomPose(elapsedTime);
      }
    } else if (isReset) {
      // isAnimatingReset = true;
      // reset();
    } else {
      // isAnimatingJump = true;
      // jump();
    }

    lampRoot.draw(gl);
  }

  /**
   * Calculates the rotation angles of the joints
   */
  private void calculateRandomPose() {
    /* Lower joint */
    initialLowerJointAngle = lowerJointRotateZ.getTransform().getZRotationAngle();
    int min = MIN_LOWER_JOINT_ANGLE_Z - initialLowerJointAngle;
    int max = MAX_LOWER_JOINT_ANGLE_Z - initialLowerJointAngle;
    targetLowerJointAngle = r.nextInt(max - min) + min;

    /* Upper joint */
    initialUpperJointAngle = upperJointRotateZ.getTransform().getZRotationAngle();
    max = MAX_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle;

    // Less than this value will cause the upper body to intersect with table
    if (initialLowerJointAngle + targetLowerJointAngle <= -35) {
      min = -20 - initialUpperJointAngle;
    } else {
      min = MIN_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle;
    }

    targetUpperJointAngle = r.nextInt(max - min) + min;

    System.out.print(initialLowerJointAngle + targetLowerJointAngle + " ");
    System.out.println(initialUpperJointAngle + targetUpperJointAngle);

    /* Head joint */
    initialHeadJointAngleY = headJointRotateY.getTransform().getYRotationAngle();
    initialHeadJointAngleZ = headJointRotateZ.getTransform().getZRotationAngle();

    max = MAX_HEAD_JOINT_ANGLE_Y - initialHeadJointAngleY;
    min = MIN_HEAD_JOINT_ANGLE_Y - initialHeadJointAngleY;
    targetHeadJointAngleY = r.nextInt(max - min) + min;

    max = MAX_HEAD_JOINT_ANGLE_Z - initialHeadJointAngleZ;
    min = MIN_HEAD_JOINT_ANGLE_Z - initialHeadJointAngleZ;
    targetHeadJointAngleZ = r.nextInt(max - min) + min;

    isRandom = false;
    isAnimatingRandom = true;
    startTime = getSeconds();
  }

  /**
   * The lamp will make a random pose
   *
   * @param time The elapsed time
   */
  private void randomPose(float time) {
    float rotateAngleL = initialLowerJointAngle + targetLowerJointAngle * time;
    float rotateAngleU = initialUpperJointAngle + targetUpperJointAngle * time;
    float rotateAngleHY = initialHeadJointAngleY + targetHeadJointAngleY * time;
    float rotateANgleHZ = initialHeadJointAngleZ + targetHeadJointAngleZ * time;

    float finalAngleL = initialLowerJointAngle + targetLowerJointAngle - rotateAngleL;
    float finalAngleU = initialUpperJointAngle + targetUpperJointAngle - rotateAngleU;
    float finalAngleHY = initialHeadJointAngleY + targetHeadJointAngleY - rotateAngleHY;
    float finalAngleHZ = initialHeadJointAngleZ + targetHeadJointAngleZ - rotateANgleHZ;

    if (Math.abs(finalAngleL) < 0.1f && Math.abs(finalAngleU) < 0.1f &&
        Math.abs(finalAngleHY) < 0.1f && Math.abs(finalAngleHZ) < 0.1f) {
      isAnimatingRandom = false;
    } else {
      if (Math.abs(finalAngleL) > 0.1f) {
        lowerJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleL));
      }

      if (Math.abs(finalAngleU) > 0.1f) {
        upperJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleU));
      }

      if (Math.abs(finalAngleHY) > 0.1f) {
        headJointRotateY.setTransform(Mat4Transform.rotateAroundY(rotateAngleHY));
      }

      if (Math.abs(finalAngleHZ) > 0.1f) {
        headJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateANgleHZ));
      }

      lampRoot.update();
    }
  }

  /**
   * Resets the lamp to default pose
   */
  private void reset() {
    lowerJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(DEFAULT_LOWER_JOINT_ANGLE_Z));
    lowerJointRotateZ.update();

    upperJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(DEFAULT_UPPER_JOINT_ANGLE_Z));
    upperJointRotateZ.update();

    headJointRotateY.setTransform(Mat4Transform.rotateAroundY(DEFAULT_HEAD_JOINT_ANGLE_Y));
    headJointRotateY.update();

    headJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(DEFAULT_HEAD_JOINT_ANGLE_Z));
    headJointRotateZ.update();
  }

  /**
   * The lamp will jump to random position
   */
  private void jump() {
    System.out.println("jump");
  }

  /**
   * Renders the cylinder base of the lamp
   *
   * @param parent Parent node
   */
  private void createBase(SGNode parent) {
    float outerBaseRadius = lampRadius + 0.005f;
    float outerBaseHeight = baseHeight / 3.5f;
    float outerBasePosY = -baseHeight / 4 + outerBaseHeight / 4;

    baseRotateY = new TransformNode("Head joint rotate Y",
        Mat4Transform.rotateAroundY(DEFAULT_BASE_ANGLE_Y));

    NameNode innerBase = new NameNode("Inner base");
    Mat4 m = Mat4Transform.scale(lampRadius, baseHeight, lampRadius);
    TransformNode innerBaseTransform = new TransformNode("Inner base transform", m);
    ModelNode innerBaseModel = new ModelNode("Inner base model", cylinder);

    NameNode outerBase = new NameNode("Outer base");
    m = Mat4Transform.scale(outerBaseRadius, outerBaseHeight, outerBaseRadius);
    m = Mat4.multiply(Mat4Transform.translate(0, outerBasePosY, 0), m);
    TransformNode outerBaseTransform = new TransformNode("Outer base transform", m);
    ModelNode outerBaseModel = new ModelNode("Outer base model", cylinder);

    parent.addChild(baseRotateY);
    baseRotateY.addAllChildren(innerBase, innerBaseTransform, innerBaseModel);
      createLowerBody(innerBase);
    baseRotateY.addAllChildren(outerBase, outerBaseTransform, outerBaseModel);
  }

  /**
   * Creates the lower body of the lamp
   *
   * @param parent Parent node
   */
  private void createLowerBody(SGNode parent) {
    final float POS_X = -lampRadius / 4f;
    final float POS_Y = baseHeight / 3;

    TransformNode lowerJointTranslate = new TransformNode("Lower joint translate",
        Mat4Transform.translate(POS_X, POS_Y, 0));

    lowerJointRotateZ = new TransformNode("Lower joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_LOWER_JOINT_ANGLE_Z));

    NameNode lowerJoint = new NameNode("Base joint");
    Mat4 m = Mat4Transform.scale(jointRadius, jointRadius, jointRadius);
    TransformNode lowerJointTransform = new TransformNode("Base joint transform", m);
    ModelNode lowerJointModel = new ModelNode("Base joint model", sphere);

    TransformNode lowerBodyTranslate = new TransformNode("Lower body translate",
        Mat4Transform.translate(0, lowerBodyHeight / 4, 0));

    NameNode lowerBody = new NameNode("Lower body");
    m = Mat4Transform.scale(bodyRadius, lowerBodyHeight, bodyRadius);
    TransformNode lowerBodyTransform = new TransformNode("Lower body transform", m);
    ModelNode lowerBodyModel = new ModelNode("Lower body model", cylinder);

    parent.addChild(lowerJointTranslate);
      lowerJointTranslate.addChild(lowerJointRotateZ);
        lowerJointRotateZ.addAllChildren(lowerJoint, lowerJointTransform, lowerJointModel);
          lowerJoint.addChild(lowerBodyTranslate);
            lowerBodyTranslate.addAllChildren(lowerBody, lowerBodyTransform, lowerBodyModel);
              createUpperBody(lowerBody);
  }

  /**
   * Creates the upper body of the lamp
   *
   * @param parent Parent node
   */
  private void createUpperBody(SGNode parent) {
    TransformNode upperJointTranslate = new TransformNode("Upper joint translate",
        Mat4Transform.translate(0, lowerBodyHeight / 4, 0));

    upperJointRotateZ = new TransformNode("Upper joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_UPPER_JOINT_ANGLE_Z));

    NameNode upperJoint = new NameNode("Upper joint");
    Mat4 m = Mat4Transform.scale(jointRadius, jointRadius, jointRadius);
    TransformNode upperJointTransform = new TransformNode("Upper joint transform", m);
    ModelNode upperJointModel = new ModelNode("Upper joint model", sphere);

    TransformNode upperBodyTranslate = new TransformNode("Upper body translate",
        Mat4Transform.translate(0, upperBodyHeight / 4 , 0));

    NameNode upperBody = new NameNode("Upper body");
    m = Mat4Transform.scale(bodyRadius, upperBodyHeight, bodyRadius);
    TransformNode upperBodyTransform = new TransformNode("Upper body transform", m);
    ModelNode upperBodyModel = new ModelNode("Upper body model", cylinder);

    parent.addChild(upperJointTranslate);
      upperJointTranslate.addChild(upperJointRotateZ);
        upperJointRotateZ.addAllChildren(upperJoint, upperJointTransform, upperJointModel);
          createTail(upperJoint);
          upperJoint.addChild(upperBodyTranslate);
            upperBodyTranslate.addAllChildren(upperBody, upperBodyTransform, upperBodyModel);
              createBackHead(upperBody);
  }

  /**
   * Creates the back head of the lamp
   *
   * @param parent Parent node
   */
  private void createBackHead(SGNode parent) {
    final float HEAD_JOINT_RADIUS = jointRadius / 2;
    final float BACK_HEAD_RADIUS = bodyRadius * 3f;
    final float BACK_HEAD_HEIGHT = 0.8f;

    TransformNode headJointTranslate = new TransformNode("Head joint translate",
        Mat4Transform.translate(0, upperBodyHeight / 4, 0));

    // Left right rotation
    headJointRotateY = new TransformNode("Head joint rotate Y",
        Mat4Transform.rotateAroundY(DEFAULT_HEAD_JOINT_ANGLE_Y));

    // Up down rotation
    headJointRotateZ = new TransformNode("Head joint rotate Z",
            Mat4Transform.rotateAroundZ(DEFAULT_HEAD_JOINT_ANGLE_Z));

    NameNode headJoint = new NameNode("Head joint");
    Mat4 m = Mat4Transform.scale(HEAD_JOINT_RADIUS, HEAD_JOINT_RADIUS, HEAD_JOINT_RADIUS);
    TransformNode headJointTransform = new TransformNode("Head joint transform", m);
    ModelNode headJointModel = new ModelNode("Head joint model", sphere);

    TransformNode backHeadTranslate = new TransformNode("Back head translate",
        Mat4Transform.translate(0, BACK_HEAD_HEIGHT / 4, 0));

    TransformNode backHeadRotate = new TransformNode("Back head rotate",
        Mat4Transform.rotateAroundZ(90));

    NameNode backHead = new NameNode("Back head");
    m = Mat4Transform.scale(BACK_HEAD_RADIUS, BACK_HEAD_HEIGHT, BACK_HEAD_RADIUS);
    TransformNode backHeadTranform = new TransformNode("Back head transform", m);
    ModelNode backHeadModel = new ModelNode("Back head model", cylinder);

    parent.addChild(headJointTranslate);
      headJointTranslate.addChild(headJointRotateY);
        headJointRotateY.addChild(headJointRotateZ);
          headJointRotateZ.addAllChildren(headJoint, headJointTransform, headJointModel);
            headJoint.addChild(backHeadTranslate);
              backHeadTranslate.addChild(backHeadRotate);
                backHeadRotate.addAllChildren(backHead, backHeadTranform, backHeadModel);
                  createEars(backHead, BACK_HEAD_RADIUS);
                  createFrontHead(backHead);
  }

  /**
   * Creates the front head of the lamp, with a light bulb inside
   *
   * @param parent Parent node
   */
  private void createFrontHead(SGNode parent) {
    final float FRONT_HEAD_RADIUS = bodyRadius * 3.5f;
    final float FRONT_HEAD_HEIGHT = 0.75f;
    final float LIGHT_SCALE = 0.33f;
    final float POS_Y = -FRONT_HEAD_HEIGHT / 1.5f;

    TransformNode frontHeadTranslate = new TransformNode("Front head translate",
        Mat4Transform.translate(0, POS_Y, 0));

    NameNode frontHead = new NameNode("Front head");
    Mat4 m = Mat4Transform.scale(FRONT_HEAD_RADIUS, FRONT_HEAD_HEIGHT, FRONT_HEAD_RADIUS);
    TransformNode frontHeadTransform = new TransformNode("Front head transform", m);
    ModelNode frontHeadModel = new ModelNode("Front head model", frustumCone);

    NameNode lightBulb = new NameNode("Light bulb");
    m = Mat4Transform.scale(LIGHT_SCALE, LIGHT_SCALE, LIGHT_SCALE);
    m = Mat4.multiply(Mat4Transform.translate(0, LIGHT_SCALE / 2, 0), m);
    TransformNode lightBulbTransform = new TransformNode("Light bulb transform", m);
    LightNode lightBulbModel = new LightNode("Light bulb node", lampLight);

    parent.addChild(frontHeadTranslate);
      frontHeadTranslate.addAllChildren(frontHead, frontHeadTransform, frontHeadModel);
        frontHead.addAllChildren(lightBulb, lightBulbTransform, lightBulbModel);
  }

  /**
   * Creates the Pikachu ears for the lamp
   *
   * @param parent Parent node
   */
  private void createEars(SGNode parent, float parentWidth) {
    final float EAR_RADIUS = bodyRadius * 0.8f;
    final float EAR_HEIGHT = 0.6f;
    final float POS_Z = parentWidth / 3f;
    final float POS_Y = EAR_HEIGHT / 2;

    // Left ear and right ear have different rotations
    NameNode leftEar = new NameNode("Left ear");
    Mat4 m = Mat4Transform.scale(EAR_RADIUS, EAR_HEIGHT, EAR_RADIUS);
    m = Mat4.multiply(Mat4Transform.translate(0, POS_Y, -POS_Z), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(-20), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-10), m);
    TransformNode leftEarTransform = new TransformNode("Left ear transform", m);
    ModelNode leftEarModel = new ModelNode("Left ear model", lampEar);

    NameNode rightEar = new NameNode("Right ear");
    m = Mat4.multiply(Mat4Transform.translate(0, 0, POS_Z * 2), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(40), m);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-20), m);
    TransformNode rightEarTransform = new TransformNode("Right ear transform", m);
    ModelNode rightEarModel = new ModelNode("Right ear model", lampEar);

    parent.addAllChildren(leftEar, leftEarTransform, leftEarModel);
    parent.addAllChildren(rightEar, rightEarTransform, rightEarModel);
  }

  /**
   * Creates the Pikachu tail
   *
   * @param parent Parent node
   */
  private void createTail(SGNode parent) {
    final float WIDTH = 0.05f;
    final float LOWER_HEIGHT = 0.1f;
    final float LOWER_DEPTH = 0.2f;
    final float MIDDLE_HEIGHT = LOWER_HEIGHT * 1.4f;
    final float MIDDLE_DEPTH = LOWER_DEPTH * 1.4f;
    final float UPPER_HEIGHT = MIDDLE_HEIGHT * 1.3f;
    final float UPPER_DEPTH = MIDDLE_DEPTH * 1.3f;

    // Lower tail
    TransformNode lowerTailTranslateAndRotate = new TransformNode("Lower tail translate and rotate",
        Mat4.multiply(Mat4Transform.rotateAroundZ(45),
            Mat4Transform.translate(0, LOWER_DEPTH, 0)));

    NameNode lowerTailV = new NameNode("Vertical lower tail");
    Mat4 m = Mat4Transform.scale(WIDTH, LOWER_DEPTH, LOWER_HEIGHT);
    TransformNode lowerTailTransformV = new TransformNode("Vertical lower tail transform", m);
    ModelNode lowerTailModelV = new ModelNode("Vertical lower tail model", lowerTail);

    TransformNode lowerTailHTranslate = new TransformNode("Lower tail rotate",
        Mat4Transform.translate(0, LOWER_HEIGHT / 2, -LOWER_HEIGHT / 2));

    NameNode lowerTailH = new NameNode("Horizontal lower tail");
    m = Mat4Transform.scale(WIDTH, LOWER_HEIGHT, LOWER_DEPTH);
    TransformNode lowerTailTransformH = new TransformNode("Horizontal lower tail transform", m);
    ModelNode lowerTailModelH = new ModelNode("Horizontal lower tail model", lowerTail);

    parent.addChild(lowerTailTranslateAndRotate);
      lowerTailTranslateAndRotate.addAllChildren(lowerTailV, lowerTailTransformV, lowerTailModelV);
        lowerTailV.addChild(lowerTailHTranslate);
          lowerTailHTranslate.addAllChildren(lowerTailH, lowerTailTransformH, lowerTailModelH);

    // Middle tail
    TransformNode middleTailVTranslate = new TransformNode("Vertical middle tail translate",
        Mat4Transform.translate(0, MIDDLE_HEIGHT / 2, -(LOWER_DEPTH - MIDDLE_HEIGHT) / 2));

    NameNode middleTailV = new NameNode("Vertical middle tail");
    m = Mat4Transform.scale(WIDTH, MIDDLE_DEPTH, MIDDLE_HEIGHT);
    TransformNode middleTailTransformV = new TransformNode("Vertical middle tail transform", m);
    ModelNode middleTailModelV = new ModelNode("Vertical middle tail model", lowerTail); // Use lowerTail texture

    TransformNode middleTailHTranslate = new TransformNode("Horizontal middle tail translate",
        Mat4Transform.translate(0, MIDDLE_HEIGHT / 2, -MIDDLE_HEIGHT / 2));

    NameNode middleTailH = new NameNode("Horizontal middle tail");
    float offset = 0.001f; // minimal offset so texture colour don't overlap each other
    m = Mat4Transform.scale(WIDTH - offset, MIDDLE_HEIGHT - offset, MIDDLE_DEPTH - offset);
    TransformNode middleTailTransformH = new TransformNode("Horizontal middle tail transform", m);
    ModelNode middleTailModelH = new ModelNode("Horizontal middle tail model", cube);

    lowerTailH.addChild(middleTailVTranslate);
      middleTailVTranslate.addAllChildren(middleTailV, middleTailTransformV, middleTailModelV);
        middleTailV.addChild(middleTailHTranslate);
          middleTailHTranslate.addAllChildren(middleTailH, middleTailTransformH, middleTailModelH);

    // Upper tail
    TransformNode upperTailVTranslate = new TransformNode("Vertical upper tail",
        Mat4Transform.translate(0, UPPER_HEIGHT / 2, -(MIDDLE_DEPTH - UPPER_HEIGHT) / 2));

    NameNode upperTailV = new NameNode("Vertical upper tail");
    m = Mat4Transform.scale(WIDTH + offset, UPPER_DEPTH + offset, UPPER_HEIGHT + offset);
    TransformNode upperTailTransformV = new TransformNode("Vertical upper tail transform", m);
    ModelNode upperTailModelV = new ModelNode("Vertical upper tail model", cube);

    NameNode upperTailH = new NameNode("Horizontal upper tail");
    m = Mat4Transform.scale(WIDTH, UPPER_HEIGHT, UPPER_DEPTH);
    m = Mat4.multiply(Mat4Transform.translate(0, UPPER_HEIGHT / 2, -UPPER_HEIGHT / 2), m);
    TransformNode upperTailTransformH = new TransformNode("Horizontal upper tail transform", m);
    ModelNode upperTailModelH = new ModelNode("Horizontal upper tail model", cube);

    middleTailH.addChild(upperTailVTranslate);
      upperTailVTranslate.addAllChildren(upperTailV, upperTailTransformV, upperTailModelV);
        upperTailV.addAllChildren(upperTailH, upperTailTransformH, upperTailModelH);
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
