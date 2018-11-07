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

  private Model floor, topWall, bottomWall, leftWall, rightWall;

  private float roomWidth, roomHeight, roomDepth;
  private float topWallHeight, bottomWallHeight, leftWallWidth, rightWallWidth;

  /**
   * RoomFrame constructor
   *
   * @param roomDimension The room dimension in width, height, depth
   * @param floor The floor
   * @param topWall The wall above the window
   * @param bottomWall The wall below the window
   * @param leftWall The wall on the left side of the window
   * @param rightWall The wall on the right side of the window
   */
  RoomFrame(Vec3 roomDimension, Model floor, Model topWall, Model bottomWall, Model leftWall,
      Model rightWall) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.floor = floor;
    this.topWall = topWall;
    this.bottomWall = bottomWall;
    this.leftWall = leftWall;
    this.rightWall = rightWall;
  }

  void render(GL3 gl) {
    floor.setModelMatrix(getMatForFloor());
    floor.render(gl);
  }

  /**
   * Get the model matrix for floor
   *
   * @return The model matrix for floor
   */
  private Mat4 getMatForFloor() {
    return  Mat4Transform.scale(roomWidth, 1f, roomDepth);
  }
}
