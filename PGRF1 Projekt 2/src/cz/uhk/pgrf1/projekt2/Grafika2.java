/**
 * 
 */
package cz.uhk.pgrf1.projekt2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/**
 * @author Jan Hladěna
 * jan.hladena@uhk.cz
 * 
 * PGRF1 Úkol 2 - Vyplňování oblastí
 *
 */
public class Grafika2 extends JFrame {

	/** serial UID */
	private static final long serialVersionUID = 1583540430927532091L;

	/** Hlavní kreslící plátno */
	protected BufferedImage canvas;
	
	/** Kontejner plátna */
	protected JPanel panelCanvas;
	
	/** Počátek X */
	protected int originX;
	/** Počátek Y */
	protected int originY;
	
	/** Konec X */
	protected int endX;
	/** Konec Y */
	protected int endY;
	
	/** barva poločáry - šedá */
	protected int lineHalf = 0xffe0e0e0;
	/** barva plnočáry - černá */
	protected int lineFull = 0xff000000;
	
	/** barva výplně - červená */
	protected int fillColor = 0xffe04040;
	/** barva pro nahrazení - bílá */
	protected int fillReplace = 0xffffffff;
	
	/** ukazovat otisk */
	protected boolean showHalf = false; // pro úkol 2 vždycky FALSE
	
	/**
	 * Použitý algoritmus - magické číslo
	 * [nepoužívá get/set, pouze pro ilustrační účely]
	 * 
	 * 0 - Flood fill
	 * 1 - Queued flood fill
	 * 2 - Scan-line flood fill
	 */
	protected int algoMode = 0;

	
	/**
	 * Konstruktor swingového okna.
	 * 
 	 * @param width Šířka okna
 	 * @param height Výška okna
 	 */
	public Grafika2(int width, int height) {
		
		// inicializace okna
		super("PGRF1 Projekt 2: Vyplňování oblastí");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(width, height);
		setResizable(false);
		setVisible(true);
		setLayout(new BorderLayout());
		
		layoutGUI();
	}
	
	/**
	 * Rozložení komponent okna
	 */
	protected void layoutGUI() {
		// rozdělení ploch //
		panelCanvas = new JPanel();
		add(panelCanvas, BorderLayout.CENTER);
				
		// panelové číhače na myš a její události - listenery //
		panelCanvas.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
								
				// líná inicializace
				if (canvas == null) {
					initCanvas();
				}
								
				String button = "jiné";
				// žádná diskriminace myší pro praváky a leváky
				if (e.getButton() == MouseEvent.BUTTON1) button = "primární";
				if (e.getButton() == MouseEvent.BUTTON3) button = "sekundární";
				System.out.println("Stisknuto " + button + " tlačítko na [" + e.getX() + ", " + e.getY() + "]");
								
				// nastavení počátku
				setOrigin(e.getX(), e.getY());
				
				// sekundární tlačítko - vyplnění oblasti
				if (e.getButton() == MouseEvent.BUTTON3) {
					fill(e.getX(), e.getY(), fillColor);
				}
			}
						
			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("Tlačítko uvolněno na [" + e.getX() + ", " + e.getY() + "]");
						
				// nastavení konce
				setEnd(e.getX(), e.getY());
				
				// primární tlačítko - krelsení čáry
				if (e.getButton() == MouseEvent.BUTTON1) {
					line(originX, originY, endX, endY, lineFull);
				}
				
