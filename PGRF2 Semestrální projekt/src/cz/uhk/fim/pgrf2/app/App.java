/**
 * 
 */
package cz.uhk.fim.pgrf2.app;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;

import cz.uhk.fim.pgrf2.runtime.Renderer;


/**
 * PGRF2: Semestrální projekt
 * 
 * Kolize objektů v uzavřeném prostoru využívající oktalové stromy.
 * 
 * UHK FIM K-AI-3 3., ZS 2014/2015
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * 
 *
 */
public class App extends JFrame {

	/** serializační id */
	private static final long serialVersionUID = -8653221989084757418L;
	
	/** frame limiter */
	private static final int FPS = 60;

	/**
	 * Konstruktor. Pošle supertřídě titulek okna.
	 */
	public App() {
		super("PGRF2 Semestrální projekt - Detekce kolizí pomocí oktalových stromů :: Jan Hladěna");
	}
	
	/**
	 * inicializace okna 
	 * 
	 * @param width
	 * @param height
	 */
	private void initApp(int width, int height) {
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(width, height);
		setLocationRelativeTo(null);
		setVisible(true);
		
		// asynchronní inicializace plátna
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				// plátno
				initCanvas();

			}
		});
		
	}
	
	/**
	 * inicializace plátna
	 */
	private void initCanvas() {
		
		GLProfile glProfile = GLProfile.get(GLProfile.GL2);
		GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		
		// plátno
		GLCanvas canvas = new GLCanvas(glCapabilities);
		
		// zavěšení rendereru
		Renderer renderer = new Renderer();
		canvas.addGLEventListener(renderer);
		canvas.addMouseListener(renderer);
		canvas.addMouseMotionListener(renderer);
		canvas.addKeyListener(renderer);
		
		// velikost plátna
		canvas.setSize(this.getWidth(), this.getHeight());
		
		// puf do okna
		this.add(canvas);
		
		final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
				// zastavení animátoru
				if (animator.isStarted()) {
					animator.pause();
				}
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				new Thread() {
					
					@Override
                    public void run() {
                    	
						// zabití animátoru i aplikace
						if (animator.isStarted()) animator.stop();
						System.exit(0);
                    }
					
                 }.start();
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
				// znovurozběhnutí
				if (animator.isPaused()) {
					animator.resume();
				}
				
			}
		});
		
		animator.start();
		canvas.requestFocus();
	}

	/**
	 * __main()
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// 800x600
				new App().initApp(1280, 720);
			}
			
		});

	}

}
