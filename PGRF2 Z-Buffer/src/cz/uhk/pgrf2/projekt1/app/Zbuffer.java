/**
 * 
 */
package cz.uhk.pgrf2.projekt1.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import cz.uhk.pgrf2.projekt1.model.AxisX3D;
import cz.uhk.pgrf2.projekt1.model.AxisY3D;
import cz.uhk.pgrf2.projekt1.model.AxisZ3D;
import cz.uhk.pgrf2.projekt1.model.Cube;
import cz.uhk.pgrf2.projekt1.model.CurveBezier;
import cz.uhk.pgrf2.projekt1.model.CurveCoons;
import cz.uhk.pgrf2.projekt1.model.CurveFerguson;
import cz.uhk.pgrf2.projekt1.model.Dodecahedron;
import cz.uhk.pgrf2.projekt1.model.ISolid;
import cz.uhk.pgrf2.projekt1.model.Light;
import cz.uhk.pgrf2.projekt1.model.LightManager;
import cz.uhk.pgrf2.projekt1.model.Renderer;
import cz.uhk.pgrf2.projekt1.model.SceneObject;
import cz.uhk.pgrf2.projekt1.model.Tetrahedron;
import cz.uhk.pgrf2.projekt1.transforms3D.Camera;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4Identity;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4OrthoRH;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4PerspRH;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4RotXYZ;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4Scale;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4Transl;
import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;
import cz.uhk.pgrf2.projekt1.transforms3D.Vec3D;

/**
 * @author Jan Hlad??na <jan.hladena@uhk.cz>
 *
 * Jednot????dn??, jednookenn?? aplikace.
 */
public class Zbuffer extends JFrame {
	
	/** Hlavn?? kresl??c?? pl??tno */
	protected BufferedImage canvas;
	
	/** Kontejner pl??tna */
	protected JPanel panelCanvas;
	/** sou??adnice kamery */
	final protected JLabel labelXYZ = new JLabel("Camera [X, Y, Z]");
	
	/** Po????tek X */
	protected int originX;
	/** Po????tek Y */
	protected int originY;
	
	/** Konec X */
	protected int endX;
	/** Konec Y */
	protected int endY;
	
	/** d??lka osy */
	protected int axisLen = 50;
	/** krok pohybu */
	protected int stepSize = 5;
	/** ??????ka pl??tna */
	protected int canvasWidth;
	/** v????ka pl??tna */
	protected int canvasHeight;
	
	// barva dr??tu
	@Deprecated
	protected int colorWire = 0xff000000;
	// barva osy
	@Deprecated
	protected int colorAxisX = 0xffff0000;
	@Deprecated
	protected int colorAxisY = 0xff00ff00;
	@Deprecated
	protected int colorAxisZ = 0xff0000ff;
	
	/** renderer */
	private Renderer renderer;
	/** kamera */
	private Camera camera = new Camera();
	/** spr??vce sv??tel */
	private LightManager lightManager;
	
	/** pou??it?? matice ortogon??ln?? projekce */
	protected boolean ortho = false;
	/** re??im kreslen?? - wireframe nebo vypln??n?? plochy */
	protected boolean wireframe = true;
	/** pou????vat osv??tlen?? */
	protected boolean useLights = false;
	/** matice perpektivy */
	protected Mat4 perps = new Mat4PerspRH(Math.PI/4, 1, 0.5, 1000);
	
	/** seznam objekt?? sc??ny */
	protected ArrayList<SceneObject> sceneList = new ArrayList<SceneObject>();
	/** grafick?? reprezentace seznamu objekt?? na sc??n?? */
	protected DefaultListModel<String> listModel = new DefaultListModel<String>();
	protected JList<String> objectList = new JList<String>(listModel);
	
	
	/** UID */
	private static final long serialVersionUID = 104158975974418940L;

	public Zbuffer() {
		super("PGRF2 Projekt Z-buffer");
	}
	
