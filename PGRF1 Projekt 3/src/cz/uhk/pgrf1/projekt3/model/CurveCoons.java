package cz.uhk.pgrf1.projekt3.model;

import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Coonsova křivka v prostoru
 *
 */
public class CurveCoons extends Curve {

	public CurveCoons(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
		super(p1, p2, p3, p4);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getCubics() {
		return CURVE_COON;
	}

}
