/**
 * 
 */
package cz.uhk.pgrf1.projekt3.app;

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

import cz.uhk.pgrf1.projekt3.model.AxisX;
import cz.uhk.pgrf1.projekt3.model.AxisY;
import cz.uhk.pgrf1.projekt3.model.AxisZ;
import cz.uhk.pgrf1.projekt3.model.Cube;
import cz.uhk.pgrf1.projekt3.model.CurveBezier;
import cz.uhk.pgrf1.projekt3.model.CurveCoons;
import cz.uhk.pgrf1.projekt3.model.CurveFerguson;
import cz.uhk.pgrf1.projekt3.model.Dodecahedron;
import cz.uhk.pgrf1.projekt3.model.Renderer;
import cz.uhk.pgrf1.projekt3.model.SceneObject;
import cz.uhk.pgrf1.projekt3.model.ISolid;
import cz.uhk.pgrf1.projekt3.model.Tetrahedron;
import cz.uhk.pgrf1.projekt3.transforms3D.Camera;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4Identity;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4PerspRH;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4OrthoRH;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4RotXYZ;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4Scale;
import cz.uhk.pgrf1.projekt3.transforms3D.Mat4Transl;
import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;
import cz.uhk.pgrf1.projekt3.transforms3D.Vec3D;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Jednotřídní, jednookenní aplikace.
 */
public class Grafika3 extends JFrame {
	
	/** Hlavní kreslící plátno */
	protected BufferedImage canvas;
	/** Kontejner plátna */
	protected JPanel panelCanvas;
	/** souřadnice kamery */
	final protected JLabel labelXYZ = new JLabel("[X, Y, Z]");
	
	/** Počátek X */
	protected int originX;
	/** Počátek Y */
	protected int originY;
	
	/** Konec X */
	protected int endX;
	/** Konec Y */
	protected int endY;
	
	/** délka osy */
	protected int axisLen = 50;
	/** krok pohybu */
	protected int stepSize = 5;
	/** šířka plátna */
	protected int canvasWidth;
	/** výška plátna */
	protected int canvasHeight;
	
	// barva drátu
	protected int colorWire = 0xff000000;
	// barva osy
	protected int colorAxisX = 0xffff0000;
	protected int colorAxisY = 0xff00ff00;
	protected int colorAxisZ = 0xff0000ff;
	
	/** renderer */
	private Renderer renderer;
	/** kamera */
	private Camera camera = new Camera();
	
	/** použití matice ortogonální projekce */
	protected boolean ortho = false;
	/** matice perpektivy */
	protected Mat4 perps = new Mat4PerspRH(Math.PI/4, 1, 0.5, 500);
	
	/** seznam objektů scény */
	protected ArrayList<SceneObject> sceneList = new ArrayList<SceneObject>();
	/** grafická reprezentace seznamu objektů na scéně */
	protected DefaultListModel<String> listModel = new DefaultListModel<String>();
	protected JList<String> objectList = new JList<String>(listModel);
	
	
	/** UID */
	private static final long serialVersionUID = 104128975370418040L;

	public Grafika3() {
		super("PGRF1 Projekt 3: Transformace a zobrazení drátového modelu 3D tělesa");
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
		
		// asynchronní inicializace plátna
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
	 * Kompletní GUI ve Swingu, prakticky všechno včetně dialogů
	 * je namatláno v obsluze ActionListenerů.
	 */
	private void layoutGUI() {
		
		// hlavní komponenta
		panelCanvas = new JPanel();
		add(panelCanvas, BorderLayout.CENTER);
		
		// myš - klik
		panelCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {		
				
				render();
				endX = e.getX();
				endY = e.getY();
			}
		});
		// myš - pohyb
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
		
		// klávesnice
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
				
