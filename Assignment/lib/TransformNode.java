package lib;

import lib.gmaths.*;

/**
 * TransformNode class adapted from tutorial 7
 *
 * @author Dr. Steve Maddock
 */
public class TransformNode extends SGNode {

  private Mat4 transform;

  public TransformNode(String name, Mat4 t) {
    super(name);
    transform = new Mat4(t);
  }

  /**
   * Gets the transform matrix of the transform node
   *
   * @return The transform matrix
   */
  public Mat4 getTransform() { return transform; }

  public void setTransform(Mat4 m) {
    transform = new Mat4(m);
  }

  protected void update(Mat4 t) {
    worldTransform = t;
    t = Mat4.multiply(worldTransform, transform);
    for (SGNode aChildren : children) {
      aChildren.update(t);
    }
  }

  public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent) + "Name: " + name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
      System.out.println("transform node:");
      System.out.println(transform);
    }
    for (SGNode aChildren : children) {
      aChildren.print(indent + 1, inFull);
    }
  }
}
