/**
 * 
 */
package cz.uhk.fim.pgrf2.runtime;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL2; 
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import cz.uhk.fim.transforms3D.Point3D;
import cz.uhk.fim.transforms3D.Vec3D;
import cz.uhk.fim.pgrf2.model.Ball;
import cz.uhk.fim.pgrf2.model.Box;
import cz.uhk.fim.pgrf2.runtime.CameraJOGL;



/**
 * OpenGL Renderer.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {
	
	private Box box;

	final int TIMER = 15;
	
	/** řízení gravitace */
	final float GRAVITY_ACC   = 9.81f; // zrychlení
	final Vec3D GRAVITY       = new Vec3D(0, 0, -GRAVITY_ACC); // směr gravitace je vždycky na zem podle -Z (OpenGL -Y)
	
	/** rozměry okna */
	int width = 1280;
	int height = 720;
	
	/** kreslič */
	GLAutoDrawable glDrawable;
	
	GL2 gl;	
	GLU glu;	
	GLUT glut;
	
	/** kamera */
	CameraJOGL camera = new CameraJOGL(); // používá se hybridní transforms3D/JOGL objekt
	
	/** textový kreslič */
	TextRenderer renderer;
	
	// měření skutečných FPS
	long oldmils, oldFPSmils;
	double realFPS;
	
	float speed = 10;
	double stepSize = 0.3;
	
	// pohyb kamery
	// ... myš
	int dx = 0;
	int dy = 0;
	boolean mouseLPressed = false;
	int ox, oy;
	// ... klávesnice
	boolean forward  = false;
	boolean backward = false;
	boolean up       = false;
	boolean down     = false;
	boolean left     = false;
	boolean right    = false;

	// řízení prostředí 
	boolean debugScreen  = false; // ladící informace
	boolean enableLights = true; // světla
	boolean enableFog    = true;  // mlha
	boolean visualizeOctrees = false; // vizualizace oktalových stromů
	
	// zvolené demo
	private enum Demo {DEMO1, DEMO2, DEMO3, DEMO4, DEMO5};
	private Demo demo = Demo.DEMO1;
	
	// světlo
	private float[] lightColorAmbient = {.6f, .6f, .5f, 1.0f};
	// test
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	private float[] lightPosition = {0.0f, 0.0f, 0.0f, 1.0f};
	//private float[] lightPosition = {30.0f, 30.0f, 30.0f, 1.0f};
	private double pos = 0.0;
	
	// barva mlhy - musí korespondovat s mazacím pozadím
	float[] fogColor = {.7f, .7f, .7f, 1.0f};
	
	
	public Renderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		switch (e.getKeyCode()) {
			
			// ladící obrazovka s informacemi
			case KeyEvent.VK_F3:
				debugScreen = !debugScreen;
			break;
			// L: toggle mlhy
			case KeyEvent.VK_F:
				enableFog = !enableFog;
			break;
			// L: toggle světel
			case KeyEvent.VK_L:
				enableLights = !enableLights;
			break;
			// V: toggle vizualizace stromů
			case KeyEvent.VK_V:
				visualizeOctrees = !visualizeOctrees;
			break;
				
			// pohyb
			case KeyEvent.VK_W:
				forward = true;
			break;
			case KeyEvent.VK_S:
				backward = true;
			break;
			case KeyEvent.VK_A:
				left = true;
			break;
			case KeyEvent.VK_D:
				right = true;
			break;
			case KeyEvent.VK_SPACE:
				up = true;
			break;
			case KeyEvent.VK_SHIFT:
				down = true;
			break;
			
			default:
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
				
			// release - konec pohybu
			case KeyEvent.VK_W:
				forward = false;
			break;
			case KeyEvent.VK_S:
				backward = false;
			break;
			case KeyEvent.VK_A:
				left = false;
			break;
			case KeyEvent.VK_D:
				right = false;
			break;
			case KeyEvent.VK_SPACE:
				up = false;
			break;
			case KeyEvent.VK_SHIFT:
				down = false;
			break;
			
			case KeyEvent.VK_1:
			case KeyEvent.VK_NUMPAD1:
				demo = Demo.DEMO1;
				demo1init();
			break;	
			
			case KeyEvent.VK_2:
			case KeyEvent.VK_NUMPAD2:
				demo = Demo.DEMO2;
				demo2init();
			break;	
			
			case KeyEvent.VK_3:
			case KeyEvent.VK_NUMPAD3:
				demo = Demo.DEMO3;
				demo3init();
			break;
			
			case KeyEvent.VK_4:
			case KeyEvent.VK_NUMPAD4:
				demo = Demo.DEMO4;
				demo4init();
			break;
			
			case KeyEvent.VK_5:
			case KeyEvent.VK_NUMPAD5:
				demo = Demo.DEMO5;
				demo5init();
			break;
			
			default:
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (mouseLPressed) {
			dx += ox - e.getX();
			dy += e.getY() - oy;
		}
		
		ox = e.getX();
		oy = e.getY();
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseLPressed = true;
		}
		
		dx = 0;
		dy = 0;
		
		ox = e.getX();
		oy = e.getY();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseLPressed = false;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(GLAutoDrawable gLDrawable) {
		
		// počítač skutečných FPS
		long mils = System.currentTimeMillis();
		//float spd = speed * (mils - oldmils) / 1000.0f;
		if ((mils - oldFPSmils) > 300) {
			realFPS = 1000 / (double) (mils - oldmils + 1);
			oldFPSmils = mils;
		}
		oldmils = mils;
		
		// přepočítání pohledu kamery
		camera.addAzimuth((float) (+dx * Math.PI / width));
		if (camera.getAzimuth() > 2*Math.PI) camera.addAzimuth(-Math.PI * 2);
		if (camera.getAzimuth() < 0) camera.addAzimuth(Math.PI * 2);
		
		camera.addZenith((float) (-dy * Math.PI / height));
		
		// vynulování dif. pohledu
		dy = 0;
		dx = 0;
		
		// pohyb
		if (forward) camera.forward(stepSize);
		if (backward) camera.backward(stepSize);
		if (up) camera.up(stepSize);
		if (down) camera.down(stepSize);
		if (left) camera.left(stepSize);
		if (right) camera.right(stepSize);

	
		
		// inicializace OpenGL
		final GL2 gl = gLDrawable.getGL().getGL2();

		// přemazání bufferů
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		// mlha
		if (enableFog) {
			// ♪ das Grau frisst deine Tage auf -- Eva Croissant
			gl.glEnable(GL2.GL_FOG);
		} else {
			gl.glDisable(GL2.GL_FOG);
		}
		
		// světla
		if (enableLights) {
			gl.glEnable(GL2.GL_LIGHTING);
		} else {
			gl.glDisable(GL2.GL_LIGHTING);
		}
			
		// nastavení kamery - perspektiva
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(50, width / (float) height, 0.3f, 400.0f);
		
		// MODELY
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// KAMERA JE OBJEKT!
		camera.setMatrix(glu);
		
		/*********************************/
		
		demo();
		
		/*********************************/
		
		pos += (float) TIMER / 100;
		
		if (pos >= 360) {
			pos -= 360;
		}
		
		/*********************************/
		
		// DEBUG obrazovka
		if (debugScreen) {
			// osy 
			gl.glBegin(GL2.GL_LINES);
				gl.glColor3f(1.0f, 0.0f, 0.0f);
				gl.glVertex3f(0.0f, 0.0f, 0.0f);
				gl.glVertex3f(100.0f, 0.0f, 0.0f);
				
				gl.glColor3f(0.0f, 1.0f, 0.0f);
				gl.glVertex3f(0.0f, 0.0f, 0.0f);
				gl.glVertex3f(0.0f, 100.0f, 0.0f);
				
				gl.glColor3f(0.0f, 0.0f, 1.0f);
				gl.glVertex3f(0.0f, 0.0f, 0.0f);
				gl.glVertex3f(0.0f, 0.0f, 100.0f);
			gl.glEnd();
		
			DrawStr2D(width - 73, height - 15, String.format("%4dx%4d", width, height));
			DrawStr2D(width - 65, height - 30, "FPS: " + String.format("%4.1f", realFPS));
			DrawStr2D(width - 95, height - 45, "Rotace: " + String.format("%4.2f", pos));
			
			DrawStr2D(3, 25+16, "Pohyb: WASD (ve směru pohledu), Mezerník: nahoru , Shift: dolu (up-vektor). Rozhlížení: tažení myší. Možnosti: ["+((enableFog) ? "F" : "f")+"] Mlha, ["+((enableLights) ? "L" : "l") + "] Osvětlení, ["+((visualizeOctrees) ? "V" : "v") + "] Vizualizace oktalových stromů.");
			DrawStr2D(3, 25, "Demo: "+((demo == Demo.DEMO1) ? "[1]" : "1")+": Jeden míček, "+((demo == Demo.DEMO2) ? "[2]" : "2")+": Základní kolizní box s velkými míčky, "+((demo == Demo.DEMO3) ? "[3]" : "3")+": 24 míčků, nízká rychlost, "+((demo == Demo.DEMO4) ? "[4]" : "4")+": 30 míčků, silný útlum "+((demo == Demo.DEMO5) ? "[5]" : "5")+": Velký box");
			
			DrawStr2D(3, height - 15, String.format("Kamera: [%4.1f, %4.1f, %4.1f]", camera.getPosition().x, camera.getPosition().y, camera.getPosition().z));
			DrawStr2D(3, height - 30, String.format("Azimut: %3.1f °, Zenit: %3.1f °", camera.getAzimuth()/Math.PI * 180, camera.getZenith()/Math.PI*180));
		} else {
			DrawStr2D(3, height - 15, String.format("F3 - Informace, ovládání"));
		}
		
	
		gl.glFlush();
	}
	
	public void DrawStr2D(int x, int y, String s) {
		// optionally set the color
		// renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
		renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));
		renderer.beginRendering(width, height);
		renderer.draw(s, x, y);
		renderer.endRendering();
	}

	/**
	 * Vypsání textu do plátna
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param s
	 */
	public void DrawStr(float x, float y, float z, String s) {
		// uložení aktuální projekce
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		// model
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glRasterPos2f(x, y);
		glut.glutBitmapString(GLUT.BITMAP_8_BY_13, s);
		// obnovení modelu
		gl.glPopMatrix();
		// obnovení projekce
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
	}
	
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable gLDrawable) {
		
		oldmils = System.currentTimeMillis();
		
		width = 1280;
		height = 720;
		
		// inicializace OpenGL
		gl = gLDrawable.getGL().getGL2();
		
		System.out.println("Library: " + gl.getClass().getName());
		System.out.println("Vendor: " + gl.glGetString(GL2.GL_VENDOR)); // vyrobce
		System.out.println("Model: " + gl.glGetString(GL2.GL_RENDERER)); // graficka karta
		System.out.println("OpenGL version: " + gl.glGetString(GL2.GL_VERSION)); // verze OpenGL
		
		gl.glClearColor(.7f, .7f, .7f, 1.0f); // mlha
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		//gl.glEnable(GL2.GL_LIGHTING);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
		
		// správce světel
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, this.lightColorAmbient, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, this.lightDiffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, this.lightPosition, 0);
		gl.glEnable(GL2.GL_LIGHT0);
		//gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHTING);
		
		
		// mlha
		gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
		gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0);
		gl.glFogf(GL2.GL_FOG_DENSITY, 0.00915f);
		gl.glHint(GL2.GL_FOG_HINT, GL2.GL_DONT_CARE);
		gl.glFogf(GL2.GL_FOG_START, 15.0f);
		gl.glFogf(GL2.GL_FOG_END, 55.0f);

		glu = new GLU();
		glut = new GLUT();
		
		// nastavení kamery - GLU
		//camera.setAzimuth(0);
		//camera.setZenith(0);
		camera.setAzimuth(Math.PI/180 * 270);
		camera.setZenith(Math.PI/180 * (-35));
		//camera.setPosition(new Vec3D(10, 10, 60));
		camera.setPosition(new Vec3D(90, 50, 0));
		
		camera.setMatrix(glu);
		
		demo1init();
	}

	@Override
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	/*********/
	
	public void demo() {
		box.setVisualizeOctrees(visualizeOctrees);
		
		box.advance(pos);
		box.display(gl);
	}
	
	///// DEMO inicializace /////
	
	public void demo1init() {
		
		box = new Box(50, GRAVITY);
		box.setSpeedLoss(1.0f);
		// injekce GLUT
		box.setGlut(glut);
		box.setPosition(new Point3D(0, 0, 0));
		
		for (int b = 1; b <= 1; b++) {
			Ball ball = new Ball(new Vec3D( box.getPosition().mul(-10.5 * Math.random()).add( new Point3D(3*Math.random(),3*Math.random(),3*Math.random()+7) ) ), new Vec3D(), (float) Math.random() + 2, 0xff00ff00);
			ball.setDirection( new Vec3D(0.5 - 4.4 * Math.random(), 0.5 - 6.5 * Math.random(), 0.5 - 12.5 * Math.random()) );
			box.addBall(ball);
		}
		
	}
	
	public void demo2init() {
		
		box = new Box(50, GRAVITY);
		box.setSpeedLoss(1.0f);
		// injekce GLUT
		box.setGlut(glut);
		box.setPosition(new Point3D(0, 0, 0));
		
		for (int b = 1; b <= 10; b++) {
			Ball ball = new Ball(new Vec3D( box.getPosition().mul(-10.5 * Math.random()).add( new Point3D(3*Math.random(),3*Math.random(),3*Math.random()+7) ) ), new Vec3D(), (float) Math.random() + 2, 0xff00ff00);
			ball.setDirection( new Vec3D(0.5 + 4.4 * Math.random(), 0.5 + 6.5 * Math.random(), 0.5 + 12.5 * Math.random()) );
			box.addBall(ball);
		}
		
	}
	
	public void demo3init() {

		box = new Box(50, GRAVITY.mul(0.005));
		box.setSpeedLoss(1.0f);
		// injekce GLUT
		box.setGlut(glut);
		box.setPosition(new Point3D(0, 0, 0));
		
		for (int b = 1; b <= 24; b++) {
			Ball ball = new Ball(new Vec3D( box.getPosition().mul(-10.5 * Math.random()).add( new Point3D(3*Math.random(),3*Math.random(),3*Math.random()+7) ) ), new Vec3D(), (float) Math.random() + 1, 0xff00ff00);
			ball.setDirection( new Vec3D(0.5 + 0.24 * Math.random(), 0.5 + 0.35 * Math.random(), 0.5 + 0.45 * Math.random()) );
			box.addBall(ball);
		}
		
	}
	
	public void demo4init() {
		box = new Box(50, GRAVITY);
		box.setSpeedLoss(.7981f);
		// injekce GLUT
		box.setGlut(glut);
		box.setPosition(new Point3D(0, 0, 0));
		
		for (int b = 1; b <= 30; b++) {
			Ball ball = new Ball(new Vec3D( box.getPosition().mul(-10.5 * Math.random()).add( new Point3D(3*Math.random(),3*Math.random(),3*Math.random()+7) ) ), new Vec3D(), (float) Math.random() + 1, 0xff00ff00);
			ball.setDirection( new Vec3D(0.5 + 2.4 * Math.random(), 0.5 + 3.5 * Math.random(), 0.5 + 4.5 * Math.random()) );
			box.addBall(ball);
		}
	}
	
	public void demo5init() {
		box = new Box(100, GRAVITY);
		//box.setSpeedLoss(.7981f);
		// injekce GLUT
		box.setGlut(glut);
		box.setPosition(new Point3D(0, 0, 0));
		
		for (int b = 1; b <= 40; b++) {
			Ball ball = new Ball(new Vec3D( box.getPosition().mul(-10.5 * Math.random()).add( new Point3D(3*Math.random(),3*Math.random(),3*Math.random()+7) ) ), new Vec3D(), (float) Math.random() + 3, 0xff00ff00);
			ball.setDirection( new Vec3D(0.5 + 24 * Math.random(), 0.5 + 35 * Math.random(), 0.5 + 45 * Math.random()) );
			box.addBall(ball);
		}
	}

}
