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

public interface Player
    extends Controllable
{

    public abstract void realize()
        throws MediaException;

    public abstract void prefetch()
        throws MediaException;

    public abstract void start()
        throws MediaException;

    public abstract void stop()
        throws MediaException;

    public abstract void deallocate();

    public abstract void close();

    public abstract void setTimeBase(TimeBase timebase)
        throws MediaException;

    public abstract TimeBase getTimeBase();

    public abstract long setMediaTime(long l)
        throws MediaException;

    public abstract long getMediaTime();

    public abstract int getState();

    public abstract long getDuration();

    public abstract String getContentType();

    public abstract void setLoopCount(int i);

    public abstract void addPlayerListener(PlayerListener playerlistener);

    public abstract void removePlayerListener(PlayerListener playerlistener);

    public static final int UNREALIZED = 100;
    public static final int REALIZED = 200;
    public static final int PREFETCHED = 300;
    public static final int STARTED = 400;
    public static final int CLOSED = 0;
    public static final long TIME_UNKNOWN = -1L;
}
