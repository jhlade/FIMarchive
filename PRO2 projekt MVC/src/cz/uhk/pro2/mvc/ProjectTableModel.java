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
package cz.uhk.pro2.mvc;

import java.lang.reflect.Method;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

/**
 * Obecný projektový model založený na Swing DefaultTableModel. Pracuje s modelem typu běžného javovského objektu,
 * atributy použitelné jako sloupce tabulky se v konstruktoru předávají v poli v požadovaném určitém pořadi. Každý
 * sloupcnový atribut musí mít definovaný svůj getter a setter, aby bylo možné s objektem pohodlně pracovat.
 * 
 * @author Jan Hladěna
 *
 */
public class ProjectTableModel<ProjectModelObject> extends DefaultTableModel {

	/** serial ID */
	private static final long serialVersionUID = -9099297755942922083L;
	
	/** základní seznam objektů */
	private List<ProjectModelObject> objectList = new ArrayList<ProjectModelObject>();
	
	/** atributy třídy PMO */
	private final String[] attNames;
	
	/** dostupné gettery/settery konkrétního PMO */
	private final Map<String, Method> getMethods = new HashMap<String, Method>();
	private final Map<String, Method> setMethods = new HashMap<String, Method>();
	
	/**
	 * Konstruktor obecného projektovémo modelu vycházející z výchozího tabulkového modelu Swingu.
	 * 
	 * @param pmoClass Třída modelu
	 * @param attNames Seznam atributů v tabulkově modelovaném pořadí
	 */
	public ProjectTableModel(Class<ProjectModelObject> pmoClass, String[] attNames) {
		// nastavení seznamu atributů třídy PMO
		this.attNames = attNames;
		
		// prozkoumání dostupných metod třídy PMO
		for (Method method : pmoClass.getMethods()) {
			
			// nalezení a registrace getterů
			for (String attName : attNames) {
				if (method.getName().equals("get" + attName)) {
					getMethods.put(attName, method);
					break;
				}
			}
			
			// nalezení a registrace setterů
			for (String attName : attNames) {
				if (method.getName().equals("set" + attName)) {
					setMethods.put(attName, method);
					break;
				}
			}
			
		}
		
		// Modelovaná třída PMO <b>musí</b> zahrnovat gettery a settery pro všechny svoje vtbrané atributy/sloupce!
		if (attNames.length != getMethods.size()) throw new IllegalArgumentException("Nesouhlasí počet getterů");
		if (attNames.length != setMethods.size()) throw new IllegalArgumentException("Nesouhlasí počet setterů");
	}
	
	/**
	 * 
	 * @param data
	 */
	public void loadData(List<ProjectModelObject> data) {
		this.objectList = data;
	}
	
	/**
	 * Počet atributů třídy PMO
	 * 
	 * @return int počet atributů
	 */
	public int getPropCount() {
		return attNames.length;
	}
	
	/**
	 * Název atributu na daném svislém/sloupcovém indexu
	 * 
	 * @param colIndex 
	 * @return String název atributu
	 */
	public String getPropName(int colIndex) {
		return attNames[colIndex];
	}
	
	/**
	 * Sebereflexní zjištění třídy atributu na daném sloupcovém indexu
	 * 
	 * @param colIndex sloupcový index
	 * @return třída
	 */
	public Class<?> getPMOPropertyClass(int colIndex) {
		Class<?> propertyClass = getMethods.get(attNames[colIndex]).getReturnType();
		
		// datum jako String, jinak původní třídu
		return (Date.class.equals(propertyClass)) ? String.class : propertyClass;
	}
	

	/**
	 * Zjištění hodnoty v tabulce na pozici XY (počítáno od nuly - indexy). 
	 * 
	 * @param rowIndex
	 * @param colIndex
	 * @return Hodnota
	 */
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		// obecný typ
		Object value = null;
		
		try {
			value = getMethods.get(attNames[colIndex]).invoke(objectList.get(rowIndex), new Object[] {});
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		// převod hodnoty typu Date na SimpleDateFormat
		if (value != null && (value instanceof Date)) return new SimpleDateFormat("dd.MM. yyyy").format((Date) value);
		
		return value;
	}
	
	@Override
	public void removeRow(int row) {
		objectList.remove(row);
	}
	
	/**
	 * Zjištění počtu objektů/řádků
	 * 
	 * @return int Počet objektů
	 */
	@Override
	public int getRowCount() {
		return (objectList != null) ? objectList.size() : 0;
	}
	
	/**
	 * Zjištění počtu sloupců/atributů
	 * 
	 * @return int Počet sloupců
	 */
	@Override
	public int getColumnCount() {
		return attNames.length;
	}
	
	/**
	 * Editovatelnost buňky, toto se nepoužívá
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	/**
	 * 
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	
    }
	

	/**
	 * Objekt na daném indexu
	 * 
	 * @param index
	 * @return Objekt
	 */
	public ProjectModelObject getPMO(int index) {
		return (index >= 0) ? objectList.get(index) : null;
	}
	
	/**
	 * @param index
	 * @param pmo
	 */
	public void setPMO(int index, ProjectModelObject pmo) {
		objectList.set(index, pmo);
		fireTableRowsUpdated(index, index);
	}
	
	/**
	 * 
	 * @param pmo Nový objekt
	 */
	public void add(ProjectModelObject pmo) {
		// nový záznam
		int row = getRowCount();
		// přidání
		objectList.add(row, pmo);
		// aktualizace
		fireTableRowsInserted(row, row);
	}
	
	/**
	 * Odstranění objektu na zadaném indexu
	 * 
	 * @param index
	 */
	public void remove(int index) {
		// dolní mez
		if (index >= 0) {
			// aktualizace
			fireTableRowsDeleted(index, index);
		}
	}

}
