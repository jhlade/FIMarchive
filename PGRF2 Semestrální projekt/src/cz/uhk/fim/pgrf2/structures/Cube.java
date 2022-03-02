package cz.uhk.fim.pgrf2.structures;

import java.util.Iterator;

import javax.media.opengl.GL2;
import cz.uhk.fim.transforms3D.Point3D;

/**
 * Krychle.
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class Cube extends Primitive implements ISolid {
	
	private int color = 0xffff0000;
	
	/**
	 * Konstruktor.
	 * 
	 * @param size Velikost krychle.
	 */
	public Cube(double size) {
		
		glMode = GL2.GL_TRIANGLES;
		
		// body
		size = (int) size / 2;
		vertices.add(new Point3D(-size, -size, -size));
		vertices.add(new Point3D(size, -size, -size));
		vertices.add(new Point3D(size, size, -size));
		vertices.add(new Point3D(-size, size, -size));
		vertices.add(new Point3D(-size, -size, size));
		vertices.add(new Point3D(size, -size, size));
		vertices.add(new Point3D(size, size, size));
		vertices.add(new Point3D(-size, size, size));
		
		// topologie
		triangles.add(new Triangle(0, 3, 1, color)); // 0
		triangles.add(new Triangle(3, 1, 2, color)); // 1
	
		triangles.add(new Triangle(4, 0, 5, color)); // 2
		triangles.add(new Triangle(0, 5, 1, color)); // 3

		triangles.add(new Triangle(6, 5, 1, color)); // 4
		triangles.add(new Triangle(1, 2, 6, color)); // 5
		
		triangles.add(new Triangle(2, 6, 7, color)); // 6
		triangles.add(new Triangle(2, 7, 3, color)); // 7
		
		triangles.add(new Triangle(7, 3, 4, color)); // 8
		triangles.add(new Triangle(3, 4, 0, color)); // 9

		triangles.add(new Triangle(4, 5, 7, color)); // 10
		triangles.add(new Triangle(5, 6, 7, color)); // 11
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		
		Iterator<Triangle> triangleIterator = triangles.iterator();
		
		while (triangleIterator.hasNext()) {
			Triangle tr = triangleIterator.next();
			tr.setColor(color);
		}
	}

	@Override
	public void promoteMaterial() {
		// TODO Auto-generated method stub
		
	}

}
