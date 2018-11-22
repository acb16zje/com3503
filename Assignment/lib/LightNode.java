package lib;

import com.jogamp.opengl.*;

/**
 * A class for the light node
 *
 * @author Zer Jun Eng
 */
public class LightNode extends SGNode {

  protected Light light;

  public LightNode(String name, Light l) {
    super(name);
    light = l;
  }

  public void draw(GL3 gl) {
    light.render(gl, worldTransform);
  }
}
