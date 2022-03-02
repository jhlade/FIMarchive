package cz.uhk.fim.pgrf2.structures;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Interface tělesa kresleného přes GLUT.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public interface IGLUTSolid {
	
	public void glutDraw();
	public void setGLUT(GLUT glut);
	public int getColor();

}
