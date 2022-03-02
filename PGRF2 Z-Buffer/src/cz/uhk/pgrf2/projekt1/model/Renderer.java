/**
 * 
 */
package cz.uhk.pgrf2.projekt1.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import cz.uhk.pgrf2.projekt1.transforms3D.Mat4;
import cz.uhk.pgrf2.projekt1.transforms3D.Point3D;
import cz.uhk.pgrf2.projekt1.transforms3D.Vec3D;

/**
 * @author Jan Hladěna <jan.hladena@uhk.cz>
 *
 * Renderer - vykreslovač.
 */
public class Renderer {
	
	private Graphics graphics;
	
	// unifikovaný renderer
	private List<Point3D> vertexBuffer = new ArrayList<Point3D>(); // vertexy
	private List<Integer> indexBuffer = new ArrayList<Integer>();  // indexy
	private List<Integer> colorBuffer = new ArrayList<Integer>();  // barvy
	private List<Mat4> transformationBuffer = new ArrayList<Mat4>();   // transformační matice
	private List<RenderedObject> renderedObjectBuffer = new ArrayList<RenderedObject>(); // objekty scény
	
	// správa osvícení
	private LightManager lightManager;
	
	/** transformační matice aktuálního objektu */
	private Mat4 matrix;
	
	/** velikost plátna */
	private int width;
	private int height;
	
	/** z-buffer */
	private double zbuf[][];
	/** image buffer */
	protected BufferedImage ibuf;
	
	/** minimální vykreslovaná blízkost? */
	private double wMin = 50.0;
	
	/** aktuální barva čáry - drátu */
	private int color;
	/** aktuální barva polygonu */
	private int triangleColor;
	/** aktuální polygon v homogenních souřadnicích */
	private Point3D[] currentPolygon = new Point3D[3];
	
	private long frame = 0;
	

	/**
	 * Konstruktor rendereru
	 * 
	 * @param graphics DI Graphics plátno
	 * @param width
	 * @param height
	 */
	public Renderer(Graphics graphics, int width, int height, int color) {
		super();
		this.graphics = graphics; // plátno
		
		this.width = width;
		this.height = height;
		
		// inicializace z-bufferu
		this.zbuf = new double[height][width];
		// inicializce image bufferu
		this.ibuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		System.out.println("Renderer zaveden.");
	}
	
	/**
	 * Image buffer
	 * 
	 * @return
	 */
	public BufferedImage getIBuf() {
		return ibuf;
	}
	
