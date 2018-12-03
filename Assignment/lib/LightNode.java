package lib;

import com.jogamp.opengl.*;

/**
 * I declare that this code is my own work.
 * A class for the light node
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk)
 */
public class LightNode extends SGNode {

  private final Light light;

  /**
   * LightNode constructor, for spotlight
   *
   * @param name Name of the light node
   * @param l Light object
   */
  public LightNode(String name, Light l) {
    super(name);
    light = l;
  }

  /**
   * Sets the position and direction of the spotlight, and renders it
   *
   * @param gl OpenGL object, for rendering
   */
  public void draw(GL3 gl) {
    light.setPosition(worldTransform.getTranslateVec());
    light.setDirection(worldTransform.getRotationVec());
    light.render(gl, worldTransform);
  }
}
