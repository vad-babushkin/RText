/*
 * 11/14/2003
 *
 * RTextEditorPane.java - The text editor used by RText.
 * Copyright (C) 2003 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://rtext.fifesoft.com
 *
 * This file is a part of RText.
 *
 * RText is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * RText is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.fife.rtext;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.IOException;
import javax.swing.JComponent;

import org.fife.print.RPrintUtilities;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTATextTransferHandler;


/**
 * An extension of {@link TextEditorPane} that adds RText-specific features.
 *
 * @author Robert Futrell
 * @version 1.2
 */
public class RTextEditorPane extends TextEditorPane implements Printable {

	private RText rtext;


	/**
	 * Creates a new <code>RTextEditorPane</code>.  Syntax highlighting will
	 * be selected as follows:  filenames ending in <code>".java"</code>
	 * default to Java syntax highlighting; all others default to no syntax
	 * highlighting.
	 *
	 * @param rtext The owning RText instance.
	 * @param wordWrapEnabled Whether or not to use word wrap in this pane.
	 * @param textMode Either <code>INSERT_MODE</code> or
	 *        <code>OVERWRITE_MODE</code>.
	 * @param loc The location of the file to open.
	 * @param encoding The encoding of the file.
	 * @throws IOException If an IO error occurs reading the file to load.
	 */
	public RTextEditorPane(RText rtext, boolean wordWrapEnabled,
		int textMode, FileLocation loc, String encoding) throws IOException {
		super(textMode, wordWrapEnabled, loc, encoding);
		this.rtext = rtext;
		// Change the transfer handler to one that recognizes drag-and-dropped
		// files as needing to be opened in the parent main view.
		setTransferHandler(new RTextEditorPaneTransferHandler());
	}


	/**
	 * Method called when it's time to print this badboy (the old-school, AWT
	 * way).  This method overrides <code>RTextArea</code>'s <code>print</code>
	 * method so that we can use the font specified in RText when printing.
	 *
	 * @param g The context into which the page is drawn.
	 * @param pageFormat The size and orientation of the page being drawn.
	 * @param pageIndex The zero based index of the page to be drawn.
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		Font printWithMeFont = rtext.getMainView().getPrintFont();
		if (printWithMeFont==null)	// null => print with the current font.
			printWithMeFont = this.getFont();
		return RPrintUtilities.printDocumentWordWrap(g, this,
				printWithMeFont, pageIndex, pageFormat, this.getTabSize());
	}


	/**
	 * Transfer handler for editor panes.  Overrides the default transfer
	 * handler so we can drag-and-drop files into a text area, and know to
	 * open it in the parent main view.
	 */
	class RTextEditorPaneTransferHandler extends RTATextTransferHandler {

		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			return MainPanelTransferHandler.hasFileFlavor(flavors) ||
					super.canImport(c, flavors);
		}

		public boolean importData(JComponent c, Transferable t) {
			return MainPanelTransferHandler.
				importDataImpl(rtext.getMainView(), c, t) ||
						super.importData(c, t);
		}

	}


}