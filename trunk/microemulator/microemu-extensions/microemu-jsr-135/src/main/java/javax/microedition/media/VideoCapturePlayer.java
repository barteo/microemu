/**
 *  MicroEmulator
 *  Copyright (C) 2009 Rainer Burgstaller
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
 *  @version $Id: MicroEmulator.java 2183 2009-10-19 08:36:28Z barteo $
 */

package javax.microedition.media;

import java.util.ArrayList;

import javax.microedition.media.control.VideoControl;

/**
 * Simulation player to be able to simulate recording video on microemu.
 * 
 * @author Rainer Burgstaller
 */
public class VideoCapturePlayer implements Player {
	ArrayList m_listeners = new ArrayList();
	private int m_state = UNREALIZED;
	private VideoCaptureControl m_videoControl;
	private String m_locator;

	public VideoCapturePlayer(String locator) {
		m_locator = locator;
	}

	public synchronized void addPlayerListener(PlayerListener playerListener) {
		m_listeners.add(playerListener);
	}

	public synchronized void close() {
		deallocate();
		m_state = CLOSED;
		notifyListeners(PlayerListener.CLOSED, null);
	}

	public synchronized void deallocate() {
		if (m_state == STARTED) {
			try {
				stop();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (m_state == PREFETCHED) {
			m_state = REALIZED;
		} else if (m_state == REALIZED) {
			m_state = UNREALIZED;
		}
	}

	public String getContentType() {
		ensureNotClosed();
		return "video/mpeg";
	}

	public long getDuration() {
		ensureNotClosed();
		return TIME_UNKNOWN;
	}

	public long getMediaTime() {
		ensureNotClosed();
		return TIME_UNKNOWN;
	}

	public synchronized int getState() {
		return m_state;
	}

	public synchronized void prefetch() throws MediaException {
		ensureNotClosed();
		if (m_state == UNREALIZED) {
			realize();
		}
		if (m_state > PREFETCHED) {
			return;
		}
		m_state = PREFETCHED;
	}

	private synchronized void ensureNotClosed() {
		if (m_state == CLOSED) {
			throw new IllegalStateException("Cannot call method on a closed player.");
		}
	}

	public synchronized void realize() throws MediaException {
		ensureNotClosed();
		if (m_state >= REALIZED) {
			return;
		}
		m_state = REALIZED;
	}

	public synchronized void removePlayerListener(PlayerListener playerListener) {
		m_listeners.remove(playerListener);
	}

	public void setLoopCount(int count) {
		ensureNotClosed();
	}

	public long setMediaTime(long now) throws MediaException {
		throw new MediaException("Seeking is not supported by video capture player");
	}

	public synchronized void start() throws MediaException {
		ensureNotClosed();
		if (m_state > PREFETCHED) {
			prefetch();
		}
		if (m_state == STARTED) {
			return;
		}
		m_state = STARTED;
		notifyListeners(PlayerListener.STARTED, new Long(0));
		if (m_videoControl != null) {
			m_videoControl.startVideo();
		}
	}

	private synchronized void notifyListeners(String event, Object eventData) {
		for (int i = 0; i < m_listeners.size(); i++) {
			((PlayerListener) m_listeners.get(i)).playerUpdate(this, event, eventData);
		}
	}

	public synchronized void stop() throws MediaException {
		ensureNotClosed();
		if (m_state <= REALIZED) {
			throw new IllegalStateException("Cannot stop an unrealized player.");
		}
		if (m_state == PREFETCHED) {
			return;
		}
		m_state = PREFETCHED;
		if (m_videoControl != null) {
			m_videoControl.stopVideo();
		}
		notifyListeners(PlayerListener.STOPPED, new Long(0));
	}

	public Control getControl(String controlType) {
		ensureNotClosed();
		if ("VideoControl".equals(controlType) || VideoControl.class.getName().equals(controlType)) {
			return getVideoControl();
		}
		return null;
	}

	private synchronized Control getVideoControl() {
		if (m_videoControl == null) {
			m_videoControl = new VideoCaptureControl(this, m_locator);
		}
		return m_videoControl;
	}

	public Control[] getControls() {
		ensureNotClosed();
		return new Control[] { getVideoControl() };
	}

}
