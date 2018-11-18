package lib;

import com.jogamp.opengl.*;

/**
 * ModelNode class adapted from tutorial 7
 *
 * @author Dr. Steve Maddock
 */
public class ModelNode extends SGNode {

  protected Model model;

  public ModelNode(String name, Model m) {
    super(name);
    model = m;
  }

  public void draw(GL3 gl) {
    model.render(gl, worldTransform);
    for (SGNode aChildren : children) {
      aChildren.draw(gl);
    }
  }
}