				// překreslit
				render();
			}
	
		});
		
		// ovládací panel
		JPanel panelControl = new JPanel();
		panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
		
		// [X, Y, Z] kamery
		panelControl.add(labelXYZ);
		
		// přepínač projekce
		final JButton buttonRH = new JButton("[Persp] / Ortho");
		buttonRH.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						// negace projekce
						ortho = !ortho;
						
						if (ortho) {
							// ortogonální
							perps = new Mat4OrthoRH(camera.getPosition().length(), camera.getPosition().length(), 1, 500);
							buttonRH.setText("Persp / [Ortho]");
							System.out.println("Přepnuto do ortogonální projekce.");
						} else {
							// perspektivní
							perps = new Mat4PerspRH(Math.PI/4, 1, 0.5, 500);
							buttonRH.setText("[Persp] / Ortho");
							System.out.println("Přepnuto do perspektivní projekce.");
						}
						
						render();
					}
		});
		panelControl.add(buttonRH);
		
		// náhodné tlačítko
		JButton buttonRand = new JButton("Přidat náhodné objekty");
		buttonRand.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						
						addRandomObjects(10);
						
						render();
					}
		});
		panelControl.add(buttonRand);
		
		// mazací tlačítko
		JButton buttonClear = new JButton("Smazat vše");
		buttonClear.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("Požadováno smazání objektů...");
						sceneList.clear();
						clearCanvas();
						render();
					}
		});
		panelControl.add(buttonClear);
		
		JLabel labelAddObject = new JLabel("Přidat:");
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
					    
					    dialogPanel.add(new JLabel("Počátek"));
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
					               "Přidat krychli", JOptionPane.OK_CANCEL_OPTION);
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
		
		JButton buttonTetrahedron = new JButton("Čtyřstěn");
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
					    
					    dialogPanel.add(new JLabel("Počátek"));
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
					               "Přidat čtyřstěn", JOptionPane.OK_CANCEL_OPTION);
					      if (result == JOptionPane.OK_OPTION) {

					    	 int tetrahedronSize = Integer.parseInt(size.getText());
					    	 int x = Integer.parseInt(xOrig.getText());
					    	 int y = Integer.parseInt(yOrig.getText());
					    	 int z = Integer.parseInt(zOrig.getText());
					    	 
					    	 Tetrahedron newTetrahedron = new Tetrahedron(tetrahedronSize);
					    	 SceneObject tetrahedronObj = new SceneObject("Čtyřstěn [" + tetrahedronSize + "; ["+x+"; "+y+"; "+z+"]]", x, y, z, newTetrahedron);
					    	 sceneList.add(tetrahedronObj);
					      }
						
						render();
					}
		});
		panelControl.add(buttonTetrahedron);
		
		JButton buttonDodecahedron = new JButton("Dvanáctistěn");
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
					    
					    dialogPanel.add(new JLabel("Počátek"));
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
					               "Přidat dvanáctistěn, nejkrásnější z platónských těles", JOptionPane.OK_CANCEL_OPTION);
					      if (result == JOptionPane.OK_OPTION) {

					    	 int dodecahedronSize = Integer.parseInt(size.getText());
					    	 int x = Integer.parseInt(xOrig.getText());
					    	 int y = Integer.parseInt(yOrig.getText());
					    	 int z = Integer.parseInt(zOrig.getText());
					    	 
					    	 // Όψης γαρ tóν αδélóν τα φαινόμενα
					    	 Dodecahedron δωδεκάεδρον = new Dodecahedron(dodecahedronSize);
					    	 SceneObject dodecahedronObj = new SceneObject("Dvanáctistěn [" + dodecahedronSize + "; ["+x+"; "+y+"; "+z+"]]", x, y, z, δωδεκάεδρον);
					    	 sceneList.add(dodecahedronObj);
					      }
						
						render();
					}
		});
		panelControl.add(buttonDodecahedron);
		
		// křivka
		JButton buttonCurve = new JButton("Křivku");
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
			    
			    JRadioButton radioBezier = new JRadioButton("Beziér");
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
							.addComponent(new JLabel("Křivka:"))
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
			               "Přidat křivku", JOptionPane.OK_CANCEL_OPTION);
				
				if (result == JOptionPane.OK_OPTION) {
				
					
					Point3D p1 = new Point3D(Double.parseDouble(x1.getText()), Double.parseDouble(y1.getText()), Double.parseDouble(z1.getText()));
					Point3D p2 = new Point3D(Double.parseDouble(x2.getText()), Double.parseDouble(y2.getText()), Double.parseDouble(z2.getText()));
					Point3D p3 = new Point3D(Double.parseDouble(x3.getText()), Double.parseDouble(y3.getText()), Double.parseDouble(z3.getText()));
					Point3D p4 = new Point3D(Double.parseDouble(x4.getText()), Double.parseDouble(y4.getText()), Double.parseDouble(z4.getText()));
					
					
					ISolid curve = null;
					String objName = "";
					String idCoords = " [" + Integer.parseInt(x1.getText()) + ", " + Integer.parseInt(y1.getText()) + ", " + Integer.parseInt(z1.getText()) + "]";
					
					if (radioBezier.isSelected()) {
						objName = "Beziér" + idCoords;
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
		
		panelControl.add(new JLabel("Objekty:"));
		
		// seznam objektů na scéně
		objectList.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(objectList);
		scrollPane.setPreferredSize(new Dimension(190, 90));
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		panelControl.add(scrollPane);
		
		panelControl.add(new JLabel("Vybrané:"));
		
		// posunutí
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
					System.err.println("Nevybrán žádný objekt.");
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
				    
					dialogPanel.add(new JLabel("X [°]:"));
					dialogPanel.add(x);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Y [°]:"));
					dialogPanel.add(y);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Z [°]:"));
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
					System.err.println("Nevybrán žádný objekt.");
				}
				
			}
		});
		panelControl.add(buttonRotate);
		
		// zvětšení
		JButton buttonScale = new JButton("Změnit měřítko");
		buttonScale.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (objectList.getSelectedIndex() >= 0) {

					// kontejner dialogu
					JPanel dialogPanel = new JPanel();
					
					JTextField x = new JTextField("1.0", 5);
					JTextField y = new JTextField("1.0", 5);
		    		JTextField z = new JTextField("1.0", 5);
		    		
		    		dialogPanel.add(new JLabel("Změnit měřítko:"));
				    dialogPanel.add(Box.createHorizontalStrut(10));
				    
					dialogPanel.add(new JLabel("X ×:"));
					dialogPanel.add(x);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Y ×:"));
					dialogPanel.add(y);
					dialogPanel.add(Box.createHorizontalStrut(10));
					dialogPanel.add(new JLabel("Z ×:"));
					dialogPanel.add(z);
					
					int result = JOptionPane.showConfirmDialog(null, dialogPanel, 
								"Změnit měřítko objektu",
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
					System.err.println("Nevybrán žádný objekt.");
				}
			}
		});
		panelControl.add(buttonScale);
		
		// rušení úprav
		JButton buttonClearMatrix = new JButton("Zrušit úpravy");
		buttonClearMatrix.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (objectList.getSelectedIndex() >= 0) {

					// kompletní změna na jednotkovou matici
					sceneList.get(objectList.getSelectedIndex()).setMatrix(new Mat4Identity());
					
					render();
				} else {
					System.err.println("Nevybrán žádný objekt.");
				}
				
			}
		});
		panelControl.add(buttonClearMatrix);
		
		// řídící panel
		add(panelControl, BorderLayout.EAST);
		
		panelCanvas.requestFocus();
	}
	
	/**
	 * Nastavení rendereru a kamery
	 * 
	 */
	protected void setStart() {
		
		// zdroj - plátno
		Graphics gr = canvas.getGraphics();
		gr.setColor(new Color(colorWire));
		
		// hlavní nastavení rendereru
		renderer = new Renderer(gr, canvas.getWidth(), canvas.getHeight(), colorWire);
		
		// kamera
		camera.setPosition(new Vec3D(40, 10, 40));
		camera.setAzimuth(Math.PI);
		camera.setZenith(-Math.PI/4);
		camera.backward(200);
	}
	
	/**
	 * Inicializace plátna
	 */
	protected void initCanvas() {
		this.canvasWidth = panelCanvas.getWidth();
		this.canvasHeight = panelCanvas.getHeight();
		
		System.out.println("Zavádí se plátno o šířce " + panelCanvas.getWidth() + " px a výšce " + panelCanvas.getHeight() + " px.");
		canvas = new BufferedImage(panelCanvas.getWidth(), panelCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		clearCanvas();
	}
	
	/**
	 * Překreslení plátna na kontejner.
	 * 
	 */
	protected void repaintCanvas() {
		panelCanvas.getGraphics().drawImage(canvas, 0, 0, null);
		panelCanvas.requestFocus();
	}
	
	/**
	 * Vyčištění kreslícího plátna
	 * 
	 */
	protected void clearCanvas() {
		
		// kdyby náhodou - lazy init
		if (canvas == null) {
			initCanvas();
		}
		
		// grafický zdroj
		Graphics canvasGr = canvas.getGraphics();
		// nastavení na sněhobílo - liliově bílo
		canvasGr.setColor(Color.white);
		// překrytí celé plochy
		canvasGr.fillRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
	
		repaintCanvas();
		//System.out.println("A je vymalováno.");
	}
	
	
	/**
	 * Kreslení.
	 */
	public void render() {
		labelXYZ.setText("[" + (int) camera.getPosition().x + ", " + (int) camera.getPosition().y + ", " + (int) camera.getPosition().z + "]");
		clearCanvas();
		
		// vždy se vykreslí osy
		
		AxisX ax = new AxisX(axisLen);
		renderer.setColor(colorAxisX);
		renderer.setIndices(ax.indices());
		renderer.setVertices(ax.vertices());
		renderer.setMatrix(camera.getViewMatrix().mul(perps));
		renderer.render();
		
		AxisY ay = new AxisY(axisLen);
		renderer.setColor(colorAxisY);
		renderer.setIndices(ay.indices());
		renderer.setVertices(ay.vertices());
		renderer.setMatrix(camera.getViewMatrix().mul(perps));
		renderer.render();
		
		AxisZ az = new AxisZ(axisLen);
		renderer.setColor(colorAxisZ);
		renderer.setIndices(az.indices());
		renderer.setVertices(az.vertices());
		renderer.setMatrix(camera.getViewMatrix().mul(perps));
		renderer.render();
		
		/*
		@Deprecated
		Axes axes = new Axes(axisLen);
		renderer.setIndices(axes.indices());
		renderer.setVertices(axes.vertices());
		renderer.setMatrix(camera.getViewMatrix().mul(perps));
		renderer.render();
		*/
		
		renderer.setColor(colorWire);
		listModel.clear();
		
		Iterator<SceneObject> iter = sceneList.iterator();
		while (iter.hasNext()) {
			SceneObject obj = iter.next();
			
			listModel.addElement(obj.getName());
			
			//System.out.println("Renderuje se " + obj.getName());
			
			renderer.setIndices(obj.getSolid().indices());
			renderer.setVertices(obj.getSolid().vertices());
			
			renderer.setMatrix(new Mat4Transl(obj.getX(), obj.getY(), obj.getZ()).mul(obj.getMatrix().mul(camera.getViewMatrix().mul(perps))));
			renderer.render();
		}
		
		repaintCanvas();
	}
	
	/**
	 * přidá pár objektů do prostoru
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
			
			// podle nálady
			if (rand.nextInt(101) <= 33) {
				solid = new Cube(size);
				name = "Krychle ["+size+";["+trX+"; "+trY+"; "+trZ+"]]";
			} else {
				if (rand.nextInt(101) <= 66) {
					solid = new Tetrahedron(size);
					name = "Čtyřstěn ["+size+";["+trX+"; "+trY+"; "+trZ+"]]";
				} else {
					solid = new Dodecahedron(size);
					name = "Dvanáctistěn ["+size+";["+trX+"; "+trY+"; "+trZ+"]]";
				}
			}
			
			SceneObject obj = new SceneObject(name,	trX, trY, trZ, solid);
			
			sceneList.add(obj);
		}
		
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
				new Grafika3().initApp(800, 600);
			}
			
		});
		

	}

}
