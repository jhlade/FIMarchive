package cz.uhk.pgrf2.projekt1.model;

import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Interface pro vykreslitelné těleso.
 */
public interface ISolid {
	
	int getWireColor();
	List<Integer> colorsAsList();
	List<Point3D> vertices(); // vertexy
	List<Integer> indices();  // hrany
	
}
