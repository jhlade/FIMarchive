package cz.uhk.fim.pgrf2.model;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import cz.uhk.fim.pgrf2.structures.Cube;
import cz.uhk.fim.pgrf2.structures.Renderable;
import cz.uhk.fim.pgrf2.structures.SemiCube;
import cz.uhk.fim.transforms3D.Mat4RotXYZ;
import cz.uhk.fim.transforms3D.Point3D;
import cz.uhk.fim.transforms3D.Vec3D;

/**
 * Třída popisující jeden kompletní kolizní systém s grafickou reprezentací.
 * 
 * Box = Šárčina Kouzelná krabička. 
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Box extends Renderable {
	
	/** kořenový oktalový strom tohoto boxu */
	private Octree octree;
	
	/** velikost boxu */
	private float size;
	
	/** grafická reprezentace boxu */
	private SemiCube cube;
	private SemiCube outline;
	
	/** vizualizace stromů */
	private boolean visualizeOctrees = false;
	
	/** gravitační vektor */
	private Vec3D gravity = new Vec3D(0, 0, 9.81f * 0.01);
	/** útlum */
	private float speedLoss = 1.0f;
	
	/** GLUT */
	private GLUT glut;
	
	private double oldTime = 0.0;

	
	/** míčky */
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	
	public Box(float size, Vec3D gravity) {
		
		//System.out.println("Konstruktor boxu["+size+"]: " + this);
		
		this.size = size;
		
		// stěny
		cube = new SemiCube(size);
		//cube.preMulMatrix(this.getMatrix());
		
		// neviditelné stěny
		outline = new SemiCube(size);
		//outline.preMulMatrix(this.getMatrix());
		outline.setWireframe(true);
		outline.preMulMatrix(new Mat4RotXYZ(Math.PI, Math.PI/2, 0));
		
		// základní oktalový strom, hloubka 1
		this.octree = new Octree(
				new Vec3D(getPosition().x -size/2, getPosition().y - size/2, getPosition().z - size/2),
				new Vec3D(getPosition().x + size/2, getPosition().y + size/2, getPosition().z + size/2),
				1);
		this.octree.setMatrix(getMatrix());
		
		Iterator<Ball> ballIterator = balls.iterator();
		
		while (ballIterator.hasNext()) {
			this.octree.addBall(ballIterator.next());
		}
		
		this.gravity = gravity;
	}

	/**
	 * @return the size
	 */
	public float getSize() {
		return size;
	}

	/**
	 * @return the balls
	 */
	public ArrayList<Ball> getBalls() {
		return balls;
	}

	/**
	 * @return the gravity
	 */
	public Vec3D getGravity() {
		return gravity;
	}



	/**
	 * @param gravity the gravity to set
	 */
	public void setGravity(Vec3D gravity) {
		this.gravity = gravity;
	}

	/**
	 * @return the speedLoss
	 */
	public float getSpeedLoss() {
		return speedLoss;
	}

	/**
	 * @param speedLoss the speedLoss to set
	 */
	public void setSpeedLoss(float speedLoss) {
		this.speedLoss = speedLoss;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Point3D position) {
		this.position = position;

		// oprava rohů octree
		this.octree.setvCorner1( new Vec3D(getPosition().x -size/2, getPosition().y - size/2, getPosition().z - size/2) );
		this.octree.setvCorner2( new Vec3D(getPosition().x + size/2, getPosition().y + size/2, getPosition().z + size/2) );
		
		this.cube.setPosition(position);
		this.outline.setPosition(position);
	}
	
	/**
	 * @param glut the glut to set
	 */
	public void setGlut(GLUT glut) {
		this.glut = glut;
	}
	
	/**
	 * Přidání míčku
	 * 
	 * @param ball
	 */
	public void addBall(Ball ball) {
		balls.add(ball);
		this.octree.addBall(ball);
	}
	
	/**
	 * Smazání všech míčků
	 * 
	 */
	public void clearBalls() {
		balls.clear();
	}
	
	private void applyGravity() {
		
		Iterator<Ball> ballIterator = balls.iterator();
		
		while(ballIterator.hasNext()) {
			Ball gravityBall = ballIterator.next();
			
			gravityBall.setDirection( gravityBall.getDirection().add( getGravity().mul(0.025) ) );
		}
		
	}
	
	
	/**
	 * Pohyb míčků.
	 * 
	 */
	public void moveBalls() {

		Iterator<Ball> ballIterator = balls.iterator();
		
		while(ballIterator.hasNext()) {
			
			float mul = 1/50f;
			
			Ball movedBall = ballIterator.next();
			Vec3D oldPosition = movedBall.getPosition();
			
			movedBall.setPosition( oldPosition.add( movedBall.getDirection().mul(mul) ) );
			
			// notifikace do stromu
			octree.ballMoved(movedBall, oldPosition);
		}
		
	}
	
	/**
	 * Posun boxu do následujícího stavu.
	 * 
	 */
	public void advance(double time) {
		
		cube.setMatrix(cube.getMatrix().mul(getMatrix()));
		outline.setMatrix(outline.getMatrix().mul(getMatrix()));
		//octree.setMatrix(octree.getMatrix().mul(getMatrix()));
		
		/*
		Iterator<Ball> ballIterator = balls.iterator();
		
		while (ballIterator.hasNext()) {
			Ball advBall = ballIterator.next();
			advBall.getSphere().setMatrix( advBall.getSphere().getMatrix().mul(getMatrix()) );
		}*/
		
		// 1) posun míčků přirozenou rychlostí
		moveBalls();
		// 2) aplikace gravitace
		applyGravity();
		
		// limiter
		if (time - this.oldTime < 25) {
			// 3a) obsluha kolizí mezi míčky
			ballPairCollisionHandler();
			// 3b) obsluha kolizí se zdmi
			ballWallCollisionHandler();
		}
		
		this.oldTime = time;
	}
	
	/**
	 * Ošetření kolizí mezí míčky.
	 * 
	 */
	private void ballPairCollisionHandler() {
		// inicializace
		ArrayList<BallPair> bp = new ArrayList<BallPair>();
		
		// naplnění ze stromu
		this.octree.detectBallCollisions(bp);
		
		Iterator<BallPair> bpIterator = bp.iterator();
		
		// iterační ošetření
		while (bpIterator.hasNext()) {
			BallPair pair = bpIterator.next();
			
			// kolidují?
			if (pair.testCollision()) {
				
				Ball b1 = pair.getBall1();
				Ball b2 = pair.getBall2();
				
				// odrazí se od sebe - normalizovaný vektor styčné plochy
				// N = |p1-p2|
				Vec3D reflection = b1.getPosition().add( b2.getPosition().mul(-1) ).normalized();
				
				// novýSměr = útlum * ( -2*(V . N)*N + V )
				b1.setDirection(new Vec3D( new Vec3D(reflection).mul(-2).mul( b1.getDirection().dot(reflection) ).add(b1.getDirection()) ).mul( getSpeedLoss() ));
				b2.setDirection(new Vec3D( new Vec3D(reflection).mul(-2).mul( b2.getDirection().dot(reflection) ).add(b2.getDirection()) ).mul( getSpeedLoss() ));
			}
			
		}
		
	}
	
	/**
	 * Ošetření kolizí se stěnami boxu.
	 * 
	 */
	private void ballWallCollisionHandler() {
		
		// inicializace
		ArrayList<BallWall> bw = new ArrayList<BallWall>();
		
		// naplnění ze stromu
		this.octree.detectWallCollisions(bw, this);
		
		Iterator<BallWall> bwIterator = bw.iterator();
		
		// kolizní smyčka
		while (bwIterator.hasNext()) {
			
			BallWall pair = bwIterator.next();
			
			if (pair.testCollision()) {
				
				Ball ball = pair.getBall();
				Wall wall = pair.getWall();
				
				// odraz míčku
				// N = plocha
				Vec3D normDirection = wall.normalized();

				// ( ( N * (-2) * (V . N) ) + V ) * útlum
				ball.setDirection(new Vec3D( new Vec3D(normDirection).mul(-2).mul( ball.getDirection().dot(normDirection) ).add(ball.getDirection()) ).mul( getSpeedLoss() ));
				
			}
			
		}
		
	}

	/**
	 * @param visualizeOctrees the visualizeOctrees to set
	 */
	public void setVisualizeOctrees(boolean visualizeOctrees) {
		this.visualizeOctrees = visualizeOctrees;
	}
	
	private void drawOctree(GL2 gl, Octree octree) {
		
		if (octree.hasSubtrees()) {
			
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					for (int z = 0; z < 2; z++) {
						this.drawOctree(gl, octree.getSubtrees()[x][y][z]);
					} //z
				} // y
			} // x
			
		} else {
			Cube visualOctree = new Cube( Math.abs(octree.getvCorner2().x - octree.getvCorner1().x) );
			visualOctree.setPosition( new Point3D(octree.getvCenter()) );
			visualOctree.setWireframe(true);
			
			visualOctree.display(gl);
		}
		
	}

	/**
	 * Vykreslení.
	 * 
	 * @param gl
	 */
	public void display(GL2 gl) {
		
		// vykreslení míčků
		Iterator<Ball> ballIterator = balls.iterator();

		while (ballIterator.hasNext()) {
			
			Ball tmpBall = ballIterator.next();			
			
			// injekce GLUT do grafického primitiva
			tmpBall.getSphere().setGLUT(glut);
			
			// vykreslení grafického primitiva
			tmpBall.getSphere().display(gl);
		}
		
		if (this.visualizeOctrees) {
			this.drawOctree(gl, this.octree);
		}
		
		// vykreslení hraniční kostky
		this.cube.display(gl);
		
		// vykreslení outlines
		this.outline.display(gl);
	}

}