				/*
				// sekundární tlačítko - kreslení kružnice
				if (e.getButton() == MouseEvent.BUTTON3) {
					circle(originX, originY, endX, endY, lineFull);
				}*/

			}
					
		});
						
		// chytač pohybu
		panelCanvas.addMouseMotionListener(new MouseMotionAdapter() {
							
			@Override
			public void mouseDragged(MouseEvent e) {
				
				// předcházení coordinate out of bounds exception
				int xLoc = (e.getX() < 0) ? 0 : ((e.getX() > panelCanvas.getWidth() - 1) ? panelCanvas.getWidth() - 1 : e.getX());
				int yLoc = (e.getY() < 0) ? 0 : ((e.getY() > panelCanvas.getHeight() - 1) ? panelCanvas.getHeight() - 1 : e.getY());
				
				// kontrola tlačítka
				String button = "jiným";
				if (e.getButton() == MouseEvent.BUTTON1) button = "primárním";
				if (e.getButton() == MouseEvent.BUTTON3) button = "sekundárním";
				// generuje spoustu textu
				System.out.println("Tah " + button + " tlačítkem: [" + originX + ", " + originY + "] -> [" + xLoc + ", " + yLoc + "]");
				
			}
							
		});
				
				
		// ovládací panel //
		JPanel panelControl = new JPanel();
		panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
		
		JLabel labelFill = new JLabel("Algoritmus výplně");
		panelControl.add(labelFill);
		
		final JRadioButton radioFillFlood = new JRadioButton("Flood fill [R]");
		// flood fill bude výchozí
		radioFillFlood.setSelected(true);
		radioFillFlood.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioFillFlood.isSelected()) {
					algoMode = 0;
					System.out.println("Algoritmus výplně byl změněn na Flood Fill (" + algoMode + ").");
				}
			}
			
		});
		
		final JRadioButton radioFillFloodQ = new JRadioButton("Flood fill [Q]");
		radioFillFloodQ.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioFillFloodQ.isSelected()) {
					algoMode = 1;
					System.out.println("Algoritmus výplně byl změněn na Flood fill pomocí fronty (" + algoMode + ").");
				}
			}
			
		});
		
		
		final JRadioButton radioFillScanline = new JRadioButton("Scan-line");
		radioFillScanline.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioFillScanline.isSelected()) {
					algoMode = 2;
					System.out.println("Algoritmus výplně byl změněn na Scan-line (" + algoMode + ").");
				}
			}
			
		});
		
			
		panelControl.add(radioFillFlood);
		panelControl.add(radioFillFloodQ);
		panelControl.add(radioFillScanline);
			
		// skupina vyplňovacích tlačítek
		ButtonGroup groupFill = new ButtonGroup();
		groupFill.add(radioFillFlood);
		groupFill.add(radioFillFloodQ);
		groupFill.add(radioFillScanline);
		
		
		// mazací tlačítko
		JButton buttonClear = new JButton("Vyčistit");
		buttonClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Požadováno vyčištění plátna...");
				clearCanvas();
			}
		});
		panelControl.add(buttonClear);
				
		add(panelControl, BorderLayout.EAST);
		
				
	}
	
	protected void initCanvas() {
		System.out.println("Zavádí se plátno o šířce " + panelCanvas.getWidth() + " px a výšce " + panelCanvas.getHeight() + " px.");
		canvas = new BufferedImage(panelCanvas.getWidth(), panelCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
		clearCanvas();
	}
	
	/**
	 * Překreslení plátna na kontejner.
	 * 
	 */
	protected void repaintCanvas() {
		panelCanvas.getGraphics().drawImage(canvas, 0, 0, null);
	}
	
	/**
	 * Vyčištění kreslícího plátna
	 * 
	 */
	protected void clearCanvas() {
		
		// kdyby náhodou
		if (canvas == null) {
			initCanvas();
		}
		
		// grafický zdroj
		Graphics canvasGr = canvas.getGraphics();
		// nastavení na sněhobílo
		canvasGr.setColor(Color.white);
		// překrytí celé plochy
		canvasGr.fillRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
	
		repaintCanvas();
		System.out.println("A je vymalováno.");
	}
	
	/**
	 * Nastavení souřadnic bodu počátku. Počátek je definován dvěma třídními proměnnými
	 * originX a originY typu int. Typická inicializace počátku budiž provedena načtením
	 * souřadnic kurzoru myši během stisknutí tlačítka.
	 * 
	 * @param x 
	 * @param y
	 */
	protected void setOrigin(int x, int y) {
		originX = (x < 0) ? 0 : ((x > panelCanvas.getWidth() - 1) ? panelCanvas.getWidth() - 1 : x); // nastavení x-ové souřadnice výchozího bodu
		originY = (y < 0) ? 0 : ((y > panelCanvas.getHeight() - 1) ? panelCanvas.getHeight() - 1 : y); // nastavení y-ové souřadnice výchozího bodu
	}
	
	/**
	 * Nastavení souřadnic koncového bodu.
	 * 
	 * @param x
	 * @param y
	 */
	protected void setEnd(int x, int y) {
		endX = (x < 0) ? 0 : ((x > panelCanvas.getWidth() - 1) ? panelCanvas.getWidth() - 1 : x); // nastavení x-ové souřadnice koncového bodu
		endY = (y < 0) ? 0 : ((y > panelCanvas.getHeight() - 1) ? panelCanvas.getHeight() - 1 : y); // nastavení y-ové souřadnice koncového bodu
	}
	
	// kreslící metody //
	
	/**
	 * Wrapper pro obecné kreslení čáry.
	 * 
	 * Projekt 2 - je použita pouze čára z java.awt.Graphics
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	protected void line(int x1, int y1, int x2, int y2, int color) {
		
		lineNative(x1, y1, x2, y2, color);
		
		// překreselní výsledku
		repaintCanvas();
	}
	
	/**
	 * Referenční metoda kreslení čáry pomocí java.awt.Graphics.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	private void lineNative(int x1, int y1, int x2, int y2, int color) {
		// objekt Graphics
		Graphics gr = canvas.getGraphics();
		// nastavení barvy
		gr.setColor(new Color(color));
		// odpálení čáry
		gr.drawLine(x1, y1, x2, y2);
	}
	
	
	/**
	 * Wrapper pro vyplňování oblastí. Poskytuje jednotné rozhraní pro všechny vyplňovací funkce. Jako parametry
	 * očekává vstupní bod - seed - zadaný dvojicí x a y a dále barvu, kterou má oblast vyplnit.
	 * 
	 * @param x
	 * @param y
	 * @param color
	 * 
	 */
	protected void fill(int x, int y, int color) {
		
		// semínkové plnění - flood fill
		if (algoMode == 0) {
			floodFill(x, y, color);
		}
		
		// semínkové plnění - flood fill pomocí fronty
		if (algoMode == 1) {
			floodFillQueue(x, y, color);
		}
		
		// scanline
		if (algoMode == 2) {
			fillScanLine(x, y, color);
		}
		
		// propagace změn
		repaintCanvas();
	}
	
	/**
	 * Záplavové vyplňování pomocí rekurze. Způsobuje přetečení zásobníku.
	 * 
	 * @param x
	 * @param y
	 * @param color
	 */
	private void floodFill(int x, int y, int color) {
		
		// vymalováno
		if (canvas.getRGB(x, y) != fillReplace)
		{
			return;
		}
		
		// maluj
		canvas.setRGB(x, y, fillColor);
		
		// čaruj
		floodFill(x - 1, y, color); // W
		floodFill(x + 1, y, color); // E
		floodFill(x, y - 1, color); // N
		floodFill(x, y + 1, color); // S
		
	}
	
	/**
	 * Záplavové plnění pomocí fronty. Optimalizované řešení pomocí přepisu původní rekurze na iteraci. Implementace pomocí fronty
	 * obchází omezení standardní velikosti zásobníku JVM, který není dostatečně velký pro vyplňování větších oblastí. V principu
	 * je paměť šetřena tím, že se místo rekurzivního volání ukládají do interní LIFO, realizované LinkedListem, pouze jednotlivé body.
	 * Tyto jsou při každém obarvení z fronty okamžitě odstraněny. Další optimalizace urychluje proces okamžitým vybarvením první
	 * vodorovné řady ve zvoleném místě s následným přidáním všech možných bodů pod a nad touto řadou.
	 * 
	 * @param x
	 * @param y
	 * @param color
	 */
	private void floodFillQueue(int x, int y, int color) {
		
		// vymalováno
		if (canvas.getRGB(x, y) != fillReplace)
		{
			return;
		}
		
		// vytvoření fronty
		Queue<Point> queue = new LinkedList<Point>();
		
		// první naplnění aktuálním bodem
		queue.add(new Point(x, y));
		
		// dokud není fronta úplně prázdná
		while (!queue.isEmpty()) {
				
			// odebrání posledního přidaného bodu
			Point pt = queue.remove();
			
			// pokud má smysl pracovat s aktuálním bodem
			if (canvas.getRGB(pt.x, pt.y) == fillReplace) {
				
				// západní směr - kopie aktuálního bodu
				Point w = new Point(pt.x, pt.y);
				
				// postupuje pořád doleva, dokud to má smysl
				// - představuje základ pro scan-line 
				while (canvas.getRGB(w.x, w.y) == fillReplace) {
					if (w.x == 0) break;
					w.x--;
				}
					
				// východní směr
				Point e = new Point(pt.x, pt.y);
				
				// postupuje neustále vpravo
				while (canvas.getRGB(e.x, e.y) == fillReplace) {
					if (e.x == canvas.getWidth() - 1) break;
					e.x++;
				}
					
				
				for (int ix = w.x + 1; ix < e.x; ix++) {
					// body se zleva doprava vybarví
					canvas.setRGB(ix, pt.y, fillColor);
					
					// pokud je místo pod a nad právě barvenými pixely
					if (pt.y > 0 && pt.y < canvas.getHeight() - 1) {
						
						// přidají se do fronty
						
						// N
						if (canvas.getRGB(ix, pt.y - 1) == fillReplace) {
							queue.add(new Point(ix, pt.y - 1));
						}
						
						// S
						if (canvas.getRGB(ix, pt.y + 1) == fillReplace) {
							queue.add(new Point(ix, pt.y + 1));
						}
							
					}

				}
				
				
			}

		}
		
		
	}
	
	/**
	 * Optimalizované scan-line vyplňování.
	 * 
	 * Provádí hledání hranic a okamžité vyplňování odpovídajících částí již rasterizovaného polygonu, ve kterých se
	 * vstupní bod ([x, y] - seed) nachází. Od něj jsou v zápětí vyhledány průsečíky a do fronty je uložen segment
	 * jimi tvořený. Po jeho zpracování jsou hledány další segmenty nad a pod ním. Optimalizace jednoduchý průchod
	 * polygonem a okamžitě nalezené segmenty z fronty vyjímá a obarvuje.
	 * 
	 * @param x
	 * @param y
	 * @param color
	 */
	private void fillScanLine(int x, int y, int color) {
		
		/***********/
		
		// Interní úložiště/struktura pro čáry - obdobně jako pro Point jsou jeho členské proměnné public.
		// Protože Java neumožňuje vytvářet nested funkce nebo statické metody ve vnitřních třídách,
		// je zde jako členská metoda zahrnuta funkce pro přidání dalšího segmentu do fronty.
		class ScanLineRange {
			
			public int xMin;
			public int xMax;
			public int y;        
			public int vertical;
			public boolean eL;
			public boolean eR;
			
			/**
			 * Konstruktor interní struktury pro čáry
			 * 
			 * @param xMin 		počátení x-ová hodnota
			 * @param xMax 		koncová x-ová hodnota
			 * @param y 		aktuální y-pozice čáry
			 * @param vertical 	posun nahoru (-1) nebo dolu (1) - šlo by to realizovat i jako boolean
			 * @param eL 		rozšíření směrem vlevo
			 * @param eR		rozšíření směrem vpravo
			 */
			public ScanLineRange(int xMin, int xMax, int y, int vertical, boolean eL, boolean eR) {
				this.xMin = xMin;
				this.xMax = xMax;
				this.y = y;
				this.vertical = vertical;
				this.eL = eL;
				this.eR = eR;
			}
			
			/**
			 * "statická" funkce pro přidání segmentu do fronty.
			 * 
			 * @param parentQ	rodič - fronta, do které se bude segment přidávat
			 * @param minX		spočítaná levá hranice x
			 * @param maxX		spočítaná pravá hranice x
			 * @param yNext		y-označení zpracovávaného řádku
			 * @param isNext	určuje, zda byl či nebyl tento řádek již zpracován
			 * @param down		určuje, zda dochází k vyplňování dolu ve směru y
			 */
			public void addLine(Queue<ScanLineRange> parentQ, int minX, int maxX, int yNext, boolean isNext, boolean down) {
				
				// kontrola mezí y
				if (yNext >= 0 && yNext < canvas.getHeight()) {
				
					/** pomocná proměnná pro určení hranice x */
					int rangeMin = minX;
					/** příznak, zda je bod v dosahu */
					boolean inRange = false;
				
					// inicializace počítadla
					int xi;
				
					// smyčka zpracovává daný řádek zleva od zadaných hranic
					for (xi = minX; xi <= maxX; xi++) {
					
						// kontrola mezí x
						if (xi >= 0 && xi < canvas.getWidth()) {
		
							// je potřeba xi-tý bod na tomto y vybarvit?
							// ano, pokud vybarvený ještě není a zároveň
							// - leží mimo zadané hranice
							// - je v ještě nezpracovaném řádku
							boolean toFill = (isNext || (xi < this.xMin || xi > this.xMax)) && canvas.getRGB(xi, yNext) == fillReplace;
							
							// bod je sice mimo dosah, ale potřebuje vybarvit
							if (!inRange && toFill) {
								// minimální hranice je nastavena na tento bod
								rangeMin = xi;
								// a ten je vložen do dosahu
								inRange = true;
								
								// v dosahu, ale není potřeba vybravit
							} else if (inRange && !toFill) {
								// řez - rozvoj vlevo
								parentQ.add(new ScanLineRange(rangeMin, xi - 1, yNext, (down ? 1 : -1), (rangeMin == minX), false));
								// bod je zpracován a vyřazen z dosahu
								inRange = false;
							}
					
							// vybarvení bodu
							if (inRange) {
								canvas.setRGB(xi, yNext, fillColor);
							}
					
							// přeskočení, pokud to už nemá smysl
							if (!isNext && xi == this.xMin) {
								xi = this.xMax;
							}
						
						}
					
					}
				
					// bod se dostal do rozsahu z druhé strany
					if (inRange) {
						// řez - rozvoj vpravo
						parentQ.add(new ScanLineRange(rangeMin, xi - 1, yNext, (down ? 1 : -1), (rangeMin == minX), true));
					}
				
				}
				
			}
		}
		
		/***********/
		
		// fronta čar a kouzel
		Queue<ScanLineRange> lineRanges = new LinkedList<ScanLineRange>();
		
		// inicializace fronty; začíná se v místě kliknutí ([x, y]) a čára expanduje oběma směry,
		// velikost segmentu sahá od x do x (1 bod) a vertikální původ není definován
		lineRanges.add(new ScanLineRange(x, x, y, 0, true, true));
		
		// první pixel po kliknutí
		if (canvas.getRGB(x, y) == fillReplace) {
			canvas.setRGB(x, y, fillColor);
		}
		
		// hlavní smyčka, čeká se na vyprázdnění fronty segmentů
		while (!lineRanges.isEmpty()) {

			// výběr čáry ze fronty
			ScanLineRange ln = lineRanges.remove();
			
			// směr pohybu odspoda
			boolean d = (ln.vertical == 1) ? true : false;
			// směr pohybu shora
			boolean u = (ln.vertical == -1) ? true : false;
			// počáteční bod teče oběma směry, jeho původ je tedy 0 (d = F, u = F)
			
			// pomocné proměnné pro tuto čáru
			int minX = ln.xMin;
			int maxX = ln.xMax;
			
			// rozšíření hranic vlevo
			if (ln.eL) {
				// zachování mezí a kontrola barvení
				while (minX > 0 && canvas.getRGB(minX - 1, ln.y) == fillReplace) {
					
					// posun vlevo
					minX--;
					
					// rovnou maluje levou část čáry
					canvas.setRGB(minX, ln.y, fillColor);	
				}
			}
			
			// rozšíření hranic vpravo
			if (ln.eR) {
				// kontrola mezí a barvení
				while (maxX < canvas.getWidth() - 1 && canvas.getRGB(maxX + 1, ln.y) == fillReplace) {
					
					// posun vpravo
					maxX++;
					
					// maluje se vpravo
					canvas.setRGB(maxX, ln.y, fillColor);
				}
			}
			
			// rozšíření okolí segmentu mimo původní hranice
			ln.xMin--; // E
			ln.xMax++; // W
			
			// S
			if (ln.y < canvas.getHeight() - 1) {
				// přiřazení čáry do fronty ve směru nahoru
				ln.addLine(lineRanges, minX, maxX, ln.y + 1, !u, true);
			}
			
			// N
			if (ln.y > 0) {
				// přiřazení čáry ve směru dolu
				ln.addLine(lineRanges, minX, maxX, ln.y - 1, !d, false);
			}
			
		} // while fronta
		
	}
	

	/**
	 * main() - Vstupní bod pro odstartování aplikace
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// 800x600
				new Grafika2(800, 600);
			}
			
		});
		

	}

}
