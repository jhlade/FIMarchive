package cz.uhk.fim.pgrf2.model;

import cz.uhk.fim.transforms3D.Vec3D;


/**
 * Pomocná třída pro deteckci kolizí mezi stěnou a míčkem
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class BallWall {
	
	/** míček */
	private Ball ball;

	/** stěna */
	private Wall wall;
	
	
	/**
	 * Konstruktor.
	 * 
	 * @param ball
	 * @param wall
	 */
	public BallWall(Ball ball, Wall wall) {
		this.ball = ball;
		this.wall = wall;
	}
	
	/**
	 * @return the ball
	 */
	public Ball getBall() {
		return ball;
	}

	/**
	 * @return the wall
	 */
	public Wall getWall() {
		return wall;
	}

	/**
	 * Otestování potenciální kolizní situace
	 * 
	 * @return
	 */
	public boolean testCollision() {

		Vec3D test = ball.getPosition().add( wall.getBoxCenter().mul(-1) );
		double tDot = Math.abs(test.dot( wall.normalized() ) + ball.getRadius());
		
		if (
				( ( tDot  > wall.getBoxSize()/4 ) )
			&&
				( ( getBall().getDirection().dot( getWall() ) ) > 0)
			) {
			
			return true;
		} else {
			return false;
		}
	}

}
