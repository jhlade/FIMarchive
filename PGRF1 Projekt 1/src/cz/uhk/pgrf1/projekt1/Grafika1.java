/**
 * 
 */
package cz.uhk.pgrf1.projekt1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/**
 * @author Jan Hladěna
 * jan.hladena@uhk.cz
 * 
 * PGRF1 Úkol 1 - úsečky a kružnice
 *
 */
public class Grafika1 extends JFrame {

	/** serial UID */
	private static final long serialVersionUID = 6374651578936643102L;

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
	/** narva plnočáry - černá */
	protected int lineFull = 0xff000000;
	
	/** ukazovat otisk */
	protected boolean showHalf = true;
	
	/**
	 * Použitý algoritmus - magické číslo
	 * 
	 * 0 - Triviální
	 * 1 - DDA
	 * 2 - Bresenham
	 * 3 - Graphics - referenční 
	 */
	protected int algoMode = 0;

	
	/**
	 * Konstruktor swingového okna.
	 * 
 	 * @param width Šířka okna
 	 * @param height Výška okna
 	 */
	public Grafika1(int width, int height) {
		
		// inicializace okna
		super("PGRF1 Projekt 1: Úsečky a kružnice");
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
				
				// sekundární tlačítko - kreslení kružnice
				if (e.getButton() == MouseEvent.BUTTON3) {
					circle(originX, originY, endX, endY, lineFull);
				}
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
			
				// pokud se má pohyb vůbec zobrazovat
				if (showHalf == true)
				{
					// primární tlačítko - krelsení čáry
					if (e.getButton() == MouseEvent.BUTTON1) {
						line(originX, originY, xLoc, yLoc, lineHalf);
					}
				
					// sekundární tlačítko - kreslení kružnice
					if (e.getButton() == MouseEvent.BUTTON3) {
						circle(originX, originY, xLoc, yLoc, lineHalf);
					}
				}
				
			}
							
		});
				
				
		// ovládací panel //
		JPanel panelControl = new JPanel();
		panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
		
		JLabel labelLine = new JLabel("Algoritmus čáry");
		panelControl.add(labelLine);
		
		final JRadioButton radioLineTrivial = new JRadioButton("Triviální");
		// triviální algoritmus je výchozí
		radioLineTrivial.setSelected(true);
		radioLineTrivial.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioLineTrivial.isSelected()) {
					algoMode = 0;
					System.out.println("Algoritmus kreslení byl změněn na triviální (" + algoMode + ").");
				}
			}
		});
		
		final JRadioButton radioLineDDA = new JRadioButton("DDA");
		radioLineDDA.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioLineDDA.isSelected()) {
					algoMode = 1;
					System.out.println("Algoritmus kreslení byl změněn na DDA (" + algoMode + ").");
				}
			}
		});
		
		final JRadioButton radioLineBresenham = new JRadioButton("Bresenham");
		radioLineBresenham.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioLineBresenham.isSelected()) {
					algoMode = 2;
					System.out.println("Algoritmus kreslení byl změněn na Bresenhamův (" + algoMode + ").");
				}
			}
		});
		
		final JRadioButton radioLineNative = new JRadioButton("[Java Graphics]");
		radioLineNative.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioLineNative.isSelected()) {
					algoMode = 3;
					System.out.println("Algoritmus kreslení byl změněn na referenční přes java.awt.Graphics (" + algoMode + ").");
				}
			}
		});
			
		panelControl.add(radioLineTrivial);
		panelControl.add(radioLineDDA);
		panelControl.add(radioLineBresenham);
		panelControl.add(radioLineNative);
				
		// seskupení tlačítek
		ButtonGroup groupLine = new ButtonGroup();
		groupLine.add(radioLineTrivial);
		groupLine.add(radioLineDDA);
		groupLine.add(radioLineBresenham);
		groupLine.add(radioLineNative);
		
		// zaškrtávátko umožňující vidět nebo nevidět probíhající tah
		final JCheckBox checkShowHalf = new JCheckBox("Zobrazovat tah");
		checkShowHalf.setSelected(true);
		checkShowHalf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkShowHalf.isSelected()) {
					showHalf = true;
					System.out.println("Zobrazení tahu bylo aktivováno.");
				} else {
					showHalf = false;
					System.out.println("Zobrazení tahu je deaktivováno.");
				}
			}
		});
		panelControl.add(checkShowHalf);
		
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
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	protected void line(int x1, int y1, int x2, int y2, int color) {
		
		// triviální kreslení
		if (algoMode == 0) {			
			lineTrivial(x1, y1, x2, y2, color);
		}
		
		// DDA kreslení
		if (algoMode == 1) {			
			lineDDA(x1, y1, x2, y2, color);
		}
		
		// Bresenhamovo kreslení
		if (algoMode == 2) {
			lineBresenham(x1, y1, x2, y2, color);
		}
		
		// nativní kreslení Graphics
		if (algoMode == 3) {
			lineNative(x1, y1, x2, y2, color);
		}
		
		// překreselní výsledku
		repaintCanvas();
	}
	
	/**
	 * Triviální algoritmus.
	 * 
	 * Používá přirozené chápání úsečky [x1, y1]:[x2, y2] podle pravidel analytické geometrie ve symyslu rovnice
	 * přímky y = kx + q a postupně vybarvuje všechny body ležící na této úsečce. Celočíselná kartézská soustava umožňuje
	 * plynulé vybarvování bodů pouze v jednom směru, který je nejprve nutné určit pomocí směrnice přímky k. Pro k v intervalu
	 * <-1; 1>, odpovídajícímu úhlu 45°, postupuje algoritmus podle osy x. Leží-li k mimo tento interval, provede se
	 * záměna os. Protože kreslenu úsečku lze též chápat jako přeponu pravoúhlého trojuhelníka, provádí se tato detekce
	 * porovnáním hran x a y a algoritmus zpracovává čáru ve směru delší hrany. Samotný cyklus pro vybarvování jednotlivých
	 * bodů může pracovat pouze v jednom (v tomto případě kladném) směru, je tedy také nutné prohodit body v případě,
	 * že bod počáteční leží ve zvoleném směru až za bodem koncovým.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	private void lineTrivial(int x1, int y1, int x2, int y2, int color) {
		
		// směrnice úsečky
		float k;
		// posunu
		float q;
		
		// pokud je směrnice větší než 1 (y > x), bude se kreslit svisle podle y
		// - prostě prohodíme obě osy
		if (Math.abs(x2 - x1) < Math.abs(y2 - y1)) {
			
			// výpočet směrnice úsečky na prohozené ose
			k = (float) (x2 - x1) / (y2 - y1);
			// stejně tak posun
			q = x1 - k*y1;
			
			// pokud jsou body od sebe špatným směrem, prohodí se jejich souřadnice
			if (y1 > y2) {
				// [y]
				y1 ^= y2;
				y2 ^= y1;
				y1 ^= y2;
				// [x]
				x1 ^= x2;
				x2 ^= x1;
				x1 ^= x2;
			}
			
			
			// cyklení podle y, která je v tomto chápání naše nové x :)
			int y;
			for (y = y1; y <= y2; y++) {
				// barvený bod je [ky + q, y]
				canvas.setRGB((int) (k*y + q), y, color);
			}
			
		} else {
			// směrnice je v pořádku, kreslit se bude v přirozeném stavu podle x
			
			// výpočet směrnice úsečky
			k = (float) (y2 - y1) / (x2 - x1);
			// posun
			q = y1 - k*x1;
			
			// určení kvadrantu - pokud je směr druhou stranou, prohodí se souřadnice bodů
			// integerový xorswap
			if (x1 > x2) {
				// [x]
				x1 ^= x2;
				x2 ^= x1;
				x1 ^= x2;
				// [y]
				y1 ^= y2;
				y2 ^= y1;
				y1 ^= y2;
			}
			
			// cyklení podle x
			int x; // vně cyklus, možná to v Javě už nemá cenu
			for (x = x1; x <= x2; x++) {
				// barvený bod je počítán přirozeně jako [x, kx + q]
				canvas.setRGB(x, (int) (k*x + q), color);
			}
		}
		
	}
	
	/**
	 * DDA - digital differential analyzer.
	 * 
	 * Implementace DDA algoritmu. Podle delší hrany spočítá počet kroků na odpovídající ose a následně určí
	 * dílčí reálné/float přírůstky pro interpolaci. Body se pak vykreslují podle pravidla zaokrouhlení,
	 * v tomto javovském případě podle Math.round(). 
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	private void lineDDA(int x1, int y1, int x2, int y2, int color) {
		
		// rozdíly - celková délka hran pravoúhlého trojúhelníka, jehož je krelsené čára přeponou
		int dx = x2 - x1;
		int dy = y2 - y1;
		
		// počet kroků
		int steps;
		
		// určení osy
		if (Math.abs(dx) > Math.abs(dy)) {
			steps = Math.abs(dx); // x
		} else {
			steps = Math.abs(dy); // y
		}
		
		// dílčí float přírůstky (délka / počet kroků)
		float xi = dx / (float) steps;
		float yi = dy / (float) steps;
		
		// počáteční hodnoty
		float x = x1;
		float y = y1;
		
		// počáteční bod se hned vybarví
		canvas.setRGB(Math.round(x), Math.round(y), color);
		
		// hlavní cyklus
		int i;
		for (i = 0; i < steps; i++) {
			// nastavení bodů o přírůstek
			x += xi;
			y += yi;
			// vykreslení zaokrouhlením
			canvas.setRGB(Math.round(x), Math.round(y), color);
		}
		
	}

	/**
	 * Implementace Bresenhamova algoritmu.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	private void lineBresenham(int x1, int y1, int x2, int y2, int color) {
		
		if ((x1 == x2) && (y1 == y2)) {
			// žádná úsečka, kreslí se bod
			canvas.setRGB(x1, y1, color);
		} else {
			// určení absolutné délky hran
			int dx = Math.abs(x2 - x1);
			int dy = Math.abs(y2 - y1);
			// rozdíl délek hran
			int e = dx - dy;
			
			// určení směru - posun je vždy o 1 pixel
			int xv = (x1 < x2) ? 1 : -1;
			int yv = (y1 < y2) ? 1 : -1;
			
			// dvojnásobek rozdílu délek hran
			int e2;
			
			// dokud není dosaženo výchozího bodu
			while ((x1 != x2) || (y1 != y2)) {
				// spočítání aktuálního dvojnásobku rozdílu délek hran
				e2  = 2 * e;
				
				// korekce podle výšky
				if (e2 > -dy) {
					x1 += xv; // x se posunuje o 1
					e -= dy; // a od chyby se odečítá výška
				}
				
				// korekce podle délky
				if (e2 < dx) {
					y1 += yv;
					e += dx;
				}
				
				// nakreslení bodu
				canvas.setRGB(x1, y1, color);
			}
			
		}
		
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
	 * Wrapper pro kreslení kružnice.
	 * 
	 * @param ox
	 * @param oy
	 * @param px
	 * @param py
	 * @param color
	 */
	protected void circle(int ox, int oy, int px, int py, int color) {
		
		// referenční kreslení
		if (algoMode == 3) {
			circleNative(ox, oy, px, py, color);
		} else {
			// vše ostatní Bresenham
			circleBresenham(ox, oy, px, py, color);
		}
			
		repaintCanvas();
	}
	
	
	/**
	 * Bresenham - midpoint circle algorithm
	 * 
	 * @param ox
	 * @param oy
	 * @param px
	 * @param py
	 * @param color
	 */
	protected void circleBresenham(int ox, int oy, int px, int py, int color) {
		
		// prvotní spočítání poloměru
		int x = (int) Math.sqrt((ox-px)*(ox-px)+(oy-py)*(oy-py)); // nasazení do x
		int y = 0;
		
		// chyba
		int rE = 1 - x;
		
		while (x >= y) {
			
			// symetrické body na všechny oktanty
	
			try {
				// 1. kvadrant
				canvas.setRGB(ox + x, oy + y, color);
				canvas.setRGB(ox + y, oy + x, color);
			
				// 2. kvadrant
				canvas.setRGB(ox - x, oy + y, color);
				canvas.setRGB(ox - y, oy + x, color);
			
				// 3. kvadrant
				canvas.setRGB(ox - x, oy - y, color);
				canvas.setRGB(ox - y, oy - x, color);
			
				// 4. kvadrant
				canvas.setRGB(ox + x, oy - y, color);
				canvas.setRGB(ox + y, oy - x, color);
			} catch (Exception e) {
				// Výjimka, {...} coordinate out of bounds
			}
			
			// prefixová unární inkrementace na y-ose se provede v jednom kroku
			if (rE < 0) {
				rE += 2 * ++y + 1;
			} else {
				// dekrementace na x-ose - směrem k hodnotě y
				rE += 2 * (++y - --x + 1);
			}
		}
		
		/*
		 * pravědpodobně by to šlo i takto na čtyři nedělené kvadranty
		 *
		 *
		 *
		// prvotní spočítání poloměru
		int r = (int) Math.sqrt((ox-px)*(ox-px)+(oy-py)*(oy-py));
		int x = -r; // a nastavení do -x
		int y = 0;
		
		// zaokrouhlovací chyba poloměru
		int rE = 2 - 2*r;
		
		do {
			// protože se vykreslují všechny kvadranty najednou,
			// může dojít k zápisu mimo hranice plátna
			try {
				// body se kreslí symetricky na celé kvadranty
				canvas.setRGB(ox - x, oy + y, color); // 1. kvadrant
				canvas.setRGB(ox - y, oy - x, color); // 2. kvadrant
				canvas.setRGB(ox + x, oy - y, color); // 3. kvadrant
				canvas.setRGB(ox + y, oy + x, color); // 4. kvadrant
			} catch (Exception e) {
				// příliš mnoho textu - Coordinate out of bounds
				//System.err.println(e.getLocalizedMessage());
			}
			
			// nový poloměr se nastavuje podle chyby spočítané v předchozím kroku
			r = rE;
			
			// pokud je poloměr menší nebo roven y,
			// y se inkrementuje a jeho dvojnásobek + 1 je připočten k chybě
			if (r <= y) rE += ++y * 2 + 1;
			
			// pokud je poloměr větší než x nebo je chyba větší než y,
			// x se inkrementuje (a pokračuje od záporných hodnot až po nulu)
			// a k chybě se připočítá jeho dvojnásobek + 1
			if (r > x || rE > y) rE += ++x * 2 + 1;
			
			// dokud x nedosáhne nuly
		} while(x < 0);
		*/

	}
	
	/**
	 * Referenční metoda kreslení kružnice pomocí java.awt.Graphics
	 * 
	 * @param ox
	 * @param oy
	 * @param px
	 * @param py
	 * @param color
	 */
	private void circleNative(int ox, int oy, int px, int py, int color) {
		// objekt Graphics
		Graphics gr = canvas.getGraphics();
		// nastavení barvy
		gr.setColor(new Color(color));
		
		// poloměr
		int r = (int) Math.sqrt((ox-px)*(ox-px)+(oy-py)*(oy-py));
	
		// vykroužení kružnice
		gr.drawOval(ox - r, oy - r, 2*r, 2*r);
	}
	

	/**
	 * main() - Vstupní bod pro odstartování aplikace
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// podle pravidel se vše spouští pěkně v rytmu Swingu
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// 800x600
				new Grafika1(800, 600);
			}
			
		});
		

	}

}
