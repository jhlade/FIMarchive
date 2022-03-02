package cz.uhk.fim.hladeja1.pgrf3.gl1.runtime;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import cz.uhk.fim.pgrf.transforms.Camera;
import cz.uhk.fim.pgrf.transforms.Mat4;
import cz.uhk.fim.pgrf.transforms.Mat4PerspRH;
import cz.uhk.fim.pgrf.transforms.Mat4RotXYZ;
import cz.uhk.fim.pgrf.transforms.Vec3D;

import cz.uhk.fim.hladeja1.pgrf3.gl1.model.GLCamera;
import cz.uhk.fim.hladeja1.pgrf3.gl1.model.Grid;
import cz.uhk.fim.hladeja1.pgrf3.gl1.model.LightSource;
import cz.uhk.fim.hladeja1.pgrf3.gl1.model.Renderable;
import cz.uhk.fim.hladeja1.pgrf3.gl1.model.Triangle;
import cz.uhk.fim.hladeja1.pgrf3.utils.SimpleGL3Texture;
import cz.uhk.fim.pgrf.transforms.Col;
import cz.uhk.fim.pgrf.utils.ToFloatArray;

// přímé zpracování checkboxů
import java.awt.event.ItemEvent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Renderer.
 *
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 */
public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

	// GL3 Kontext
	private GL3 gl;
	
	// počáteční rozměry GL plátna
	int width = 1280;
	int height = 720;

	// konstanty projekce
	final float ALPHA_FOV = 53.16f/180f * (float) Math.PI; // alpha FOV úhel, nějaká empirická magie
	final float Z_NEAR = .25f;  // blízké Z
	final float Z_FAR = 250.0f;  // vzdálené Z

	// měření skutečných FPS
	long oldmils, oldFPSmils;
	double realFPS;

	// POHYB ve scéně
	float speed = 10;
	double stepSize = 0.5;
	
	
	// povolení dynamiky
	boolean dynamics = true;
	
	// dynamický čítač
	double dynCounter = 0;
	
	// zobrazování normál
	boolean showNormals = false;
	// zobrazování textury
	boolean showTexture = false;
	// mapping
	boolean texBump = false;
	boolean texParallax = false;
	
	// per fragment = 1 / per vertex = 0
	boolean perFragment = false;
	
	// reflektorový zdroj světla
	boolean reflector = false;
	
	// velikost gridu
	// mohlo by to být jako vlastnost objektu, ale
	int gridSize = 32;//8;
	
	// inkrementace v kroku
	final double DYNDIF = 0.025;

	// drátěný model
	private boolean wireframe = true;
	
	// povolení rotace pomocí matice
	private boolean rotate = true;
	
	// myš ?
	int dx = 0;
	int dy = 0;
	boolean mouseLPressed = false;
	int ox, oy;

	// ... klávesnice
	boolean forward = false;
	boolean backward = false;
	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;

	// nastavení globálních matic (uniform variables)
	Mat4 matrixProjection = new Mat4PerspRH(ALPHA_FOV, 1.0f * (width / height), Z_NEAR, Z_FAR);
	
	// GL Kamera, pohledová matice je schovaná tady
	GLCamera camera = new GLCamera(new Camera());

	// Objekty scény.
	List<Renderable> objects = new ArrayList<>();
	
	// Požadovaný DEMO program
	int demoId = 1;

	////////////////////////////////////////////////////////////////////////////
	
	// testovací trojúhelník
	//private Triangle triangle = new Triangle("tri");
	
	// základní grid
	//private Grid grid1 = new Grid("grid");
	//private Grid grid2 = new Grid("sphere");
	
	// pokročilý grid
	private Grid pgrf3grid = new Grid("pgrf3a1", true);

	// zdroj světla
	private LightSource light = new LightSource();
	
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param glDrawable 
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {

		GL3 gl = glDrawable.getGL().getGL3();
		
		//this.gl = gl;

		// Výpis informací o systému
		System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
		System.out.println("Init GL is " + gl.getClass().getName());
		System.out.println("OpenGL version " + gl.glGetString(GL.GL_VERSION));
		System.out.println("OpenGL shading language version " + gl.glGetString(GL3.GL_SHADING_LANGUAGE_VERSION));
		System.out.println("OpenGL vendor " + gl.glGetString(GL.GL_VENDOR));
		System.out.println("OpenGL renderer " + gl.glGetString(GL.GL_RENDERER));
		System.out.println("OpenGL extension " + gl.glGetString(GL.GL_EXTENSIONS));

		// Základní nastavení - Z+bílá
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		// přidání objektů do scény
		//objects.add(triangle);
		//objects.add(grid1);
		//objects.add(grid2);
		objects.add(pgrf3grid);

		// Iterátor přes všechny objekty scény
		Iterator<Renderable> objectIteratorInit = objects.iterator();
		while (objectIteratorInit.hasNext()) {
			
			Renderable obj = objectIteratorInit.next();
			
			// injekce GL a inicializace bufferů a programů objektu
			obj.init(gl);
			
			// inicializace textury
			obj.setTexture(Renderable.TextureType.TexColor, new SimpleGL3Texture(gl, "bricks.jpg"), "texture");
			obj.setTexture(Renderable.TextureType.TexNormal, new SimpleGL3Texture(gl, "bricksn.png"), "texture_normal");
			obj.setTexture(Renderable.TextureType.TexHeight, new SimpleGL3Texture(gl, "bricksh.png"), "texture_height");
			
			// nastavení objektového programu
			//obj.setProgramID(ShaderUtils.newProgram(gl, obj));
			// nastavení patřičných bufferů
			//obj.setVAOid(BufferUtils.generateVAOid(gl));
			//BufferUtils.newFloatVNCBuffers(gl, obj);
		}
		
		// globální inicializace kamery
		resetCamera();
		
		// inicializace světla
		light.setLightColor(new Col(1.0, 1.0, 1.0)); // bílé světlo
		light.setSpotCutOff((float) Math.PI * 6/180); // 6°?
		light.setSpot(new Vec3D(0, 0, 0)); // kouká na střed?
		light.setPosition(new Vec3D(-21, 42, 42)); // vlevo od kamery?
		
	}

	/**
	 * Hlavní smyčka - vykreslování scény.
	 *
	 * @param glDrawable
	 */
	@Override
	public void display(GLAutoDrawable glDrawable) {

		// čítač FPS ///////////////////////////////////////////////////////////
		long mils = System.currentTimeMillis();
		if ((mils - oldFPSmils) > 300) {
			realFPS = 1000 / (double) (mils - oldmils + 1);
			oldFPSmils = mils;
		}
		oldmils = mils;

		// přepočítání pohledu kamery v závislosti na ovládání /////////////////
		camera.addAzimuth((float) (-dx * Math.PI / width));
		if (camera.getAzimuth() > 2 * Math.PI) {
			camera.addAzimuth(-Math.PI * 2);
		}
		if (camera.getAzimuth() < 0) {
			camera.addAzimuth(Math.PI * 2);
		}
		camera.addZenith((float) (-dy * Math.PI / height));

		// vynulování dif. pohledu ovládání kamery
		dy = 0;
		dx = 0;

		// pohyb kamery ////////////////////////////////////////////////////////
		if (forward) {
			camera.forward(stepSize);
		}
		if (backward) {
			camera.backward(stepSize);
		}
		if (up) {
			camera.up(stepSize);
		}
		if (down) {
			camera.down(stepSize);
		}
		if (left) {
			camera.left(stepSize);
		}
		if (right) {
			camera.right(stepSize);
		}
		////////////////////////////////////////////////////////////////////////

		GL3 gl = glDrawable.getGL().getGL3();

		// smazání obrazu
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

		if (wireframe) {
			gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
		} else {
			gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
		}
		
		// nějaký ten dynamický čítač
		dynCounter += DYNDIF;
		if (dynCounter >= 2 * Math.PI) {
			dynCounter = 0;
		}
		
		// dynamika
		if (this.dynamics != true) {
			dynCounter = 1.0;
		}

		// vykreslení scény
		renderScene(gl);

		// TODO odchycení nějaké chyby?
		/*
		int error = gl.glGetError();
		if (error != 0) {
			System.err.println("Chyba při kreslení: " + error);
		}
		 */
	}

	/**
	 * Hlavní metoda vykreslení objektů scény
	 *
	 * @param gl
	 */
	public void renderScene(GL3 gl) {

		// iterátor přes všechny objekty scény
		Iterator<Renderable> objectIterator = objects.iterator();
		
		while (objectIterator.hasNext()) {
			
			// získání objektu
			Renderable obj = objectIterator.next();
			
			// POKUS - rotace objektu jeho modelovou maticí
			if (this.rotate) {
				obj.setModelMatrix(obj.getModelMatrix().mul(new Mat4RotXYZ(0, 0, Math.PI/360)));
			}
			
			// injekce projekční a pohledové matice
			obj.setMatrices(this.matrixProjection, camera.getViewMatrix());
			
			// ZOBRAZENÍ se vším všudy
			obj.display();
			
			// velikost mřížky
			gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "gridSize"), this.gridSize);
			// DEMO PROGRAM (vertex shader)
			gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "demoId"), this.demoId);
			// DYNAMICKÝ ČÍTAČ - pokud v programu objektu existuje, nastaví se
			gl.glUniform1f(gl.glGetUniformLocation(obj.getProgramID(), "dyncounter"), (float) this.dynCounter);
			// zobrazovat normály? (geometry shader)
			gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "showNormals"), (this.showNormals ? 1 : 0));
			// textura?
			gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "showTexture"), (this.showTexture ? 1 : 0));
			// mapping
			gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "bump"), (this.texBump ? 1 : 0));
			gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "parallax"), (this.texParallax ? 1 : 0));
			
			// per-fragment / per-vertex režim
			if (this.perFragment) {
				gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "perfragment"), 1);
			} else {
				gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "perfragment"), 0);
			}
			
			// uvažovat reflektorový zdroj světla?
			if (this.reflector) {
				gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "reflector"), 1);
			} else {
				gl.glUniform1i(gl.glGetUniformLocation(obj.getProgramID(), "reflector"), 0);
			}
			
			// nastavení světla
			gl.glUniform3fv(gl.glGetUniformLocation(obj.getProgramID(), "CameraPosition"), 1, ToFloatArray.convert(camera.getPosition()), 0);
			gl.glUniform1f(gl.glGetUniformLocation(obj.getProgramID(), "spotCutOff"), light.getSpotCutOff());
			gl.glUniform3fv(gl.glGetUniformLocation(obj.getProgramID(), "LightPosition"), 1, ToFloatArray.convert(light.getPosition()), 0);
			gl.glUniform3fv(gl.glGetUniformLocation(obj.getProgramID(), "spotDirection"), 1, ToFloatArray.convert(light.getSpotDirection()), 0);
			gl.glUniform4fv(gl.glGetUniformLocation(obj.getProgramID(), "LightColor"), 1, ToFloatArray.convert(light.getLightColor()), 0);

			// útlum prostředí
			gl.glUniform1f(gl.glGetUniformLocation(obj.getProgramID(), "constantAttenuation"), (float) 1.0);
			gl.glUniform1f(gl.glGetUniformLocation(obj.getProgramID(), "linearAttenuation"), (float) 0.00025);
			gl.glUniform1f(gl.glGetUniformLocation(obj.getProgramID(), "quadraticAttenuation"), (float) 0.0001);
			
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Ukončení.
	 *
	 * @param glDrawable
	 */
	@Override
	public void dispose(GLAutoDrawable glDrawable) {
		// Hotovo...
	}

	@Override
	public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {

		this.width = width;
		// ochrana proti dělení nulou
		this.height = (height == 0) ? 1 : height;

		// úprava projekční matice
		this.matrixProjection = new Mat4PerspRH(ALPHA_FOV, 1.0f * (this.width / this.height), Z_NEAR, Z_FAR);
	}

    // VLASTNÍ VOLÁNÍ //////////////////////////////////////////////////////////

	/**
	 * Nastavení kamery do výchozího stavu
	 */
	public void resetCamera() {
		//camera.setPosition(new Vec3D(0.00f, 75.0f, 75.0f)); // velký grid
		camera.setPosition(new Vec3D(0.00f, 42.0f, 42.0f)); // menší grid
		camera.setLookAt(.0f, .0f, .0f);
	}
	
	/**
	 * Výpis nějakých informací
	 */
	public void getInfo() {
		System.out.println("CAM POS: [" + this.camera.getPosition().x + ", "+ this.camera.getPosition().y + ", " + this.camera.getPosition().y +"]");
		System.out.println("CAM AZ:  ["+ this.camera.getAzimuth() + ", " + this.camera.getZenith() +"]");
		System.out.println("FPS: " + realFPS);
	}
	
	/**
	 * Zobrazení normál
	 * 
	 * @param state 
	 */
	public void toggleNormals(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.showNormals = true;
			this.wireframe = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.showNormals = false;
		}
	}
	
	public void toggleTexture(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.showNormals = false;
			this.wireframe = false;
			this.showTexture = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.showTexture = false;
		}
	}
	
	public void toggleBump(int state) {
		if (state == ItemEvent.SELECTED) {
			this.texBump = true;
			this.texParallax = false;
		}
		if (state == ItemEvent.DESELECTED) {
			this.texBump = false;
		}
	}
	
	public void toggleParallax(int state) {
		if (state == ItemEvent.SELECTED) {
			this.texBump = false;
			this.texParallax = true;
		}
		if (state == ItemEvent.DESELECTED) {
			this.texParallax = false;
		}
	}
	
	/**
	 * Wireframe
	 * 
	 * @param state 
	 */
	public void toggleWireframe(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.wireframe = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.wireframe = false;
		}
	}
	
	/**
	 * Přepnutí rotace
	 * 
	 * @param state 
	 */
	public void toggleRotate(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.rotate = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.rotate = false;
		}
	}
	
	/**
	 * Přepnutí dynamiky
	 * 
	 * @param state 
	 */
	public void toggleDynamic(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.dynamics = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.dynamics = false;
		}
	}
	
	/**
	 * Přepnutí osvětlování Per Fragment / Per Vertex
	 * 
	 * @param state 
	 */
	public void togglePerFragment(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.perFragment = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.perFragment = false;
		}
	}
	
	/**
	 * Přepnutí osvětlování reflektorovým a bodovým zdrojem
	 * 
	 * @param state 
	 */
	public void toggleReflector(int state) {
		
		if (state == ItemEvent.SELECTED) {
			this.reflector = true;
		}
		
		if (state == ItemEvent.DESELECTED) {
			this.reflector = false;
		}
	}
	
	/**
	 * Výběr programu
	 * @param pgid 
	 */
	public void setDemoProgram(int pgid) {
		this.demoId = pgid;
	}
	
	
	// OVLÁDÁNÍ - OMÁČKA PRO LISTENERY /////////////////////////////////////////
	
	@Override
	public void mouseClicked(MouseEvent e) {

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

	}

	@Override
	public void mouseExited(MouseEvent e) {

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

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
			
			// wireframe
			/*
			case KeyEvent.VK_Q:
				wireframe = !wireframe;
			break;
			*/
			
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

			default:
				break;
		}

	}

}
