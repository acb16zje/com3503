package models;

import com.jogamp.opengl.*;
import java.util.*;
import lib.*;
import lib.gmaths.*;

/**
 * A class for rendering a lamp with spotlight and animation
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class Lamp {

  private SGNode lampRoot;
  private TransformNode rootTranslateX;
  private TransformNode baseRotateY, baseRotateZ;
  private TransformNode lowerJointRotateZ;
  private TransformNode upperJointRotateZ;
  private TransformNode headJointRotateY, headJointRotateZ;

  private Model cube, cylinder, sphere, frustumCone, lampEar, lowerTail;
  private Light lampLight;

  private float lampRadius, baseHeight, jointRadius, bodyRadius, lowerBodyHeight, upperBodyHeight;
  private float lampX, lampY;

  private double startTime;

  public boolean clickedRandom = false;
  public boolean isAnimatingRandom = false;

  public boolean clickedReset = false;
  public boolean isAnimatingReset = false;

  public boolean clickedJump = false;
  public boolean isAnimatingJump = false;
  private boolean preparingToJump = false;
  private float jumpHeight, jumpSpeed = 1;

  private final int DEFAULT_BASE_ANGLE_Y = -20;
  private float initialBaseAngle = DEFAULT_BASE_ANGLE_Y, targetBaseAngle, baseSwingAngle;
  private float initialPosX, targetPosX, targetPosZ, initialPosZ, distance, maxDistance;

  private final int DEFAULT_LOWER_JOINT_ANGLE_Z = 10;
  private final int MIN_LOWER_JOINT_ANGLE_Z = -20;
  private final int MAX_LOWER_JOINT_ANGLE_Z = 60;
  private float initialLowerJointAngle = DEFAULT_LOWER_JOINT_ANGLE_Z, targetLowerJointAngle;

  private final int DEFAULT_UPPER_JOINT_ANGLE_Z = -60;
  private final int MIN_UPPER_JOINT_ANGLE_Z = -120;
  private final int MAX_UPPER_JOINT_ANGLE_Z = 0;
  private float initialUpperJointAngle = DEFAULT_UPPER_JOINT_ANGLE_Z, targetUpperJointAngle;

  private final int DEFAULT_HEAD_JOINT_ANGLE_Y = 0;
  private final int MIN_HEAD_JOINT_ANGLE_Y = -80;
  private final int MAX_HEAD_JOINT_ANGLE_Y = 80;
  private float initialHeadJointAngleY = DEFAULT_HEAD_JOINT_ANGLE_Y, targetHeadJointAngleY;

  private final int DEFAULT_HEAD_JOINT_ANGLE_Z = -10;
  private final int MIN_HEAD_JOINT_ANGLE_Z = -30;
  private final int MAX_HEAD_JOINT_ANGLE_Z = 50;
  private float initialHeadJointAngleZ = DEFAULT_HEAD_JOINT_ANGLE_Z, targetHeadJointAngleZ;

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
    lowerBodyHeight = Table.tableHeight * 0.4f;
    upperBodyHeight = Table.tableHeight * 0.37f;

    lampX = -Table.tableWidth / 2 + 2;
    lampY = (Table.FRAME_DIM + baseHeight / 2) / 2;
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    initialPosX = lampX;

    lampRoot = new NameNode("Lamp root");
    TransformNode rootTranslateY = new TransformNode("Root translate Y",
        Mat4Transform.translate(0, lampY, 0));

    rootTranslateX = new TransformNode("Root translate X",
        Mat4Transform.translate(lampX, 0, 0));

    Table.tableTop.addChild(lampRoot);
      lampRoot.addChild(rootTranslateY);
        rootTranslateY.addChild(rootTranslateX);
          createBase(rootTranslateX);

    Table.tableRoot.update();
    lampRoot.update();
  }

  /**
   * Renders a lamp
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    if (clickedRandom || isAnimatingRandom) {
      if (clickedRandom) {
        calculateRandomPose();
      } else {
        float elapsedTime = (float) Math.sin(getSeconds() - startTime);
        changePose(elapsedTime);
      }
    } else if (clickedReset || isAnimatingReset) {
      if (clickedReset) {
        calculateResetPose();
      } else {
        float elapsedTime = (float) Math.sin(getSeconds() - startTime);
        changePose(elapsedTime);
      }
    } else if (clickedJump || isAnimatingJump) {
      if (clickedJump) {
        calculateTarget();
        calculateJump();
      } else {
        float elapsedTime = (float) (getSeconds() - startTime);

        if (preparingToJump) {
          changePose((float) Math.sin(elapsedTime));
        } else {
          jump(elapsedTime);
        }
      }
    }

    lampRoot.update();
    lampRoot.draw(gl);
  }

  /*------------------ ANIMATION -----------------------*/
  /**
   * Calculates the random rotation angles of the joints, while maintaining the lamp balance
   */
  private void calculateRandomPose() {
    targetBaseAngle = 0;

    /* Lower joint */
    float min = MIN_LOWER_JOINT_ANGLE_Z - initialLowerJointAngle;
    float max = MAX_LOWER_JOINT_ANGLE_Z - initialLowerJointAngle;
    targetLowerJointAngle = min + r.nextFloat() * (max - min);

    float finalLowerJointAngle = initialLowerJointAngle + targetLowerJointAngle;

    /* Upper joint, try to maintain the balance of the lamp */
    if (finalLowerJointAngle > 0) {
      max = MIN_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle;
      min = MIN_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle + finalLowerJointAngle / 2;
    } else {
      max = MAX_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle;
      min = initialLowerJointAngle + targetLowerJointAngle;
    }

    targetUpperJointAngle = min + r.nextFloat() * (max - min);

    /* Head joint */
    max = MAX_HEAD_JOINT_ANGLE_Y - initialHeadJointAngleY;
    min = MIN_HEAD_JOINT_ANGLE_Y - initialHeadJointAngleY;
    targetHeadJointAngleY = min + r.nextFloat() * (max - min);

    max = MAX_HEAD_JOINT_ANGLE_Z - initialHeadJointAngleZ;
    min = MIN_HEAD_JOINT_ANGLE_Z - initialHeadJointAngleZ;
    targetHeadJointAngleZ = min + r.nextFloat() * (max - min);

    clickedRandom = false;
    isAnimatingRandom = true;
    startTime = getSeconds();
  }

  /**
   * Calculate the reset pose
   */
  private void calculateResetPose() {
    targetBaseAngle = 0;
    targetLowerJointAngle = DEFAULT_LOWER_JOINT_ANGLE_Z - initialLowerJointAngle;
    targetUpperJointAngle = DEFAULT_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle;
    targetHeadJointAngleY = DEFAULT_HEAD_JOINT_ANGLE_Y - initialHeadJointAngleY;
    targetHeadJointAngleZ = DEFAULT_HEAD_JOINT_ANGLE_Z - initialHeadJointAngleZ;

    clickedReset = false;
    isAnimatingReset = true;
    startTime = getSeconds();
  }

  /**
   * Calculate the random position, and base angle to rotate
   */
  private void calculateTarget() {
    /* Random position */
    final float BORDER_X = lampRadius / 1.5f;
    final float MIN_POS_X = -Table.tableWidth / 2 + BORDER_X;
    final float MAX_POS_X = Table.tableWidth / 2 - BORDER_X;

    // Prevent intersecting with table objects and wall
    final float BORDER_Z = Math.abs(PictureFrame.holderZ) + lampRadius / 2;
    final float MIN_POS_Z = Table.tableDepth / 2 - BORDER_X;
    final float MAX_POS_Z = -Table.tableDepth / 2 + BORDER_Z;
    maxDistance = (float) Math.sqrt(Math.pow(MAX_POS_X - MIN_POS_X, 2) + Math.pow(MAX_POS_Z - MIN_POS_Z, 2));

    // Do not make very very small jump
    float deltaX, deltaZ;
    do {
      targetPosX = MIN_POS_X + r.nextFloat() * (MAX_POS_X - MIN_POS_X);
      targetPosZ = MIN_POS_Z + r.nextFloat() * (MAX_POS_Z - MIN_POS_Z);
      deltaX = targetPosX - initialPosX;
      deltaZ = targetPosZ - initialPosZ;
      distance = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    } while (distance < 1);

    /* Base angle to rotate */
    // Acute angle between the initial position and the target position
    targetBaseAngle = (float) Math.abs(Math.toDegrees(Math.asin(deltaZ / distance)));

    // Calculates the actual angle required to rotate to target position
    if (deltaX > 0 && deltaZ < 0) {
      targetBaseAngle -= initialBaseAngle;                                 // First quadrant
    } else if (deltaX < 0 && deltaZ > 0) {
      targetBaseAngle = -180 + targetBaseAngle - initialBaseAngle;         // Second quadrant
    } else if (deltaX < 0 && deltaZ < 0) {
      targetBaseAngle = 180 - targetBaseAngle - initialBaseAngle;          // Third quadrant
    } else if (deltaX > 0 && deltaZ > 0) {
      targetBaseAngle = -targetBaseAngle - initialBaseAngle;               // Fourth quadrant
    }

    // Always rotate the smallest possible angle
    targetBaseAngle %= 360;
    if (targetBaseAngle > 180) targetBaseAngle -= 360;
    if (targetBaseAngle < -180) targetBaseAngle += 360;
  }

  /**
   * Calculate the jump height, jump speed, and compression angle
   */
  private void calculateJump() {
    /* Jump height and speed */
    final float HEIGHT_CONSTANT = 0.21f;
    jumpHeight = HEIGHT_CONSTANT * distance + 0.5f;

    final float SPEED_CONSTANT = 2.8f;
    jumpSpeed = SPEED_CONSTANT / jumpHeight;

    /* Angle to compress */
    final float BASE_CONSTANT = 40;
    baseSwingAngle = BASE_CONSTANT * (float) Math.log(distance);

    final float COMPRESS_CONSTANT = distance / maxDistance;
    targetLowerJointAngle = (MAX_LOWER_JOINT_ANGLE_Z - initialLowerJointAngle) * COMPRESS_CONSTANT;
    targetUpperJointAngle = (MIN_UPPER_JOINT_ANGLE_Z - initialUpperJointAngle) * COMPRESS_CONSTANT;
    targetHeadJointAngleY = DEFAULT_HEAD_JOINT_ANGLE_Y - initialHeadJointAngleY;
    targetHeadJointAngleZ = (MAX_HEAD_JOINT_ANGLE_Z - initialHeadJointAngleZ) / 2;

    clickedJump = false;
    isAnimatingJump = true;
    preparingToJump = true;
    startTime = getSeconds();
  }

  /**
   * The lamp will change its current pose
   *
   * @param time The elapsed time
   */
  private void changePose(float time) {
    float rotateAngleBY = initialBaseAngle + targetBaseAngle * time;            // Base Angle Y
    float rotateAngleL = initialLowerJointAngle + targetLowerJointAngle * time; // Lower joint
    float rotateAngleU = initialUpperJointAngle + targetUpperJointAngle * time; // Upper joint
    float rotateAngleHY = initialHeadJointAngleY + targetHeadJointAngleY * time; // Head joint Y
    float rotateANgleHZ = initialHeadJointAngleZ + targetHeadJointAngleZ * time; // Head joint Z

    float finalBaseAngleY = initialBaseAngle + targetBaseAngle - rotateAngleBY;
    float finalAngleL = initialLowerJointAngle + targetLowerJointAngle - rotateAngleL;
    float finalAngleU = initialUpperJointAngle + targetUpperJointAngle - rotateAngleU;
    float finalAngleHY = initialHeadJointAngleY + targetHeadJointAngleY - rotateAngleHY;
    float finalAngleHZ = initialHeadJointAngleZ + targetHeadJointAngleZ - rotateANgleHZ;

    final float THRESHOLD = 0.1f;

    if (Math.abs(finalBaseAngleY) < THRESHOLD && Math.abs(finalAngleL) < THRESHOLD &&
        Math.abs(finalAngleU) < THRESHOLD && Math.abs(finalAngleHY) < THRESHOLD &&
        Math.abs(finalAngleHZ) < THRESHOLD) {
      // Update the initial angles
      initialBaseAngle += targetBaseAngle;
      initialLowerJointAngle += targetLowerJointAngle;
      initialUpperJointAngle += targetUpperJointAngle;
      initialHeadJointAngleY += targetHeadJointAngleY;
      initialHeadJointAngleZ += targetHeadJointAngleZ;

      // Random, reset and compress are sharing this method
      isAnimatingRandom = false;
      isAnimatingReset = false;
      preparingToJump = false;
      startTime = getSeconds();
    } else {
      baseRotateY.setTransform(Mat4Transform.rotateAroundY(rotateAngleBY));
      lowerJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleL));
      upperJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleU));
      headJointRotateY.setTransform(Mat4Transform.rotateAroundY(rotateAngleHY));
      headJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateANgleHZ));
    }
  }

  /**
   * The lamp will jump to random position, height and speed are affected by the distance
   *
   * @param time The elapsed time
   */
  private void jump(float time) {
    time = (float) Math.sin(time * jumpSpeed); // Speed is affected by height and distance

    float translateBPosX = initialPosX + (targetPosX - initialPosX) * time; // Base Pos X
    float translateBPosY = bezierCurve(jumpHeight, jumpHeight, time); // Base Pos Y
    float translateBPosZ = initialPosZ + (targetPosZ - initialPosZ) * time; // Base Pos Z

    // Fine tuning the jumping animation
    float rotateAngleBZ = bezierCurve(-baseSwingAngle / 1.2f, baseSwingAngle, time); // Base swing

    final float STRETCH = (initialLowerJointAngle - DEFAULT_LOWER_JOINT_ANGLE_Z) * 3.5f;
    float rotateAngleU = initialUpperJointAngle + bezierCurve(STRETCH, -STRETCH / 2, time); // Upper joint stretch and compress

    float finalBasePosX = targetPosX - translateBPosX;
    float finalBasePosZ = targetPosZ - translateBPosZ;

    final float THRESHOLD = 0.005f;

    if (Math.abs(finalBasePosX) < THRESHOLD && Math.abs(finalBasePosZ) < THRESHOLD) {
      initialPosX = targetPosX;
      initialPosZ = targetPosZ;
      clickedReset = true;
      isAnimatingJump = false;
    } else {
      rootTranslateX.setTransform(Mat4Transform.translate(translateBPosX, translateBPosY, translateBPosZ));
      baseRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleBZ));
      upperJointRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleU));
    }
  }

  /**
   * Bezier cubic curve calculation
   *
   * @param p1 Guide point P1
   * @param p2 Guide point P2
   * @param t The time, 0 < t < 1
   * @return The value at a the given t
   */
  private float bezierCurve(float p1, float p2, float t) {
    double p1Part = p1 * 3 * t * Math.pow((1 - t), 2);
    double p2Part = p2 * 3 * Math.pow(t, 2) * (1 - t);

    return (float) (p1Part + p2Part);
  }

  /*------------------ SCENE GRAPH -----------------------*/
  /**
   * Renders the cylinder base of the lamp
   *
   * @param parent Parent node
   */
  private void createBase(SGNode parent) {
    float outerBaseRadius = lampRadius + 0.005f;
    float outerBaseHeight = baseHeight / 3.5f;
    float outerBasePosY = -baseHeight / 4 + outerBaseHeight / 4;

    baseRotateY = new TransformNode("Base rotate Y",
        Mat4Transform.rotateAroundY(DEFAULT_BASE_ANGLE_Y));

    // For jumping use
    baseRotateZ = new TransformNode("Base rotate Z", Mat4Transform.rotateAroundZ(0));

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
      baseRotateY.addChild(baseRotateZ);
        baseRotateZ.addAllChildren(innerBase, innerBaseTransform, innerBaseModel);
          createLowerBody(innerBase);
        baseRotateZ.addAllChildren(outerBase, outerBaseTransform, outerBaseModel);
  }

  /**
   * Creates the lower body of the lamp
   *
   * @param parent Parent node
   */
  private void createLowerBody(SGNode parent) {
    final float POS_Y = baseHeight / 3;

    TransformNode lowerJointTranslate = new TransformNode("Lower joint translate",
        Mat4Transform.translate(0, POS_Y, 0));

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
    final float LOWER_HEIGHT = 0.11f;
    final float LOWER_DEPTH = 0.22f;
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
    float offset = 0.001f; // minimal offset so texture colour don't overlap each other
    m = Mat4Transform.scale(WIDTH + offset, MIDDLE_DEPTH + offset, MIDDLE_HEIGHT + offset);
    TransformNode middleTailTransformV = new TransformNode("Vertical middle tail transform", m);
    ModelNode middleTailModelV = new ModelNode("Vertical middle tail model", lowerTail); // Use lowerTail texture

    TransformNode middleTailHTranslate = new TransformNode("Horizontal middle tail translate",
        Mat4Transform.translate(0, MIDDLE_HEIGHT / 2, -MIDDLE_HEIGHT / 2));

    NameNode middleTailH = new NameNode("Horizontal middle tail");
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
    m = Mat4Transform.scale(WIDTH, UPPER_DEPTH, UPPER_HEIGHT);
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