	/**
	 * Nastavení image bufferu
	 * 
	 * @param cnavas
	 */
	public void setIBuf(BufferedImage ibuf) {
		this.ibuf = ibuf;
	}
	
	
	/**
	 * Pročišění z-bufferu
	 */
	public void clearZbuff() {
		
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				zbuf[y][x] = 1.0; // nejdál
			}
		}
		
	}
	
	//pročištění image bufferu
	public void clearIBuff() {
		Graphics ibg = getIBuf().getGraphics();
		ibg.setColor(Color.WHITE);
		ibg.fillRect(0, 0, width - 1, height - 1);
	}
	
	public LightManager getLightManager() {
		return lightManager;
	}

	public void setLightManager(LightManager lightManager) {
		this.lightManager = lightManager;
	}

	/**
	 * Unifikovaný renderer
	 * 
	 */
	public void renderNG() {
		
		// iterátor samotných objektů
		Iterator<RenderedObject> objectIter = renderedObjectBuffer.iterator();
		// iterátor přes transformační matice objektů
		Iterator<Mat4> matrixIter = transformationBuffer.iterator();
		// iterátor přes používanou barvu
		Iterator<Integer> colorIter = colorBuffer.iterator();
		// iterátor hran/polygonů
		Iterator<Integer> indicesIter = indexBuffer.iterator();
		
		int vertexOffset = 0;
		
		// dokud jsou objekty
		while(objectIter.hasNext()) {
			
			RenderedObject currentObject = objectIter.next();
			
			// současná transformační matice - per objekt
			this.matrix = matrixIter.next();
			
			int single = currentObject.getIterIndexCount();
			
			//System.out.println("Režim objektu: "+ (currentObject.isWireframe() ? "wireframe ("+single+")" : "surface ("+single+")"));
				
			// indexy
			for (int i = 0; i < single; i++) {
				
				// wireframe
				if (currentObject.isWireframe()) {
					
					// barva čáry a transformační matice pro aktuální indexovou sadu
					this.color = colorIter.next();
					
					// indexy
					int index1 = vertexOffset + indicesIter.next();
					int index2 = vertexOffset + indicesIter.next();
					
					//System.out.println("Indexy: " + index1 +", " + index2);
					
					// vertexy
					Point3D vertex1 = vertexBuffer.get(index1);
					Point3D vertex2 = vertexBuffer.get(index2);
					
					//System.out.println("Vertexy: ["+vertex1.x+", "+vertex1.y+", "+vertex1.z+"], ["+vertex2.x+", "+vertex2.y+", "+vertex2.z+"]");
					
					// čára
					renderLine(vertex1, vertex2);
				} else {
					
					// barva polygonu
					this.triangleColor = colorIter.next();
					
					// indexy
					int index1 = vertexOffset + indicesIter.next();
					int index2 = vertexOffset + indicesIter.next();
					int index3 = vertexOffset + indicesIter.next();
					
					// vertexy
					Point3D vertex1 = vertexBuffer.get(index1);
					Point3D vertex2 = vertexBuffer.get(index2);
					Point3D vertex3 = vertexBuffer.get(index3);
					
					// trojúhelník
					renderSingleTriangle(vertex1, vertex2, vertex3);
				}
				
			}
			
			// další objekt, udělá se skok na další blok vrcholů
			vertexOffset += currentObject.getVertexCount();
		}
		
		// až když jsou vyrenderovány všechny objekty, reflektuje se změna na plátno
		redrawCanvas();
	}
	
	/**
	 * Zpětná kompatibilita
	 * 
	 */
	public void render() {
		renderNG();
		//saveIB();
	}
	

	private void saveIB() {
		
		String base = "/tmp/ibuf/";
		
		if (ibuf != null) {
			
			File outputfile = new File(base + "frame" + String.format("%04d", frame) + ".png");
			try {
				ImageIO.write(ibuf, "png", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.printf("%04d\n", frame);
			frame++;
		}
		
	}
	
	private void redrawCanvas() {
		graphics.drawImage(ibuf, 0, 0, null);
	}

	
	/**
	 * kreslení čar
	 */
	private void renderLine(Point3D v1, Point3D v2) {
		
		// transformace
		v1 = v1.mul(matrix);
		v2 = v2.mul(matrix);
		
		// viditelnsot
		if (v1.w > 0 && v2.w > 0)
		{
			// dehomogenizace
			Vec3D dv1 = v1.dehomog();
			Vec3D dv2 = v2.dehomog();
			
			Graphics ibg = getIBuf().getGraphics();
			ibg.setColor(new Color(color));
			
			// cesta do pekel useknutím floatu
			ibg.drawLine(
					(int)(0.5 * (dv1.x + 1) * (width - 1)),  // x1
					(int)(0.5 * (1 - dv1.y) * (height - 1)), // y1
					(int)(0.5 * (dv2.x + 1) * (width - 1)),  // x2
					(int)(0.5 * (1 - dv2.y) * (height - 1))  // y2
			);
		}
		
	}
	
	
	/**
	 * Kreslení trojúhelníků
	 * 
	 * transformace bodů jednoho trojúhelníku v prostoru a jeho ořezání
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	private void renderSingleTriangle(Point3D p1, Point3D p2, Point3D p3) {
		
		// transformace právě danou zobrazovací maticí
		p1 = p1.mul(matrix);
		p2 = p2.mul(matrix);
		p3 = p3.mul(matrix);
		
		// a jéje, náš polygón se nevejde do kamery :'( 
		if (p1.w <= 0 || p2.w <= 0 || p3.w <= 0) {
			
			// jako první zkontrolujeme všechno znovu, co kdyby...
			if (p1.w <= 0 && p2.w <= 0 && p3.w <= 0) {
				// konec, vážení, polygon je úplně pod obraz
				return;
			}
			
			// seřadíme jednotlivé čipery podle w a vyšetřujeme situace
			
			// p1 < p2
			if (p1.w < p2.w) {
				Point3D tmp = p1;
				p1 = p2;
				p2 = tmp;
			} // -> p1 >= p2
			
			// p1 < p3
			if (p1.w < p3.w) {
				Point3D tmp = p1;
				p1 = p3;
				p3 = tmp;
			} // -> p1 >= p3
			
			// p2 < p3
			if (p2.w < p3.w) {
				Point3D tmp = p2;
				p2 = p3;
				p3 = tmp;
			} // -> p2 >= p3
			
			// ––> p1 >= p2 >= p3

			// parametr pro interpolaci
			double param;
			// připravíme další body, jsou to osamělé průsečiky bloudící po hraně příboje absurdit (definovaného pomocí wMin)
			Point3D p4, p5;
			if (p1.w >= p2.w && p2.w >= p3.w) {
				
				// ...
			} else {
				System.out.println("!!! CHYBA POROVNÁVÁNÍ !!!");
			}
			
			// P2 je pod nulou, tudíž i P3 je pod nulou. Vzniká jeden trojúhelník P1-P4-P5.
			if (p2.w <= 0) {
					
				// P4 je směrem na neviditelné P2
				param = (p1.w - wMin) / (p1.w - p2.w);
				if (param >= 0) {
					p4 = p1.mul(1 - param).add(p2.mul(param));
				} else {
					p4 = p1;
				}
			
				// P5 je směrem na ještě neviditelnější P3
				param = (p1.w - wMin) / (p1.w - p3.w);
				if (param >= 0) {
					p5 = p1.mul(1 - param).add(p3.mul(param));
				} else {
					p5 = p1;
				}
				
				// a teď ten nový trojúhelník P1-P4-P5 odpálíme do kosmu:
				drawTriangle(p1, p4, p5);
				
			} else {
			// P2 leží nad nulou a P3 pod ní, vznikají tedy dva trojúhelníky, P1-P2-P4 a P1-P4-P5
				
				// hledáme směr z viditelného P2 do neviditelného P3 a průsečíkem s rovinou kamery je P4			
				param = (p2.w - wMin) / (p2.w - p3.w);
				if (param >= 0) {
					p4 = p2.mul(1 - param).add(p3.mul(param));
				} else {
					p4 = p2;
				}
				
				
				// z viditelného P1 do neviditelného P3 vznikne průsečík P5
				param = (p1.w - wMin) / (p1.w - p3.w);
				if (param >= 0) {
					p5 = p1.mul(1 - param).add(p3.mul(param));
				} else {
					p5 = p1;
				}
				
				// oba nové ad-hoc zpracujeme
				drawTriangle(p1, p2, p4); // P1-P2-P4
				drawTriangle(p1, p4, p5); // P1-P4-P5
			}

		} else {
			// vykreslí se normálně celý polygon podle plánu
			// konečná rasterizace
			drawTriangle(p1, p2, p3);
		}
	}
	
	/**
	 * Kreslení trojúhelníků
	 * 
	 * 3) konečné vykrelsení viditlených částí
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	private void drawTriangle(Point3D p1, Point3D p2, Point3D p3) {
		
		// globální paměť polygonu
		setCurrentPolygon(p1, p2, p3);
		
		// dehomogenizace bodů
		Vec3D vec1 = p1.dehomog();
		Vec3D vec2 = p2.dehomog();
		Vec3D vec3 = p3.dehomog();
		
		// seřazení podle nejmenšího Y od horního okraje plátna;
		// hodnoty jsou dehomogenizované v (-1; 1)
		
		// přidáno - kvůli světlům se stejně prohodí ještě netransformované (w = 1.0) homogenní body
		// uložené v globálním úložišti
		Point3D[] currentTriangle = getCurrentPolygon();
		
		// v1 nad v2
		if (vec1.y < vec2.y) {
			Vec3D tmp = vec1;
			vec1 = vec2;
			vec2 = tmp;
			
			Point3D tmpP = currentTriangle[0];
			currentTriangle[0] = currentTriangle[1];
			currentTriangle[1] = tmpP;
		}
				
		// v1 nad v3
		if (vec1.y < vec3.y) {
			Vec3D tmp = vec1;
			vec1 = vec3;
			vec3 = tmp;
			
			Point3D tmpP = currentTriangle[0];
			currentTriangle[0] = currentTriangle[2];
			currentTriangle[2] = tmpP;
		}
				
		// v2 nad v3
		if (vec2.y < vec3.y) {
			Vec3D tmp = vec2;
			vec2 = vec3;
			vec3 = tmp;
			
			Point3D tmpP = currentTriangle[1];
			currentTriangle[1] = currentTriangle[2];
			currentTriangle[2] = tmpP;
		}
		
		// uložení změn v globálním úložišti polygonu
		setCurrentPolygon(currentTriangle[0], currentTriangle[1], currentTriangle[2]);
		
		// transformace y z afinního prostoru (-1; 1) pro plátno (0; height)
		double y1 = (0.5 * (1 - vec1.y) * (height - 1));
		double y2 = (0.5 * (1 - vec2.y) * (height - 1));
		double y3 = (0.5 * (1 - vec3.y) * (height - 1));

		// končí u dolní hrany y1 -> y2
		processTriangle(y1, y2, y1, y3, vec1, vec2, vec3, triangleColor);
		// druhá část začíná horní hranou y2 -> y3
		processTriangle(y2, y3, y1, y3, vec1, vec2, vec3, triangleColor);
		
	}
	
	/**
	 * Samotné vykreslení částečného trojúhelníka do image bufferu
	 * 
	 * @param yStart
	 * @param yEnd
	 * @param y1
	 * @param y3
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	private void processTriangle(double yStart, double yEnd, double y1, double y3, Vec3D v1, Vec3D v2, Vec3D v3, int color) {
		
		// spočítá se normálový vektor aktuálně zpracovávaného polygonu
		
		Point3D p1 = getCurrentPolygon()[0];
		Point3D p2 = getCurrentPolygon()[1];
		Point3D p3 = getCurrentPolygon()[2];
		
		for (int y = Math.max((int) yStart + 1, 0); y <= Math.min(yEnd, height - 1); y++) {
			
			// poměr y - jak moc je odpracováno
			double t = (y - yStart) / (yEnd - yStart); 
			
			// první průsečík
			Vec3D va;
			// odpovídající homogenní bod
			Point3D pA;
			
			// od y1 do y2 - trojúhelník se spodní plochou
			if (yStart == y1) {
				va = v1.mul(1 - t).add(v2.mul(t));
				pA = p1.mul(1 - t).add(p2.mul(t));
			} else {
			// od y2 do y3 - trojúhelník s horní plochou
				va = v2.mul(1 - t).add(v3.mul(t));
				pA = p2.mul(1 - t).add(p3.mul(t));
			}
			
			// přepočítání pro celý trojúhelník
			t = (y - y1) / (y3 - y1);
			
			// druhý průsečík
			Vec3D vb = v1.mul(1 - t).add(v3.mul(t));
			
			// a jemu odpovídající hom. bod
			Point3D pB = p1.mul(1 - t).add(p3.mul(t));
					
			// otočení; vždycky se bude do plátna kreslit směrem vpravo, i když byde trojúhelník třeba opačně
			if (va.x > vb.x) {
				Vec3D tmp = va;
				va = vb;
				vb = tmp;
				
				// obdobně pomocné body v homogenním systému
				Point3D tmpP = pA;
				pA = pB;
				pB = tmpP;
			}
					
			// korekce x z jednotkového systému affinního prostoru pro obrazovku (resp. canvas)
			double xa = 0.5 * (va.x + 1) * (width - 1);
			double xb = 0.5 * (vb.x + 1) * (width - 1);
				
			// scanline xa -> xb
			for (int x = Math.max((int) xa + 1, 0); x <= Math.min(xb, width - 1); x++) {
				
				// jak moc je odpracováno z x
				t = (x - xa) / (xb - xa);
				// a výsledný průsečík
				Vec3D v = va.mul(1 - t).add(vb.mul(t));
				
				if (v.z < zbuf[y][x]) {
					// nové nastavení z-bufferu
					zbuf[y][x] = v.z;
					
					// lokální barva
					int currentColor = color;

					// aktivní správce osvětlení
					if (lightManager != null) {
						
						// a odpovídající homogenní bod
						Point3D currentPoint = pA.mul(1 - t).add(pB.mul(t));
						
						// pro každý bod teda spočítáme zvlášť normálový vektor, se kterým bude každé světlo svírat nějaký úhel
						Vec3D u1 = currentPoint.add(pA.mul(-1)).ignoreW();
						Vec3D u2 = currentPoint.add(pB.mul(-1)).ignoreW();
						Vec3D nVec = u1.mul(u2);

						Color lc = lightManager.lightedPointColor(currentPoint, nVec, new Color(currentColor));
						currentColor = lc.getRGB();
					}
					
					getIBuf().setRGB(x, y, currentColor);
					
				}
			}
					
		}
		
	}

	
	/**
	 * @return the currentPolygon
	 */
	private Point3D[] getCurrentPolygon() {
		return currentPolygon;
	}

	/**
	 * @param currentPolygon the currentPolygon to set
	 */
	private void setCurrentPolygon(Point3D p1, Point3D p2, Point3D p3) {
		this.currentPolygon[0] = p1;
		this.currentPolygon[1] = p2;
		this.currentPolygon[2] = p3;
	}

	/**
	 * Akruální zobrazovací matice
	 * 
	 * @return
	 */
	public Mat4 getMatrix() {
		return matrix;
	}

	/**
	 * Nastavení aktuální zobrazovací matice rendereru
	 * 
	 * @param matrix
	 */
	public void setMatrix(Mat4 matrix) {
		this.matrix = matrix;
	}

	/**
	 * získání aktuáljí barvy čáry
	 * 
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * aktuální barva čáry
	 * 
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * Přidání vykreslovaného objektu do rendereru.
	 * 
	 * @param object
	 * @param mode
	 */
	public void addRenderedObject(SceneObject object, boolean mode) {
		
		if (renderedObjectBuffer == null) {
			renderedObjectBuffer = new ArrayList<RenderedObject>();
		}
		
		RenderedObject renderedObject = new RenderedObject(
				(object.isSurface()) ? object.getSurface().verticesSurface().size() : object.getSolid().vertices().size(),
				(mode && object.isSurface()) ? object.getSurface().triangles().size() : object.getSolid().indices().size(),
				(mode) ? object.isSurface() : false);
		
		// samotný objekt
		renderedObjectBuffer.add(renderedObject);
		
		// vertex buffer
		vertexBuffer.addAll( (object.isSurface()) ? object.getSurface().verticesSurface() : object.getSolid().vertices());
		
		// index buffer
		indexBuffer.addAll((mode && object.isSurface()) ? object.getSurface().triangles() : object.getSolid().indices());
		
		// color buffer
		colorBuffer.addAll((mode && object.isSurface()) ? object.getSurface().colors() : object.getSolid().colorsAsList());
    }
	
	public void addRenderedObject(SceneObject object, boolean mode, Mat4 matrix) {
		transformationBuffer.add(matrix);
		
		addRenderedObject(object, mode);
	}
	
	/**
	 * Smazání všech bufferů rendereru
	 */
	public void reset() {
		
		// unifikovaný
		if (vertexBuffer != null) vertexBuffer.clear();
		if (indexBuffer != null) indexBuffer.clear();
		if (colorBuffer != null) colorBuffer.clear();
		if (transformationBuffer != null) transformationBuffer.clear();
		if (renderedObjectBuffer != null) renderedObjectBuffer.clear();
		
		// z-buffer
		clearZbuff();
		clearIBuff();
	}

}
