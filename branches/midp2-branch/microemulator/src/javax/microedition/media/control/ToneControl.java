/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package javax.microedition.media.control;

import javax.microedition.media.Control;


public interface ToneControl extends Control 
{
	public static final byte SILENCE = -1;
	public static final byte VERSION = -2;
	public static final byte TEMPO = -3;
	public static final byte RESOLUTION = -4;
	public static final byte BLOCK_START = -5;
	public static final byte BLOCK_END = -6;
	public static final byte PLAY_BLOCK = -7;
	public static final byte SET_VOLUME = -8;
	public static final byte REPEAT = -9;

	public static final byte C4 = 60;
	

	public void setSequence(byte[] sequence);
		
}
