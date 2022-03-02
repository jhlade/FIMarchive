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

import static cz.uhk.pro2.mvc.JCFactory.panel;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import cz.uhk.pro2.mvc.AbstractFrame;
import cz.uhk.pro2.mvc.AbstractView;

/**
 * @author Jan Hladěna
 *
 */
public class StatusBarView extends AbstractView<JPanel> {

	/** Stavový popisek */
	private JLabel statusLabel;
	
	/**
	 * @param mainFrame
	 */
	public StatusBarView(AbstractFrame mainFrame) {
		super(mainFrame);
		showStatus();
	}


	@Override
	protected JPanel layout() {
		JPanel statusBar = panel(null);
		
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		statusBar.setBorder((Border) new BevelBorder(BevelBorder.LOWERED));
		statusBar.setPreferredSize(new Dimension(640, 18));

		JLabel label = new JLabel();
		statusBar.add(label);
		
		this.statusLabel = label;

		return statusBar;
	}
	
	/**
	 * 
	 */
	public void showStatus() {
		statusLabel.setText(getMainFrame().getController(StatusBarController.class).getStatusMessage());
		statusLabel.repaint();
	}

}
