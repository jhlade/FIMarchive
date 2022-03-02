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
package cz.uhk.pro2.app;

import javax.swing.SwingUtilities;

import cz.uhk.pro2.gui.MainWindowFrame;

/**
 * Spouštěcí třída aplikace. V samostatném vlákně vytvoří <b>hlavní okno</b>.
 * 
 * @author Jan Hladěna
 *
 */
public class DigitalLibraryApp {
	
	/** Titulek hlavního okna aplikace. */
	//public static final String mainTitle = "Digitální Archiv";

	/**
	 * Vstupní bod aplikace. Vytváří nové vlákno pro vykreslení hlavního okna aplikace s titulkem
	 * určeným v konstantě <em>mainTitle</em>. Velikost okna po spuštění je nastavena na 980x650 pixelů,
	 * což dává základní prostor pro manipulaci s daty, zbytek je vyhrazen pro logo ESF.
	 * 
	 * @param args Argumenty spuštění - nepoužívají se.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new MainWindowFrame().show();
				/*
				JFrame window = new MainWindowView(mainTitle);
				window.setSize(980, 650);
				window.setVisible(true);
				*/
			}
			
		});

	}

}
