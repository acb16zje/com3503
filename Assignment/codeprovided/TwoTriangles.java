package codeprovided;

/**
 * TwoTriangles class adapted from tutorial 7
 * I declare that the code below line 27 is my own work
 *
 * @author Dr. Steve Maddock and Zer Jun Eng
 */
public final class TwoTriangles {

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public float[] vertices = {      // position, colour, tex coords
      -0.5f, 0.0f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // top left
      -0.5f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,  // bottom left
      0.5f, 0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,  // bottom right
      0.5f, 0.0f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f   // top right
  };

  public final int[] indices = {         // Note that we start from 0!
      0, 1, 2,
      0, 2, 3
  };

  // I created the methods below to modify the texture coordinates


  /**
   * Constructor for TwoTriangles
   *
   * @param texCoords The texture coordinates for four vertices
   */
  public TwoTriangles(float[] texCoords) {
    setTexCoords(texCoords);
  }

  /**
   * Set the texture coordinates of four vertices to the one given
   *
   * @param texCoords The texture coordinates for four vertices
   */
  private void setTexCoords(float[] texCoords) {
    for (int i=0;i<4;i++) {
      for (int j=0;j<2;j++) {
        vertices[(i*8)+6+j] = texCoords[(i*2)+j];
      }
    }
  }
}
