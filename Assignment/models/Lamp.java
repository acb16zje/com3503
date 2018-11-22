package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;

/**
 * A class for rendering a lamp with spotlight and animation
 *
 * @author Zer Jun Eng
 */
public class Lamp {

  private Model cylinder, sphere, frustumCone;
  private Light lampLight;

  private float lampRadius, baseHeight, jointRadius, bodyRadius, lowerBodyHeight, upperBodyHeight;
  private float lampX;

  private final float DEFAULT_LOWER_JOINT_ANGLE = 30;
  private final float DEFAULT_UPPER_JOINT_ANGLE = -90;
  private final float DEFAULT_HEAD_JOINT_ANGLE = 10;

  /**
   * Lamp constructor
   *
   * @param cylinder Cylinder shape
   * @param sphere Sphere shape
   * @param frustumCone Frustum cone shape
   * @param lampLight Light bulb
   */
  public Lamp(Model cylinder, Model sphere, Model frustumCone, Light lampLight) {
    this.cylinder = cylinder;
    this.sphere = sphere;
    this.frustumCone = frustumCone;
    this.lampLight = lampLight;

    lampRadius = Table.tableWidth * 0.1f;
    baseHeight = Table.tableHeight * 0.06f;
    jointRadius = lampRadius * 0.2f;
    bodyRadius = jointRadius / 2;
    lowerBodyHeight = Table.tableHeight * 0.45f;
    upperBodyHeight = Table.tableHeight * 0.4f;

    lampX = -Table.tableWidth / 2 + 2;
  }

  /**
   * Renders a lamp
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    SGNode lampRoot = new NameNode("Lamp root");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(lampX, (Table.FRAME_DIM + baseHeight / 2) / 2, 0));

    Table.tableTop.addChild(lampRoot);
      lampRoot.addChild(rootTranslate);
        createBase(rootTranslate);

    Table.tableRoot.update();
    lampRoot.update();
    lampRoot.draw(gl);
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

    NameNode innerBase = new NameNode("Inner base");
    Mat4 m = Mat4Transform.scale(lampRadius, baseHeight, lampRadius);
    TransformNode innerBaseTransform = new TransformNode("Inner base transform", m);
    ModelNode innerBaseModel = new ModelNode("Inner base model", cylinder);

    NameNode outerBase = new NameNode("Outer base");
    m = Mat4Transform.scale(outerBaseRadius, outerBaseHeight, outerBaseRadius);
    m = Mat4.multiply(Mat4Transform.translate(0, outerBasePosY, 0), m);
    TransformNode outerBaseTransform = new TransformNode("Outer base transform", m);
    ModelNode outerBaseModel = new ModelNode("Outer base model", cylinder);

    parent.addAllChildren(innerBase, innerBaseTransform, innerBaseModel);
      createLowerBody(innerBase);
    parent.addAllChildren(outerBase, outerBaseTransform, outerBaseModel);
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

    TransformNode lowerJointRotate = new TransformNode("Lower joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_LOWER_JOINT_ANGLE));

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
      lowerJointTranslate.addChild(lowerJointRotate);
        lowerJointRotate.addAllChildren(lowerJoint, lowerJointTransform, lowerJointModel);
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

    TransformNode upperJointRotate = new TransformNode("Upper joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_UPPER_JOINT_ANGLE));

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
      upperJointTranslate.addChild(upperJointRotate);
        upperJointRotate.addAllChildren(upperJoint, upperJointTransform, upperJointModel);
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

    TransformNode headJointRotate = new TransformNode("Head joint rotate",
        Mat4Transform.rotateAroundZ(DEFAULT_HEAD_JOINT_ANGLE));

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
      headJointTranslate.addChild(headJointRotate);
        headJointRotate.addAllChildren(headJoint, headJointTransform, headJointModel);
          headJoint.addChild(backHeadTranslate);
            backHeadTranslate.addChild(backHeadRotate);
              backHeadRotate.addAllChildren(backHead, backHeadTranform, backHeadModel);
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
}
