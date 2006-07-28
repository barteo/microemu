/*
 *  MicroEmulator
 *  Copyright (C) 2006 Travis Berthelot
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

package javax.microedition.media;

import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

class WavPlayer implements Player {

	private AudioInputStream audioInputStream;

	private Clip clip;

	private int repeat;

	public WavPlayer(InputStream inputStream) {
		try {
			this.audioInputStream = AudioSystem
					.getAudioInputStream(inputStream);
			clip = AudioSystem.getClip();
			clip.open(this.audioInputStream);

			this.repeat = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
	}

	public void deallocate() {
	}

	public String getContentType() {
		return null;
	}

	public long getDuration() {
		return 0;
	}

	public long getMediaTime() {
		return 0;
	}

	public int getState() {
		return 0;
	}

	public void prefetch() throws MediaException {
	}

	public void realize() throws MediaException {
	}

	public void setLoopCount(int count) {
		this.repeat = count;
	}

	public long setMediaTime(long now) throws MediaException {
		return 0;
	}

	public synchronized void start() throws MediaException {
		try {
			clip.setFramePosition(0);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() throws MediaException {
		try {
			clip.drain();
			clip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Control getControl(String controlType) {
		return null;
	}

	public Control[] getControls() {
		return null;
	}

	public void addPlayerListener(PlayerListener playerListener) {
	}

	public void removePlayerListener(PlayerListener playerListener) {
	}

}
