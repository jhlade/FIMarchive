/**
 * 
 */
package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * Válec
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Cylinder implements ISolid, ISurface {

	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Integer> colors = new ArrayList<Integer>();
	private List<Integer> colorsAsList = new ArrayList<Integer>();
	
	/**
	 * 
	 */
	public Cylinder() {
		try {
			TriParser sphere = new TriParser(this.getClass().getResourceAsStream("/cz/uhk/pgrf2/projekt1/data/cylinder.tri"));
			
			vertices = sphere.getVertices();
			indices = sphere.getTriangles();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see cz.uhk.pgrf2.projekt1.model.ISolid#getWireColor()
	 */
	@Override
	public int getWireColor() {
		return 0xff000000;
	}

	/* (non-Javadoc)
	 * @see cz.uhk.pgrf2.projekt1.model.ISolid#colorsAsList()
	 */
	@Override
	public List<Integer> colorsAsList() {
		
		if (colorsAsList.size() == 0) {
			for (int i = 0; i < indices.size()/2; i++) {
				colorsAsList.add(0xff000000);
			}
		}

		return colorsAsList;
	}

	/* (non-Javadoc)
	 * @see cz.uhk.pgrf2.projekt1.model.ISolid#vertices()
	 */
	@Override
	public List<Point3D> vertices() {
		return vertices;
	}

	/* (non-Javadoc)
	 * @see cz.uhk.pgrf2.projekt1.model.ISolid#indices()
	 */
	@Override
	public List<Integer> indices() {
		return indices;
	}

	@Override
	public List<Point3D> verticesSurface() {
		return vertices;
	}

	@Override
	public List<Integer> triangles() {
		return indices;
	}

	@Override
	public List<Integer> colors() {
		
		if (colors.size() == 0) {
			for (int i = 0; i < indices.size()/3; i++) {
				colors.add(0xffc0c0c0);
			}
		}

		return colors;
	}

}
