package cz.uhk.pgrf2.projekt1.model;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

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
public class Dodecahedron implements ISolid, ISurface {
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Integer> triangles = new ArrayList<Integer>();
	private List<Integer> colorsAsList = new ArrayList<Integer>();
	private List<Integer> colors = new ArrayList<Integer>();
	

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
        // (±1, ±1, ±1) - interní krychle
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
		
		// základní topologie:
        
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
        
        // Polygony. Jeden interní pentagon je tvořen třemi trojúhelníky.
        triangles.add(6); triangles.add(17); triangles.add(19);
        triangles.add(6); triangles.add(19); triangles.add(11);
        triangles.add(19); triangles.add(11); triangles.add(7);
        colors.add(0xffFFFF99); colors.add(0xffFFFF99); colors.add(0xffFFFF99);
        
        triangles.add(6); triangles.add(17); triangles.add(14);
        triangles.add(17); triangles.add(14); triangles.add(2);
        triangles.add(14); triangles.add(2); triangles.add(12);
        colors.add(0xffFFCC99); colors.add(0xffFFCC99); colors.add(0xffFFCC99); 

        triangles.add(2); triangles.add(12); triangles.add(0);
        triangles.add(2); triangles.add(0); triangles.add(10);
        triangles.add(0); triangles.add(10); triangles.add(8);
        colors.add(0xffFF9999); colors.add(0xffFF9999); colors.add(0xffFF9999); 

        triangles.add(10); triangles.add(2); triangles.add(17);
        triangles.add(17); triangles.add(10); triangles.add(19);
        triangles.add(10); triangles.add(19); triangles.add(3);
        colors.add(0xffFF6699); colors.add(0xffFF6699); colors.add(0xffFF6699); 

        triangles.add(19); triangles.add(3); triangles.add(7);
        triangles.add(3); triangles.add(7); triangles.add(13);
        triangles.add(7); triangles.add(13); triangles.add(15);
        colors.add(0xffFF6666); colors.add(0xffFF6666); colors.add(0xffFF6666); 

        triangles.add(7); triangles.add(15); triangles.add(11);
        triangles.add(15); triangles.add(11); triangles.add(5);
        triangles.add(11); triangles.add(5); triangles.add(9);
        colors.add(0xffFF9966); colors.add(0xffFF9966); colors.add(0xffFF9966); 

        triangles.add(5); triangles.add(9); triangles.add(4);
        triangles.add(4); triangles.add(5); triangles.add(18);
        triangles.add(18); triangles.add(4); triangles.add(16);
        colors.add(0xffFFCC66); colors.add(0xffFFCC66); colors.add(0xffFFCC66); 
 
        triangles.add(4); triangles.add(16); triangles.add(0);
        triangles.add(0); triangles.add(4); triangles.add(14);
        triangles.add(14); triangles.add(0); triangles.add(12);
        colors.add(0xffFFFF66); colors.add(0xffFFFF66); colors.add(0xffFFFF66); 

        triangles.add(0); triangles.add(8); triangles.add(1);
        triangles.add(0); triangles.add(1); triangles.add(16);
        triangles.add(1); triangles.add(16); triangles.add(18);
        colors.add(0xff99FF99); colors.add(0xff99FF99); colors.add(0xff99FF99);

        triangles.add(1); triangles.add(18); triangles.add(5);
        triangles.add(5); triangles.add(1); triangles.add(15);
        triangles.add(15); triangles.add(1); triangles.add(13);
        colors.add(0xff99CC99); colors.add(0xff99CC99); colors.add(0xff99CC99); 
 
        triangles.add(1); triangles.add(13); triangles.add(3);
        triangles.add(1); triangles.add(3); triangles.add(8);
        triangles.add(3); triangles.add(8); triangles.add(10); 
        colors.add(0xff999999); colors.add(0xff999999); colors.add(0xff999999); 

        triangles.add(11); triangles.add(6); triangles.add(14);
        triangles.add(11); triangles.add(14); triangles.add(4);
        triangles.add(11); triangles.add(4); triangles.add(9);
        colors.add(0xff996699); colors.add(0xff996699); colors.add(0xff996699); 
        
        
	}

	@Override
	public List<Point3D> vertices() {
		return vertices;
	}

	@Override
	public List<Integer> indices() {
		return indices;
	}
	
	@Override
	public int getWireColor() {
		return 0xff000000;
	}

	@Override
	public List<Integer> colorsAsList() {
		
		if (colorsAsList.size() == 0) {
			for (int i = 0; i < 30; i++) {
				colorsAsList.add(0xff000000);
			}
		}
		
		return colorsAsList;
	}

	@Override
	public List<Point3D> verticesSurface() {
		return vertices;
	}

	@Override
	public List<Integer> triangles() {
		return triangles;
	}

	@Override
	public List<Integer> colors() {
		return colors;
	}

}
