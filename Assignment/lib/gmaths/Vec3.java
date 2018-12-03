package lib.gmaths;

/**
 * A class for a 3D vector.
 * This includes two components: x, y, and z.
 *
 * @author    Dr Steve Maddock
 */
public final class Vec3 {
  public float x;
  public float y;
  public float z;
  
  public Vec3() {
    this(0,0,0);
  }
  
  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vec3(Vec3 v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
  }

  private float magnitude() {
    return magnitude(this);
  }
  
  private static float magnitude(Vec3 v) {
    return (float)Math.sqrt(v.x*v.x+v.y*v.y+v.z*v.z);
  }
  
  public void normalize() {
    float mag = magnitude();   // fails if mag = 0
    x /= mag;
    y /= mag;
    z /= mag;
  }

  public void add(Vec3 v) {
    x += v.x;
    y += v.y;
    z += v.z;
  }

  public static Vec3 add(Vec3 a, Vec3 b) {
    return new Vec3(a.x+b.x, a.y+b.y, a.z+b.z);
  }

  public static Vec3 subtract(Vec3 a, Vec3 b) {
    return new Vec3(a.x-b.x, a.y-b.y, a.z-b.z);
  }

  public static Vec3 multiply(Vec3 v, float f) {
    return new Vec3(v.x*f, v.y*f, v.z*f);
  }
  
  public static Vec3 crossProduct(Vec3 a, Vec3 b) {
    return new Vec3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
  }
 
  public String toString() {
    return "("+x+","+y+","+z+")";
  }
  
} // end of Vec3 class