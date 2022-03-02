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

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


/**
 * "Java Component Factory"
 * Soubor statických továren (Factory pattern - kdo nahlas říká slovo "továrnička", nejspíš by zasloužil lopatou nebo cepem do palice)
 * na konkrétní komponenty. Slouží především ke zjednodušení práce s tvobrou GUI, ke zpřehlednění zbytečně zdlouhavého Swingového
 * kódu pro vykreslování a nastavování jednotlivých komponent.
 * 
 * 
 * @author Jan Hladěna
 *
 */
public abstract class JCFactory {

	/**
	 * Továrna na JFrame - budiž tím zamýšleno hlavní okno obsahující jiné pohledy. Šlo by využít v MDI ke tvorbě podoken.
	 * 
	 * @return JFrame
	 */
	public static JFrame frame(String title, JComponent contentPane) {
		// základní JFrame
		JFrame frame = new JFrame();
		// automaticky vypínat aplikaci po zavření
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// nastavení titulku okna
		if (title != null) {
			frame.setTitle(title);
		}
		
		// hlavní kontejner pohledu
		if (contentPane != null) {
			frame.getContentPane().add(contentPane, BorderLayout.CENTER);
		}
		
		// velikost okna 980x650
		frame.setSize(980, 650);
		
		return frame;
	}
	
	/**
	 * Zobrazení předaného JFrame
	 * 
	 * @param frame JFrame k zobrazení
	 * @return JFrame
	 */
	public static JFrame showFrame(JFrame frame) {
		// zviditelnění
		frame.setVisible(true);
		return frame;
	}
	
	
	/**
	 * Továrna na JPanel
	 * 
	 * @param layoutManager Použitý správce rozložení
	 * @return JPanel 
	 */
	public static JPanel panel(LayoutManager layoutManager) {
		JPanel panel = new JPanel();
		
		if (layoutManager != null) {
			panel.setLayout(layoutManager);
		}
		
		return panel;
	}
	
	/**
	 * Továrna na tlačítko (JButton). Přebírá textový popisek tlačítka a rovnou mapuje jeho actionListener, číhač na událost.
	 * Tento Listener může být předán i anonymní vnitřní třídou, nebo může být nulový - v takovém případě nebude vůbec
	 * použit a tlačítko nejspíš nebude dělat vůbec nic.
	 * 
	 * @param caption Popisek tlačítka
	 * @param actionListener Číhač na událost
	 * @return JButton
	 */
	public static JButton button(String caption, ActionListener actionListener) {
		JButton button = new JButton();
		
		// popisek tlačítka - není-li žádný, nenastaví se (a předejde se případné null-pointer výjimce)
		if (caption != null) {
			button.setText(caption);
		}
		
		// nasazení actionListeneru
		if (actionListener != null) {
			button.addActionListener(actionListener);
		}
		
		// hotovo
		return button;
	}
	
	/**
	 * Továrna na položku nabídky. Přebírá ukazatel na nabídku, ve které má být umístěna, titulek, volitelnou bublinkovou
	 * nápovědu a volitelný Listener.
	 * 
	 * @param menu Nabídka, na které má být položka zavěšena
	 * @param caption Titulek
	 * @param tooltip Bublinková nápověda
	 * @param actionListener Číhač na událost
	 * @return JMenuItem
	 */
	public static JMenuItem menuItem(JMenu menu, String caption, String tooltip, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(caption);
		
		// bublinková nápověda
		if (tooltip != null) menuItem.setToolTipText(tooltip);
		
		// action listener
		if (actionListener != null) menuItem.addActionListener(actionListener);
		
		// zavěšení na menu
		menu.add(menuItem);
		
		// vrácení objektu
		return menuItem;
	}
	
	/**
	 * Továrna na popisek
	 * 
	 * @param caption
	 * @return JLabel
	 */
	public static JLabel label(String caption) {
		JLabel label = new JLabel();
		
		// popisek
		if (caption != null) label.setText(caption);
		
		return label;
	}
	
	@SuppressWarnings("rawtypes")
	public static JComboBox comboBox(String[] values) {
		@SuppressWarnings({ "unchecked" })
		JComboBox comboBox = new JComboBox(values);
		
		return comboBox;
	}
	
	
}
