package cz.uhk.fim.pgrf2.model;

import cz.uhk.fim.transforms3D.Vec3D;

/**
 * Kolizní pár dvou míčků.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class BallPair {
	
	private Ball ball1;
	private Ball ball2;

	public BallPair(Ball b1, Ball b2) {
		
		this.ball1 = b1;
		this.ball2 = b2;
		
	}

	/**
	 * @return the ball1
	 */
	public Ball getBall1() {
		return ball1;
	}

	/**
	 * @param ball1 the ball1 to set
	 */
	public void setBall1(Ball ball1) {
		this.ball1 = ball1;
	}

	/**
	 * @return the ball2
	 */
	public Ball getBall2() {
		return ball2;
	}

	/**
	 * @param ball2 the ball2 to set
	 */
	public void setBall2(Ball ball2) {
		this.ball2 = ball2;
	}
	
	
	/**
	 * Testuje daný pár na kolizi
	 * 
	 * @return
	 */
	public boolean testCollision() {
		
		// absolutní blízkost - dotek
		float proximityRange = getBall1().getRadius() + getBall2().getRadius();
		
		// vzájemný posun
		Vec3D displacement = getBall1().getPosition().add( getBall2().getPosition().mul(-1) );
		
		if (displacement.length() == 0.0) {
			return false;
		}

		// vzdálenost mezi středy < součet poloměrů
		if ( displacement.length() < proximityRange  ) {

			// rozdíl rychlostí
			Vec3D netDirection = getBall1().getDirection().add( getBall2().getDirection().mul(-1) );
			
			// skalární součin je menší jak 0
			return netDirection.dot(displacement) <= 0.0;
			
		} else {
			return false;
		}
		
	}
	

}
