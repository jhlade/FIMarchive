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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import cz.uhk.pro2.mvc.AbstractFrame;
import cz.uhk.pro2.mvc.AbstractView;
import static cz.uhk.pro2.mvc.JCFactory.panel;
import static cz.uhk.pro2.mvc.JCFactory.button;
import static cz.uhk.pro2.mvc.JCFactory.label;

/**
 * Detailní pohled.
 * 
 * @author Jan Hladěna
 *
 */
public class DetailView extends AbstractView<JPanel> {

	private JTextField inputIdentifier;
	private JTextField inputAuthor;
	private JTextField inputName;
	private JTextField inputDate;
	private JTextField inputURL;
	private JTextField inputSubject;
	private JTextField inputGrade;
	
	private JTextArea inputAnnotation;
	
	/**
	 * Konstruktor
	 * 
	 * @param mainFrame
	 */
	public DetailView(AbstractFrame mainFrame) {
		super(mainFrame);
	}

	/**
	 * Implementace rozložení Detailu
	 */
	@Override
	protected JPanel layout() {
		JPanel detailPanel = panel(new BorderLayout());
		
		// panel tlačítek
		JPanel buttonBar = panel(null);
		buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.X_AXIS));
		
		// tlačítko Zavřít
		JButton buttonClose = button("Zavřít", new Close());
		
		// tlačítko Uložit
		JButton buttonSave = button("Uložit", new Save());
		
		// tlačítka na liště tlačítek
		buttonBar.add(buttonClose);
		buttonBar.add(buttonSave);
		
		
		// organizační struktura
		JPanel detailContainer = panel(new GridLayout(0, 2));
		JPanel detailLeft = panel(new GridLayout(0, 1));
		JPanel detailRight = panel(new BorderLayout());
		
		/*** inicializace vstupů **/
		
		inputAnnotation = new JTextArea();
		inputAnnotation.setBorder((Border) new BevelBorder(BevelBorder.LOWERED));
		inputAnnotation.setLineWrap(true);
		
		inputIdentifier = new JTextField();
		inputName = new JTextField();
		inputSubject = new JTextField();
		inputGrade = new JTextField();
		inputAuthor = new JTextField();
		inputDate = new JTextField();
		inputURL = new JTextField();
		/*** ***/
		
		// vlevo
		JLabel labelIdentifier = label("Identifikátor:");
		detailLeft.add(labelIdentifier);
		detailLeft.add(inputIdentifier);
		
		JLabel labelName = label("Název:");
		detailLeft.add(labelName);
		detailLeft.add(inputName);
		
		JLabel labelSubject = label("Předmět:");
		detailLeft.add(labelSubject);
		detailLeft.add(inputSubject);
		
		JLabel labelGrade = label("Ročník:");
		detailLeft.add(labelGrade);
		detailLeft.add(inputGrade);
		
		JLabel labelAuthor = label("Autor:");
		detailLeft.add(labelAuthor);
		detailLeft.add(inputAuthor);
		
		JLabel labelDate = label("Datum zařazení:");
		detailLeft.add(labelDate);
		detailLeft.add(inputDate);
		
		JLabel labelURL = label("URL:");
		detailLeft.add(labelURL);
		detailLeft.add(inputURL);
		
		// vpravo
		JLabel labelAnnotation = label("Anotace:");
		detailRight.add(labelAnnotation, BorderLayout.NORTH);
		detailRight.add(inputAnnotation, BorderLayout.CENTER);
		
		// rozložení na obsah
		detailContainer.add(detailLeft);
		detailContainer.add(detailRight);
		
		// obsah uprostřed
		detailPanel.add(detailContainer, BorderLayout.CENTER);
		// lišta tlačítek dole
		detailPanel.add(buttonBar, BorderLayout.SOUTH);
		
		// hotovo
		return detailPanel;
	}
	
	/**
	 * 
	 */
	protected void clearVars() {

		inputAnnotation.setText("");
		inputIdentifier.setText("");
		inputName.setText("");
		inputSubject.setText("");
		inputGrade.setText("");
		inputAuthor.setText("");
		inputDate.setText("");
		inputURL.setText("");
		
		getMainFrame().getController(DetailController.class).clearVars();
	}
	
	public void fillVars(String annotation, String identifier, String name, String subject, String grade, String author, String date, String url) {
		
		if (annotation != null) inputAnnotation.setText(annotation);
		if (identifier != null) inputIdentifier.setText(identifier);
		if (name != null) inputName.setText(name);
		if (subject != null) inputSubject.setText(subject);
		if (grade != null) inputGrade.setText(grade);
		if (author != null) inputAuthor.setText(author);
		if (date != null) inputDate.setText(date);
		if (url != null) inputURL.setText(url);
	}
	
	
	// --- požadavky
	
	// Zavřít bez uložení změn
	private class Close implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// zrušit všechno
			clearVars();
			getMainFrame().getController(DetailController.class).clearVars();
			
			// přepnout zpět na Tabulku
			getMainFrame().setContent(getMainFrame().getView(TableView.class).getContentPane());
		}
		
	}
	
	// Uložit změny/vytvořit nový
	private class Save implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (getMainFrame().getController(DetailController.class).saveChanges()) {
				// přepnout zpět na Tabulku
				getMainFrame().setContent(getMainFrame().getView(TableView.class).getContentPane());
			}
		}
		
	}
	
	
	// --- odpovědi
	
	// gettery textových polí
	public String getInputAnnotation() { return inputAnnotation.getText(); }
	public String getInputIdentifier() { return inputIdentifier.getText(); }
	public String getInputName() { return inputName.getText(); }
	public String getInputSubject() { return inputSubject.getText(); }
	public String getInputGrade() { return inputGrade.getText(); }
	public String getInputAuthor() { return inputAuthor.getText(); }
	public String getInputDate() { return inputDate.getText(); }
	public String getInputURL() { return inputURL.getText(); }
	
	/**
	 * Zobrazí dialog s informacemi o chybách
	 * 
	 * @param messages
	 */
	public void invalidMessage(ArrayList<String> messages) {
		String formatMessage = "";
		
		for (String errorString : messages) {
			formatMessage += "\n- " + errorString;
		}
		
		JOptionPane.showMessageDialog(getMainFrame().getContent(), "Některé informace nebyly správně vyplněny\n" + formatMessage);
	}

}
