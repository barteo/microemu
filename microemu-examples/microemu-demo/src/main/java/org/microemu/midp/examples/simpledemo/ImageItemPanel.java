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
package org.microemu.midp.examples.simpledemo;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

public class ImageItemPanel extends BaseExamplesForm {

	public ImageItemPanel() {
		super("ImageItem");

		try {
			Image image = Image.createImage("/org/microemu/midp/examples/simpledemo/image.png");
			append(new ImageItem("Default Layout", image, ImageItem.LAYOUT_DEFAULT, null));
			append(new ImageItem("Left Layout", image, ImageItem.LAYOUT_LEFT, null));
			append(new ImageItem("Center Layout", image, ImageItem.LAYOUT_CENTER, null));
			append(new ImageItem("Right Layout", image, ImageItem.LAYOUT_RIGHT, null));
		} catch (IOException ex) {
			append("Cannot load images");
		}

	}

}
