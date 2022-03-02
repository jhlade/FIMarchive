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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cz.uhk.pro2.dat.IConstantData;
import cz.uhk.pro2.model.DLM;
import cz.uhk.pro2.mvc.AbstractController;
import cz.uhk.pro2.mvc.AbstractFrame;

/**
 * Controller detailního pohledu.
 * 
 * @author Jan Hladěna
 *
 */
public class DetailController extends AbstractController implements IConstantData {

	/** Konkrétní materiál */
	private DLM element;
	
	/** Flag pro vložení */
	private boolean insertNeeded = false;
	
	/** Chybová hlášení */
	private ArrayList<String> invalidMessages = new ArrayList<String>();
	
	/**
	 * Konstruktor
	 * 
	 * @param mainFrame
	 */
	public DetailController(AbstractFrame mainFrame) {
		super(mainFrame);
	}
	
	/**
	 * Inicializace konkrétního Digitálního Učebního Materiálu
	 * 
	 * @param singleDLM
	 */
	public void initDLM(int rowModel) {
		
		this.clearVars();
		
		if (rowModel != -1) {
			// otevření materiálu podle řádku
			this.element = getMainFrame().getController(TableController.class).getTableModel().getPMO(rowModel);
			
			getMainFrame().getView(DetailView.class).fillVars(element.getAnnotation(),
					element.getIdentifier(), element.getName(), element.getSubject(), ((Integer)element.getGrade()).toString(),
					element.getAuthor(), element.getDateString(), element.getUrl());
			
		} else {
			// vyčištění formuláře
			getMainFrame().getView(DetailView.class).clearVars();
			
			this.insertNeeded = true;
			// nový prázdný DUM
			this.element = new DLM();
		}
		
	}

	/**
	 * Vyčištění proměnných
	 */
	public void clearVars() {
		this.insertNeeded = false;
		this.element = null;
		this.invalidMessages.clear();
	}
	
	/**
	 * Uloží provedené změny nebo ohlásí chyby
	 * 
	 * @return
	 */
	public boolean saveChanges() {
		if (validateInput()) {
			
				this.element.setAnnotation(getMainFrame().getView(DetailView.class).getInputAnnotation());
				this.element.setAuthor(getMainFrame().getView(DetailView.class).getInputAuthor());
				try {
					this.element.setDate(getMainFrame().getView(DetailView.class).getInputDate());
				} catch (ParseException e) {
					// nemělo by nastat :)
					System.err.println(e.getLocalizedMessage());
				}
				this.element.setGrade(Integer.parseInt((getMainFrame().getView(DetailView.class).getInputGrade())));
				this.element.setIdentifier(getMainFrame().getView(DetailView.class).getInputIdentifier());
				this.element.setName(getMainFrame().getView(DetailView.class).getInputName());
				this.element.setSubject(getMainFrame().getView(DetailView.class).getInputSubject().toUpperCase());
				this.element.setUrl(getMainFrame().getView(DetailView.class).getInputURL());
				
			if (insertNeeded) {
				getMainFrame().getController(TableController.class).getTableModel().add(this.element);
			} else {
				// asi nic?
			}
			
			return true;
		} else {
			getMainFrame().getView(DetailView.class).invalidMessage(this.invalidMessages);
			return false;
		}
	}
	
	/**
	 * Validace vstupních dat
	 */
	@SuppressWarnings("unused")
	public boolean validateInput() {

		// vyčištění předchozích chyb
		this.invalidMessages.clear();
		
		if (getMainFrame().getView(DetailView.class).getInputIdentifier().length() < 1) {
			invalidMessages.add("není vyplněn identifikátor materiálu");
		}
			
		if (getMainFrame().getView(DetailView.class).getInputAuthor().length() < 1) {
			invalidMessages.add("není uveden autor materiálu");
		}
		
		if (!Arrays.asList(IConstantData.SUBJECTS).contains(getMainFrame().getView(DetailView.class).getInputSubject().toUpperCase())) {
			invalidMessages.add("zkratka předmětu není platná");
		}
		
		if (!Arrays.asList(IConstantData.GRADES).contains(getMainFrame().getView(DetailView.class).getInputGrade())) {
			invalidMessages.add("zadaný ročník není platný");
		}
		
		if (getMainFrame().getView(DetailView.class).getInputName().length() < 1) {
			invalidMessages.add("není uveden název materiálu");
		}
		
		try {
			Date dateFormatTest = new SimpleDateFormat("dd.MM.yyyy").parse(getMainFrame().getView(DetailView.class).getInputDate());
		} catch (ParseException e) {
			// výjimka se zahodí a místo toho vyskočí oznámení
			invalidMessages.add("datum nemá správný formát (DD.MM.RRRR)");
		}
			
	    return (invalidMessages.size() > 0) ? false : true;
	}

}
