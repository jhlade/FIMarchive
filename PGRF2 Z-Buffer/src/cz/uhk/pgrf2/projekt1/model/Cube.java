/**
 * 
 */
package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Krychle.
 * 
 * Změněno na střed v [0, 0, 0]
 */
public class Cube implements ISolid, ISurface {
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Integer> triangles = new ArrayList<Integer>();
	private List<Integer> colors = new ArrayList<Integer>();
	private List<Integer> colorsAsList = new ArrayList<Integer>();

	/**
	 * 
	 */
	public Cube(int size) {
		
		// body krychle se středem v [0, 0, 0]
		size = (int) size / 2;
		vertices.add(new Point3D(-size, -size, -size));
		vertices.add(new Point3D(size, -size, -size));
		vertices.add(new Point3D(size, size, -size));
		vertices.add(new Point3D(-size, size, -size));
		vertices.add(new Point3D(-size, -size, size));
		vertices.add(new Point3D(size, -size, size));
		vertices.add(new Point3D(size, size, size));
		vertices.add(new Point3D(-size, size, size));
		
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
		
		// polygony a jejich barvy
		triangles.add(0); triangles.add(1); triangles.add(3);
		triangles.add(1); triangles.add(2); triangles.add(3);
		colors.add(0xffFF0000);
		colors.add(0xffFF0000);
		//colors.add(0xff0000FF);
		//colors.add(0xff0000FF);
		
		triangles.add(0); triangles.add(4); triangles.add(5);
		triangles.add(0); triangles.add(5); triangles.add(1);
		colors.add(0xff00FF00);
		colors.add(0xff00FF00);
		//colors.add(0xff0000FF);
		//colors.add(0xff0000FF);
		
		triangles.add(1); triangles.add(5); triangles.add(6);
		triangles.add(1); triangles.add(6); triangles.add(2);
		colors.add(0xff0000FF);
		colors.add(0xff0000FF);
		
		triangles.add(2); triangles.add(6); triangles.add(7);
		triangles.add(2); triangles.add(7); triangles.add(3);
		colors.add(0xff00FFFF);
		colors.add(0xff00FFFF);
		//colors.add(0xff0000FF);
		//colors.add(0xff0000FF);
		
		triangles.add(3); triangles.add(7); triangles.add(4);
		triangles.add(3); triangles.add(4); triangles.add(0);
		colors.add(0xffFFFF00);
		colors.add(0xffFFFF00);
		//colors.add(0xff0000FF);
		//colors.add(0xff0000FF);
		
		triangles.add(4); triangles.add(5); triangles.add(7);
		triangles.add(5); triangles.add(6); triangles.add(7);
		colors.add(0xffFF00FF);
		colors.add(0xffFF00FF);
		//colors.add(0xff0000FF);
		//colors.add(0xff0000FF);
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
