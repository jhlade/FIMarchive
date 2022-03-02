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

import javax.swing.table.TableRowSorter;

import cz.uhk.pro2.model.DLM;
import cz.uhk.pro2.mvc.AbstractController;
import cz.uhk.pro2.mvc.AbstractFrame;
import cz.uhk.pro2.mvc.ProjectTableModel;

/**
 * 
 * @author Jan Hladěna
 *
 */
public class TableController extends AbstractController {
	
	/** sloupce */
	private String[] cols = {"Name", "Grade", "Subject", "Author", "Date", "Identifier"};
	private String[] labels = {"Název", "Ročník", "Předmět", "Autor", "Zařazeno", "Identifikátor"};
	
	/** tabulkový model */
	private ProjectTableModel<DLM> model = new ProjectTableModel<DLM>(DLM.class, cols);
	
	/** řadič, náš vyhledávací filtr osudově připoutaný k Modelu */
	private TableRowSorter<ProjectTableModel<DLM>> filterTable = new TableRowSorter<ProjectTableModel<DLM>>(model);


	public TableController(AbstractFrame mainFrame) {
		super(mainFrame);
	}
	
	/**
	 * Získání tabulkového modelu z hlavního modelu
	 * 
	 * @return
	 */
	public ProjectTableModel<DLM> getTableModel() {
		
		this.model.fireTableDataChanged();
		
		// inicializace tabulkového modelu
		this.model.loadData(this.getMainFrame().getController(MainWindowController.class).getModel().getDLMs());
		this.model.setColumnIdentifiers(this.labels);
		
		return this.model;
	}
	
	/**
	 * Odeslání modelu do pohledu
	 */
	public void setTableModel() {
		this.getMainFrame().getView(TableView.class).setTableModel();

		this.getMainFrame().getController(TableController.class).getTableModel().fireTableDataChanged();
	}
	
	/**
	 * Získání řadiče tabulky
	 * 
	 * @return řadič
	 */
	public TableRowSorter<ProjectTableModel<DLM>> getTableFilter() {
		return this.filterTable;
	}
	
	/**
	 * Smazání záznamu na zadaném řádku
	 * 
	 * @param row
	 */
	public void deleteRow(int row) {
		this.getTableModel().removeRow(row);
		
		this.getMainFrame().getView(TableView.class).rowDeleted();
	}
	
	/**
	 * Vytvoření vyhledávacího filtru
	 * 
	 * @param grade
	 * @param subject
	 * @param search
	 */
	public void filterTable(String gradeSel, String subjectSel, String searchSel) {
		
		// příprava kritérií
		Integer grade = (gradeSel.length() > 1) ? 0 : Integer.parseInt(gradeSel);
		String subject = (subjectSel.length() == 4) ? subjectSel : "";
		String search = searchSel;//.toLowerCase();
		
		// žáden filter
		if (grade == 0 && subject.length() == 0 && search.length() == 0) {
			getTableFilter().setRowFilter(null);
		} else {
			
			//RowFilter<? super ProjectTableModel<DLM>, ? super Integer> filter = RowFilter.regexFilter(search);
			
			if (grade != 0) {

			}
			
			System.out.println("G: " + grade + " S: " + subject + " Exp: " + search);
			// aplikace filtru
			//getTableFilter().setRowFilter(filter);
		
			//System.out.println(getTableFilter() + " - " + filter);
		}
		
		// aktualizace Tabulky
		getMainFrame().getView(TableView.class).applyFilter();
	}
	
	// --- test
	
	public String test() {
		return "<Manifestace ducha Controlleru Tabulky ve hmotě>";
	}

}