	private void initApp(int w, int h) {
		// inicializace okna
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(w, h);
		setResizable(false);
		setVisible(true);
		setLayout(new BorderLayout());
		
		// prvky
		layoutGUI();
		
		// asynchronn?? inicializace pl??tna
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initCanvas();
				setStart();
				render();
			}
		});
		
		
	}
	
	/**
	 * Kompletn?? GUI ve Swingu, prakticky v??echno v??etn?? dialog??
	 * je namatl??no v obsluze ActionListener??.
	 */
	private void layoutGUI() {
		
		// hlavn?? komponenta
		panelCanvas = new JPanel();
		add(panelCanvas, BorderLayout.CENTER);
		
		// my?? - klik
		panelCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {		
				
				render();
				endX = e.getX();
				endY = e.getY();
			}
		});
		// my?? - pohyb
		panelCanvas.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
				double deltaX = e.getX() - endX;
				double deltaY = e.getY() - endY;
				
				camera.addAzimuth(+deltaX/( (1.0 * canvasWidth/canvasHeight) * canvasWidth) * Math.PI);
				camera.addZenith(-deltaY/( (1.0 * canvasHeight/canvasWidth) * canvasHeight) * Math.PI);
				
				render();
				endX = e.getX();
				endY = e.getY();
				
			}
			
		});
		
		// kl??vesnice
		panelCanvas.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				switch(e.getKeyCode()) {
				
					case KeyEvent.VK_W:
					case KeyEvent.VK_UP:
						camera.forward(stepSize);
						
						if (ortho) {
							perps = new Mat4OrthoRH(camera.getPosition().length(), camera.getPosition().length(), 1, 500);
						}
						
					break;
				
					case KeyEvent.VK_S:
					case KeyEvent.VK_DOWN:
						camera.backward(stepSize);
						
						if (ortho) {
							perps = new Mat4OrthoRH(camera.getPosition().length(), camera.getPosition().length(), 1, 500);
						}
					break;
					
					case KeyEvent.VK_A:
					case KeyEvent.VK_LEFT:
						camera.left(stepSize);
					break;
					
					case KeyEvent.VK_D:
					case KeyEvent.VK_RIGHT:
						camera.right(stepSize);
					break;
					
					case KeyEvent.VK_SPACE:
						camera.up(stepSize);
					break;
					
					case KeyEvent.VK_SHIFT:
						camera.down(stepSize);
					break;
						
					default:	
					
				}
				
				// p??ekreslit
				render();
			}
	
		});
		
		// ovl??dac?? panel
		JPanel panelControl = new JPanel();
		panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
		
		// [X, Y, Z] kamery
		panelControl.add(labelXYZ);
		
		// p??ep??na?? projekce
		final JButton buttonRH = new JButton("[Persp] / Ortho");
		buttonRH.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						// negace projekce
						ortho = !ortho;
						
						if (ortho) {
							// ortogon??ln??
							perps = new Mat4OrthoRH(camera.getPosition().length(), camera.getPosition().length(), 1, 500);
							buttonRH.setText("Persp / [Ortho]");
							System.out.println("P??epnuto do ortogon??ln?? projekce.");
						} else {
							// perspektivn??
							perps = new Mat4PerspRH(Math.PI/4, 1, 0.5, 500);
							buttonRH.setText("[Persp] / Ortho");
							System.out.println("P??epnuto do perspektivn?? projekce.");
						}
						
						// ... render
						render();
					}
		});
		panelControl.add(buttonRH);
		
		// p??ep??na?? re??imu zobrazen??
		final JButton buttonWZ = new JButton("[Wireframe] / Surface");
		buttonWZ.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// znegov??n?? flagu
				wireframe =! wireframe;
				
				if (wireframe)
				{
					buttonWZ.setText("[Wireframe] / Surface");
					System.out.println("P??epnuto do zobrazen?? dr??tov??ho modelu.");
				} else {
					buttonWZ.setText("Wireframe / [Surface]");
					System.out.println("P??epnuto do zorazen?? vypln??n??ch ploch.");
				}
				
				// ... render
				render();
				
			}
		});
		panelControl.add(buttonWZ);
		
		// p??ep??na?? spr??vce sv??tel
		final JButton buttonLights = new JButton("[Norm??ln??] / Sv??tla");
		buttonLights.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// znegov??n?? flagu spr??vce sv??tel
				useLights =! useLights;
				
				if (useLights)
				{
					renderer.setLightManager(lightManager);
					buttonLights.setText("Norm??ln?? / [Sv??tla]");
					System.out.println("Byl aktivov??n spr??vce osv??tlen??.");
				} else {
					renderer.setLightManager(null);
					buttonLights.setText("[Norm??ln??] / Sv??tla");
					System.out.println("Spr??vce osv??tlen?? byl deaktivov??n.");
				}
				
				// .. render
				render();
			}
		});
		panelControl.add(buttonLights);
		
		// na????st demo
		final JButton buttonDemo = new JButton("Na????st demo");
		buttonDemo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				demo();
				
			}
		});
		panelControl.add(buttonDemo);
		
		JLabel labelAddObject = new JLabel("P??idat:");
		panelControl.add(labelAddObject);
		
		JButton buttonCube = new JButton("Krychli");
		buttonCube.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						// kontejner
						JPanel dialogPanel = new JPanel();
						
						JTextField size = new JTextField("35", 5);
						
						JTextField xOrig = new JTextField("0", 5);
					    JTextField yOrig = new JTextField("0", 5);
					    JTextField zOrig = new JTextField("0", 5);
					    		
					    dialogPanel.add(new JLabel("Velikost:"));
						dialogPanel.add(size);
						dialogPanel.add(Box.createHorizontalStrut(10));
					    
					    dialogPanel.add(new JLabel("Po????tek"));
					    dialogPanel.add(Box.createHorizontalStrut(10));
					    
						dialogPanel.add(new JLabel("X:"));
						dialogPanel.add(xOrig);
						dialogPanel.add(Box.createHorizontalStrut(10));
						dialogPanel.add(new JLabel("Y:"));
						dialogPanel.add(yOrig);
						dialogPanel.add(Box.createHorizontalStrut(10));
						dialogPanel.add(new JLabel("Z:"));
						dialogPanel.add(zOrig);
						
						int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
					               "P??idat krychli", JOptionPane.OK_CANCEL_OPTION);
					      if (result == JOptionPane.OK_OPTION) {

					    	 int cubeSize = Integer.parseInt(size.getText());
					    	 int x = Integer.parseInt(xOrig.getText());
					    	 int y = Integer.parseInt(yOrig.getText());
					    	 int z = Integer.parseInt(zOrig.getText());
					    	 
					    	 Cube newCube = new Cube(cubeSize);
					    	 SceneObject cubeObj = new SceneObject("Krychle [" + cubeSize + "; ["+x+"; "+y+"; "+z+"]]", x, y, z, newCube);
					    	 sceneList.add(cubeObj);
					      }
						
						render();
					}
		});
		panelControl.add(buttonCube);
		
		JButton buttonTetrahedron = new JButton("??ty??st??n");
		buttonTetrahedron.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						// kontejner
						JPanel dialogPanel = new JPanel();
						
						JTextField size = new JTextField("35", 5);
						
						JTextField xOrig = new JTextField("0", 5);
					    JTextField yOrig = new JTextField("0", 5);
					    JTextField zOrig = new JTextField("0", 5);
					    		
					    dialogPanel.add(new JLabel("Velikost:"));
						dialogPanel.add(size);
						dialogPanel.add(Box.createHorizontalStrut(10));
					    
					    dialogPanel.add(new JLabel("Po????tek"));
					    dialogPanel.add(Box.createHorizontalStrut(10));
					    
						dialogPanel.add(new JLabel("X:"));
						dialogPanel.add(xOrig);
						dialogPanel.add(Box.createHorizontalStrut(10));
						dialogPanel.add(new JLabel("Y:"));
						dialogPanel.add(yOrig);
						dialogPanel.add(Box.createHorizontalStrut(10));
						dialogPanel.add(new JLabel("Z:"));
						dialogPanel.add(zOrig);
						
						int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
					               "P??idat ??ty??st??n", JOptionPane.OK_CANCEL_OPTION);
					      if (result == JOptionPane.OK_OPTION) {

					    	 int tetrahedronSize = Integer.parseInt(size.getText());
					    	 int x = Integer.parseInt(xOrig.getText());
					    	 int y = Integer.parseInt(yOrig.getText());
					    	 int z = Integer.parseInt(zOrig.getText());
					    	 
					    	 Tetrahedron newTetrahedron = new Tetrahedron(tetrahedronSize);
					    	 SceneObject tetrahedronObj = new SceneObject("??ty??st??n [" + tetrahedronSize + "; ["+x+"; "+y+"; "+z+"]]", x, y, z, newTetrahedron);
					    	 sceneList.add(tetrahedronObj);
					      }
						
						render();
					}
		});
		panelControl.add(buttonTetrahedron);
		
		JButton buttonDodecahedron = new JButton("Dvan??ctist??n");
		buttonDodecahedron.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						// kontejner
						JPanel dialogPanel = new JPanel();
						
						JTextField size = new JTextField("35", 5);
						
						JTextField xOrig = new JTextField("0", 5);
					    JTextField yOrig = new JTextField("0", 5);
					    JTextField zOrig = new JTextField("0", 5);

					    dialogPanel.add(new JLabel("Velikost:"));
						dialogPanel.add(size);
						dialogPanel.add(Box.createHorizontalStrut(10));
					    
					    dialogPanel.add(new JLabel("Po????tek"));
					    dialogPanel.add(Box.createHorizontalStrut(10));
					    
						dialogPanel.add(new JLabel("X:"));
						dialogPanel.add(xOrig);
						dialogPanel.add(Box.createHorizontalStrut(10));
						dialogPanel.add(new JLabel("Y:"));
						dialogPanel.add(yOrig);
						dialogPanel.add(Box.createHorizontalStrut(10));
						dialogPanel.add(new JLabel("Z:"));
						dialogPanel.add(zOrig);
						
						int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
					               "P??idat dvan??ctist??n, nejkr??sn??j???? z plat??nsk??ch t??les", JOptionPane.OK_CANCEL_OPTION);
					      if (result == JOptionPane.OK_OPTION) {

					    	 int dodecahedronSize = Integer.parseInt(size.getText());
					    	 int x = Integer.parseInt(xOrig.getText());
					    	 int y = Integer.parseInt(yOrig.getText());
					    	 int z = Integer.parseInt(zOrig.getText());
					    	 
					    	 // ???????? ?????? t???? ??????l???? ???? ??????????????????
					    	 Dodecahedron ?????????????????????? = new Dodecahedron(dodecahedronSize);
					    	 SceneObject dodecahedronObj = new SceneObject("Dvan??ctist??n [" + dodecahedronSize + "; ["+x+"; "+y+"; "+z+"]]", x, y, z, ??????????????????????);
					    	 sceneList.add(dodecahedronObj);
					      }
						
						render();
					}
		});
		panelControl.add(buttonDodecahedron);
		
		// k??ivka
		JButton buttonCurve = new JButton("K??ivku");
		buttonCurve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// kontejner
				JPanel dialogPanel = new JPanel();
				dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
				
				JPanel curveType = new JPanel();
				JPanel point1 = new JPanel();
				JPanel point2 = new JPanel();
				JPanel point3 = new JPanel();
				JPanel point4 = new JPanel();
				
				JTextField x1 = new JTextField("0", 5);
			    JTextField y1 = new JTextField("0", 5);
			    JTextField z1 = new JTextField("0", 5);
			    
			    JTextField x2 = new JTextField("0", 5);
			    JTextField y2 = new JTextField("0", 5);
			    JTextField z2 = new JTextField("0", 5);
			    
			    JTextField x3 = new JTextField("0", 5);
			    JTextField y3 = new JTextField("0", 5);
			    JTextField z3 = new JTextField("0", 5);
			    
			    JTextField x4 = new JTextField("0", 5);
			    JTextField y4 = new JTextField("0", 5);
			    JTextField z4 = new JTextField("0", 5);
			    
			    JRadioButton radioBezier = new JRadioButton("B??zier");
			    radioBezier.setSelected(true);
			    JRadioButton radioFerguson = new JRadioButton("Ferguson");
			    JRadioButton radioCoons = new JRadioButton("Coons");
			    
			    ButtonGroup radioCurveGroup = new ButtonGroup();
			    radioCurveGroup.add(radioBezier);
			    radioCurveGroup.add(radioFerguson);
			    radioCurveGroup.add(radioCoons);
			    
			    
			    GroupLayout layoutCurve = new GroupLayout(curveType);
			    layoutCurve.setAutoCreateGaps(true);
			    layoutCurve.setAutoCreateContainerGaps(true);
				
			    layoutCurve.setHorizontalGroup(
			    		layoutCurve.createSequentialGroup()
							.addComponent(new JLabel("K??ivka:"))
						    .addComponent(radioBezier)
						    .addComponent(radioFerguson)
						    .addComponent(radioCoons)
				);
				dialogPanel.add(curveType);
				
				GroupLayout layout1 = new GroupLayout(point1);
				layout1.setAutoCreateGaps(true);
				layout1.setAutoCreateContainerGaps(true);
				
				layout1.setHorizontalGroup(
						layout1.createSequentialGroup()
							.addComponent(new JLabel("Bod 1:"))
						    .addComponent(x1)
						    .addComponent(y1)
						    .addComponent(z1)
				);
				dialogPanel.add(point1);
				
				GroupLayout layout2 = new GroupLayout(point2);
				layout2.setAutoCreateGaps(true);
				layout2.setAutoCreateContainerGaps(true);
				
				layout2.setHorizontalGroup(
						layout2.createSequentialGroup()
							.addComponent(new JLabel("Bod 2:"))
						    .addComponent(x2)
						    .addComponent(y2)
						    .addComponent(z2)
				);
				dialogPanel.add(point2);
				
				GroupLayout layout3 = new GroupLayout(point3);
				layout3.setAutoCreateGaps(true);
				layout3.setAutoCreateContainerGaps(true);
				
				layout3.setHorizontalGroup(
						layout3.createSequentialGroup()
							.addComponent(new JLabel("Bod 3:"))
						    .addComponent(x3)
						    .addComponent(y3)
						    .addComponent(z3)
				);
				dialogPanel.add(point3);
				
				GroupLayout layout4 = new GroupLayout(point4);
				layout4.setAutoCreateGaps(true);
				layout4.setAutoCreateContainerGaps(true);
				
				layout4.setHorizontalGroup(
						layout4.createSequentialGroup()
							.addComponent(new JLabel("Bod 4:"))
						    .addComponent(x4)
						    .addComponent(y4)
						    .addComponent(z4)
				);
				dialogPanel.add(point4);
		
				
				int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
			               "P??idat k??ivku", JOptionPane.OK_CANCEL_OPTION);
				
				if (result == JOptionPane.OK_OPTION) {
				
					
					Point3D p1 = new Point3D(Double.parseDouble(x1.getText()), Double.parseDouble(y1.getText()), Double.parseDouble(z1.getText()));
					Point3D p2 = new Point3D(Double.parseDouble(x2.getText()), Double.parseDouble(y2.getText()), Double.parseDouble(z2.getText()));
					Point3D p3 = new Point3D(Double.parseDouble(x3.getText()), Double.parseDouble(y3.getText()), Double.parseDouble(z3.getText()));
					Point3D p4 = new Point3D(Double.parseDouble(x4.getText()), Double.parseDouble(y4.getText()), Double.parseDouble(z4.getText()));
					
					
					ISolid curve = null;
					String objName = "";
					String idCoords = " [" + Integer.parseInt(x1.getText()) + ", " + Integer.parseInt(y1.getText()) + ", " + Integer.parseInt(z1.getText()) + "]";
					
					if (radioBezier.isSelected()) {
						objName = "B??zier" + idCoords;
						curve = new CurveBezier(p1, p2, p3, p4);
					}
					
					if (radioFerguson.isSelected()) {
						objName = "Ferguson" + idCoords;
						curve = new CurveFerguson(p1, p2, p3, p4);
					}
					
					if (radioCoons.isSelected()) {
						objName = "Coons" + idCoords;
						curve = new CurveCoons(p1, p2, p3, p4);
					}
					
					SceneObject curveObj = new SceneObject(objName, 0, 0, 0, curve);
					sceneList.add(curveObj);
					
				}
				
				render();
			}
		});
		panelControl.add(buttonCurve);
		
		JButton buttonAddLight = new JButton("Sv??tlo...");
		buttonAddLight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// kontejner
				JPanel dialogPanel = new JPanel();
				dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
				
				// TODO ...
				
				JPanel nameSet = new JPanel();
				JPanel point = new JPanel();
				JPanel colorSet = new JPanel();
				JPanel intensitySet = new JPanel();
				
				JTextField nameBox = new JTextField("Sv??tlo", 10);
				
				JTextField x1 = new JTextField("0", 5);
			    JTextField y1 = new JTextField("0", 5);
			    JTextField z1 = new JTextField("0", 5);
			    
			    JTextField r = new JTextField("255", 5);
			    JTextField g = new JTextField("255", 5);
			    JTextField b = new JTextField("255", 5);
			    
			    JTextField intensityBox = new JTextField("0.5", 5);
			    JTextField dropRateBox = new JTextField("0.0", 5);
			    
			    GroupLayout layout0 = new GroupLayout(nameSet);
				layout0.setAutoCreateGaps(true);
				layout0.setAutoCreateContainerGaps(true);
				
				layout0.setHorizontalGroup(
						layout0.createSequentialGroup()
							.addComponent(new JLabel("N??zev:"))
						    .addComponent(nameBox)
				);
				dialogPanel.add(nameBox);
			    
			    GroupLayout layout1 = new GroupLayout(point);
				layout1.setAutoCreateGaps(true);
				layout1.setAutoCreateContainerGaps(true);
				
				layout1.setHorizontalGroup(
						layout1.createSequentialGroup()
							.addComponent(new JLabel("Poloha:"))
						    .addComponent(x1)
						    .addComponent(y1)
						    .addComponent(z1)
				);
				dialogPanel.add(point);
				
				GroupLayout layout2 = new GroupLayout(colorSet);
				layout2.setAutoCreateGaps(true);
				layout2.setAutoCreateContainerGaps(true);
				
				layout2.setHorizontalGroup(
						layout2.createSequentialGroup()
							.addComponent(new JLabel("Barva [RGB]:"))
						    .addComponent(r)
						    .addComponent(g)
						    .addComponent(b)
				);
				dialogPanel.add(colorSet);
				
				GroupLayout layout3 = new GroupLayout(intensitySet);
				layout3.setAutoCreateGaps(true);
				layout3.setAutoCreateContainerGaps(true);
				
				layout3.setHorizontalGroup(
						layout3.createSequentialGroup()
							.addComponent(new JLabel("Intenzita (0-1):"))
						    .addComponent(intensityBox)
						    .addComponent(new JLabel("Rozptyl:"))
						    .addComponent(dropRateBox)
				);
				dialogPanel.add(intensitySet);
				
				// dialog - vstup
				int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
			               "P??idat sv??tlo", JOptionPane.OK_CANCEL_OPTION);
				
				// potvrzen?? dialogu
				if (result == JOptionPane.OK_OPTION) {
				
					
					// poloha sv??tla
					Point3D position = new Point3D(
							Double.parseDouble(x1.getText()),
							Double.parseDouble(y1.getText()),
							Double.parseDouble(z1.getText())
							);
					
					// intenzita
					double intensity = Double.parseDouble(intensityBox.getText());
					
					// barva
					Color color = new Color(Float.parseFloat(r.getText())/255, Float.parseFloat(g.getText())/255, Float.parseFloat(b.getText())/255);
					
					// rozptyl
					double dropRate = Double.parseDouble(dropRateBox.getText());
					
					Light light = new Light(position, intensity, color, dropRate);
					
					String lightName = nameBox.getText();
					
					// vytvo??en?? sv??tl ajko objektu sc??ny
					SceneObject lightObj = new SceneObject(lightName, (int) position.x, (int) position.y, (int) position.z, light);
					sceneList.add(lightObj);
				}
				
				// proveden?? zm??n
				render();
			}
		});
		panelControl.add(buttonAddLight);
		
		panelControl.add(new JLabel("Objekty:"));
		
		// seznam objekt?? na sc??n??
		objectList.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(objectList);
		scrollPane.setPreferredSize(new Dimension(190, 90));
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		panelControl.add(scrollPane);
		
		// n??hodn?? tla????tko
		JButton buttonRand = new JButton("P??idat n??hodn?? objekty");
		buttonRand.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						addRandomObjects(10);
						
						render();
					}
		});
		panelControl.add(buttonRand);
				
		// mazac?? tla????tko
		JButton buttonClear = new JButton("Smazat v??e");
		buttonClear.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("Po??adov??no smaz??n?? objekt??...");
						sceneList.clear();
						clearCanvas();
						render();
					}
		});
		panelControl.add(buttonClear);
		
		panelControl.add(new JLabel("Vybran?? objekty:"));
		
		// Smaz??n??
		JButton buttonRemove = new JButton("Smazat");
		buttonRemove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (objectList.getSelectedIndex() >= 0) {
					
					sceneList.remove(objectList.getSelectedIndex());
					
				}
				
				render();
				
			}
			
		});
		panelControl.add(buttonRemove);
		
		// posunut??
		JButton buttonMove = new JButton("Posunout");
		buttonMove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (objectList.getSelectedIndex() >= 0) {

					// kontejner dialogu
					JPanel dialogPanel = new JPanel();
					
					JTextField x = new JTextField("0", 5);
					JTextField y = new JTextField("0", 5);
		    		JTextField z = new JTextField("0", 5);
		    		
		    		dialogPanel.add(new JLabel("Posunout o"));
				    dialogPanel.add(Box.createHorizontalStrut(10));
				    
					dialogPanel.add(new JLabel("X:"));
					dialogPanel.add(x);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Y:"));
					dialogPanel.add(y);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Z:"));
					dialogPanel.add(z);
					
					int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
								"Posunout objekt",
								JOptionPane.OK_CANCEL_OPTION);
					
					if (result == JOptionPane.OK_OPTION) {
					
						int newX = Integer.parseInt(x.getText());
						int newY = Integer.parseInt(y.getText());
						int newZ = Integer.parseInt(z.getText());
						
						sceneList.get(objectList.getSelectedIndex()).setMatrix(sceneList.get(objectList.getSelectedIndex()).getMatrix().mul(new Mat4Transl(newX, newY, newZ)));
					
						// hotovo:
						render();
					}
					
				} else {
					System.err.println("Nevybr??n ????dn?? objekt.");
				}
				
			}
		});
		panelControl.add(buttonMove);
		
		// rotace
		JButton buttonRotate = new JButton("Rotovat");
		buttonRotate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (objectList.getSelectedIndex() >= 0) {

					// kontejner dialogu
					JPanel dialogPanel = new JPanel();
					
					JTextField x = new JTextField("0", 5);
					JTextField y = new JTextField("0", 5);
		    		JTextField z = new JTextField("0", 5);
		    		
		    		dialogPanel.add(new JLabel("Rotovat o:"));
				    dialogPanel.add(Box.createHorizontalStrut(10));
				    
					dialogPanel.add(new JLabel("X [??]:"));
					dialogPanel.add(x);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Y [??]:"));
					dialogPanel.add(y);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Z [??]:"));
					dialogPanel.add(z);
					
					int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
								"Rotovat objekt",
								JOptionPane.OK_CANCEL_OPTION);
					
					if (result == JOptionPane.OK_OPTION) {
					
						double newX = Math.PI/180 * Integer.parseInt(x.getText());
						double newY = Math.PI/180 * Integer.parseInt(y.getText());
						double newZ = Math.PI/180 * Integer.parseInt(z.getText());
						
						sceneList.get(objectList.getSelectedIndex()).setMatrix(sceneList.get(objectList.getSelectedIndex()).getMatrix().mul(new Mat4RotXYZ(newX, newY, newZ)));
					
						// hotovo:
						render();
					}
					
				} else {
					System.err.println("Nevybr??n ????dn?? objekt.");
				}
				
			}
		});
		panelControl.add(buttonRotate);
		
		// zv??t??en??
		JButton buttonScale = new JButton("Zm??nit m??????tko");
		buttonScale.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (objectList.getSelectedIndex() >= 0) {

					// kontejner dialogu
					JPanel dialogPanel = new JPanel();
					
					JTextField x = new JTextField("1.0", 5);
					JTextField y = new JTextField("1.0", 5);
		    		JTextField z = new JTextField("1.0", 5);
		    		
		    		dialogPanel.add(new JLabel("Zm??nit m??????tko:"));
				    dialogPanel.add(Box.createHorizontalStrut(10));
				    
					dialogPanel.add(new JLabel("X ??:"));
					dialogPanel.add(x);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Y ??:"));
					dialogPanel.add(y);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Z ??:"));
					dialogPanel.add(z);
					
					int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
								"Zm??nit m??????tko objektu",
								JOptionPane.OK_CANCEL_OPTION);
					
					if (result == JOptionPane.OK_OPTION) {
					
						double newX = Double.parseDouble(x.getText());
						double newY = Double.parseDouble(y.getText());
						double newZ = Double.parseDouble(z.getText());
						
						sceneList.get(objectList.getSelectedIndex()).setMatrix(sceneList.get(objectList.getSelectedIndex()).getMatrix().mul(new Mat4Scale(newX, newY, newZ)));
					
						// hotovo:
						render();
					}
					
				} else {
					System.err.println("Nevybr??n ????dn?? objekt.");
				}
			}
		});
		panelControl.add(buttonScale);
		
		// ru??en?? ??prav
		JButton buttonClearMatrix = new JButton("Zru??it transformace");
		buttonClearMatrix.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (objectList.getSelectedIndex() >= 0) {

					// kompletn?? zm??na na jednotkovou matici
					sceneList.get(objectList.getSelectedIndex()).setMatrix(new Mat4Identity());
					
					render();
				} else {
					System.err.println("Nevybr??n ????dn?? objekt.");
				}
				
			}
		});
		panelControl.add(buttonClearMatrix);
		
		// ????d??c?? panel
		add(panelControl, BorderLayout.EAST);
		
		panelCanvas.requestFocus();
	}
	
	/**
	 * Nastaven?? rendereru a kamery
	 * 
	 */
	protected void setStart() {
		
		// zdroj - pl??tno
		Graphics gr = canvas.getGraphics();
		gr.setColor(new Color(colorWire));
		
		// hlavn?? nastaven?? rendereru
		renderer = new Renderer(gr, canvas.getWidth(), canvas.getHeight(), colorWire);
		
		// sv??tla
		lightManager = new LightManager();
		renderer.setLightManager((useLights) ? lightManager : null);
		
		// kamera
		camera.setPosition(new Vec3D(50, 80, 95));
		camera.setAzimuth(1 + Math.PI);
		camera.setZenith(-0.75);
		camera.backward(100);
	}
	
	/**
	 * Inicializace pl??tna
	 */
	protected void initCanvas() {
		this.canvasWidth = panelCanvas.getWidth();
		this.canvasHeight = panelCanvas.getHeight();
		
		System.out.println("Zav??d?? se pl??tno o ??????ce " + panelCanvas.getWidth() + " px a v????ce " + panelCanvas.getHeight() + " px.");
		canvas = new BufferedImage(panelCanvas.getWidth(), panelCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		clearCanvas();
	}
	
	/**
	 * P??ekreslen?? pl??tna na kontejner.
	 * 
	 */
	protected void repaintCanvas() {
		panelCanvas.getGraphics().drawImage(canvas, 0, 0, null);
		panelCanvas.requestFocus();
	}
	
	/**
	 * Vy??i??t??n?? kresl??c??ho pl??tna
	 * 
	 */
	protected void clearCanvas() {
		
		// kdyby n??hodou - lazy init
		if (canvas == null) {
			initCanvas();
		}
		
		// grafick?? zdroj
		Graphics canvasGr = canvas.getGraphics();
		// nastaven?? na liliov?? b??lo
		canvasGr.setColor(Color.white);
		// p??ekryt?? cel?? plochy
		canvasGr.fillRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
	
		// p??ekreslen??
		repaintCanvas();
	}
	
	
	/**
	 * Kreslen??.
	 * 
	 */
	public void render() {
		labelXYZ.setText("Kamera [" + (int) camera.getPosition().x + ", " + (int) camera.getPosition().y + ", " + (int) camera.getPosition().z + "]");
		clearCanvas();
		
		long startTime = System.nanoTime();
		
		renderer.reset(); // David??v reset
		
		AxisX3D ax = new AxisX3D(axisLen);
		SceneObject obj = new SceneObject("Osa X", 0, 0, 0, ax);
		renderer.addRenderedObject(obj, !wireframe, camera.getViewMatrix().mul(perps));
		
		AxisY3D ay = new AxisY3D(axisLen);
		SceneObject obj2 = new SceneObject("Osa Y", 0, 0, 0, ay);
		renderer.addRenderedObject(obj2, !wireframe, camera.getViewMatrix().mul(perps));

		AxisZ3D az = new AxisZ3D(axisLen);
		SceneObject obj3 = new SceneObject("Osa Z", 0, 0, 0, az);
		renderer.addRenderedObject(obj3, !wireframe, camera.getViewMatrix().mul(perps));
		
		// proch??zen?? seznamu t??les na sc??n??
		listModel.clear();
		// vymaz??n?? spr??vce sv??tel
		lightManager.clearLights();
		
		Iterator<SceneObject> iter = sceneList.iterator();
		while (iter.hasNext()) {
			SceneObject listObj = iter.next();
			
			listModel.addElement(listObj.getName());
			
			//System.out.println("Renderuje se " + listObj.getName());
			
			// t??leso
			if (listObj.getSolid() != null) {
				renderer.addRenderedObject(listObj, !wireframe, new Mat4Transl(listObj.getX(), listObj.getY(), listObj.getZ()).mul(listObj.getMatrix().mul(camera.getViewMatrix().mul(perps))));
			}
			
			// sv??tlo
			if (listObj.getLight() != null) {
				
				// pozice sv??tla
				listObj.getLight().setMatrix(new Mat4Transl(listObj.getX(), listObj.getY(), listObj.getZ()).mul(listObj.getLight().getMatrix()));
				
				// p??id??n?? do spr??vce sv??tel
				lightManager.addLight(listObj.getLight());
			}
		}
		
		// ...a odpal to
		renderer.render();
		
		long stopTime = System.nanoTime();
		
		repaintCanvas();
		
		System.out.println("Vykresleno " + (3 + sceneList.size()) + " objekt?? za " + (stopTime - startTime) / 1000 / 1000 + " ms.");
		//System.out.println("Camera position ["+camera.getPosition().x+", "+camera.getPosition().y+", "+camera.getPosition().z+"], azimuth " + camera.getAzimuth() + ", zenith " + camera.getZenith());
		
	}
	
	/**
	 * vypl??cne p??r objekt?? do prostoru
	 * 
	 * @param max
	 */
	public void addRandomObjects(int max) {
		
		Random rand = new Random();
		
		int i = 0;
		for (i = 0; i < max; i ++) {
			
			ISolid solid;
			String name;
			int size = rand.nextInt(40) + 10;
			int trX = rand.nextInt(120) - 60;
			int trY = rand.nextInt(120) - 60;
			int trZ = rand.nextInt(120) - 60;
			
			// podle n??lady
			if (rand.nextInt(101) <= 33) {
				solid = new Cube(size);
				name = "Krychle ["+size+";["+trX+"; "+trY+"; "+trZ+"]]";
			} else {
				if (rand.nextInt(101) <= 66) {
					solid = new Tetrahedron(size);
					name = "??ty??st??n ["+size+";["+trX+"; "+trY+"; "+trZ+"]]";
				} else {
					solid = new Dodecahedron(size);
					name = "Dvan??ctist??n ["+size+";["+trX+"; "+trY+"; "+trZ+"]]";
				}
			}
			
			SceneObject obj = new SceneObject(name,	trX, trY, trZ, solid);
			
			sceneList.add(obj);
		}
		
		// jen pokud se aktu??ln?? sv??tluje
		if (useLights) {
			addRandomLights(4);
		}
	}
	
	/**
	 * n??hodn?? sv??tla 
	 * 
	 * @param max
	 */
	public void addRandomLights(int max) {
		
		Random rand = new Random();
		
		int i = 0;
		for (i = 0; i < max; i ++) {
			
			int trX = rand.nextInt(200) - 120;
			int trY = rand.nextInt(200) - 120;
			int trZ = rand.nextInt(200) - 120;
			
			Point3D position = new Point3D(trX, trY, trZ);
			
			double clR = Math.random();
			double clG = Math.random();
			double clB = Math.random();
			
			double intensity = Math.random();
			Color color = new Color((float)clR, (float)clG, (float)clB);
			
			int dR = rand.nextInt(200) - 10;
			
			String name = "L#"+Integer.toHexString(color.getRGB())+" "+intensity+"/"+dR+" ["+trX+";"+trY+";"+trZ+"]";
			
			Light light = new Light(position, intensity, color, dR);
			
			SceneObject objLi = new SceneObject(name, trX, trY, trZ, light);
			
			sceneList.add(objLi);
		}
		
	}
	
	/**
	 * Na??te demo data
	 * 
	 */
	private void demo() {
		
		System.out.println("Zav??d?? se demo...");
		
		// 1) smazat v??e
		System.out.print("Maz??n?? v??ech sou??asn??ch objekt??... ");
		sceneList.clear();
		clearCanvas();
		System.out.println("OK.");
		
		// 3) p????prava objekt??
		
		// centr??ln?? krychle [0]
		Cube cube = new Cube(35);
		SceneObject cubeObj = new SceneObject("Demo Krychle", 0, 0, 0, cube);
		sceneList.add(cubeObj);

		// ??ty??st??ny
		System.out.print("Zav??d?? se t??lesa... ");
		// prvn?? ??ty??st??n [1]
		Tetrahedron tet1 = new Tetrahedron(25);
		SceneObject tet1Obj = new SceneObject("Demo ??ty??st??n 1", 0, 0, 0, tet1);
		// rotuje o 90 podle X
		tet1Obj.setMatrix(tet1Obj.getMatrix().mul(new Mat4RotXYZ(Math.PI/2, 0, 0)));
		sceneList.add(tet1Obj);
		
		// druh?? ??ty??st??n [2]
		Tetrahedron tet2 = new Tetrahedron(25);
		SceneObject tet2Obj = new SceneObject("Demo ??ty??st??n 2", 0, 0, 0, tet2);
		// rotuje o 90 podle Z
		tet2Obj.setMatrix(tet2Obj.getMatrix().mul(new Mat4RotXYZ(0, 0, Math.PI/2)));
		sceneList.add(tet2Obj);

		// t??et?? ??ty??st??n [3]
		Tetrahedron tet3 = new Tetrahedron(25);
		SceneObject tet3Obj = new SceneObject("Demo ??ty??st??n 3", 0, 0, 0, tet3);
		// rotuje o 90 podle Y
		tet3Obj.setMatrix(tet3Obj.getMatrix().mul(new Mat4RotXYZ(0, Math.PI/2, 0)));
		sceneList.add(tet3Obj);
		
		// ??tvrt?? ??ty??st??n [4]
		Tetrahedron tet4 = new Tetrahedron(25);
		SceneObject tet4Obj = new SceneObject("Demo ??ty??st??n 4", 0, 0, 0, tet4);
		// rotuje o 90 podle X a Z
		tet4Obj.setMatrix(tet4Obj.getMatrix().mul(new Mat4RotXYZ(Math.PI/2, 0, Math.PI/2)));
		sceneList.add(tet4Obj);
		
		// p??t?? ??ty??st??n [5]
		Tetrahedron tet5 = new Tetrahedron(25);
		SceneObject tet5Obj = new SceneObject("Demo ??ty??st??n 5", 0, 0, 0, tet5);
		// rotuje o 90 podle X a Y
		tet5Obj.setMatrix(tet5Obj.getMatrix().mul(new Mat4RotXYZ(Math.PI/2, Math.PI/2, 0)));
		sceneList.add(tet5Obj);
		
		// ??est?? ??ty??st??n [6]
		Tetrahedron tet6 = new Tetrahedron(25);
		SceneObject tet6Obj = new SceneObject("Demo ??ty??st??n 6", 0, 0, 0, tet6);
		sceneList.add(tet6Obj);
		
		// mal?? krychle 0
		Cube smCube0 = new Cube(5);
		SceneObject sC0Obj = new SceneObject("Demo mal?? krychle 0", 0, 0, 0, smCube0);
		sceneList.add(sC0Obj);
		
		// mal?? krychle 1
		Cube smCube1 = new Cube(5);
		SceneObject sC1Obj = new SceneObject("Demo mal?? krychle 1", 40, 0, 0, smCube1);
		sceneList.add(sC1Obj);
		
		// mal?? krychle 2
		Cube smCube2 = new Cube(5);
		SceneObject sC2Obj = new SceneObject("Demo mal?? krychle 2", 0, 40, 0, smCube2);
		sceneList.add(sC2Obj);
		
		// mal?? krychle 3
		Cube smCube3 = new Cube(5);
		SceneObject sC3Obj = new SceneObject("Demo mal?? krychle 3", 0, 0, 40, smCube3);
		sceneList.add(sC3Obj);
		
		// mal?? krychle 4
		Cube smCube4 = new Cube(5);
		SceneObject sC4Obj = new SceneObject("Demo mal?? krychle 4", 40, 40, 0, smCube4);
		sceneList.add(sC4Obj);
		
		// mal?? krychle 5
		Cube smCube5 = new Cube(5);
		SceneObject sC5Obj = new SceneObject("Demo mal?? krychle 5", 40, 0, 40, smCube5);
		sceneList.add(sC5Obj);
		
		// mal?? krychle 6
		Cube smCube6 = new Cube(5);
		SceneObject sC6Obj = new SceneObject("Demo mal?? krychle 6", 0, 40, 40, smCube6);
		sceneList.add(sC6Obj);
		
		// mal?? krychle 7
		Cube smCube7 = new Cube(5);
		SceneObject sC7Obj = new SceneObject("Demo mal?? krychle 7", 40, 40, 40, smCube7);
		sceneList.add(sC7Obj);
		
		// mal?? krychle 8
		Cube smCube8 = new Cube(5);
		SceneObject sC8Obj = new SceneObject("Demo mal?? krychle 8", -40, 0, 0, smCube8);
		sceneList.add(sC8Obj);
		
		// mal?? krychle 9
		Cube smCube9 = new Cube(5);
		SceneObject sC9Obj = new SceneObject("Demo mal?? krychle 9", 0, -40, 0, smCube9);
		sceneList.add(sC9Obj);
		
		// mal?? krychle 10
		Cube smCubeA = new Cube(5);
		SceneObject sCAObj = new SceneObject("Demo mal?? krychle 10", 0, 0, -40, smCubeA);
		sceneList.add(sCAObj);
		
		// mal?? krychle 11
		Cube smCubeB = new Cube(5);
		SceneObject sCBObj = new SceneObject("Demo mal?? krychle 11", -40, -40, 0, smCubeB);
		sceneList.add(sCBObj);
		
		// maly dvan??ctist??n
		Dodecahedron smDode = new Dodecahedron(5);
		SceneObject sDoObj = new SceneObject("Demo Dvan??stist??n", 0, 0, 50, smDode);
		sceneList.add(sDoObj);
		
		System.out.println("OK.");
		
		// 4) sv??tla
		
		System.out.print("Nastavuj?? se sv??tla... ");
		
		Light sun = new Light(new Point3D(500, 500, 500), 1.0f, new Color(255, 255, 255), 0.0f);
		Light pokus1 = new Light(new Point3D(50, 2, 2), 0.7f, new Color(0, 0, 255), 5.0f);
		Light pokus2 = new Light(new Point3D(2, 50, 2), 0.7f, new Color(255, 0, 0), 5.0f);
		Light pokus3 = new Light(new Point3D(2, 2, 50), 0.7f, new Color(0, 255, 0), 5.0f);
			
		SceneObject lightSun = new SceneObject("Slunce", 500, 500, 500, sun);
		SceneObject lightP1 = new SceneObject("Modr?? X 0.7/5", 50, 2, 2, pokus1);
		SceneObject lightP2 = new SceneObject("??erven?? Y 0.7/5", 2, 50, 2, pokus2);
		SceneObject lightP3 = new SceneObject("Zelen?? Z 0.7/5", 2, 2, 50, pokus3);
				
		sceneList.add(lightSun);
		sceneList.add(lightP1);
		sceneList.add(lightP2);
		sceneList.add(lightP3);
				
		System.out.println("OK.");
		
		// 5) nastaven?? kamery
		System.out.print("Nastavuje se kamera... ");
		
		camera.setPosition(new Vec3D(50, 80, 95));
		camera.setAzimuth(1 + Math.PI);
		camera.setZenith(-0.75);
		
		System.out.println("OK.");
		
		// 6) render
		this.render();
		
	}

	/**
	 * __main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// 800x600
				new Zbuffer().initApp(800, 600);
			}
			
		});
		

	}

}
