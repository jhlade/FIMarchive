/**
 * 
 */
package cz.uhk.pgrf2.projekt1.model;

import cz.uhk.pgrf2.projekt1.transforms3D.Mat4;
import cz.uhk.pgrf2.projekt1.transforms3D.Mat4Identity;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Wrapper pro vykreslitelný objekt 
 *
 */
public class SceneObject {

	/** jméno objektu */
	private String name;
	
	/** počáteční x */
	private int x;
	/** počáteční y */
	private int y;
	/** počáteční z */
	private int z;
	
	/** matice transformace */
	private Mat4 matrix = new Mat4Identity();

	/** samotné těleso */
	private ISolid solid = null;
	/** ...nebo světlo */ 
	private Light light = null;
	
	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 * @param solid
	 */
	public SceneObject(String name, int x, int y, int z, ISolid solid) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.light = null;
		this.solid = solid;
	}
	
	/**
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 * @param light
	 */
	public SceneObject(String name, int x, int y, int z, Light light) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.light = light;
		this.solid = null;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return z;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * @return the matrix
	 */
	public Mat4 getMatrix() {
		return matrix;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(Mat4 matrix) {
		this.matrix = matrix;
	}

	/**
	 * @return the solid
	 */
	public ISolid getSolid() {
		return solid;
	}
	

	/**
	 * @param solid the solid to set
	 */
	public void setSolid(ISolid solid) {
		this.solid = solid;
	}
	
	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}

	public boolean isSurface() {
		return (solid instanceof ISurface);
	}

	/**
	 *  vykreslitelný povrch
	 * @return
	 */
	public ISurface getSurface() {
		if (solid instanceof ISurface) {
			return (ISurface) solid;
		}
		
		return null;
	}

}
