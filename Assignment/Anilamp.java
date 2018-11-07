import codeprovided.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout.*;

/**
 * I declare that this code is my own work.
 * Main class for the program
 *
 * @author Zer Jun Eng
 */
public class Anilamp extends JFrame implements ActionListener {

  private GLCanvas canvas;
  private Camera camera;

  /**
   * Sets the frame properties
   */
  private void setFrameProperties() {
    final int WIDTH = 1422;
    final int HEIGHT = 800;

    /* Set title, size, and location */
    final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    setTitle("COM3503 Anilamp");
    setSize(WIDTH, HEIGHT);
    setLocation(new Point((screenDimensions.width - WIDTH) / 2,
        (screenDimensions.height - HEIGHT) / 2)
    );

    /* Basic properties */
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  /**
   * Creates a panel containing control buttons
   *
   * @return A JPanel containing the control buttons
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1, 0, 0));

    // Camera panel
    JPanel cameraPanel = new JPanel();
    cameraPanel.setBorder(BorderFactory.createTitledBorder("Camera"));
    panel.add(cameraPanel);

    final JButton cameraX = new JButton("Camera X");
    final JButton cameraZ = new JButton("Camera Z");
    cameraX.addActionListener(this);
    cameraZ.addActionListener(this);

    GroupLayout cameraGroup = new GroupLayout(cameraPanel);
    cameraGroup.setAutoCreateGaps(true);
    cameraGroup.setAutoCreateContainerGaps(true);
    cameraGroup.setHorizontalGroup(
        cameraGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(cameraX)
            .addComponent(cameraZ)
    );
    cameraGroup.setVerticalGroup(
        cameraGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(cameraGroup.createSequentialGroup()
                .addComponent(cameraX)
                .addComponent(cameraZ))
    );
    cameraPanel.setLayout(cameraGroup);

    // World light panel
    JPanel lightPanel = new JPanel();
    lightPanel.setBorder(BorderFactory.createTitledBorder("World Lights"));
    panel.add(lightPanel);

    JButton btnNewButton_1 = new JButton("light 1");

    JButton btnNewButton_3 = new JButton("light 2");
    GroupLayout lightGroup = new GroupLayout(lightPanel);
    lightGroup.setAutoCreateGaps(true);
    lightGroup.setAutoCreateContainerGaps(true);
    lightGroup.setHorizontalGroup(
        lightGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(btnNewButton_1)
            .addComponent(btnNewButton_3)
    );
    lightGroup.setVerticalGroup(
        lightGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(lightGroup.createSequentialGroup()
                .addComponent(btnNewButton_1)
                .addComponent(btnNewButton_3))
    );
    lightPanel.setLayout(lightGroup);

    // Lamp panel
    JPanel lampPanel = new JPanel();
    lampPanel.setBorder(BorderFactory.createTitledBorder("Lamp"));
    panel.add(lampPanel);

    JButton resetLamp = new JButton("Reset");
    JButton jump = new JButton("Jump");

    GroupLayout lampGroup = new GroupLayout(lampPanel);
    lampGroup.setAutoCreateGaps(true);
    lampGroup.setAutoCreateContainerGaps(true);
    lampGroup.setHorizontalGroup(
        lampGroup.createParallelGroup(Alignment.LEADING)
            .addComponent(resetLamp)
            .addComponent(jump)
    );
    lampGroup.setVerticalGroup(
        lampGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(lampGroup.createSequentialGroup()
                .addComponent(resetLamp)
                .addComponent(jump))
    );
    lampPanel.setLayout(lampGroup);

    return panel;
  }

  /**
   * Constructor for the Anilamp frame
   */
  private Anilamp() {
    final Container contentPane = getContentPane();

    // Creates a button panel on the left side
    final JPanel buttonPanel = createButtonPanel();
    contentPane.add(buttonPanel, BorderLayout.WEST);

    // Canvas on center
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    Anilamp_GLEventListener glEventListener = new Anilamp_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    contentPane.add(canvas, BorderLayout.CENTER);

    FPSAnimator animator = new FPSAnimator(canvas, 60);
    animator.start();

    setFrameProperties();
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("camera X")) {
      camera.setCamera(Camera.CameraType.X);
      canvas.requestFocusInWindow();
    } else if (e.getActionCommand().equalsIgnoreCase("camera Z")) {
      camera.setCamera(Camera.CameraType.Z);
      canvas.requestFocusInWindow();
    }
  }

  /**
   * The main method for Anilamp
   *
   * @param args Command line arguments  (not used)
   */
  public static void main(String[] args) {
    new Anilamp();
  }
}

/**
 * A class for handling keyboard input. Code provided by Dr. Steve Maddock
 *
 * @author Dr. Steve Maddock
 */
class MyKeyboardInput extends KeyAdapter {

  private Camera camera;

  MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }

  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        m = Camera.Movement.LEFT;
        break;
      case KeyEvent.VK_RIGHT:
        m = Camera.Movement.RIGHT;
        break;
      case KeyEvent.VK_UP:
        m = Camera.Movement.UP;
        break;
      case KeyEvent.VK_DOWN:
        m = Camera.Movement.DOWN;
        break;
      case KeyEvent.VK_A:
        m = Camera.Movement.FORWARD;
        break;
      case KeyEvent.VK_Z:
        m = Camera.Movement.BACK;
        break;
    }
    camera.keyboardInput(m);
  }
}

/**
 * A class for handling mouse movement. Code provided by Dr. Steve Maddock.
 *
 * @author Dr. Steve Maddock
 */
class MyMouseInput extends MouseMotionAdapter {

  private Point lastpoint;
  private Camera camera;

  MyMouseInput(Camera camera) {
    this.camera = camera;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e instance of MouseEvent
   */
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx = (float) (ms.x - lastpoint.x) * sensitivity;
    float dy = (float) (ms.y - lastpoint.y) * sensitivity;
    if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
      camera.updateYawPitch(dx, -dy);
    }
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e instance of MouseEvent
   */
  public void mouseMoved(MouseEvent e) {
    lastpoint = e.getPoint();
  }
}
