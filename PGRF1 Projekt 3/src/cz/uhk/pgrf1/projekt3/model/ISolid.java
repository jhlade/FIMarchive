package cz.uhk.pgrf1.projekt3.model;

import java.util.List;
import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Interface pro vykreslitelné těleso.
 */
public interface ISolid {
	
	List<Point3D> vertices(); // vertexy
	List<Integer> indices();  // hrany
	
}
