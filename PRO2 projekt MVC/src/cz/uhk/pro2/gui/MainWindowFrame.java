/**
 * Copyright (c) 2013, Jan Hladěna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package cz.uhk.pro2.gui;

import static cz.uhk.pro2.mvc.JCFactory.frame;
import static cz.uhk.pro2.mvc.JCFactory.panel;
import static cz.uhk.pro2.mvc.JCFactory.menuItem;
import cz.uhk.pro2.gui.MainWindowController;
import cz.uhk.pro2.mvc.AbstractFrame;
import cz.uhk.pro2.gui.StatusBarView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Hlavní okno aplikace.
 * 
 * @author Jan Hladěna
 *
 */
public class MainWindowFrame extends AbstractFrame {
	
	/**
	 * Konstruktor hlavního okna.
	 */
	public MainWindowFrame() {
		super();
		
		// stavový řádek
		this.frame.add(getView(StatusBarView.class).getContentPane(), BorderLayout.SOUTH);
		getController(StatusBarController.class).setStatusMessage("Aplikace spuštěna.");
		
		// aktivace tabulky hned po sestavení
		setContent(getView(TableView.class).getContentPane());
	}
	
	/**
	 * Implementace abstraktní metody pro registraci Pohledů.
	 * 
	 * Jsou pouze dva - Tabluka a Detail.
	 */
	@Override
	protected void regViews() {
		// Stavový řádek
		views.put(StatusBarView.class, new StatusBarView(this));
		
		// Pohled tabulky na celý model
		views.put(TableView.class, new TableView(this));
		
		// Detailní pohled na jeden prvek
		views.put(DetailView.class, new DetailView(this));
	}

	/**
	 * Implememtace abstraktní metody pro registraci Controllerů.
	 * 
	 * Obsahuje Controllery pro Tabulku a Detail a jeden pro samo hlavní okno.
	 */
	@Override
	protected void regControllers() {
		
		// controller hlavního okna samotného - aby se držitelem Modelu nebylo View
		controllers.put(MainWindowController.class, new MainWindowController(this));
		
		// Stavový řádek
		controllers.put(StatusBarController.class, new StatusBarController(this));
		
		// controller Pohledu tabulky - celého modelu
		controllers.put(TableController.class, new TableController(this));
		
		// controller Pohledu detailu položky
		controllers.put(DetailController.class, new DetailController(this));
	}

	/**
	 * Implementace abstraktní metody pro vykreslení/rozložení komponent okna. Některé komponenty z továrny jsou sebenastavující,
	 * proto ignorujeme varování "unused".
	 */
	@Override
	@SuppressWarnings("unused")
	protected JFrame layout() {
		// inicializace hlavního obsahu
		this.cont = panel(new BorderLayout());
		
		// stvoření základního okna
		JFrame window = frame("Digitální archiv učebních materiálů", this.getContent());
		
		// nastavení velikosti
		window.setSize(980, 650);
		
		// **** menu **** //
		JMenuBar menuBar = new JMenuBar();
		
		// SOUBOR
		JMenu file = new JMenu("Soubor");
		file.setMnemonic(KeyEvent.VK_F);
		
		// nový
		JMenuItem fileNew = menuItem(file, "Nový archiv", "Zapomenout změny a začít nový digitální archiv", new NewArchive());
		// otevřít
		JMenuItem fileOpen = menuItem(file, "Otevřít...", "Otevřít soubor s archivem", new LoadDLMs());
		// uložit
		JMenuItem fileSave = menuItem(file, "Uložit...", "Uložit soubor s archivem", new SaveDLMs());
		// export
		JMenuItem fileExportXML = menuItem(file, "Exportovat do XML...", "Exportovat celý archiv do XML souboru", new ExportDLMs());
		
		// konec
		JMenuItem exitMenuItem = menuItem(file, "Konec", "Ukončit aplikaci", new ExitApp());

		// NÁPOVĚDA
		JMenu help = new JMenu("Nápověda");
		
		JMenuItem aboutMenuItem = menuItem(help, "O aplikaci", null, new AboutDialog());
		JMenuItem loadDemoData = menuItem(help, "Načíst ukázková data", null, new LoadDemoData());
				
		// připnutí hlavní nabídky
		menuBar.add(file);
		menuBar.add(help);
		// nastavení Menu
		window.setJMenuBar(menuBar);
		

		
		// záhlaví a logo ESF
		JPanel header = panel(new FlowLayout());
		header.setBackground(Color.WHITE); // na bílém pozadí
		
		BufferedImage wPic = null;
		try {
			wPic = ImageIO.read(this.getClass().getResource("/cz/uhk/pro2/img/esf_logo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel wIcon = new JLabel(new ImageIcon(wPic));
		header.add(wIcon);
		// přišpendlení ESF záhlaví do okna
		window.add(header, BorderLayout.NORTH);
		
		return window;
	}
	
	@Override
	public JPanel getContent() {
		return (JPanel) cont;
	}
	
	@Override
	public void setContent(JComponent content) {

		getContent().removeAll();
		getContent().add(content, BorderLayout.CENTER);
			
		getContent().revalidate();
		getContent().repaint();
	}
	
	// --- požadavky --- ve tvaru pojmenovaných vnitřních tříd
	
	// Ukončení aplikace
	private class ExitApp implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			getController(MainWindowController.class).exitApp();
		}
	}
	
	// Načtení ukázkových dat z /cz/uhk/pro2/dat/demo.dat
	private class LoadDemoData implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			getController(MainWindowController.class).loadDemoData();
		}
	}
	
	// Nový archiv
	private class NewArchive implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int question = JOptionPane.showConfirmDialog(frame, "Všechny neuložené změny budou navždy ztraceny. Přejete si pokračovat?");
			
			if (question == 0) {
				getController(MainWindowController.class).newArchive();
			}
		}
	}
	
	// Otevření binárního souboru
	private class LoadDLMs implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Digitální archiv", "dlm"));
			
			int returnVal = fc.showDialog(frame, "Otevřít");
			
			if (returnVal == 0) {
				getController(MainWindowController.class).openFile(fc.getSelectedFile());
			}
		}
	}
	
	// Uložení binárního souboru
	private class SaveDLMs implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Digitální archiv", "dlm"));
			
			int returnVal = fc.showSaveDialog(frame);
			
			if (returnVal == 0) {
				getController(MainWindowController.class).saveFile(fc.getSelectedFile());
			}
		}
	}
	
	// Export XML souboru
	private class ExportDLMs implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("XML soubory", "xml"));
			
			int returnVal = fc.showSaveDialog(frame);
			
			if (returnVal == 0) {
				getController(MainWindowController.class).exportXML(fc.getSelectedFile());
			}
			
		}
	}
	
	// Import XML souboru
	@SuppressWarnings("unused")
	private class ImportDLMs implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("XML soubory", "xml"));
			
			int returnVal = fc.showDialog(frame, "Importovat");
			
			if (returnVal == 0) {
				// TODO - import XML
			}
		}
	}
	
	// O aplikaci
	private class AboutDialog implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(frame, "Digitální archiv učebních materiálů\n\n(c) 2013-2014 Jan Hladěna\njan.hladena@uhk.cz\nPRO2 K-AI FIM UHK\n\nhttp://www.zs-studanka.cz/dum.html");
		}
		
	}
	
	// --- ostatní, debug a testy


}
