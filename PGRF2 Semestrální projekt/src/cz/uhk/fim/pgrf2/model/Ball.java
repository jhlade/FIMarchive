package cz.uhk.fim.pgrf2.model;

import cz.uhk.fim.pgrf2.structures.Sphere;
import cz.uhk.fim.transforms3D.Point3D;
import cz.uhk.fim.transforms3D.Vec3D;

/**
 * Míček.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Ball {
	
	/** grafická reprezentace míčku */
	private Sphere sphere;
	
	/** poloha */
	private Vec3D position;
	
	/** směr */
	private Vec3D direction;
	
	/** poloměr */
	private float radius;

	/** barva */
	private int color;
	
	public Ball() {
		
		initSphere();
		
	}
	
	/**
	 * @param position
	 * @param direction
	 * @param radius
	 * @param color
	 */
	public Ball(Vec3D position, Vec3D direction, float radius, int color) {
		super();
		this.position = position;
		this.direction = direction;
		this.radius = radius;
		this.color = color;
		
		initSphere();
	}

	/**
	 * 
	 * @return the radius
	 */
	public float getRadius() {
		return radius/2; // hack, glutSolidSphere to kreslí větší, než doopravdy je
	}

	/**
	 * @return the direction
	 */
	public Vec3D getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Vec3D direction) {
		this.direction = direction;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * inicializace reprezentace míčku
	 * 
	 */
	private void initSphere() {
		sphere = new Sphere(radius);
		sphere.setPosition(new Point3D(position));
		sphere.setColor(color);
	}

	/**
	 * Poloha
	 * 
	 * @return the position
	 */
	public Vec3D getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vec3D position) {
		this.position = position;
		// a současně i grafický objekt
		sphere.setPosition(new Point3D(position));
	}

	/**
	 * 
	 * @return Sphere
	 */
	public Sphere getSphere() {
		return this.sphere;
	}
	
	
}
