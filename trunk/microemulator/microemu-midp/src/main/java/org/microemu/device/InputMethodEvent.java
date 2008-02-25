/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.device;


public class InputMethodEvent
{

	public static final int CARET_POSITION_CHANGED = 1;
	public static final int INPUT_METHOD_TEXT_CHANGED = 2;
	
	int type;
	int caret;
	String text;


	public InputMethodEvent(int type, int caret, String text)
	{
		this.type = type;
		this.caret = caret;
		this.text = text;
	}


	public int getCaret()
	{
		return caret;
	}


	public String getText()
	{
		return text;
	}

}
