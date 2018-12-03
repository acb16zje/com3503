package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * A class for rendering the outside scene
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class OutsideScene {

  private SGNode sceneRoot;

  private final Model grass;
  private final Model garden;
  private final Model leftGarden;
  private final Model rightGarden;
  private final Model sky;

  private final double startTime;
  private final float roomWidth;
  private final float roomHeight;
  private final float roomDepth;
  private final float grassDepth;

  /**
   * Outside scene constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   * @param grass Bottom grass
   * @param garden Centre garden
   * @param leftGarden Left garden
   * @param rightGarden Right garden
   * @param sky Top sky
   */
  public OutsideScene(Vec3 roomDimension, Model grass, Model garden,
      Model leftGarden, Model rightGarden, Model sky) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.grass = grass;
    this.garden = garden;
    this.leftGarden = leftGarden;
    this.rightGarden = rightGarden;
    this.sky = sky;

    grassDepth = roomDepth * 2;
    startTime = getSeconds();
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    sceneRoot = new NameNode("Outside scene root");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(0, 0, -(roomDepth + grassDepth) / 2 - Cube.THICKNESS));

    sceneRoot.addChild(rootTranslate);
      createGrass(rootTranslate);
      createGarden(rootTranslate);
      createLeftRightGarden(rootTranslate);
      createSky(rootTranslate);
    sceneRoot.update();
  }

  /**
   * Renders the outside scene
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    double elapsedTime = getSeconds() - startTime;
    double wavelength = elapsedTime * 0.5;
    double t = wavelength * 0.1;
    float cosine = (float) ((Math.cos(wavelength) + 1) * 0.5);
    float offsetX = (float) (t - Math.floor(t));
    float offsetY = (float) (Math.sin(wavelength) * 0.1);

    garden.setDayNightCycle(cosine);
    sky.setMovingTexture(offsetX, offsetY);
    sceneRoot.draw(gl);
  }

  /**
   * Creates a grass on the ground
   *
   * @param parent Parent node
   */
  private void createGrass(SGNode parent) {
    NameNode grass = new NameNode("Grass");
    Mat4 m = Mat4Transform.scale(roomWidth, 1, grassDepth);
    TransformNode grassTransform = new TransformNode("Grass transform", m);
    ModelNode grassModel = new ModelNode("Grass model", this.grass);

    parent.addAllChildren(grass, grassTransform, grassModel);
  }

  /**
   * Creates a centre garden
   *
   * @param parent Parent node
   */
  private void createGarden(SGNode parent) {
    NameNode garden = new NameNode("Centre garden");
    Mat4 m = Mat4Transform.scale(roomWidth, 1, roomHeight);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, roomHeight / 2, -grassDepth / 2), m);
    TransformNode gardenTransform = new TransformNode("Centre garden transform", m);
    ModelNode gardenModel = new ModelNode("Centre garden model", this.garden);

    parent.addAllChildren(garden, gardenTransform, gardenModel);
  }

  /**
   * Creates left and right garden
   *
   * @param parent Parent node
   */
  private void createLeftRightGarden(SGNode parent) {
    NameNode leftGarden = new NameNode("Left garden");
    Mat4 m = Mat4Transform.scale(roomHeight, 1, grassDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), m);
    m = Mat4.multiply(Mat4Transform.translate(-roomWidth / 2, roomHeight / 2, 0), m);
    TransformNode leftGardenTransform = new TransformNode("Left garden transform", m);
    ModelNode leftGardenModel = new ModelNode("Left garden model", this.leftGarden);

    NameNode rightGarden = new NameNode("Right garden");
    m = Mat4Transform.scale(roomHeight, 1, grassDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(90), m);
    m = Mat4.multiply(Mat4Transform.translate(roomWidth / 2, roomHeight / 2, 0), m);
    TransformNode rightGardenTransform = new TransformNode("Right garden transform", m);
    ModelNode rightGardenModel = new ModelNode("Right garden model", this.rightGarden);

    parent.addAllChildren(leftGarden, leftGardenTransform, leftGardenModel);
    parent.addAllChildren(rightGarden, rightGardenTransform, rightGardenModel);
  }

  /**
   * Creates a sky at the top
   *
   * @param parent Parent node
   */
  private void createSky(SGNode parent) {
    NameNode sky = new NameNode("Sky");
    Mat4 m = Mat4Transform.scale(roomWidth, 1, grassDepth);
    m = Mat4.multiply(Mat4Transform.rotateAroundZ(180), m);
    m = Mat4.multiply(Mat4Transform.translate(0, roomHeight, 0), m);
    TransformNode skyTransform = new TransformNode("Sky transform", m);
    ModelNode skyModel = new ModelNode("Sky model", this.sky);

    parent.addAllChildren(sky, skyTransform, skyModel);
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
