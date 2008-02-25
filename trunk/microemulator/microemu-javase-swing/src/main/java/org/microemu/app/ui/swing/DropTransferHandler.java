/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
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
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.microemu.app.Common;
import org.microemu.app.ui.Message;
import org.microemu.app.util.IOUtils;
import org.microemu.log.Logger;

/**
 * @author vlads
 *
 */
public class DropTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	private static DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
	
	private static boolean debugImport = false;
	
	public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

	public boolean canImport(JComponent comp, DataFlavor transferFlavors[]) {
		for (int i = 0; i < transferFlavors.length; i++) {
			Class representationclass = transferFlavors[i].getRepresentationClass();
			// URL from Explorer or Firefox, KDE
        	if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
        		if (debugImport) {
        			Logger.debug("acepted ", transferFlavors[i]);
        		}
        		return true;
        	}
        	// Drop from Windows Explorer
        	if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
        		if (debugImport) {
        			Logger.debug("acepted ", transferFlavors[i]);
        		}
        		return true;
            }
        	// Drop from GNOME
            if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
            	if (debugImport) {
        			Logger.debug("acepted ", transferFlavors[i]);
        		}
                return true;
            }
			if (uriListFlavor.equals(transferFlavors[i])) {
				if (debugImport) {
        			Logger.debug("acepted ", transferFlavors[i]);
        		}
				return true;
        	}
//          String mimePrimaryType = transferFlavors[i].getPrimaryType();
//			String mimeSubType = transferFlavors[i].getSubType();
//			if ((mimePrimaryType != null) && (mimeSubType != null)) {
//				if (mimePrimaryType.equals("text") && mimeSubType.equals("uri-list")) {
//					Logger.debug("acepted ", transferFlavors[i]);
//					return true;
//				}
//			}
			if (debugImport) {
				Logger.debug(i + " unknown import ", transferFlavors[i]);
			}
		}
		if (debugImport) {
			Logger.debug("import rejected");
		}
        return false;
	}
	
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] transferFlavors = t.getTransferDataFlavors();
        for (int i = 0; i < transferFlavors.length; i++) {
        	// Drop from Windows Explorer
        	if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
        		Logger.debug("importing", transferFlavors[i]);
        		try {
					List fileList = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
					if (fileList.get(0) instanceof File) {
						File f = (File) fileList.get(0);
						if (Common.isJadExtension(f.getName())) {
							Common.openJadUrlSafe(IOUtils.getCanonicalFileURL(f));
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
        	
        	// Drop from GNOME Firefox
            if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
            	Object data;
				try {
					data = t.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException e) {
					continue;
				} catch (IOException e) {
					continue;
				}
            	if (data instanceof String) {
                	Logger.debug("importing", transferFlavors[i]);
                	String path = getPathString((String) data);
          			if (Common.isJadExtension(path)) {
						Common.openJadUrlSafe(path);
					} else {
						Message.warn("Unable to open " + path + ", Only JAD files are acepted");	
					}
          			return true;
              	}
            }
            // Drop from GNOME Nautilus
            if (uriListFlavor.equals(transferFlavors[i])) {
				Object data;
				try {
					data = t.getTransferData(uriListFlavor);
				} catch (UnsupportedFlavorException e) {
					continue;
				} catch (IOException e) {
					continue;
				}
				if (data instanceof String) {
					Logger.debug("importing", transferFlavors[i]);
					String path = getPathString((String) data);
					if (Common.isJadExtension(path)) {
						Common.openJadUrlSafe(path);
					} else {
						Message.warn("Unable to open " + path + ", Only JAD files are acepted");
					}
					return true;
				}
			}
            if (debugImport) {
            	Logger.debug(i + " unknown importData ", transferFlavors[i]);
            }
        }
        // This is the second best option since it works incorrectly  on Max OS X making url like this [file://localhost/users/work/app.jad]
        for (int i = 0; i < transferFlavors.length; i++) {
        	Class representationclass = transferFlavors[i].getRepresentationClass();
        	// URL from Explorer or Firefox, KDE
        	if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
        		Logger.debug("importing", transferFlavors[i]);
        		try {
					URL jadUrl = (URL)t.getTransferData(transferFlavors[i]);
					String urlString = jadUrl.toExternalForm();
					if (Common.isJadExtension(urlString)) {
						Common.openJadUrlSafe(urlString);
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
        }
		return false;
	}
	
	private String getPathString(String path) {
		if (path == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(path.trim(), "\n\r");
		if (st.hasMoreTokens()) {
			return st.nextToken();
		}
		return path;
	}
}
