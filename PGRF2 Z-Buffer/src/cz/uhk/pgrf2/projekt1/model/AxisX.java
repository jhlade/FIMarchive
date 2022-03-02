package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

public class AxisX implements ISolid {

	protected List<Point3D> vertices = new ArrayList<Point3D>();
	protected List<Integer> indices = new ArrayList<Integer>();
	protected List<Integer> colorsAsList = new ArrayList<Integer>();
	
	public AxisX(int length) {
		
		// 2D objekt:
		
		vertices.add(new Point3D(0, 0, 0)); // 0
		
		// body
		vertices.add(new Point3D(length, 0, 0)); // 1
		// propojení
		indices.add(0);
		indices.add(1);
		
		// barva
		colorsAsList.add(0xffFF0000);
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
		return 0xffFF0000;
	}

	@Override
	public List<Integer> colorsAsList() {
		return colorsAsList;
	}

}
