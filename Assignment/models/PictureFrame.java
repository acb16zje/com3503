package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work
 * A class for rendering a picture frame with picture on the top right corner of the table
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class PictureFrame {

  private SGNode frameRoot;

  private Model frame, holder, picture;

  private float frameWidth, frameHeight, holderHeight;

  // The X and Z position of the picture frame holder (parent of frame)
  private float holderX;
  static float holderZ;

  // Used to define the height of horizontal bars, and width of vertical bars
  private final float FRAME_DIM = Cube.THICKNESS * 0.15f;
  private final float FRAME_DEPTH = Cube.THICKNESS / 16;

  /**
   * Picture frame constructor
   *
   * @param frame Cube horizontal frame model
   * @param holder Cube holder model
   * @param picture TwoTriangles picture model
   */
  public PictureFrame(Model frame, Model holder, Model picture) {
    this.frame = frame;
    this.holder = holder;
    this.picture = picture;

    // Dimension ratio of picture frame with respect to table width
    frameWidth = Table.tableWidth * 0.1f;
    frameHeight = Table.tableWidth * 0.1f;
    holderHeight = frameHeight / 2;

    // Top right corner
    holderX = Table.tableWidth / 2 - 0.5f;
    holderZ = -Table.tableDepth / 2 + 0.8f;
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    frameRoot = new NameNode("Picture frame root (frame holder)");
    TransformNode rootTranslate = new TransformNode("Root translate",
        Mat4Transform.translate(holderX, holderHeight / 2 + 0.1f, holderZ));

    Table.tableTop.addChild(frameRoot);
      frameRoot.addChild(rootTranslate);
        createFrameHolder(rootTranslate);

    Table.tableRoot.update();
    frameRoot.update();
  }

  /**
   * Renders a complete picture frame with frame, picture, and support holder
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    frameRoot.draw(gl);
  }

  /**
   * Creates a picture frame holder inclined at an 45 degree with the table top
   *
   * @param parent Parent node
   */
  private void createFrameHolder(SGNode parent) {
    TransformNode frameHolderRotate = new TransformNode("Frame holder rotate",
        Mat4.multiply(Mat4Transform.rotateAroundY(-45), Mat4Transform.rotateAroundX(30)));

    NameNode frameHolder = new NameNode("Frame holder");
    Mat4 m = Mat4Transform.scale(FRAME_DIM, holderHeight, FRAME_DEPTH);
    TransformNode frameHolderTransform = new TransformNode("Frame holder transform", m);
    ModelNode frameHolderModel = new ModelNode("Frame holder model", holder);

    parent.addChild(frameHolderRotate);
      frameHolderRotate.addAllChildren(frameHolder, frameHolderTransform, frameHolderModel);
        createBackSupport(frameHolder);
  }

  /**
   * Creates a backbaord
   *
   * @param parent Parent node
   */
  private void createBackSupport(SGNode parent) {
    Mat4 m = Mat4Transform.rotateAroundX(120);
    m = Mat4.multiply(Mat4Transform.translate(0, holderHeight / 2, 0), m);

    TransformNode backBoardRotate = new TransformNode("Back board rotate", m);

    NameNode backBoard = new NameNode("Back board");
    m = Mat4Transform.scale(frameWidth, frameHeight, FRAME_DEPTH);
    TransformNode backBoardTransform = new TransformNode("Back board transform", m);
    ModelNode backBoardModel = new ModelNode("Back board model", holder);

    parent.addChild(backBoardRotate);
      backBoardRotate.addAllChildren(backBoard, backBoardTransform, backBoardModel);
      createFrame(backBoard);
  }

  /**
   * Creates a picture frame
   *
   * @param parent Parent node
   */
  private void createFrame(SGNode parent) {
    NameNode frame = new NameNode("Frame");
    Mat4 m = Mat4Transform.scale(frameWidth, frameHeight, 0.01f);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, -FRAME_DEPTH / 2), m);
    TransformNode frameTransform = new TransformNode("Frame transform", m);
    ModelNode frameModel = new ModelNode("Frame model", this.frame);

    parent.addAllChildren(frame, frameTransform, frameModel);
      createPicture(frame);
  }

  /**
   * Create a picture on the center of the picture frame
   *
   * @param parent Parent node
   */
  private void createPicture(SGNode parent) {
    // 0.007f offset to prevent the picture from appearing to "merge" with the backboard
    final float POS_Z = -FRAME_DEPTH / 2 - 0.007f;

    NameNode picture = new NameNode("Picture");
    Mat4 m = Mat4Transform.scale(frameWidth - FRAME_DIM * 2, frameHeight, 1);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(-90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, 0, POS_Z), m);
    TransformNode pictureTransform = new TransformNode("Picture transform", m);
    ModelNode pictureModel = new ModelNode("Picture model", this.picture);

    parent.addAllChildren(picture, pictureTransform, pictureModel);
  }
}
