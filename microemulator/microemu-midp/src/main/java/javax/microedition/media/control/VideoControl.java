/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package javax.microedition.media.control;

import javax.microedition.media.MediaException;

public interface VideoControl extends GUIControl {

	public static final int USE_DIRECT_VIDEO = 1;

	public abstract Object initDisplayMode(int i, Object obj);

	public abstract void setDisplayLocation(int i, int j);

	public abstract int getDisplayX();

	public abstract int getDisplayY();

	public abstract void setVisible(boolean flag);

	public abstract void setDisplaySize(int i, int j) throws MediaException;

	public abstract void setDisplayFullScreen(boolean flag) throws MediaException;

	public abstract int getSourceWidth();

	public abstract int getSourceHeight();

	public abstract int getDisplayWidth();

	public abstract int getDisplayHeight();

	public abstract byte[] getSnapshot(String s) throws MediaException;

}
