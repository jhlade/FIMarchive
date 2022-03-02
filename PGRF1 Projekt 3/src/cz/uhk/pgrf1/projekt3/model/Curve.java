package cz.uhk.pgrf1.projekt3.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Kubika;
import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * Obecná křivka v prostoru.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Curve implements ISolid, ICurve {
	
	protected List<Point3D> vertices = new ArrayList<Point3D>();
	protected List<Integer> indices = new ArrayList<Integer>();

	public Curve(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
		
		Kubika curveCubic = new Kubika(getCubics());
		curveCubic.init(p1, p2, p3, p4);
		
		int i;
		for (i = 0; i < SEGMENTS; i++) {
			
			double t = i / (double) SEGMENTS;
			Point3D point = curveCubic.compute(t);

			// body
			vertices.add(point);
			
			// spojnice
			if (i >= 1) {
				indices.add(i - 1);
				indices.add(i);
			}
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

	@Override
	public int getCubics() {
		return 0;
	}

}
