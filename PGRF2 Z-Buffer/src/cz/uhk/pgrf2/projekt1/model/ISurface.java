package cz.uhk.pgrf2.projekt1.model;

import java.util.List;
import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Interface pro vykreslitelný povrch.
 */

public interface ISurface {

	/**
	 * Seznam vertexů, většinou se neliší od wireframe
	 * 
	 * @return
	 */
	List<Point3D> verticesSurface();
	
	/**
	 * Seznam jednotlivých trojúhelníků.
	 * 
	 * @return
	 */
	List<Integer> triangles();
	
	/**
	 * Seznam předem stanovených barev pro určené pořadí vykreslování trojúhelníků.
	 * 
	 * @return
	 */
	List<Integer> colors();
	
}
