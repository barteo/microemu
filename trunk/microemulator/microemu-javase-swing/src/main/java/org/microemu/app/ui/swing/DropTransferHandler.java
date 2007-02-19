/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.app.ui.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.microemu.app.Common;
import org.microemu.app.ui.Message;
import org.microemu.log.Logger;

/**
 * @author vlads
 *
 */
public class DropTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

	public boolean canImport(JComponent comp, DataFlavor transferFlavors[]) {
		for (int i = 0; i < transferFlavors.length; i++) {
			Class representationclass = transferFlavors[i].getRepresentationClass();
			// URL from Explorer or Firefox, KDE
        	if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
        		return true;
        	}
        	// Drop from Windows Explorer
        	if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
                return true;
            }
        	// Drop from GNOME
            if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
                return true;
            }
//			String mimePrimaryType = transferFlavors[i].getPrimaryType();
//			String mimeSubType = transferFlavors[i].getSubType();
//			Logger.debug(i + " canImport mimeType ", mimePrimaryType, mimeSubType);
//			if ((mimePrimaryType != null) && (mimeSubType != null)) {
//				if (mimePrimaryType.equals("application") && mimeSubType.equals("x-java-url")) {
//					return true;
//				}
//			}
			Logger.debug(i + " canImport ", transferFlavors[i]);
		}
        return false;
	}
	
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] transferFlavors = t.getTransferDataFlavors();
        for (int i = 0; i < transferFlavors.length; i++) {
        	Class representationclass = transferFlavors[i].getRepresentationClass();
        	// URL from Explorer or Firefox, KDE
        	if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
        		try {
					URL jadUrl = (URL)t.getTransferData(transferFlavors[i]);
					String urlString = jadUrl.toExternalForm();
					if (Common.isJadExtension(urlString)) {
						Common.openJadUrl(urlString);
					} else {
						Message.warn("Unable to open " + urlString + ", Only JAD url are acepted");	
					}
				} catch (UnsupportedFlavorException e) {
					Logger.debug(e);
				} catch (IOException e) {
					Logger.debug(e);
				}
				return true;
        	}
        	// Drop from Windows Explorer
        	if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
        		try {
					List fileList = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
					if (fileList.get(0) instanceof File) {
						File f = (File) fileList.get(0);
						if (Common.isJadExtension(f.getName())) {
							Common.openJadUrl(f.toURL().toExternalForm());
						} else {
							Message.warn("Unable to open " + f.getAbsolutePath() + ", Only JAD files are acepted");	
						}
					} else {
						Logger.debug("Unknown object in list ", fileList.get(0));	
					}
				} catch (UnsupportedFlavorException e) {
					Logger.debug(e);
				} catch (IOException e) {
					Logger.debug(e);
				}
        		return true;
            }

        	Logger.debug(i + " importData ", transferFlavors[i]);
        }
		return false;
	}
}
