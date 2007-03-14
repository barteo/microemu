/**
 *  MicroEmulator
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

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface MIDIControl extends Control {

	public static final int NOTE_ON = 144;

	public static final int CONTROL_CHANGE = 176;
	
	public abstract boolean isBankQuerySupported();

	public abstract int[] getProgram(int i) throws MediaException;

	public abstract int getChannelVolume(int i);

	public abstract void setProgram(int i, int j, int k);

	public abstract void setChannelVolume(int i, int j);

	public abstract int[] getBankList(boolean flag) throws MediaException;

	public abstract int[] getProgramList(int i) throws MediaException;

	public abstract String getProgramName(int i, int j) throws MediaException;

	public abstract String getKeyName(int i, int j, int k) throws MediaException;

	public abstract void shortMidiEvent(int i, int j, int k);

	public abstract int longMidiEvent(byte abyte0[], int i, int j);

}
