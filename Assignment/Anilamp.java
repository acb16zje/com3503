import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout.*;
import lib.*;

/**
 * I declare that this code is my own work. UI are generated using Eclipse Window Builder and
 * modified by myself. MyKeyboardInput and MyMouseInput was adapted from M04.java
 * Main class for the program
 *
 * @author Zer Jun Eng (zjeng1@sheffield.ac.uk) and Dr. Steve Maddock
 */
public class Anilamp extends JFrame {

  private final GLCanvas canvas;
  private final Camera camera;
  private final Anilamp_GLEventListener glEventListener;

  static JButton random, reset, jump;

  /**
   * The main method for Anilamp
   *
   * @param args Command line arguments  (not used)
   */
  public static void main(String[] args) { new Anilamp(); }

  /**
   * Constructor for the Anilamp frame
   */
  private Anilamp() {
    final Container contentPane = getContentPane();

    // Canvas on center
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.ROOM_X, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Anilamp_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));

    // Creates a control panel on the left side
    final JPanel buttonPanel = createControlPanel();
    contentPane.add(buttonPanel, BorderLayout.WEST);
    contentPane.add(canvas, BorderLayout.CENTER);

    FPSAnimator animator = new FPSAnimator(canvas, 60);
    animator.start();

    setFrameProperties();
  }

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
  private JPanel createControlPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1, 0, 0));
    panel.add(createCameraSubpanel());
    panel.add(createLightSubpanel());
    panel.add(createLampSubpanel());

    return panel;
  }

  /**
   * Creates a subpanel for camera control
   *
   * @return Camera subpanel
   */
  private JPanel createCameraSubpanel() {
    JPanel cameraPanel = new JPanel();
    cameraPanel.setBorder(BorderFactory.createTitledBorder("Camera"));

    JPanel roomPanel = new JPanel();
    roomPanel.setBorder(BorderFactory.createTitledBorder("Room"));

    JButton frontView = new JButton("Front View");
    JButton leftView = new JButton("Left View");
    JButton rightView = new JButton("Right View");

    JPanel deskPanel = new JPanel();
    deskPanel.setBorder(BorderFactory.createTitledBorder("Desk"));

    JButton deskView = new JButton("Front View");

    GroupLayout cameraGroup = new GroupLayout(cameraPanel);
    cameraGroup.setAutoCreateGaps(true);
    cameraGroup.setHorizontalGroup(cameraGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(roomPanel)
        .addComponent(deskPanel)
    );
    cameraGroup.setVerticalGroup(cameraGroup.createParallelGroup(Alignment.LEADING)
        .addGroup(cameraGroup.createSequentialGroup()
            .addComponent(roomPanel)
            .addComponent(deskPanel))
    );
    cameraPanel.setLayout(cameraGroup);

    GroupLayout roomGroup = new GroupLayout(roomPanel);
    roomGroup.setAutoCreateContainerGaps(true);
    roomGroup.setAutoCreateGaps(true);
    roomGroup.setHorizontalGroup(roomGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(frontView)
        .addComponent(leftView)
        .addComponent(rightView)
    );
    roomGroup.setVerticalGroup(roomGroup.createParallelGroup(Alignment.LEADING)
        .addGroup(roomGroup.createSequentialGroup()
            .addComponent(frontView)
            .addComponent(leftView)
            .addComponent(rightView))
    );
    roomPanel.setLayout(roomGroup);

    GroupLayout deskGroup = new GroupLayout(deskPanel);
    deskGroup.setAutoCreateContainerGaps(true);
    deskGroup.setAutoCreateGaps(true);
    deskGroup.setHorizontalGroup(deskGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(deskView)
    );
    deskGroup.setVerticalGroup(deskGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(deskView)
    );
    deskPanel.setLayout(deskGroup);

    frontView.addActionListener(e -> {
      camera.setCamera(Camera.CameraType.X);
      canvas.requestFocusInWindow();
    });
    leftView.addActionListener(e -> {
      camera.setCamera(Camera.CameraType.NZ);
      canvas.requestFocusInWindow();
    });
    rightView.addActionListener(e -> {
      camera.setCamera(Camera.CameraType.Z);
      canvas.requestFocusInWindow();
    });
    deskView.addActionListener(e -> {
      camera.setCamera(Camera.CameraType.DESK);
      canvas.requestFocusInWindow();
    });

    return cameraPanel;
  }

  /**
   * Creates a subpanel for light control
   *
   * @return Light subpanel
   */
  private JPanel createLightSubpanel() {
    JPanel lightPanel = new JPanel();
    lightPanel.setBorder(BorderFactory.createTitledBorder("World Lights"));

    JButton onOrOff = new JButton("Turn OFF");

    // Generated using Eclipse GUI Builder
    JPanel intensityPanel = new JPanel();
    GroupLayout lightGroup = new GroupLayout(lightPanel);
    lightGroup.setAutoCreateContainerGaps(true);
    lightGroup.setAutoCreateGaps(true);
    lightGroup.setHorizontalGroup(
        lightGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(lightGroup.createSequentialGroup()
                .addGroup(lightGroup.createParallelGroup(Alignment.LEADING)
                    .addComponent(onOrOff)
                    .addComponent(intensityPanel)))
    );
    lightGroup.setVerticalGroup(
        lightGroup.createParallelGroup(Alignment.LEADING)
            .addGroup(lightGroup.createSequentialGroup()
                .addComponent(onOrOff)
                .addComponent(intensityPanel)
                .addGap(130))
    );

    GridBagLayout gbl_intensityPanel = new GridBagLayout();
    gbl_intensityPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    intensityPanel.setLayout(gbl_intensityPanel);

    JLabel intensityLabel = new JLabel("Intensity");
    GridBagConstraints gbc_intensityLabel = new GridBagConstraints();
    gbc_intensityLabel.fill = GridBagConstraints.BOTH;
    gbc_intensityLabel.insets = new Insets(0, 0, 0, 5);
    intensityPanel.add(intensityLabel, gbc_intensityLabel);

    SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0, 1, 0.1);
    JSpinner intensity = new JSpinner(model);
    intensity.setEditor(new JSpinner.DefaultEditor(intensity));
    GridBagConstraints gbc_intensity = new GridBagConstraints();
    gbc_intensity.fill = GridBagConstraints.BOTH;
    intensityPanel.add(intensity, gbc_intensity);
    lightPanel.setLayout(lightGroup);

    // Turn world lights on or off
    onOrOff.addActionListener(e -> {
      if (glEventListener.lightIsOn) {
        onOrOff.setText("Turn ON");
        intensity.setValue(0.0);
      } else {
        onOrOff.setText("Turn OFF");
        intensity.setValue(1.0); // Default intensity is 1
      }
    });

    // Change the intensity of world lights
    intensity.addChangeListener(e -> {
      float val = ((Double) intensity.getValue()).floatValue();
      glEventListener.setIntensity(val);

      if (val == 0) {
        glEventListener.lightIsOn = false;
        onOrOff.setText("Turn ON");
      } else {
        glEventListener.lightIsOn = true;
        onOrOff.setText("Turn OFF");
      }
    });

    return lightPanel;
  }

  /**
   * Creates subpanel for lamp controls
   *
   * @return Lamp subpanel
   */
  private JPanel createLampSubpanel() {
    JPanel lampPanel = new JPanel();
    lampPanel.setBorder(BorderFactory.createTitledBorder("Lamp"));

    JPanel lightPanel = new JPanel();
    lightPanel.setBorder(BorderFactory.createTitledBorder("Spotlight"));

    JButton onOrOff = new JButton("Turn OFF");

    JPanel animationPanel = new JPanel();
    animationPanel.setBorder(BorderFactory.createTitledBorder("Animation"));

    random = new JButton("Random Pose");
    reset = new JButton("Reset");
    jump = new JButton("Jump");

    GroupLayout lampGroup = new GroupLayout(lampPanel);
    lampGroup.setAutoCreateGaps(true);
    lampGroup.setHorizontalGroup(lampGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(lightPanel)
        .addComponent(animationPanel)
    );
    lampGroup.setVerticalGroup(lampGroup.createParallelGroup(Alignment.LEADING)
        .addGroup(lampGroup.createSequentialGroup()
            .addComponent(lightPanel)
            .addComponent(animationPanel))
    );
    lampPanel.setLayout(lampGroup);

    GroupLayout lightGroup = new GroupLayout(lightPanel);
    lightGroup.setAutoCreateContainerGaps(true);
    lightGroup.setAutoCreateGaps(true);
    lightGroup.setHorizontalGroup(lightGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(onOrOff)
    );
    lightGroup.setVerticalGroup(lightGroup.createParallelGroup(Alignment.LEADING)
        .addGroup(lightGroup.createSequentialGroup()
            .addComponent(onOrOff))
    );
    lightPanel.setLayout(lightGroup);

    GroupLayout animationGroup = new GroupLayout(animationPanel);
    animationGroup.setAutoCreateContainerGaps(true);
    animationGroup.setAutoCreateGaps(true);
    animationGroup.setHorizontalGroup(animationGroup.createParallelGroup(Alignment.LEADING)
        .addComponent(random)
        .addComponent(reset)
        .addComponent(jump)
    );
    animationGroup.setVerticalGroup(animationGroup.createParallelGroup(Alignment.LEADING)
        .addGroup(animationGroup.createSequentialGroup()
            .addComponent(random)
            .addComponent(reset)
            .addComponent(jump))
    );
    animationPanel.setLayout(animationGroup);

    // Turn spotlight on or off
    onOrOff.addActionListener(e -> {
      if (glEventListener.spotlightIsOn) {
        onOrOff.setText("Turn ON");
      } else {
        onOrOff.setText("Turn OFF");
      }

      glEventListener.setSpotlightOnOrOff();
    });

    // Animation controls
    random.addActionListener(e -> glEventListener.lamp.clickedRandom = true);
    reset.addActionListener(e -> glEventListener.lamp.clickedReset = true);
    jump.addActionListener(e -> {
      glEventListener.lamp.clickedReset = true;
      glEventListener.lamp.clickedJump = true;
    });

    return lampPanel;
  }

  /**
   * A class for handling keyboard input. Code provided by Dr. Steve Maddock
   *
   * @author Dr. Steve Maddock
   */
  private class MyKeyboardInput extends KeyAdapter {

    private final Camera camera;

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
  private class MyMouseInput extends MouseMotionAdapter {

    private Point lastpoint;
    private final Camera camera;

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
      if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
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
}