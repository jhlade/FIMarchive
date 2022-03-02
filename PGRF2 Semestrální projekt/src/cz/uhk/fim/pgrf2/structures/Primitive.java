package cz.uhk.fim.pgrf2.structures;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import cz.uhk.fim.transforms3D.Mat4Transl;
import cz.uhk.fim.transforms3D.Point3D;
import cz.uhk.fim.transforms3D.Vec3D;


/**
 * Trojrozměrné primitivum v OpenGL.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public abstract class Primitive extends Renderable implements ISolid {
	
	protected float[] ambient  = new float[]{.0f, .0f, .0f};
	protected float[] diffuse  = new float[]{.0f, .0f, .0f};
	protected float[] specular = new float[]{.0f, .0f, .0f};
	protected float shiness = .0f;

	private boolean wireframe = false;

	protected int glMode;
	
	protected List<Point3D> vertices = new ArrayList<Point3D>();
	protected List<Triangle> triangles = new ArrayList<Triangle>();
	
	public Primitive() {
		// TODO Auto-generated constructor stub
	}
	
	public void glutDraw() {
		// ...
	}
	
	public int getColor() {
		return 0;
	}
	
	
	/**
	 * Finální vykreslení do GL2 scény předené parametrem gl.
	 * 
	 * @param gl
	 */
	@Override
	public void display(GL2 gl) {
		
		if (wireframe) {
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		} else {
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		}
		
		// kreslení GL
		//gl.glBegin(this.glMode);
		
			gl.glPushMatrix();
			
			Mat4Transl originMat = new Mat4Transl(position.x, position.y, position.z);

			// GLUT
			if (this instanceof IGLUTSolid) {
				
				// umístění
				Point3D orig = position.mul(matrix).mul(originMat);
				
				// inicializace polohy 
				gl.glTranslatef((float) orig.x, (float) orig.z, (float) orig.y);
				
				gl.glBegin(GL.GL_LINES);
				
				// barva
				Color color = new Color(this.getColor());
				gl.glColor3f((float) color.getRed()/255, (float) color.getGreen()/255, (float) color.getBlue()/255);
				
				// materiál
				this.promoteMaterial();
				gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
				gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
				gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
				gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, new float[]{shiness * 128.0f}, 0);

				
				// vykreslit
				glutDraw();
				
			} else {
				// transforms3d
				gl.glBegin(this.glMode);
				
				if (this.glMode == GL2.GL_TRIANGLES) {
				
					Iterator<Triangle> triangleIterator = triangles.iterator();
					
					while (triangleIterator.hasNext()) {
		
						Triangle tr = triangleIterator.next();
						
						// transformace
						Point3D v1 = (vertices.get(tr.getV1())).mul(matrix).mul(originMat);
						Point3D v2 = (vertices.get(tr.getV2())).mul(matrix).mul(originMat);
						Point3D v3 = (vertices.get(tr.getV3())).mul(matrix).mul(originMat);
						
						// barva
						gl.glColor3f(tr.getColor().getRed()/255.0f, tr.getColor().getGreen()/255.0f, tr.getColor().getBlue()/255.0f);
						
						// materiál
						this.promoteMaterial();
						gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
						gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
						gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
						gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, new float[]{shiness * 128.0f}, 0);
						
						// normála
						Vec3D normalVector = calculateNormal(v1, v2, v3);
						gl.glNormal3d(normalVector.x, normalVector.z, normalVector.y);
						
						// textura
						// TODO textura
						
						// vykreslení trojúhelníka
						gl.glVertex3d(v1.x, v1.z, v1.y);
						gl.glVertex3d(v2.x, v2.z, v2.y);
						gl.glVertex3d(v3.x, v3.z, v3.y);
					}
				} // triangles
				
				if (this.glMode == GL2.GL_QUADS) {

					// TODO quads

				} // quads
				
			} // transforms3D
			
			
		// konec + obnova matice
		gl.glEnd();
		gl.glPopMatrix();
		
		// obnova wireframe
		if (wireframe) {
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		}
		
		// obnova materiálu
		this.resetMaterial();
		
	}
	
	/**
	 * Kalkulace normály
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return
	 */
	public Vec3D calculateNormal(Point3D v1, Point3D v2, Point3D v3) {
		
		Vec3D vec1 = new Vec3D(v1).add(new Vec3D(v2)).mul(-1);
		Vec3D vec2 = new Vec3D(v3).add(new Vec3D(v2)).mul(-1);
		
		return vec1.cross(vec2).normalized();
	}
	
	/**
	 * Aktuální poloha v závislosti na transformační matici.
	 * 
	 * @return Point3D poloha
	 */
	public Point3D getCurrentPosition() {
		return getPosition();
	}
	
	/**
	 * Kreslit jako drátěný model.
	 * 
	 * @param wireframe
	 */
	public void setWireframe(boolean wireframe) {
		this.wireframe = wireframe;
	}
	
	/**
	 * Resetuje nastavení materiálu.
	 */
	private void resetMaterial() {
		this.ambient  = new float[]{.0f, .0f, .0f};
		this.diffuse  = new float[]{.0f, .0f, .0f};
		this.specular = new float[]{.0f, .0f, .0f};
		this.shiness = .0f;
	}

}
