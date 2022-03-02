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

import java.io.File;
import java.io.IOException;

import cz.uhk.pro2.mvc.AbstractController;
import cz.uhk.pro2.mvc.AbstractFrame;
import cz.uhk.pro2.model.*;

/**
 * Controller hlavního okna aplikace. Je držitelem instance Modelu.
 * 
 * @author Jan Hladěna
 *
 */
public class MainWindowController extends AbstractController {
	
	/** HLAVNÍ MODEL */
	private DigitalLibrary digitalLibrary = new DigitalLibrary();
	

	/**
	 * Konstruktor Controlleru hlavního okna
	 * 
	 * @param mainFrame
	 */
	public MainWindowController(AbstractFrame mainFrame) {
		super(mainFrame);		
	}
	
	
	/**
	 * Získání hlavního Modelu
	 * 
	 * @return
	 */
	public DigitalLibrary getModel() {
		return digitalLibrary;
	}
	
	/**
	 * Vynucené nastavení modelu
	 * 
	 * @param model
	 */
	public void setModel(DigitalLibrary model) {
		this.digitalLibrary = model;
	}
	
	/**
	 * Aktualizace modelu a předání řízení zpět na Pohled Tabulky. Volá se pokaždé, když se nějak výrazně mění model.
	 * 
	 */
	private void setViewBackToTable() {
		// přepnutí na pohled tabulky
		this.getMainFrame().setContent(this.getMainFrame().getView(TableView.class).getContentPane());
		
		// naplnění modelu do tabulky
		this.getMainFrame().getController(TableController.class).setTableModel();
	}
	
	// ---- ---- ---- ----
	

	// ukončení aplikace
	public void exitApp() {
		System.exit(0);
	}
	
	// úplně nový prázdný archiv
	public void newArchive() {
		
		// smazání seznamu
		this.getModel().clearAll();
		
		// aktualizace
		this.setViewBackToTable();
		
		// notifikace
		this.getMainFrame().getView(TableView.class).newArchive();
	}
	

	// načtení ukázkových dat
	public void loadDemoData() {
		
		boolean success = true;
		
		try {
			getModel().loadDemo();
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		
		this.setViewBackToTable();
		
		this.getMainFrame().getController(TableController.class).getTableModel().fireTableDataChanged();
		
		// notifikace
		this.getMainFrame().getView(TableView.class).demoDataLoaded(success);
	}
	
	// otevření binárního souboru
	public void openFile(File filePath) {
		
		boolean success = true;
		
		// načtení modelu
		try {
			this.getModel().loadLibrary(filePath.toString());
		} catch (Exception e) {
			success = false;
			System.err.println(e.getLocalizedMessage());
		}
		
		this.setViewBackToTable();
		
		this.getMainFrame().getController(TableController.class).getTableModel().fireTableDataChanged();
		
		// notifikace do stavového řádku
		this.getMainFrame().getView(TableView.class).dataLoaded(success);
	}
	
	// uložení souboru
	public void saveFile(File filePath) {
		boolean success = true;
		
		try {
			this.getModel().saveLibrary(filePath.toString());
		} catch (IOException e) {
			success = false;
			System.err.println(e.getLocalizedMessage());
		}
		
		// notifikace o uložení do stavového řádku
		this.getMainFrame().getView(TableView.class).dataSaved(success);
	}
	
	// export XML
	public void exportXML(File filePath) {
		
		boolean success = true;
		
		try {
			this.getModel().exportXML(filePath.toString());
		} catch (Exception e) {
			success = false;
			System.err.println(e.getLocalizedMessage());
		}
		
		// notifikace o exportu
		this.getMainFrame().getView(TableView.class).dataExported(success);
	}
	
	// TEST
	public void test() {

	}

}
