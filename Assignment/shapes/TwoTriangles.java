package shapes;

/**
 * TwoTriangles class adapted from tutorial 7
 * I declare that the code below line 27 is my own work
 *
 * @author Dr. Steve Maddock and Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public final class TwoTriangles {

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static float[] vertices = {      // position, colour, tex coords
      -0.5f, 0.0f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // top left
      -0.5f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,  // bottom left
      0.5f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,  // bottom right
      0.5f, 0.0f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f   // top right
  };

  public static final int[] indices = {         // Note that we start from 0!
      0, 1, 2,
      0, 2, 3
  };

  /**
   * Set the texture coordinates of four vertices to the one given
   *
   * @param original The original vertices
   * @param texCoords The texture coordinates for four vertices
   */
  public static float[] setTexCoords(float[] original, float[] texCoords) {
    // Replace 6,7 for top left, 14,15 for bottom left, 22,23 for bototm right, 30,31 for top right
    for (int i = 0; i < texCoords.length / 2; i++) {
      System.arraycopy(texCoords, (i * 2), original, (i * 8) + 6, 2);
    }

    return original;
  }
}
