package cz.uhk.fim.pgrf2.structures;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Polygon - trojúhelník - pro zpracování z filosofie transforms3d do OpenGL.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Triangle {
	
	private List<Integer> indices = new ArrayList<Integer>();
	private Integer color = 0x00000000;

	/**
	 * Obyčejný kontruktor
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	public Triangle(Integer v1, Integer v2, Integer v3) {
		addIndex(v1);
		addIndex(v2);
		addIndex(v3);
	}
	
	/**
	 * Konstruktor s barvou
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param color
	 */
	public Triangle(Integer v1, Integer v2, Integer v3, Integer color) {
		addIndex(v1);
		addIndex(v2);
		addIndex(v3);
		setColor(color);
	}
	
	/**
	 * Přidání indexu.
	 * 
	 * @param index
	 */
	public void addIndex(Integer index) {
		indices.add(index);
	}
	
	public Integer getV1() {
		return indices.get(0);
	}
	
	public Integer getV2() {
		return indices.get(1);
	}
	
	public Integer getV3() {
		return indices.get(2);
	}
	
	public void setColor(Integer color) {
		this.color = color;
	}
	
	public Color getColor() {
		return new Color(this.color);
	}
	
	/**
	 * Barva jako float pole pro použití přímo v GL.
	 * 
	 * @return
	 */
	public float[] getColorf() {
		
		float[] colorAsFloatArray = {};
		getColor().getRGBColorComponents(colorAsFloatArray);
		
		return colorAsFloatArray;
	}

}
