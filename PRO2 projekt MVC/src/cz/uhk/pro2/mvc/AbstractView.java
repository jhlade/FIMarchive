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

import javax.swing.JComponent;

/**
 * Forma obecného Pohledu, konkrétní implementace může zahrnovat jakoukoliv Swingovou komponentu dědenou od JComponent,
 * od jednoduchého JPanelu po obecnější JInternalFrame při práci v MDI prostředí.
 * 
 * Tento Pohled v konstruktoru vyžaduje ukazatel na nadřízené okno, ve kterém se nachází. Následně poskytuje přístup
 * k tomuto nadřízenému oknu (getMainFrame()) a též poskytuje přístup sám k sobě (getContentPane()).
 * 
 * @author Jan Hladěna
 *
 * @param ViewClass subtřída pohledu
 */
public abstract class AbstractView<ViewClass extends JComponent>  {
	
	/** Hlavní nadřízené okno Pohledu */
	private final AbstractFrame mainFrame;
	/** Držadlo - ukazatel na tento Pohled */
	private final ViewClass contentPane;
	
	/**
	 * Konstruktor obecného Pohledu. Jako parametr si bere ukazatel na instanci nadřízeného hlavního okna.
	 * 
	 * @param mainFrame Hlavní nadřízené okno
	 */
	public AbstractView(AbstractFrame mainFrame) {		
        this.mainFrame = mainFrame;
        this.contentPane = layout();
    }
	
	/**
	 * Metoda pro sebevykreslení pomocí továrny, vrací potomka ViewClass, obecného potomka JComponent.
	 * 
	 * @return ViewClass
	 */
	protected abstract ViewClass layout();
	
	/**
	 * Metoda pro získání instance hlavního okna.
	 * 
	 * @return Hlavní nadřízené okno
	 */
	protected AbstractFrame getMainFrame() {
        return mainFrame;
    }
	
	/**
	 * Metoda pro získání instance konkrétního Pohledu samotného.
	 * 
	 * @return Ukazatel na tento pohled (ViewClass - nějaký potomek JComponent)
	 */
	public ViewClass getContentPane() {
		return contentPane;
	}
	
}
