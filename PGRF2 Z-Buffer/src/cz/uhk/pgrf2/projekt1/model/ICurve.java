package cz.uhk.pgrf2.projekt1.model;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public interface ICurve {
	
	double THICKNESS = .5;
	
	int CURVE_BEZI = 0;
	int CURVE_FERG = 1;
	int CURVE_COON = 2;
	
	int SEGMENTS = 16;
	
	int getCubics();
}
