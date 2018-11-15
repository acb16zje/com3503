package shapes;

/**
 * I declare that this code is my own work
 * A class for drawing a cone, createVertices() is adapted and modified from Sphere.java
 *
 * @author Zer Jun Eng
 */
public class Cone {
  private static final int XLONG = 30;
  private static final int YLAT = 3;

  public static final float[] vertices = createVertices();
  public static final int[] indices = Sphere.indices;

  private static float[] createVertices() {
    // Radius
    double r = 0.5;

    int step = 8;
    float[] vertices = new float[XLONG * YLAT * step];

    for (int j = 0; j < YLAT; ++j) {
      double b = Math.toRadians(-90 + 180 * (double) (j) / (YLAT - 1));
      for (int i = 0; i < XLONG; ++i) {
        double a = Math.toRadians(360 * (double) (i) / (XLONG - 1));
        double x = Math.cos(b) * Math.sin(a);
        double y = Math.cos(b);
        double z = Math.cos(b) * Math.cos(a);
        int base = j * XLONG * step;
        vertices[base + i * step + 0] = (float) (r * x);
        vertices[base + i * step + 1] = (float) (r * y);
        vertices[base + i * step + 2] = (float) (r * z);
        vertices[base + i * step + 3] = (float) x;
        vertices[base + i * step + 4] = (float) y;
        vertices[base + i * step + 5] = (float) z;
        vertices[base + i * step + 6] = (float) (i) / (float) (XLONG - 1);
        vertices[base + i * step + 7] = (float) (j) / (float) (YLAT - 1);
      }
    }

    return vertices;
  }
}
