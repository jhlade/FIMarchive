package cz.uhk.fim.pgrf2.structures;


import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Koule kreslená přes GLUT.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Sphere extends Primitive implements ISolid, IGLUTSolid {
	
	// experimentální nastavení
	private int complexity = 16;
	
	private GLUT glut;
	private float radius;
	private int color = 0xffff0000;

	public Sphere(float radius) {
		
		this.radius = radius;
		
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public void glutDraw() {
		//glut.glutWireSphere(radius, 16, 16);
		// TODO úplně to nefunguje
		glut.glutSolidSphere(radius, complexity, complexity);
	}

	/**
	 * @param complexity the complexity to set
	 */
	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}
	
	public void decreaseComplexity() {
		if (this.complexity >= 6) {
			this.complexity--;
		}
	}
	
	public void increaseComplexity() {
		if (this.complexity <= 64) {
			this.complexity++;
		}
	}

	@Override
	public void setGLUT(GLUT glut) {
		
		this.glut = glut;
		
	}

	@Override
	public int getColor() {
		return this.color;
	}

	@Override
	public void promoteMaterial() {
		this.ambient = new float[]{.0215f, .1745f, .0215f};
		this.diffuse = new float[]{.07568f, .61424f, .07568f};
		this.specular = new float[]{.633f, .727811f, .633f};
		this.shiness = .6f;
	}

}
