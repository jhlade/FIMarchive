package cz.uhk.fim.pgrf2.model;

import cz.uhk.fim.transforms3D.Vec3D;

/**
 * Stěna kolizního boxu.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Wall extends Vec3D {
	
	private float boxSize;
	private Vec3D boxCenter;

	public Wall(Vec3D boxPosition, float boxSize, Vec3D direction) {
		super( direction.add(boxPosition.mul(-1)) ); //boxPosition.add( direction.mul(boxSize) )
		
		this.boxCenter = boxPosition;
		this.boxSize = boxSize;
	}

	/**
	 * @return the boxSize
	 */
	public float getBoxSize() {
		return boxSize;
	}

	/**
	 * @return the boxCenter
	 */
	public Vec3D getBoxCenter() {
		return boxCenter;
	}



}
