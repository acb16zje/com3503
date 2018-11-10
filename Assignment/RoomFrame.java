import codeprovided.*;
import com.jogamp.opengl.*;
import gmaths.*;

/**
 * I declare that this code is my own work.
 * A class for rendering a floor and a wall with a hole
 *
 * @author Zer Jun Eng
 */
public class RoomFrame {

  private Model floor, topWallpaper, bottomWallpaper, leftWallpaper, rightWallpaper;

  private float roomWidth, roomHeight, roomDepth;
  private float POS_Z;

  /**
   * RoomFrame constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   * @param floor The floor
   * @param topWallpaper The wall above the window
   * @param bottomWallpaper The wall below the window
   * @param leftWallpaper The wall on the left side of the window
   * @param rightWallpaper The wall on the right side of the window
   */
  RoomFrame(Vec3 roomDimension, Model floor, Model topWallpaper, Model bottomWallpaper,
      Model leftWallpaper,
      Model rightWallpaper) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.floor = floor;
    this.topWallpaper = topWallpaper;
    this.bottomWallpaper = bottomWallpaper;
    this.leftWallpaper = leftWallpaper;
    this.rightWallpaper = rightWallpaper;

    this.POS_Z = -roomDepth * 0.5f;
  }

  void render(GL3 gl) {
    floor.setModelMatrix(getMatForFloor());
    floor.render(gl);
    topWallpaper.setModelMatrix(getMatForTopWallpaper());
    topWallpaper.render(gl);
    bottomWallpaper.setModelMatrix(getMatForBottomWallpaper());
    bottomWallpaper.render(gl);
    leftWallpaper.setModelMatrix(getMatForLeftWallpaper());
    leftWallpaper.render(gl);
    rightWallpaper.setModelMatrix(getMatForRightWallpaper());
    rightWallpaper.render(gl);
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
   * Get the model matrix for top wallpaper
   *
   * @return The model matrix for top wallpaper
   */
  private Mat4 getMatForTopWallpaper() {
    final float WIDTH = Window.RAIIO.x * roomWidth;
    final float HEIGHT = roomHeight - (Window.Y_POS + Window.RAIIO.y) * roomHeight;
    final float POS_Y = roomHeight * (Window.Y_POS + Window.RAIIO.y) + HEIGHT * 0.5f;

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }

  /**
   * Get the model matrix for bottom wallpaper
   *
   * @return The model matrix for bottom wallpaper
   */
  private Mat4 getMatForBottomWallpaper() {
    final float WIDTH = Window.RAIIO.x * roomWidth;
    final float HEIGHT = roomHeight * Window.Y_POS;
    final float POS_Y = HEIGHT * 0.5f;

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }

  /**
   * Get the model matrix for left wallpaper
   *
   * @return The model matrix for left wallpaper
   */
  private Mat4 getMatForLeftWallpaper() {
    final float WIDTH = (roomWidth - (Window.RAIIO.x * roomWidth)) / 2;
    final float HEIGHT = roomHeight;
    final float POS_X = -(roomWidth - WIDTH) / 2;
    final float POS_Y = HEIGHT * 0.5f;

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(POS_X, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }

  /**
   * Get the model matrix for right wallpaper
   *
   * @return The model matrix for right wallpaper
   */
  private Mat4 getMatForRightWallpaper() {
    final float WIDTH = (roomWidth - (Window.RAIIO.x * roomWidth)) / 2;
    final float HEIGHT = roomHeight;
    final float POS_X = (roomWidth - WIDTH) / 2;
    final float POS_Y = HEIGHT * 0.5f;

    Mat4 modelMatrix = Mat4Transform.scale(WIDTH, 1, HEIGHT);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(POS_X, POS_Y, POS_Z), modelMatrix);

    return modelMatrix;
  }
}
