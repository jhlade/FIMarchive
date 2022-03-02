package cz.uhk.fim.pgrf2.model;

import java.util.ArrayList;
import java.util.Iterator;

import cz.uhk.fim.transforms3D.Mat4;
import cz.uhk.fim.transforms3D.Mat4Identity;
import cz.uhk.fim.transforms3D.Vec3D;

/**
 * Oktalový strom.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Octree {

	// řídící konstanty oktostromu
	/** maximální hloubka stromu */
	private final int MAX_DEPTH = 4;
	/** minimální počet míčků v jednom stromu */
	private final int MIN_BALLS = 2;
	/** maximální počet míčků v jednom stromu */
	private final int MAX_BALLS = 4;

	// atributy
	/** děděná transformační matice */
	private Mat4 matrix = new Mat4Identity();
	
	// rohy stromu
	private Vec3D vCorner1; // min    (minX, minY, minZ)
	private Vec3D vCorner2; // max    (maxX, maxY, maxZ)
	private Vec3D vCenter;  // center (centerX, centerY, centerZ)
	
	/** hloubka tohoto stromu */
	private int depth;
	
	/** celkový počet míčků (včetně podstromů) */
	private int numBalls = 0;
	
	/** obsluhované míčky */
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	
	/** vnořené podstromy */
	private Octree[][][] subtrees = new Octree[2][2][2];
	
	/*
	 * Struktura:
	 * 
	 * [0] x >= minX, x < centerX
	 * [1] x >= centerX, x < maxX
	 * 
	 * 	[x]	[0] y >= minY, y < centerY
	 * 	[x]	[1] y >= centerY, y < maxY
	 * 
	 * 	[x]	[y]	[0] z >= maxZ, x < centerZ
	 * 	[x]	[y]	[1] z >= centerZ, z < maxZ
	 * 
	 * */
	
	/** pokud má tento strom podstromy */
	private boolean hasSubtrees = false;
	
	/**
	 * Plný konstruktor oktostromu.
	 * 
	 * @param vCorner1
	 * @param vCorner2
	 * @param depth
	 */
	public Octree(Vec3D vCorner1, Vec3D vCorner2, int depth) {
		
		// definice rohů stromu
		this.vCorner1 = vCorner1;
		this.vCorner2 = vCorner2;
		// průměr - nalezení středu pro budoucí rozpad
		this.vCenter = new Vec3D(this.vCorner1.add(this.vCorner2)).mul(0.5);

		// hloubka stromu
		this.depth = depth;
		
		// inicializace hodnot
		this.numBalls = 0;
		this.hasSubtrees = false;
	}
	
	/**
	 * @return the subtrees
	 */
	public Octree[][][] getSubtrees() {
		return subtrees;
	}

	/**
	 * @return the vCorner1
	 */
	public Vec3D getvCorner1() {
		return vCorner1;
	}

	/**
	 * @param vCorner1 the vCorner1 to set
	 */
	public void setvCorner1(Vec3D vCorner1) {
		this.vCorner1 = vCorner1;
		this.vCenter = new Vec3D(this.vCorner1.add(this.vCorner2)).mul(0.5);
	}

	/**
	 * @return the vCorner2
	 */
	public Vec3D getvCorner2() {
		return vCorner2;
	}

	/**
	 * @param vCorner2 the vCorner2 to set
	 */
	public void setvCorner2(Vec3D vCorner2) {
		this.vCorner2 = vCorner2;
		this.vCenter = new Vec3D(this.vCorner1.add(this.vCorner2)).mul(0.5);
	}

	/**
	 * @return the vCenter
	 */
	public Vec3D getvCenter() {
		return vCenter;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
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
	 * @return the hasSubtrees
	 */
	public boolean hasSubtrees() {
		return hasSubtrees;
	}

	/**
	 * Přiřazení míčku do odpovídajícího stromu.
	 * 
	 * @param ball
	 * @param position
	 * @param b_ball
	 */
	private void assignBall(Ball ball, Vec3D position, boolean ballAdd) {
		
		// nalezení odpovídajícího podstromu
		
		// x:
		for (int x = 0; x < 2; x++) {
			
			
			if (x == 0) {
				if (position.x - ball.getRadius() > vCenter.x) {
					continue;
				}
			} else if (position.x + ball.getRadius() < vCenter.x) {
				continue;
			}
			
			// x–> y:
			for (int y = 0; y < 2; y++) {
				
				if (y == 0) {
					if (position.y - ball.getRadius() > vCenter.y) {
						continue;
					}
				} else if (position.y + ball.getRadius() < vCenter.y) {
					continue;
				}
				
				// x–>y-> z:
				for (int z = 0; z < 2; z++) {
					
					if (z == 0) {
						if (position.z - ball.getRadius() > vCenter.z) {
							continue;
						}
					} else if (position.z + ball.getRadius() < vCenter.z) {
						continue;
					}
					
					// nalezena pozice, přidat nebo odebrat míček
					if (ballAdd) {
						subtrees[x][y][z].addBall(ball);
					} else {
						subtrees[x][y][z].removeBall(ball, position);
					}
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Odebrání míčku ze stromu
	 * 
	 * @param ball Míček
	 * @param position Pozice pro přeřazení do správného stromu
	 */
	private void removeBall(Ball ball, Vec3D position) {
		
		// zmizení míčku z počtu
		this.numBalls--;
		
		// jsou podstromy, ale neobsahují dost míčků
		if (this.hasSubtrees && this.numBalls < this.MIN_BALLS) {
			// tudíž nejsou potřeba a ruší se
			destroySubtrees();
		}
		
		// pokud jsou substromy
		if (this.hasSubtrees) {
			// přeřadí se míčky do nich
			assignBall(ball, position, false);
			//assignBall(ball, position, true);
		} else {
			// jinak prostě zmizí
			this.balls.remove(ball);
		}
	}
	
	/**
	 * Rozdělí strom na podstromy
	 */
	private void splitTree() {
		
		// rozdělení aktuálního prostoru
		Vec3D subCorner1 = new Vec3D();
		Vec3D subCorner2 = new Vec3D();
		
		// x:
		for (int x = 0; x < 2; x++) {
			
			if (x == 0) {
				subCorner1.x = vCorner1.x;
				subCorner2.x = vCenter.x;
			} else {
				subCorner2.x = vCenter.x;
				subCorner1.x = vCorner2.x;
			}
			
			// x-> y:
			for (int y = 0; y < 2; y++) {
				
				if (y == 0) {
					subCorner1.y = vCorner1.y;
					subCorner2.y = vCenter.y;
				} else {
					subCorner2.y = vCenter.y;
					subCorner1.y = vCorner2.y;
				}
				
				// x->y-> z:
				for (int z = 0; z < 2; z++) {
					
					if (z == 0) {
						subCorner1.z = vCorner1.z;
						subCorner2.z = vCenter.z;
					} else {
						subCorner2.z = vCenter.z;
						subCorner1.z = vCorner2.z;
					}
					
					// konstrukce nového podstromu
					subtrees[x][y][z] = new Octree(subCorner1, subCorner2, this.depth + 1);
					
				} // for z
				
			} // for y
		
		} // for x
		
		
		// iterátor přes balls a do toho assigBall
		// čímž se dostanou do substromů, takže
		Iterator<Ball> ballIterator = balls.iterator();
		
		while (ballIterator.hasNext()) {
			
			Ball subBall = ballIterator.next();
			
			assignBall(subBall, subBall.getPosition(), true);
		}
		
		// smazání z tohoto stromu
		balls.clear();
		
		// a konečně přijde potvrzení, že má substromy
		this.hasSubtrees = true;

	}
	
	/**
	 * Zničení podstromů
	 */
	private void destroySubtrees() {
		
		// sesbírání míčků
		fetchBalls(this.balls);
		
		// destrukce podstromů - ona java nemá destruktory
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					// pokus o destrukci substromu
					try {
						subtrees[x][y][z].finalize();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					
				} // z
			} // y
		} // x
		
		// nakonec oznámení
		this.hasSubtrees = false;
	}
	
	/**
	 * Sesbírání míčků ze substromů
	 * 
	 * @param collectedBalls Pointer na výsledek
	 */
	private void fetchBalls(ArrayList<Ball> collectedBalls) {
		
		// substromy
		if (this.hasSubtrees) {
			
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					for (int z = 0; z < 2; z++) {
						subtrees[x][y][z].fetchBalls(collectedBalls);
					} // for z
				} // for y
			} // for x
			
		} else {
			// pouze tento strom - tady se jede v rekurzi, takže není úplně možné ukazatel přepsat, žáno
			Iterator<Ball> ballIterator = this.balls.iterator();
			
			// TODO iterátor, orly?
			while (ballIterator.hasNext()) {
				collectedBalls.add(ballIterator.next());
			}
			
		}
		
	}
	
	
	/**
	 * Přidání míčku do stromu
	 * 
	 * @param ball
	 */
	public void addBall(Ball ball) {
		
		// počítadlo
		this.numBalls++;
		
		// už je to moc, tak se strom rozdělí
		if (!this.hasSubtrees && this.depth < this.MAX_DEPTH && this.numBalls > this.MAX_BALLS) {
			splitTree();
		}
		
		// pokud se to tedy rozdělilo
		if (this.hasSubtrees) {
			// tak se to upraví
			assignBall(ball, ball.getPosition(), true);
		} else {
			// jinak se prostě přidá míček na seznam
			this.balls.add(ball);
		}
		
	}
	
	/**
	 * Odstranění míčku
	 * 
	 * @param ball
	 */
	public void removeBall(Ball ball) {
		this.removeBall(ball, ball.getPosition());
	}
	
	/**
	 * Posun míčku
	 * 
	 * @param ball Míček
	 * @param previous Míčkova stará pozice
	 */
	public void ballMoved(Ball ball, Vec3D previous) {
		removeBall(ball, previous);
		addBall(ball);
	}
	
	
	/**
	 * Veřejná metoda pro obsluhu kolizní situace se stěnami.
	 * 
	 * @param bw
	 */
	public void detectWallCollisions(ArrayList<BallWall> bw, Box box) {
		
		this.detectWallCollisions(bw, new Vec3D(-1, 0, 0), box); // -X, WALL_LEFT
		this.detectWallCollisions(bw, new Vec3D(1, 0, 0), box);  // +X, WALL_RIGHT
		
		this.detectWallCollisions(bw, new Vec3D(0, -1, 0), box); // -Y, WALL_BACK
		this.detectWallCollisions(bw, new Vec3D(0, 1, 0), box);  // +Y, WALL_FRONT
		
		this.detectWallCollisions(bw, new Vec3D(0, 0, -1), box); // -Z, WALL_BOTTOM
		this.detectWallCollisions(bw, new Vec3D(0, 0, 1), box);  // +Z, WALL_TOP
		
	}
	
	/**
	 * Kolizní situace se stěnami
	 * 
	 */
	private void detectWallCollisions(ArrayList<BallWall> bw, Vec3D direction, Box box) {
		
		int x = (int) direction.x;
		int y = (int) direction.y;
		int z = (int) direction.z;
		
		if (this.hasSubtrees) {
			
			int dr1 = -1; // inicizalizace
			if (x != 0) dr1 = ((x == -1) ? 0 : 1); // směr 1
			if (y != 0) dr1 = ((y == -1) ? 0 : 1); // směr 2
			if (z != 0) dr1 = ((z == -1) ? 0 : 1); // směr 3
			
			for (int dr2 = 0; dr2 < 2; dr2++) {
				for (int dr3 = 0; dr3 < 2; dr3 ++) {
					
					if (x != 0) {
						subtrees[dr1][dr2][dr3].detectWallCollisions(bw, direction, box);
					} else if (y != 0) {
						subtrees[dr2][dr1][dr3].detectWallCollisions(bw, direction, box);
					} else if (z !=0) {
						subtrees[dr2][dr3][dr1].detectWallCollisions(bw, direction, box);
					}
					
				} // dr3
			} // dr2
			
		} else {
			Iterator<Ball> ballIterator = balls.iterator();
			
			while (ballIterator.hasNext()) {
				bw.add(
						new BallWall(ballIterator.next(),
						new Wall(new Vec3D(box.getPosition()), box.getSize(), direction))
				);
			}
		}
		
	}

	
	/**
	 * 
	 * @param bp Ukazatel na pole kolizních párů
	 */
	public void detectBallCollisions(ArrayList<BallPair> bp) {

		if (this.hasSubtrees) {
			// rekurze v podstromech
			
			for(int x = 0; x < 2; x++) {
				for(int y = 0; y < 2; y++) {
					for(int z = 0; z < 2; z++) {
						subtrees[x][y][z].detectBallCollisions(bp);
					} // z
				} // y
			} // x
			
		} else {
			
			Iterator<Ball> ballIterator1 = balls.iterator();
			int i1 = 0;
			int i2 = 0;
			
			while (ballIterator1.hasNext()) {
				
				Ball b1 = ballIterator1.next();
				
				// vnitřní smyčka
				Iterator<Ball> ballIterator2 = balls.iterator();
				
				while (ballIterator2.hasNext()) {
					
					Ball b2 = ballIterator2.next();
					
					if (i1 < i2) {
						bp.add( new BallPair(b1, b2) );
					}
					
					i2++;
				}
				
				i1++;
			}
			
		}
	}
	
	

}
