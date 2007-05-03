/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Michael Lifshits
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */
package org.microemu.app.ui.swing.logconsole;

/**
 * @author Michael Lifshits
 *
 */
import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.JViewport;

public class LogTextArea extends JTextArea {

	private static final long serialVersionUID = 1L;

	private int maxLines = 20;

	private LogTextCaret caret;

	public LogTextArea(int rows, int columns, int maxLines) {
		super(rows, columns);
		caret = new LogTextCaret();
		setCaret(caret);
		this.maxLines = maxLines;
		setEditable(false);
	}

	public void append(String str) {

		JViewport viewport = (JViewport) getParent();
		boolean scrollToBottom = viewport.getViewPosition().getY() == (getHeight() - viewport.getHeight());
		caret.setVisibilityAdjustment(scrollToBottom);

		super.append(str);

		//		if (getLineCount() > maxLines) {
		//			Document doc = getDocument();
		//			if (doc != null) {
		//				try {
		//					doc.remove(0, getLineStartOffset(getLineCount() - maxLines - 1));
		//				} catch (BadLocationException e) {
		//				}
		//			}
		//			if (!scrollToBottom) {
		//				Rectangle nloc = new Rectangle(0,30,10,10);
		//		        if (SwingUtilities.isEventDispatchThread()) {
		//		        	scrollRectToVisible(nloc);
		//		        } else {
		//		            SwingUtilities.invokeLater(new SafeScroller(nloc));
		//		        }
		//			}
		//		}
	}

	class SafeScroller implements Runnable {

		Rectangle r;

		SafeScroller(Rectangle r) {
			this.r = r;
		}

		public void run() {
			LogTextArea.this.scrollRectToVisible(r);
		}
	}

}