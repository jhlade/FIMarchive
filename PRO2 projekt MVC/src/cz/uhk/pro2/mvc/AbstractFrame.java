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

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import static cz.uhk.pro2.mvc.JCFactory.showFrame;

/**
 * Abstraktní předek obecného okna, využitelný jako hlavní okno v SDI/MDI. Vyžaduje implementace metod pro registraci Pohledů
 * a jejich Controllerů, které následně schraňuje v mapách, aby k nim byl zajištěn co nejsnazší přístup.
 * 
 * @author Jan Hladěna
 *
 */
public abstract class AbstractFrame {
	
	/** Okno samo o sobě */
	protected final JFrame frame;
	
	/** Hlavní pohled */
	protected JComponent cont;

	// Mapy všech Pohledů a Controllerů - v rámci okna jsou to všudepřístupné singletony
	
	/** Mapa View - views */
    protected final Map<Class<? extends AbstractView<? extends JComponent>>, AbstractView<? extends JComponent>> views = new HashMap<Class<? extends AbstractView<? extends JComponent>>, AbstractView<? extends JComponent>>();
    
    /** Mapa Controllerů - controllers */
    protected final Map<Class<? extends AbstractController>, AbstractController> controllers = new HashMap<Class<? extends AbstractController>, AbstractController>();

	
	/**
	 * Konstruktor abstraktního okna. Má za úkol zavolat konkrétní registrace všech svých Pohledů a Controllerů
	 * a následně se okno samotné vykreslit.
	 */
	public AbstractFrame() {
		regControllers();
		regViews();
        
        // nastavení prvků okna
        this.frame = layout();
    }
	
	/**
	 * Zobrazení tohoto JFrame.
	 */
	public void show() {
        showFrame(this.frame);
    }

	/**
	 * Registrace použitých View - pohledů.
	 * 
	 * Metoda má za úkol umístit do mapy Pohledů páry tříd a instancí konkrétních Pohledů.
	 * Implementace této metody může vypadat například jako sled
	 *     views.put(TableView.class, new TableView(this));
	 * 
	 */
    protected abstract void regViews();

    /**
     * Registrace použitých Controllerů - řadičů.
     * 
     * Metoda má za úkol umístit do mapy Controllerů páry tříd a instancí konkrétních Controllerů.
     * Stejně jako v případě registrace Pohledů může implementace vypadat jako
     *     controllers.put(TableController.class, new TableController(this));
     * 
     */
    protected abstract void regControllers();

    /**
     * Metoda pro sebevykreslení a návrat vlastní instance. Měla zahrnovat inicializaci všech komponent použitých v okně.
     * 
     * @return Rámec sám o sobě
     */
    protected abstract JFrame layout();
    
    /**
     * Získání konkrétního View z mapy všech zaregistrovaných na základě názvu třídy.
     * 
     * @param viewClass
     * @return Instance požadovaného Pohledu
     */
    @SuppressWarnings("unchecked")
	public <View extends AbstractView<? extends JComponent>> View getView(Class<View> viewClass) {
        return (View) views.get(viewClass);
    }
    
    /**
     * Získání konkrétního Controlleru z mapy všech zaregistrovaných na základě názvu třídy.
     * 
     * @param controllerClass
     * @return Instance požadovaného Controlleru
     */
    @SuppressWarnings("unchecked")
    public <Controller extends AbstractController> Controller getController(Class<Controller> controllerClass) {
        return (Controller) controllers.get(controllerClass);
    }
    
    /**
     * Získání hlavního obsahu
     * 
     * @return
     */
    public abstract JComponent getContent();
    
    /**
     * Nastavení a překreslení hlavního obsahu
     */
    public abstract void setContent(JComponent content);

}
