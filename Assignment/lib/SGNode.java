package lib;

import com.jogamp.opengl.*;
import java.util.*;
import lib.gmaths.*;

/**
 * SGNode class adapted from tutorial 7
 * addAllChildren() is created to simplify the scene graph creation process
 *
 * @author Dr. Steve Maddock and Zer Jun Eng
 */
public class SGNode {

  protected String name;
  protected ArrayList<SGNode> children;
  protected Mat4 worldTransform;

  public SGNode(String name) {
    children = new ArrayList<>();
    this.name = name;
    worldTransform = new Mat4(1);
  }

  public void addChild(SGNode child) {
    children.add(child);
  }

  /**
   * Makes the name node as the parent of transform node,
   * and the transform node as the parent of model node
   *
   * @param name The name node
   * @param transform The transform node
   * @param model The model node
   */
  public void addAllChildren(NameNode name, TransformNode transform, ModelNode model) {
    children.add(name);
    name.addChild(transform);
    transform.addChild(model);
  }

  public void update() {
    update(worldTransform);
  }

  protected void update(Mat4 t) {
    worldTransform = t;
    for (SGNode aChildren : children) {
      aChildren.update(t);
    }
  }

  protected String getIndentString(int indent) {
    String s = "" + indent + " ";
    for (int i = 0; i < indent; ++i) {
      s += "  ";
    }
    return s;
  }

  public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent) + "Name: " + name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
    }
    for (SGNode aChildren : children) {
      aChildren.print(indent + 1, inFull);
    }
  }

  public void draw(GL3 gl) {
    for (SGNode aChildren : children) {
      aChildren.draw(gl);
    }
  }
}
