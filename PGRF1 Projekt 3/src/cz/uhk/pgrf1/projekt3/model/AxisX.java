package cz.uhk.pgrf1.projekt3.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

public class AxisX implements ISolid {

	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	
	public AxisX(int length) {
		
		// 2D objekt:
		
		vertices.add(new Point3D(0, 0, 0)); // 0
		
		// body
		vertices.add(new Point3D(length, 0, 0)); // 1
		// propojení
		indices.add(0);
		indices.add(1);
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
