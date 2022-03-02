package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Čtyřstěn.
 */
public class Tetrahedron implements ISolid, ISurface {
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Integer> triangles = new ArrayList<Integer>();
	private List<Integer> colors = new ArrayList<Integer>();
	private List<Integer> colorsAsList = new ArrayList<Integer>();

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
	
		// polygony + jejich barvy
		triangles.add(0); triangles.add(1); triangles.add(2);
		colors.add(0xffdd0000);
		triangles.add(0); triangles.add(1); triangles.add(3);
		colors.add(0xff00dd00);
		triangles.add(0); triangles.add(2); triangles.add(3);
		colors.add(0xff0000dd);
		triangles.add(1); triangles.add(2); triangles.add(3);
		colors.add(0xffdddd00);
	}
	

	@Override
	public List<Point3D> vertices() {
		return vertices;
	}

	@Override
	public List<Integer> indices() {
		
		return indices;
	}

	@Override
	public List<Integer> triangles() {
		return triangles;
	}

	@Override
	public List<Integer> colors() {
		return colors;
	}


	@Override
	public List<Point3D> verticesSurface() {
		return vertices;
	}
	
	@Override
	public int getWireColor() {
		return 0xff000000;
	}


	@Override
	public List<Integer> colorsAsList() {
		
		if (colorsAsList.size() == 0) {
			for (int i = 0; i < 12; i++) {
				colorsAsList.add(0xff000000);
			}
		}
		
		return colorsAsList;
	}

}
