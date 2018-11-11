import codeprovided.*;
import com.jogamp.opengl.*;
import gmaths.*;

/**
 * I declare that this code is my own work
 * A class for rendering the table
 *
 * @author Zer Jun Eng
 */
public class Table {
  private Model tableFrame;

  private float roomWidth, roomHeight, roomDepth;

  // Dimension ratio of table with respect to room dimension
  public static final Vec3 RATIO = new Vec3(0.714f, 0.39f, 0.286f);

  Table(Vec3 roomDimension, Model tableFrame) {
    this.roomWidth = roomDimension.x;
    this.roomHeight = roomDimension.y;
    this.roomDepth = roomDimension.z;
    this.tableFrame = tableFrame;
  }

  void sceneGraph(GL3 gl) {

  }

  void render(GL3 gl) {
    
  }
}
