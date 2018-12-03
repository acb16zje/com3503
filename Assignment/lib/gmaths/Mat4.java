package lib.gmaths;

/**
 * A matrix class, provided by Dr Steve Maddock.
 * Added getTranslationVec() and getRotationVec() for the position and the
 * direction of the spotlight
 *
 * @author Dr. Steve Maddock and Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class Mat4 {   // row column formulation

  private final float[][] values;

  public Mat4() {
    this(0);
  }

  public Mat4(float f) {
    values = new float[4][4];
    makeZero();
    for (int i = 0; i < 4; ++i) {
      values[i][i] = f;
    }
  }

  public Mat4(Mat4 m) {
    this.values = new float[4][4];
    for (int i = 0; i < 4; ++i) {
      System.arraycopy(m.values[i], 0, this.values[i], 0, 4);
    }
  }

  public void set(int r, int c, float f) {
    values[r][c] = f;
  }

  private void makeZero() {
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        values[i][j] = 0;
      }
    }
  }

  public static Mat4 multiply(Mat4 a, Mat4 b) {
    Mat4 result = new Mat4();
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        for (int k = 0; k < 4; ++k) {
          result.values[i][j] += a.values[i][k] * b.values[k][j];
        }
      }
    }
    return result;
  }

  public float[] toFloatArrayForGLSL() {  // col by row
    float[] f = new float[16];
    for (int j = 0; j < 4; ++j) {
      for (int i = 0; i < 4; ++i) {
        f[j * 4 + i] = values[i][j];
      }
    }
    return f;
  }

  /**
   * Gets the translated position of the spotlight
   * Source: Lecture 2, Slide 8
   *
   * @return x, y, z position of the spotlight
   */
  public Vec3 getTranslateVec() {
    float[] f;
    f = toFloatArrayForGLSL();

    return new Vec3(f[12], f[13], f[14]);
  }

  /**
   * Gets the rotated direction of the spotlight
   * Source: Lecture 2, Slide 10
   *
   * @return The x, y, z rotation of the spotlight
   */
  public Vec3 getRotationVec() {
    /*
     * The X rotation of headJoint is the Z of the light bulb
     * The Y rotation of headJoint is the Y of the light bulb
     * The Z rotation of headJoint is the X of the light bulb
     */
    float x = (float) Math.asin(values[0][1]);
    float y = (float) Math.asin(values[1][1]);
    float z = (float) Math.asin(values[2][1]);

    return new Vec3(-x, -y, -z);
  }

  public String toString() {
    StringBuilder s = new StringBuilder("{");
    for (int i = 0; i < 4; ++i) {
      s.append((i == 0) ? "{" : " {");
      for (int j = 0; j < 4; ++j) {
        s.append(String.format("%.2f", values[i][j]));
        if (j < 3) {
          s.append(", ");
        }
      }
      s.append((i == 3) ? "}" : "},\n");
    }
    s.append("}");
    return s.toString();
  }

} // end of Mat4 class