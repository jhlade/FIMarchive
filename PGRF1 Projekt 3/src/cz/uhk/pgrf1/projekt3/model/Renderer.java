/**
 * 
 */
package cz.uhk.pgrf1.projekt3.model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Mat4;
import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;
import cz.uhk.pgrf1.projekt3.transforms3D.Vec3D;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Renderer - vykreslovač.
 */
public class Renderer {
	
	private Graphics graphics;
	
	private List<Point3D> vertices;
	private List<Integer> indices;
	
	private Mat4 matrix;
	
	private int width;
	private int height;
	
	private int color;

	/**
	 * Konstruktor rendereru
	 * 
	 * @param graphics DI Graphics plátno
	 * @param width
	 * @param height
	 */
	public Renderer(Graphics graphics, int width, int height, int color) {
		super();
		this.graphics = graphics;
		
		this.width = width;
		this.height = height;
		
		System.out.println("Renderer zaveden.");
	}
	
	/**
	 * Kreslení
	 */
	public void render() {
		
		// iterátor
		Iterator<Integer> iter = indices.iterator();
		
		while (iter.hasNext()) {
			
			// indexy
			int index1 = iter.next();
			int index2 = iter.next();

			// vertexy
			Point3D vertex1 = vertices.get(index1);
			Point3D vertex2 = vertices.get(index2);
			
			// čára
			renderLine(vertex1, vertex2);
		}
		
	}
	
	/**
	 * kreslení čar
	 */
	private void renderLine(Point3D v1, Point3D v2) {
		
		// transformace
		v1 = v1.mul(matrix);
		v2 = v2.mul(matrix);
		
		// dělení nulou
		if (v1.w > 0 && v2.w > 0)
		{
			// dehomogenizace
			Vec3D dv1 = v1.dehomog();
			Vec3D dv2 = v2.dehomog();
			
			graphics.setColor(new Color(color));
			
			// cesta do pekel useknutím floatu
			graphics.drawLine(
					(int)(0.5 * (dv1.x+1) * (width-1)),  // x1
					(int)(0.5 * (1-dv1.y) * (height-1)), // y1
					(int)(0.5 * (dv2.x+1) * (width-1)),  // x2
					(int)(0.5 * (1-dv2.y) * (height-1))  // y2
			);
			
		} else {
			//System.out.println("Nulka.");
		}
	}

	public Mat4 getMatrix() {
		return matrix;
	}

	public void setMatrix(Mat4 matrix) {
		this.matrix = matrix;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}

	public List<Point3D> getVertices() {
		return vertices;
	}

	public void setVertices(List<Point3D> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}

}
