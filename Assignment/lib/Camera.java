package lib;

import lib.gmaths.*;

/**
 * Camera class adapted from tutorial 7
 *
 * @author Dr. Steve Maddock
 */
public class Camera {

  public enum CameraType {X, Z, NZ, DESK}

  public enum Movement {NO_MOVEMENT, LEFT, RIGHT, UP, DOWN, FORWARD, BACK}

  public static final Vec3 ROOM_X = new Vec3(0, 10, 25);
  private static final Vec3 ROOM_Z = new Vec3(25, 10, 0);
  private static final Vec3 ROOM_NZ = new Vec3(-25, 10, 0);
  private static final Vec3 DESK_X = new Vec3(0, 12, 3);
  public static final Vec3 DEFAULT_TARGET = new Vec3(0, 8, 0);
  private static final Vec3 DESK_TARGET = new Vec3(0, 11, 0);
  public static final Vec3 DEFAULT_UP = new Vec3(0, 11, 0);

  private final float KEYBOARD_SPEED = 0.2f;

  private Vec3 position;
  private Vec3 target;
  private Vec3 up;
  private Vec3 worldUp;
  private Vec3 front;
  private Vec3 right;

  private float yaw;
  private float pitch;

  private Mat4 perspective;

  public Camera(Vec3 position, Vec3 target, Vec3 up) {
    setupCamera(position, target, up);
  }

  private void setupCamera(Vec3 position, Vec3 target, Vec3 up) {
    this.position = new Vec3(position);
    this.target = new Vec3(target);
    this.up = new Vec3(up);
    front = Vec3.subtract(target, position);
    front.normalize();
    up.normalize();
    calculateYawPitch(front);
    worldUp = new Vec3(up);
    updateCameraVectors();
  }

  public Vec3 getPosition() {
    return new Vec3(position);
  }

  public void setCamera(CameraType c) {
    switch (c) {
      case X:
        setupCamera(ROOM_X, DEFAULT_TARGET, DEFAULT_UP);
        break;
      case Z:
        setupCamera(ROOM_Z, DEFAULT_TARGET, DEFAULT_UP);
        break;
      case NZ:
        setupCamera(ROOM_NZ, DEFAULT_TARGET, DEFAULT_UP);
        break;
      case DESK:
        setupCamera(DESK_X, DESK_TARGET, DEFAULT_UP);
        break;
    }
  }

  private void calculateYawPitch(Vec3 v) {
    yaw = (float) Math.atan2(v.z, v.x);
    pitch = (float) Math.asin(v.y);
  }

  Mat4 getViewMatrix() {
    target = Vec3.add(position, front);
    return Mat4Transform.lookAt(position, target, up);
  }

  public void setPerspectiveMatrix(Mat4 m) {
    perspective = m;
  }

  Mat4 getPerspectiveMatrix() {
    return perspective;
  }

  public void keyboardInput(Movement movement) {
    switch (movement) {
      case NO_MOVEMENT:
        break;
      case LEFT:
        position.add(Vec3.multiply(right, -KEYBOARD_SPEED));
        break;
      case RIGHT:
        position.add(Vec3.multiply(right, KEYBOARD_SPEED));
        break;
      case UP:
        position.add(Vec3.multiply(up, KEYBOARD_SPEED));
        break;
      case DOWN:
        position.add(Vec3.multiply(up, -KEYBOARD_SPEED));
        break;
      case FORWARD:
        position.add(Vec3.multiply(front, KEYBOARD_SPEED));
        break;
      case BACK:
        position.add(Vec3.multiply(front, -KEYBOARD_SPEED));
        break;
    }
  }

  public void updateYawPitch(float y, float p) {
    yaw += y;
    pitch += p;
    if (pitch > 89) {
      pitch = 89;
    } else if (pitch < -89) {
      pitch = -89;
    }
    updateFront();
    updateCameraVectors();
  }

  private void updateFront() {
    double cy, cp, sy, sp;
    cy = Math.cos(yaw);
    sy = Math.sin(yaw);
    cp = Math.cos(pitch);
    sp = Math.sin(pitch);
    front.x = (float) (cy * cp);
    front.y = (float) (sp);
    front.z = (float) (sy * cp);
    front.normalize();
    target = Vec3.add(position, front);
  }

  private void updateCameraVectors() {
    right = Vec3.crossProduct(front, worldUp);
    right.normalize();
    up = Vec3.crossProduct(right, front);
    up.normalize();
  }
}