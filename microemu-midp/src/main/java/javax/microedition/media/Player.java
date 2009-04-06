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
 */

package javax.microedition.media;

public interface Player extends Controllable
{

	public static final int UNREALIZED = 100;
	
	public static final int REALIZED = 200;
	
	public static final int PREFETCHED = 300;
	
	public static final int STARTED = 400;
	
	public static final int CLOSED = 0;
	
	public static final long TIME_UNKNOWN = -1;
	
	public void realize() throws MediaException;
	
	public void prefetch() throws MediaException;
	
	public void start() throws MediaException;
	
	public void stop() throws MediaException;
	
	public void deallocate();
	
	public void close();
	
	public long setMediaTime(long now) throws MediaException;
	
	public long getMediaTime();
	
	public int getState();
	
	public long getDuration();
	
	public String getContentType();
	
	public void setLoopCount(int count);
	
	public void addPlayerListener(PlayerListener playerListener);
	
	public void removePlayerListener(PlayerListener playerListener);
	
}
