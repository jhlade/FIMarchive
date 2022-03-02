package cz.uhk.pgrf1.projekt3.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.haldena@uhk.cz>
 *
 * Původní třída pro zobrazení všech tří os jako jednoho objektu.
 *
 */
@Deprecated
public class Axes implements ISolid {

	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	
	public Axes(int length) {
		
		// 2D objekty:
		
		vertices.add(new Point3D(0, 0, 0)); // 0
		
		// body
		vertices.add(new Point3D(length, 0, 0)); // 1
		// propojení
		indices.add(0);
		indices.add(1);
		
		// body
		vertices.add(new Point3D(0, length, 0)); // 2
		// propojení
		indices.add(0);
		indices.add(2);
		
		// body
		vertices.add(new Point3D(0, 0, length)); // 3
		// propojení
		indices.add(0);
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
