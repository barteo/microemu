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


public interface PlayerListener 
{
	public static final String CLOSED = "CLOSED";
	public static final String DEVICE_AVAILABLE = "DEVICE_AVAILABLE";
	public static final String DEVICE_UNAVAILABLE = "DEVICE_UNAVAILABLE";
	public static final String DURATION_UPDATED = "DURATION_UPDATED";
	public static final String END_OF_MEDIA = "END_OF_MEDIA";
	public static final String ERROR = "ERROR";
	public static final String STARTED = "STARTED";
	public static final String STOPPED = "STOPPED";
	public static final String VOLUME_CHANGED = "VOLUME_CHANGED";
	
	
	public void playerUpdate(Player player, String event, Object eventData);
	
}
