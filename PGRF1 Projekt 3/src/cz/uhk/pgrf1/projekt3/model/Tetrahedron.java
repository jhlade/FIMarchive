package cz.uhk.pgrf1.projekt3.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Čtyřstěn.
 */
public class Tetrahedron implements ISolid {
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();

	public Tetrahedron(int size) {
		
		// body
		vertices.add(new Point3D(size, 0, (int) (-size/Math.sqrt(2))));  // 0
		vertices.add(new Point3D(-size, 0, (int) (-size/Math.sqrt(2)))); // 1
		
		vertices.add(new Point3D(0, size, (int) (size/Math.sqrt(2))));  // 2
		vertices.add(new Point3D(0, -size, (int) (size/Math.sqrt(2)))); // 3
		
		// topologie
		indices.add(0);
		indices.add(1);
		
		indices.add(0);
		indices.add(2);
		
		indices.add(0);
		indices.add(3);
		
		indices.add(1);
		indices.add(2);
		
		indices.add(1);
		indices.add(3);
		
		indices.add(2);
		indices.add(3);
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
