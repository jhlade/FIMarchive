package cz.uhk.fim.pgrf2.structures;

import javax.media.opengl.GL2;

import cz.uhk.fim.transforms3D.Mat4;
import cz.uhk.fim.transforms3D.Mat4Identity;
import cz.uhk.fim.transforms3D.Point3D;

/**
 * Vykreslitelník.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public abstract class Renderable {

	/** poloha */
	protected Point3D position = new Point3D(0, 0, 0);
	
	/** transforamční matice */
	protected Mat4 matrix = new Mat4Identity();
	
	/**
	 * Změna pozice středu tělesa
	 * 
	 * @param position
	 */
	public void setPosition(Point3D position) {
		this.position = position;
	}
	
	public Point3D getPosition() {
		return this.position;
	}
	
	/**
	 * Getter aktuální transformační matice
	 * 
	 * @return
	 */
	public Mat4 getMatrix() {
		return this.matrix;
	}
	
	/**
	 * Nastavení transformační matice
	 * 
	 * @param matrix
	 */
	public void setMatrix(Mat4 matrix) {
		this.matrix = new Mat4Identity().mul(matrix);
	}
	
	/**
	 * Násobaení zleva
	 * 
	 * @param matrix
	 */
	public void preMulMatrix(Mat4 matrix) {
		this.matrix = matrix.mul(this.matrix);
	}
	
	/**
	 * Nasobení zprava
	 * 
	 * @param matrix
	 */
	public void mulMatrix(Mat4 matrix) {
		this.matrix.mul(matrix);
	}
	
	/**
	 * Provedení vykreslení do GL prostoru
	 * 
	 * @param gl
	 */
	abstract public void display(GL2 gl);

}
