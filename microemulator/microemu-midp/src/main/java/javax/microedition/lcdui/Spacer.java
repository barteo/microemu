/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package javax.microedition.lcdui;

public class Spacer extends Item {
	
	public Spacer(int minWidth, int minHeight) {
		super(null);
		setMinimumSize(minWidth, minHeight);
	}

	public void setLabel(String label) {
		throw new IllegalStateException("Spacer items can't have labels");
	}

	public void addCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setDefaultCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setMinimumSize(int minWidth, int minHeight) {
		if (minWidth < 0 || minHeight < 0)
			throw new IllegalArgumentException();
		
		  // TODO implement
	}
	
	// Item methods
	int paint(Graphics g) {
		return 0;
	}

}
