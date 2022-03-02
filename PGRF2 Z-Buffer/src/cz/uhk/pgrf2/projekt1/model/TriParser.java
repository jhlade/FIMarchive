package cz.uhk.pgrf2.projekt1.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;

/**
 * Zpracování souborů .tri formátu tri 1.0. Výstupem je 
 * seznam vertexů a polygonů obecného objektu.
 * 
 * K těmto seznamům se přistupuje přes rozhraní
 * getVertices()
 * getTriangles()
 * 
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 */
public class TriParser {
	
	private int vCount;
	private int iCount;
	
	private List<Point3D> vertices = new ArrayList<Point3D>();
	private List<Integer> triangles = new ArrayList<Integer>();

	public TriParser(InputStream inputStream) throws Exception {
	
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			
			// první tři řádky
			
			// tri 1.0
			String line1 = br.readLine();
			
			if (!line1.contains("tri 1.0")) {
				throw new Exception("Nepodoporvaný formát souboru.");
			}
			
			// vertices NNNN
			String[] line2 = br.readLine().split(" ");
			
			if (!line2[0].contains("vertices")) {
				throw new Exception("Nepodoporvaný formát souboru.");
			}
			
			setVCount(Integer.parseInt(line2[1]));
			
			// faces MMMM
			String[] line3 = br.readLine().split(" ");
			if (!line3[0].contains("faces")) {
				throw new Exception("Nepodoporvaný formát souboru.");
			}
			
			setICount(Integer.parseInt(line3[1]));
			
			long lineCounter = 0;
		    for (String line; (line = br.readLine()) != null;) {
		    	
		    	lineCounter++;
		    	
		    	String[] tmpValues = line.split(" ");
		    	
		    	// body
		    	if (lineCounter <= vCount) {
		    		vertices.add(new Point3D(
		    				Double.parseDouble(tmpValues[0]),
		    				Double.parseDouble(tmpValues[1]),
		    				Double.parseDouble(tmpValues[2])
		    				));
		    		
		    	} else {
		    		
		    	// trojúhelníky
		    		
		    		triangles.add(Integer.parseInt(tmpValues[0]));
		    		triangles.add(Integer.parseInt(tmpValues[1]));
		    		triangles.add(Integer.parseInt(tmpValues[2]));
		    	}
		    	
		    }
		    
		}
	}
	
	/**
	 * @return the vertices
	 */
	public List<Point3D> getVertices() {
		return vertices;
	}

	/**
	 * @return the triangles
	 */
	public List<Integer> getTriangles() {
		return triangles;
	}
	
	/**
	 * 
	 * @param count
	 */
	private void setVCount(int count) {
		this.vCount = count;
	}

	/**
	 * 
	 * @param count
	 */
	private void setICount(int count) {
		this.iCount = count;
	}

	/**
	 * @return the vCount
	 */
	public int getVCount() {
		return vCount;
	}

	/**
	 * @return the iCount
	 */
	public int getICount() {
		return iCount;
	}

}
