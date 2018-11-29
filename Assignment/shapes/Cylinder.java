package shapes;

/**
 * I declare that this code is my own work
 * A class to draw cylinder, adapted and modified from Sphere.java
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public final class Cylinder {

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering

  private static final int XLONG = 30;
  private static final int YLAT = 4;

  public static final float[] vertices = createVertices();
  public static final int[] indices = createIndices();

  private static float[] createVertices() {
    double r = 0.5;
    int step = 8;
    float[] vertices = new float[XLONG * YLAT * step + 2 * step];

    for (int j = 0; j < YLAT; ++j) {
      double b = Math.toRadians(-90 + 180 * (double) (j) / (YLAT - 1));
      for (int i = 0; i < XLONG + 2; ++i) {
        double a = Math.toRadians(360 * (double) (i) / (XLONG - 1));
        double x = Math.cos(b) * Math.sin(a);
        double y = Math.sin(b);
        double z = Math.cos(b) * Math.cos(a);
        int base = j * XLONG * step;

        // Top and bottom cover
        if (j == 0 || j == YLAT - 1) {
          vertices[base + i * step + 1] = (float) (r * y) / 2;
        } else {
          vertices[base + i * step] = (float) (r * x);
          vertices[base + i * step + 1] = (float) (r * y);
          vertices[base + i * step + 2] = (float) (r * z);
          vertices[base + i * step + 3] = (float) x;
          vertices[base + i * step + 4] = (float) y;
          vertices[base + i * step + 5] = (float) z;
          vertices[base + i * step + 6] = (float) (i) / (float) (XLONG - 1);
          vertices[base + i * step + 7] = (float) (j) / (float) (YLAT - 1);
        }
      }
    }

    return vertices;
  }

  private static int[] createIndices() {
    int[] indices = new int[XLONG * YLAT * 6];
    for (int j = 0; j < YLAT; ++j) {
      for (int i = 0; i < XLONG; ++i) {
        int base = j * XLONG * 6;
        indices[base + i * 6] = j * XLONG + i;
        indices[base + i * 6 + 1] = j * XLONG + i + 1;
        indices[base + i * 6 + 2] = (j + 1) * XLONG + i + 1;
        indices[base + i * 6 + 3] = j * XLONG + i;
        indices[base + i * 6 + 4] = (j + 1) * XLONG + i + 1;
        indices[base + i * 6 + 5] = (j + 1) * XLONG + i;
      }
    }

    return indices;
  }
}
