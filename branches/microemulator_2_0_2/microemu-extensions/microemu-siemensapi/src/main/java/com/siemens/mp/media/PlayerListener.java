/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
package com.siemens.mp.media;

public interface PlayerListener
{

    public abstract void playerUpdate(Player player, String s, Object obj);

    public static final String STARTED = "started";
    public static final String STOPPED = "stopped";
    public static final String STOPPED_AT_TIME = "stoppedAtTime";
    public static final String END_OF_MEDIA = "endOfMedia";
    public static final String DURATION_UPDATED = "durationUpdated";
    public static final String DEVICE_UNAVAILABLE = "deviceUnavailable";
    public static final String DEVICE_AVAILABLE = "deviceAvailable";
    public static final String VOLUME_CHANGED = "volumeChanged";
    public static final String SIZE_CHANGED = "sizeChanged";
    public static final String ERROR = "error";
    public static final String CLOSED = "closed";
    public static final String RECORD_STARTED = "recordStarted";
    public static final String RECORD_STOPPED = "recordStopped";
    public static final String RECORD_ERROR = "recordError";
    public static final String BUFFERING_STARTED = "bufferingStarted";
    public static final String BUFFERING_STOPPED = "bufferingStopped";
}
