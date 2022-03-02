package cz.uhk.fim.hladeja1.pgrf3.gl1.app;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import cz.uhk.fim.hladeja1.pgrf3.gl1.runtime.Renderer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * PGRF3 Projekt 1
 *
 *
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public class App {

	// fixní nastavení maximálních FPS
	private static final int FPS = 20;

	// velikost okna
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;

	// Hlavní vykreslovač - renderer
	private static Renderer renderer;
	private static FPSAnimator animator;

	private static JCheckBox chkWireframe;
	private static JCheckBox chkNormals;
	
	private static JCheckBox chkBump;
	private static JCheckBox chkParallax;
	
	/**
	 * Vstupní bod aplikace.
	 *
	 * @param args parametry
	 */
	public static void main(String[] args) {

		try {
			JFrame mainFrame = new JFrame("KF PGRF3 A1 :: Jan Hladěna <jan.hladena@uhk.cz>");
			mainFrame.setSize(WIDTH, HEIGHT);
			mainFrame.setLayout(new BorderLayout());
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// inicializace rendereru
			renderer = new Renderer();

			// OS X OpenGL Core Profile 3.2
			// https://developer.apple.com/opengl/capabilities/index.html
			GLProfile profile = GLProfile.get(GLProfile.GL3);
			GLCapabilities capabilities = new GLCapabilities(profile);

			// HW-akcelerováno
			capabilities.setHardwareAccelerated(true);
			// dvojitý buffer
			capabilities.setDoubleBuffered(true);

			// Kreslící plocha
			GLCanvas canvas = new GLCanvas(capabilities);
			canvas.setSize(WIDTH, HEIGHT);

			// Obsluhující animátor
			animator = new FPSAnimator(canvas, FPS, true);

			// Přidání plátna do okna
			mainFrame.add(canvas, BorderLayout.CENTER);

			/**
			 * Postranní panel
			 */
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			
			// interní panel
			JPanel internal = new JPanel();
			internal.setLayout(new BoxLayout(internal, BoxLayout.Y_AXIS));

			// DEMO tlačítka
			/*
			JButton demo0 = new JButton("DEBUG");
			demo0.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.getInfo();
					renderer.setDemoProgram(0);
				}

			});
			internal.add(demo0);*/
			
			JButton demo10 = new JButton("GRID");
			demo10.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(10);
				}

			});
			internal.add(demo10);
			
			JButton demo1 = new JButton("Fce XYZ1");
			demo1.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(1);
				}

			});
			internal.add(demo1);
			
			JButton demo2 = new JButton("Fce XYZ2");
			demo2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(2);
				}

			});
			internal.add(demo2);
			
			JButton demo30 = new JButton("KOULE");
			demo30.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(30);
				}

			});
			internal.add(demo30);
			
			JButton demo3 = new JButton("SP1 UFO");
			demo3.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(3);
				}

			});
			internal.add(demo3);
			
			JButton demo4 = new JButton("SP2 Větrník");
			demo4.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(4);
				}

			});
			internal.add(demo4);
			
			/*
			JButton demo50 = new JButton("VÁLEC");
			demo50.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(50);
				}

			});
			internal.add(demo50);
			*/
			
			JButton demo5 = new JButton("CY1 Sombrero");
			demo5.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(5);
				}

			});
			internal.add(demo5);
			
			JButton demo6 = new JButton("CY2 Špička");
			demo6.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(6);
				}

			});
			internal.add(demo6);
			
			JButton demo7 = new JButton("Torus");
			demo7.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.setDemoProgram(7);
				}

			});
			internal.add(demo7);

			
			// checkboxy
			JCheckBox chkRotate = new JCheckBox("Rotace");
			chkRotate.setSelected(true);
			chkRotate.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleRotate(e.getStateChange());
				}
				
			});
			internal.add(chkRotate);
			
			JCheckBox chkDynamic = new JCheckBox("Dynamika");
			chkDynamic.setSelected(true);
			chkDynamic.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleDynamic(e.getStateChange());
				}
				
			});
			internal.add(chkDynamic);
			
			JCheckBox chkPerFragment = new JCheckBox("Per-fragment");
			chkPerFragment.setSelected(false);
			chkPerFragment.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.togglePerFragment(e.getStateChange());
				}
				
			});
			internal.add(chkPerFragment);
			
			JCheckBox chkReflector = new JCheckBox("Reflektor");
			chkReflector.setSelected(false);
			chkReflector.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleReflector(e.getStateChange());
				}
				
			});
			internal.add(chkReflector);
			
			chkWireframe = new JCheckBox("Wireframe");
			chkWireframe.setSelected(true);
			chkWireframe.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleWireframe(e.getStateChange());
				}
				
			});
			internal.add(chkWireframe);
			
			chkNormals = new JCheckBox("GS Normály");
			chkNormals.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleNormals(e.getStateChange());
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						chkWireframe.setSelected(true);
						chkWireframe.setEnabled(false);
					} else {
						chkWireframe.setEnabled(true);
					}
					
				}
				
			});
			internal.add(chkNormals);
			
			JCheckBox chkTexture = new JCheckBox("Textura");
			chkTexture.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleTexture(e.getStateChange());
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						chkWireframe.setSelected(false);
						chkNormals.setSelected(false);
						
						chkWireframe.setSelected(false);
						chkNormals.setSelected(false);
						
						chkBump.setEnabled(true);
						chkParallax.setEnabled(true);
					} else {
						chkWireframe.setEnabled(true);
						chkNormals.setEnabled(true);
						
						chkBump.setSelected(false);
						chkParallax.setSelected(false);
						
						chkBump.setEnabled(false);
						chkParallax.setEnabled(false);
					}
					
				}
				
			});
			internal.add(chkTexture);
			
			chkBump = new JCheckBox("Bump");
			chkBump.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleBump(e.getStateChange());
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						chkWireframe.setSelected(false);
						chkNormals.setSelected(false);
						chkParallax.setSelected(false);
						
						chkWireframe.setEnabled(false);
						chkNormals.setEnabled(false);
						chkParallax.setEnabled(false);
					} else {
						chkWireframe.setEnabled(true);
						chkNormals.setEnabled(true);
						chkParallax.setEnabled(true);
					}
					
				}
				
			});
			internal.add(chkBump);
			
			chkParallax = new JCheckBox("Parallax");
			chkParallax.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					renderer.toggleParallax(e.getStateChange());
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						chkWireframe.setSelected(false);
						chkNormals.setSelected(false);
						chkBump.setSelected(false);
						
						chkWireframe.setEnabled(false);
						chkNormals.setEnabled(false);
						chkBump.setEnabled(false);
					} else {
						chkWireframe.setEnabled(true);
						chkNormals.setEnabled(true);
						chkBump.setEnabled(true);
					}
					
				}
				
			});
			internal.add(chkParallax);
			
			// Reset kamery
			JButton buttonCameraReset = new JButton("Reset kamery");
			buttonCameraReset.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					renderer.resetCamera();
				}

			});
			internal.add(buttonCameraReset);
			
			// umístění interního panelu nahoru
			panel.add(internal, BorderLayout.NORTH);
			
			// Tlačítko pro ukončení
			JButton buttonExit = new JButton("Konec");
			buttonExit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					closeApp(animator);
				}

			});
			panel.add(buttonExit, BorderLayout.SOUTH);

			
			mainFrame.add(panel, BorderLayout.EAST);
			/**
			 * Panel
			 */

			// Reakce na akci s oknem
			mainFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					closeApp(animator);
				}
			});

			// Úprava okna
			mainFrame.pack();
			mainFrame.setVisible(true);

			// Ovládání primárně v GL
			// Číhače v kreslící ploše - obslouženy vykreslovačem
			canvas.addGLEventListener(renderer); // GL akce
			canvas.addKeyListener(renderer); // Klávesnice
			canvas.addMouseListener(renderer); // Klikání myší
			canvas.addMouseMotionListener(renderer); // Pohyb myší

			// Spuštění animátoru
			animator.start();

		} catch (Exception e) {
			// něco se porouchalo
			e.printStackTrace();
		}

	}

	/**
	 * Ukončení aplikace se zastavením běžícího animátoru.
	 *
	 * @param animator
	 */
	private static void closeApp(FPSAnimator animator) {

		// Zastavení animátoru při ukončení - vláknem
		new Thread() {
			@Override
			public void run() {
				if (animator.isStarted()) {
					animator.stop();
				}

				// Ukončení programu
				System.exit(0);
			}
		}.start();

	}

}
