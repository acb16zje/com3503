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

  private Model floor, topWall, bottomWall, leftWall, rightWall;               // Cube models
  private Model topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper;  // TwoTriangles

  private float roomWidth, roomHeight, roomDepth;
  private float POS_Z;

  /**
   * Room constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   * @param floor The floor
   */
  public Room(Vec3 roomDimension, Model floor) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.floor = floor;
  }

  /**
   * Renders floor, wallpaper, and wall (Cannot use scene graph for TwoTriangles)
   *
   * @param gl OpenGL object, for rendering
   */
  public void render(GL3 gl) {
    // Floor
    floor.setModelMatrix(getMatForFloor());
    floor.render(gl);

    // Wallpaper
    topWallpaper.setModelMatrix(getMatForTopWall(true));
    topWallpaper.render(gl);
    bottomWallpaper.setModelMatrix(getMatForBottomWall(true));
    bottomWallpaper.render(gl);
    leftWallpaper.setModelMatrix(getMatForLeftRightWall(true, true));
    leftWallpaper.render(gl);
    rightWallpaper.setModelMatrix(getMatForLeftRightWall(false, true));
    rightWallpaper.render(gl);

    // Wall
    topWall.setModelMatrix(getMatForTopWall(false));
    topWall.render(gl);
    bottomWall.setModelMatrix(getMatForBottomWall(false));
    bottomWall.render(gl);
    leftWall.setModelMatrix(getMatForLeftRightWall(true, false));
    leftWall.render(gl);
    rightWall.setModelMatrix(getMatForLeftRightWall(false, false));
    rightWall.render(gl);
  }


  /**
   * Get the model matrix for floor
   *
   * @return The model matrix for floor
   */
  private Mat4 getMatForFloor() {
    return Mat4Transform.scale(roomWidth, 1, roomDepth);
  }

  /**
   * Get the model matrix for top wall or wallpaper
   *
   * @param isWallpaper For rendering a wallpaper
   * @return The model matrix for top wallpaper
   */
  private Mat4 getMatForTopWall(boolean isWallpaper) {
    final float WIDTH = Window.RATIO.x * roomWidth;
    final float HEIGHT = roomHeight - (Window.Y_POS + Window.RATIO.y) * roomHeight;
    final float POS_Y = roomHeight * (Window.Y_POS + Window.RATIO.y) + HEIGHT / 2;

    if (isWallpaper) {
      POS_Z = -roomDepth / 2;
    } else {
      POS_Z = -(roomDepth + Cube.THICKNESS) / 2;
    }

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }

  /**
   * Get the model matrix for bottom wall or wallpaper
   *
   * @param isWallpaper For rendering a wallpaper
   * @return The model matrix for bottom wallpaper
   */
  private Mat4 getMatForBottomWall(boolean isWallpaper) {
    final float WIDTH = Window.RATIO.x * roomWidth;
    final float HEIGHT;
    final float POS_Y;

    if (isWallpaper) {
      HEIGHT = roomHeight * Window.Y_POS;
      POS_Y = HEIGHT / 2;
      POS_Z = -roomDepth / 2;
    } else {
      // Make the bottom wall reaches the bottom level of the floor
      HEIGHT = (roomHeight + Cube.THICKNESS) * Window.Y_POS;
      POS_Y = (HEIGHT - Cube.THICKNESS) / 2;
      POS_Z = -(roomDepth + Cube.THICKNESS) / 2;
    }

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }

  /**
   * Get the model matrix for left / right wall or wallpaper
   *
   * @param isLeft Left side or right side
   * @param isWallpaper For rendering a wallpaper
   * @return The model matrix for right wallpaper
   */
  private Mat4 getMatForLeftRightWall(boolean isLeft, boolean isWallpaper) {
    final float WIDTH = (roomWidth - (Window.RATIO.x * roomWidth)) / 2;
    final float HEIGHT;
    final float POS_X;
    final float POS_Y;

    if (isWallpaper) {
      HEIGHT = roomHeight;
      POS_Y = HEIGHT / 2;
      POS_Z = -roomDepth / 2;
    } else {
      // Make the left wall reaches the bottom level of the floor
      HEIGHT = roomHeight + Cube.THICKNESS / 2;
      POS_Y = (HEIGHT - Cube.THICKNESS) / 2;
      POS_Z = -(roomDepth + Cube.THICKNESS) / 2;
    }

    if (isLeft) {
      POS_X = -(roomWidth - WIDTH) / 2;
    } else {
      POS_X = (roomWidth - WIDTH) / 2;
    }

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(POS_X, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }

  public class Wall {

    /**
     * @param top The wall above the window
     * @param bottom The wall below the window
     * @param left The wall on the left side of the window
     * @param right The wall on the right side of the window
     */
    public Wall(Model top, Model bottom, Model left, Model right) {
      topWall = top;
      bottomWall = bottom;
      leftWall = left;
      rightWall = right;
    }
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