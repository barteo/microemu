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

package javax.microedition.media;


public interface Player extends Controllable 
{
	public static final int CLOSED = 0;
	public static final int PREFETCHED = 300;
	public static final int REALIZED = 200;
	public static final int STARTED = 400;
	public static final long TIME_UNKNOWN = -1;
	public static final int UNREALIZED = 100;
	
	
	public void addPlayerListener(PlayerListener playerListener);
	
	public void close();
	
	public void deallocate();
	
	public String getContentType();
	
	public long getDuration();
	
	public long getMediaTime();
	
	public int getState();
	
	public void prefetch() throws MediaException;
	
	public void realize() throws MediaException;
	
	public void removePlayerListener(PlayerListener	playerListener);
	
	public void setLoopCount(int count);
	
	public long setMediaTime(long now) throws MediaException;
	
	public void start() throws MediaException;
	
	public void stop() throws MediaException;
	
}
