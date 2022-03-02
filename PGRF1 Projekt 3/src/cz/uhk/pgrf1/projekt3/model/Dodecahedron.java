package cz.uhk.pgrf1.projekt3.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf1.projekt3.transforms3D.Point3D;

/**
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 * 
 * Δωδεκάεδρον. Protože člověk je ζωον λογον εχον - ψυχή je duše,
 * která svými kruhy samu sebe vynáší z těla ven a současně vše
 * vnáší do těla nazpět. Tím vzniká jednoduchá jednota mezi živým
 * oduševnělým člověkem a světem kolem, vzniká ομολογειν.
 *
 */
public class Dodecahedron implements ISolid {
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();

	/**
	 * Konstruktor.
	 * 
	 * @param size
	 */
	public Dodecahedron(double size) {
		
		// do zlatého řezu
		double φ1 = ( (1.0f + Math.sqrt(5.0f)/3.0f ) / 2.0f );
		double φ2 = ( (1.0f - Math.sqrt(5.0f)/3.0f ) / 2.0f );
		
		double a = size * ( 1.0f / Math.sqrt(3.0f) );
		double b = size * ( Math.sqrt( 3.0f * φ1 / 3.0f) );
		double c = size * ( Math.sqrt( 3.0f * φ2 / 3.0f) );
        
		// vertexy:
        // (±1, ±1, ±1)
        vertices.add(new Point3D(a, a, a));    // 0
        vertices.add(new Point3D(a, a, -a));   // 1
        vertices.add(new Point3D(a, -a, a));   // 2
        vertices.add(new Point3D(a, -a, -a));  // 3
        vertices.add(new Point3D(-a, a, a));   // 4
        vertices.add(new Point3D(-a, a, -a));  // 5
        vertices.add(new Point3D(-a, -a, a));  // 6
        vertices.add(new Point3D(-a, -a, -a)); // 7
        // (±1/φ, ±φ, 0)
        vertices.add(new Point3D(b, c, 0));    // 8
        vertices.add(new Point3D(-b, c, 0));   // 9
        vertices.add(new Point3D(b, -c, 0));   // 10
        vertices.add(new Point3D(-b, -c, 0));  // 11
        // (±φ, 0, ±1/φ)
        vertices.add(new Point3D(c, 0, b));    // 12
        vertices.add(new Point3D(c, 0, -b));   // 13
        vertices.add(new Point3D(-c, 0, b));   // 14
        vertices.add(new Point3D(-c, 0, -b));  // 15
        // (0, ±1/φ, ±φ)   
        vertices.add(new Point3D(0, b, c));    // 16
        vertices.add(new Point3D(0, -b, c));   // 17
        vertices.add(new Point3D(0, b, -c));   // 18
        vertices.add(new Point3D(0, -b, -c));  // 19
        
        vertices.add(new Point3D(0, 0, 0));
		
		// topologie:
        indices.add(8); indices.add(10);  // horní hrana
        indices.add(9); indices.add(11); // spodní htrana
        
        // spodní hrana na vnitřní krychli
        indices.add(10); indices.add(2);
        indices.add(10); indices.add(3);
        indices.add(11); indices.add(6);
        indices.add(11); indices.add(7);
        
        // horní hrana na vnitřní krychli
        indices.add(8); indices.add(0);
        indices.add(8); indices.add(1);
        indices.add(9); indices.add(4);
        indices.add(9); indices.add(5);
        
        indices.add(12); indices.add(14); // přední hrana
        indices.add(13); indices.add(15); // zadní hrana
       
        // přední hrana na vnitřní krychli
        indices.add(0); indices.add(12);
        indices.add(1); indices.add(13);
        indices.add(2); indices.add(12);
        indices.add(3); indices.add(13);
        
        // zadní hrana na vnitřní krychli
        indices.add(4); indices.add(14);
        indices.add(6); indices.add(14);
        indices.add(5); indices.add(15);
        indices.add(7); indices.add(15);
        
        indices.add(16); indices.add(18); // pravá hrana
        indices.add(17); indices.add(19); // levá hrana
        
        // pravá hrana na vnitřní krychli
        indices.add(0); indices.add(16);
        indices.add(4); indices.add(16);
        indices.add(2); indices.add(17);
        indices.add(6); indices.add(17);
        
        // levá hrana na vnitřní krychli
        indices.add(1); indices.add(18);
        indices.add(5); indices.add(18);
        indices.add(3); indices.add(19);
        indices.add(7); indices.add(19);
	}

	@Override
	public List<Point3D> vertices() {
		return vertices;
	}

	@Override
	public List<Integer> indices() {
		return indices;
	}

}
