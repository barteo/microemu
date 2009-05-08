/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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
 *  Contributor(s):
 *    Travis Berthelot
 */

package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;

import org.microemu.MIDletBridge;
import org.microemu.midp.media.audio.PCTone;

public final class Manager {

	public static final String TONE_DEVICE_LOCATOR = "device://tone";

	public static String[] getSupportedContentTypes(String protocol) 
	{
		// TODO
		return new String[0];
	}

	public static String[] getSupportedProtocols(String content_type) 
	{
		// TODO
		return new String[0];
	}

	public static Player createPlayer(String locator) 
			throws IOException, MediaException 
	{
		// TODO
		return null;
	}

	public static Player createPlayer(InputStream stream, String type)
			throws IOException, MediaException 
	{
		// TODO add all types, for now just simpleaudio
		if (type.equals("audio/x-wav") || type.equals("audio/basic") || type.equals("audio/mpeg")) {
			SampledAudioPlayer audPlayer = new SampledAudioPlayer();
			audPlayer.open(stream, type);

			MIDletBridge.addMediaPlayer(audPlayer);

			return audPlayer;
		} else if (type.equals("audio/midi")) {
			MidiAudioPlayer midiPlayer = new MidiAudioPlayer();
			midiPlayer.open(stream, type);

			MIDletBridge.addMediaPlayer(midiPlayer);

			return midiPlayer;
		}

		return null;
	}

	private static final PCTone pcTone = new PCTone();

	public synchronized static void playTone(int frequency, int time, int volume)
			throws MediaException 
	{
		pcTone.play(frequency, time, volume);
	}

}
