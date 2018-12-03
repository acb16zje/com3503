package lib.gmaths;

/**
 * A class for a 2D vector.
 * This includes two components: x and y. 
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (01/10/2017)
 */
 
public final class Vec2 {
  public final float x;
  public final float y;

  /**
   * Constructor.
   */ 
  public Vec2() {
    this(0,0);
  }

  /**
   * Constructor.
   * @param x x value for the 2D vector.
   * @param y y value for the 2D vector.
   */
  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructor.
   * @param v 2D vector used to initialise the values of this vector.
   */
  public Vec2(Vec2 v) {
    x = v.x;
    y = v.y;
  }

  /**
   * Calculates and returns the magnitude of the vector.
   * 
   * @return  The magnitude (= length) of the vector.
   */
  public float magnitude() {
    return magnitude(this);
  }

  /**
   * Calculates and returns the magnitude of the vector.
   * 
   * @param v The 2D vector to calculate teh magnitude of.
   * @return  The magnitude (= length) of the supplied vector.
   */
  private static float magnitude(Vec2 v) {
    return (float)Math.sqrt(v.x*v.x+v.y*v.y);
  }

  /**
   * Creates a String from the vector's components.
   * @return  A String representing the vector.
   */
  public String toString() {
    return "("+x+","+y+")";
  }
} // end of Vec2 class