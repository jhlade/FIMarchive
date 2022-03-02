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

//import static cz.uhk.pro2.mvc.JCFactory.comboBox;
import static cz.uhk.pro2.mvc.JCFactory.panel;
import static cz.uhk.pro2.mvc.JCFactory.button;
//import static cz.uhk.pro2.mvc.JCFactory.label;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
//import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
//import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
//import javax.swing.JTextField;
//import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;

import cz.uhk.pro2.dat.IConstantData;
import cz.uhk.pro2.mvc.AbstractFrame;
import cz.uhk.pro2.mvc.AbstractView;
import cz.uhk.pro2.gui.TableController;

/**
 * Pohled tabulky.
 * 
 * @author Jan Hladěna
 *
 */
public class TableView extends AbstractView<JPanel> implements IConstantData {

	/** tabulka */
	private JTable table;
	
	/** vyhledávací políčko */
	//private JTextField searchField;
	/** ročník */
	//private JComboBox selGrade;
	/** předmět */
	//private JComboBox selSubject;
	
	
	/**
	 * Konstruktor Pohledu
	 * 
	 * @param mainFrame
	 */
	public TableView(AbstractFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	protected JPanel layout() {

		// panel sám o sobě
		JPanel tableViewPanel = panel(new BorderLayout());
	
		// interní ovládací lišta
		JPanel tableToolbar = panel(null);
		tableToolbar.setLayout(new BoxLayout(tableToolbar, BoxLayout.X_AXIS));
		
		// tlačítka ovládací lišty
		JButton buttonAdd = button("Přidat", new AddRow());
		JButton buttonDel = button("Smazat", new DeleteRow());
		//JButton buttonFil = button("Hledat", new Search());
		
		/*
		JLabel captionGrade = label("Ročník:");
		JLabel captionSubject = label("Předmět:");
		JLabel captionFilter = label("Hledat:");
		*/
		
		tableToolbar.add(buttonAdd);
		tableToolbar.add(buttonDel);
		
		/*
		// ročník
		tableToolbar.add(captionGrade);
		// drop menu pro ročníky
		String[] grades = IConstantData.GRADES;
		this.selGrade = comboBox(grades);
		tableToolbar.add(selGrade);
		*/
		
		/*
		// předmět
		tableToolbar.add(captionSubject);
	    // drop menu pro předměty
		String[] subjects = IConstantData.SUBJECTS;
		this.selSubject = comboBox(subjects);
		tableToolbar.add(selSubject);
		*/
		
		/*
		tableToolbar.add(captionFilter);
		// hledací text
		this.searchField = new JTextField();
		tableToolbar.add(this.searchField);
		// připíchnutý button
		tableToolbar.add(buttonFil);
		*/
		
		// tabulka + datový model
		this.setTableModel();
		getTable().setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		getTable().setAutoCreateRowSorter(true);
		getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// číhač na řádky
		getTable().addMouseListener(new DetailClick());
		
		// scrollovátko
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane tablePane = new JScrollPane(table);
		
		// přiřazení položek k hlavnímu panelu
		tableViewPanel.add(tableToolbar, BorderLayout.NORTH);
		tableViewPanel.add(tablePane, BorderLayout.CENTER);

		
		return tableViewPanel;
	}
	
	public JTable getTable() {
		return this.table;
	}
	
	
	// --- požadavky ---
	
	// Dvojklik na řádek
	private class DetailClick extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
			if (e.getClickCount() == 2) {
				JTable target = (JTable) e.getSource();				
				// příprava Detailu
				getMainFrame().getController(DetailController.class).initDLM(target.convertRowIndexToModel(target.getSelectedRow()));
				
				// přepnutí na Detail
				getMainFrame().setContent(getMainFrame().getView(DetailView.class).getContentPane());
			}
		}
		
	}
	
	
	// Smazat řádek
	private class DeleteRow implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (getTable().getSelectedRow() != -1) {
				
				// potvrzení
				int question = JOptionPane.showConfirmDialog(getMainFrame().getContent(), "Opravdu si přejete odstranit vybraný záznam?");
				
				// Ano
				if (question == 0) {
					getMainFrame().getController(TableController.class).deleteRow(table.getSelectedRow());
				}
				
			} else {
				// upozornění na výběr
				JOptionPane.showMessageDialog(getMainFrame().getContent(), "Nebyl vybrán řádek ke smazání!");
			}
	
		}
		
	}
	
	// Přidat nový - tlačítkem
	private class AddRow implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// příprava Detailu
			getMainFrame().getController(DetailController.class).initDLM(-1);
			
			// přepnutí na Detail
			getMainFrame().setContent(getMainFrame().getView(DetailView.class).getContentPane());
		}
		
	}
	
	/*
	// Hledat - vyhledávací filtr
	private class Search implements ActionListener {
			
		@Override
		public void actionPerformed(ActionEvent e) {
			getMainFrame().getController(TableController.class).filterTable(selGrade.getSelectedItem().toString(), selSubject.getSelectedItem().toString(), searchField.getText());
		}
		
	}
	*/
	
	// --- odovědi ---
	
	/**
	 * Nastaví tableModel pro hlavní tabulku
	 */
	public void setTableModel() {
		
		if (getTable() != null) {
			// změna modelu
			getTable().setModel(getMainFrame().getController(TableController.class).getTableModel());
		} else {
			// úplně nová inicializace
			table = new JTable(getMainFrame().getController(TableController.class).getTableModel());
		}

	}
	

	/**
	 * Do tabulky (ne)byla načtena ukázková data.
	 * 
	 * @param success Úspěch načtení ukázkových dat
	 */
	public void demoDataLoaded(boolean success) {
		String message = (success) ? "Byla načtena ukázková data." : "Došlo k chybě během získávání ukázkových dat!";
		getMainFrame().getController(StatusBarController.class).setStatusMessage(message);

	}
	
	/**
	 * Archiv (ne)byl načten ze souboru.
	 * 
	 * @param success
	 */
	public void dataLoaded(boolean success) {
		String message = (success) ? "Digitální archiv byl načten ze souboru." : "Nepodařilo se otevřít soubor!";
		getMainFrame().getController(StatusBarController.class).setStatusMessage(message);

	}
	
	/**
	 * Archiv (ne)byl uložen do souboru.
	 * 
	 * @param success
	 */
	public void dataSaved(boolean success) {
		String message = (success) ? "Digitální archiv byl uložen do souboru." : "Nepodařilo se uložit archiv do souboru!";
		getMainFrame().getController(StatusBarController.class).setStatusMessage(message);
	}

	/**
	 * Oznámení o vytvoření nového archivu.
	 */
	public void newArchive() {
		getMainFrame().getController(StatusBarController.class).setStatusMessage("Byl otevřen nový prázdný digitální archiv.");

	}

	/**
	 * Oznámení o (ne)provedeném exportu
	 * 
	 * @param success
	 */
	public void dataExported(boolean success) {
		String message = (success) ? "Digitální archiv byl exportován do XML souboru." : "Nepodařilo se exportovat archiv do XML souboru!";
		getMainFrame().getController(StatusBarController.class).setStatusMessage(message);
	}

	/**
	 * Smazání řádku
	 */
	public void rowDeleted() {
		getMainFrame().getController(StatusBarController.class).setStatusMessage("Řádek smazán.");
	}

	/**
	 * Aplikace vyhledávacího filtru
	 */
	public void applyFilter() {
		System.out.println("Aplikován filtr.");
	}

	// --- test
	
}
