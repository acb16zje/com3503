package models;

import com.jogamp.opengl.*;
import lib.*;
import lib.gmaths.*;
import shapes.*;

/**
 * I declare that this code is my own work.
 * A class for rendering a floor, a wall with a hole, and a wallpaper with a hole
 *
 * @author Zer Jun Eng
 */
public class Room {

  private SGNode roomRoot;

  private Model floor, wall;                                                   // Cube models
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper;  // TwoTriangles

  private float roomWidth, roomHeight, roomDepth;
  private float windowWidth, windowMaxYHeight;
  private float leftWallWidth, leftWallHeight;
  private float bottomWallWidth, bottomWallHeight, topWallHeight;

  /**
   * Room constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   * @param floor Cube floo model
   */
  public Room(Vec3 roomDimension, Model floor, Model wall) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.floor = floor;
    this.wall = wall;

    windowWidth = roomWidth * Window.RATIO.x;
    windowMaxYHeight = roomHeight * (Window.Y_POS + Window.RATIO.y);

    leftWallWidth = (roomWidth - windowWidth) / 2;
    leftWallHeight = roomHeight + Cube.THICKNESS / 2;
    bottomWallWidth = windowWidth;
    bottomWallHeight = (roomHeight + Cube.THICKNESS) * Window.Y_POS;
    topWallHeight = roomHeight - windowMaxYHeight;
  }

  /**
   * Initialises the scene graph
   */
  public void initialise() {
    final float POS_Z =  -(roomDepth + Cube.THICKNESS) / 2;

    roomRoot = new NameNode("Room root");
    TransformNode wallTransform = new TransformNode("Wall transform",
        Mat4Transform.translate(0, 0, POS_Z));

    createFloor(roomRoot);
    roomRoot.addChild(wallTransform);
      createBottomWall(wallTransform);
      createLeftRightWall(wallTransform);
      createTopWall(wallTransform);

    roomRoot.update();
  }

  /**
   * Renders floor, wallpaper, and wall
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    roomRoot.draw(gl);
  }

  /**
   * Creates a floor
   *
   * @param parent Parent node
   */
  private void createFloor(SGNode parent) {
    NameNode floor = new NameNode("Floor");
    Mat4 m = Mat4Transform.scale(roomWidth, 1, roomDepth);
    TransformNode floorTransform = new TransformNode("Floor transform", m);
    ModelNode floorModel = new ModelNode("Floor model", this.floor);

    parent.addAllChildren(floor, floorTransform, floorModel);
  }

  /**
   * Creates a bottom wall with wallpaper
   *
   * @param parent Parent node
   */
  private void createBottomWall(SGNode parent) {
    final float WALL_POS_Y = (bottomWallHeight - Cube.THICKNESS) / 2;

    float wallpaperHeight = bottomWallHeight - Cube.THICKNESS * (Window.Y_POS + 0.5f);
    final float WALLPAPER_POS_Y = (wallpaperHeight + Cube.THICKNESS) / 2;

    NameNode bottomWall = new NameNode("Bottom wall");
    Mat4 m = Mat4Transform.scale(bottomWallWidth, bottomWallHeight, 1);
    m = Mat4.multiply(Mat4Transform.translate(0, WALL_POS_Y, 0), m);
    TransformNode bottomWallTransform = new TransformNode("Bottom wall transform", m);
    ModelNode bottomWallModel = new ModelNode("Bottom wall model", wall);

    NameNode bottomWallpaper = new NameNode("Bottom wallpaper");
    m = Mat4Transform.scale(bottomWallWidth, 1, wallpaperHeight);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, WALLPAPER_POS_Y, Cube.THICKNESS / 2), m);
    TransformNode bottomWallpaperTransform = new TransformNode("Bottom wallpaper transform", m);
    ModelNode bottomWallpaperModel = new ModelNode("Bottom wallpaper model", this.bottomWallpaper);

    parent.addAllChildren(bottomWall, bottomWallTransform, bottomWallModel);
    parent.addAllChildren(bottomWallpaper, bottomWallpaperTransform, bottomWallpaperModel);
  }

  /**
   * Creates a left wall with wallpaper and right wall with wallpaper
   *
   * @param parent Parent node
   */
  private void createLeftRightWall(SGNode parent) {
    final float POS_X = (roomWidth - leftWallWidth) / 2;
    final float POS_Y = (leftWallHeight - Cube.THICKNESS) / 2;

    float wallpaperHeight = leftWallHeight - Cube.THICKNESS * (Window.Y_POS + 0.5f);
    final float WALLPAPER_POS_Y = (wallpaperHeight + Cube.THICKNESS) / 2;

    NameNode leftWall = new NameNode("Left wall");
    Mat4 m = Mat4Transform.scale(leftWallWidth, leftWallHeight, 1);
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, POS_Y, 0), m);
    TransformNode leftWallTransform = new TransformNode("Left wall transform", m);
    ModelNode leftWallModel = new ModelNode("Left wall modeL", wall);

    NameNode rightWall = new NameNode("Right wall");
    m = Mat4.multiply(Mat4Transform.translate(POS_X * 2, 0, 0), m);
    TransformNode rightWallTransform = new TransformNode("Right wall transform", m);
    ModelNode rightWallModel = new ModelNode("Right wall model", wall);

    NameNode leftWallpaper = new NameNode("Left wallpaper");
    m = Mat4Transform.scale(leftWallWidth, 1, wallpaperHeight);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-POS_X, WALLPAPER_POS_Y, Cube.THICKNESS / 2), m);
    TransformNode leftWallpaperTransform = new TransformNode("Left wallpaper transform", m);
    ModelNode leftWallpaperModel = new ModelNode("Left wallpaper model", this.leftWallpaper);

    NameNode rightWallpaper = new NameNode("Right wallpaper");
    m = Mat4.multiply(Mat4Transform.translate(POS_X * 2, 0, 0), m);
    TransformNode rightWallpaperTransform = new TransformNode("Right wallpaper transform", m);
    ModelNode rightWallpaperModel = new ModelNode("Right wallpaper model", this.rightWallpaper);

    parent.addAllChildren(leftWall, leftWallTransform, leftWallModel);
    parent.addAllChildren(leftWallpaper, leftWallpaperTransform, leftWallpaperModel);
    parent.addAllChildren(rightWall, rightWallTransform, rightWallModel);
    parent.addAllChildren(rightWallpaper, rightWallpaperTransform, rightWallpaperModel);
  }

  /**
   * Creates a top wall with wallpaper
   *
   * @param parent Parent node
   */
  private void createTopWall(SGNode parent) {
    final float POS_Y = windowMaxYHeight + topWallHeight / 2;
    final float WALLPAPER_POS_Z = Cube.THICKNESS / 2;

    NameNode topWall = new NameNode("Top wall");
    Mat4 m = Mat4Transform.scale(bottomWallWidth, topWallHeight, 1);
    m = Mat4.multiply(Mat4Transform.translate(0, POS_Y, 0), m);
    TransformNode topWallTransform = new TransformNode("Top wall transform", m);
    ModelNode topWallModel = new ModelNode("Top wall model", wall);

    NameNode topWallpaper = new NameNode("Top wallpaper");
    m = Mat4Transform.scale(bottomWallWidth, 1, topWallHeight);
    m = Mat4.multiply(Mat4Transform.rotateAroundX(90), m);
    m = Mat4.multiply(Mat4Transform.translate(0, POS_Y, WALLPAPER_POS_Z), m);
    TransformNode topWallpaperTransform = new TransformNode("Top wallpaper transform", m);
    ModelNode topWallpaperModel = new ModelNode("Top wallpaper model", this.topWallpaper);

    parent.addAllChildren(topWall, topWallTransform, topWallModel);
    parent.addAllChildren(topWallpaper, topWallpaperTransform, topWallpaperModel);
  }

  public class Wallpaper {

    /**
     * @param top The wallpaper above the window
     * @param bottom The wallpaper below the window
     * @param left The wallpaper on the left side of the window
     * @param right The wallpaper on the right side of the window
     */
    public Wallpaper(Model top, Model bottom, Model left, Model right) {
      topWallpaper = top;
      bottomWallpaper = bottom;
      leftWallpaper = left;
      rightWallpaper = right;
    }
  }
}