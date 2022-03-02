package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * 3D osa Z je definovaná jako trojboký hranol se skosenou stěnou směrem do prvního kvadrantu
 * 
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class AxisZ3D extends AxisZ implements ISurface {
	
	protected List<Point3D> verticesSurface = new ArrayList<Point3D>();
	protected List<Integer> triangles = new ArrayList<Integer>();
	private List<Integer> colors = new ArrayList<Integer>();
	
	private final double THICKNESS = 0.5;

	public AxisZ3D(int length) {
		super(length);
		
		verticesSurface.addAll(vertices()); // 0, 1
		// dovnitř do Y
		verticesSurface.add(new Point3D(0, THICKNESS, 0)); // 2
		verticesSurface.add(new Point3D(0, THICKNESS, length)); // 3
		// dovnitř do X
		verticesSurface.add(new Point3D(THICKNESS, 0, 0)); // 4
		verticesSurface.add(new Point3D(THICKNESS, 0, length)); // 5
		
		triangles.add(0); triangles.add(1); triangles.add(2);
		colors.add(0xff0000FF);
		triangles.add(1); triangles.add(2); triangles.add(3);
		colors.add(0xff0000FF);
		
		triangles.add(0); triangles.add(4); triangles.add(5);
		colors.add(0xff0000FF);
		triangles.add(0); triangles.add(1); triangles.add(5);
		colors.add(0xff0000FF);
		
		// zkosená
		triangles.add(2); triangles.add(4); triangles.add(5);
		colors.add(0xff0000FF);
		triangles.add(3); triangles.add(2); triangles.add(5);
		colors.add(0xff0000FF);
		
		// a konce
		triangles.add(2); triangles.add(0); triangles.add(4); // u počátku
		colors.add(0xff0000FF);
		triangles.add(3); triangles.add(1); triangles.add(5); // na konci - špička
		colors.add(0xff0000FF);
		
	}

	@Override
	public List<Point3D> verticesSurface() {
		return verticesSurface;
	}

	@Override
	public List<Integer> triangles() {
		return triangles;
	}

	@Override
	public List<Integer> colors() {
		return colors;
	}

}
