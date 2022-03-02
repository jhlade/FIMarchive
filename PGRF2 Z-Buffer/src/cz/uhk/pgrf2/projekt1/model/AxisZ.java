package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

public class AxisZ implements ISolid {

	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Integer> colorsAsList = new ArrayList<Integer>();
	
	public AxisZ(int length) {
		
		// 2D objekt:
		
		vertices.add(new Point3D(0, 0, 0)); // 0
		
		// body
		vertices.add(new Point3D(0, 0, length)); // 1
		// propojení
		indices.add(0);
		indices.add(1);
		
		// barva
		colorsAsList.add(0xff0000FF);
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
	public int getWireColor() {
		return 0xff0000FF;
	}

	@Override
	public List<Integer> colorsAsList() {
		return colorsAsList;
	}
	

}
