/**
 * 
 */
package cz.uhk.pgrf1.projekt3.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Krychle.
 */
public class Cube implements ISolid {
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();

	/**
	 * 
	 */
	public Cube(int size) {
		
		// body:
		
		// spodní podstava
		vertices.add(new Point3D(0, 0, 0)); // 0
		vertices.add(new Point3D(size, 0, 0)); // 1
		vertices.add(new Point3D(size, size, 0)); // 2
		vertices.add(new Point3D(0, size, 0)); // 3
				
		// horní podstava
		vertices.add(new Point3D(0, 0, size)); // 4 
		vertices.add(new Point3D(size, 0, size)); // 5
		vertices.add(new Point3D(size, size, size)); // 6
		vertices.add(new Point3D(0, size, size)); // 7
		
		// topologie:	
		int i;
		for (i = 0; i < 4; i++) {
					
			// spodní podstava
			indices.add(i); indices.add((i + 1) % 4);
					
			// horní podstava (+4)
			indices.add(i + 4); indices.add((i + 1) % 4 + 4);
					
			// plášť
			indices.add(i); indices.add((i + 4));
		}
		
	}

	@Override
	public List<Point3D> vertices() {
		return vertices;
	}

	@Override
	public List<Integer> indices() {
		return indices;
	}

}
