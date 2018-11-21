package lib;

import com.jogamp.opengl.*;

/**
 * I declare that this code is my own work
 * A class for spotlight
 *
 * @author Zer Jun Eng
 */
public class Spotlight extends Light {

  /**
   * Spotlight constructor, does everything at once
   *
   * @param gl OpenGL object
   * @param camera Camera object for setting
   */
  public Spotlight(GL3 gl, Camera camera) {
    super(gl, camera);
  }
}
