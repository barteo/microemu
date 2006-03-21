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

import com.siemens.mp.game.Sound;
import com.siemens.mp.media.protocol.DataSource;
import java.io.IOException;
import java.io.InputStream;


public final class Manager {
    private Manager() {
    }
    
    public static String[] getSupportedContentTypes(String protocol) {
        return new String[0];
    }
    
    public static String[] getSupportedProtocols(String content_type) {
        return new String[0];
    }
    
    public static Player createPlayer(String locator)
    throws IOException, MediaException {
        throw new MediaException("Not implemented");
    }
    
    public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException {
           throw new MediaException("Content not supported");
    }
    
    public static Player createPlayer(DataSource source)
    throws IOException, MediaException {
        throw new MediaException("Not implemented");
    }
    
    public static void playTone(int note, int duration, int volume)
    throws MediaException {
    }
    
    public static TimeBase getSystemTimeBase() {
        if(sysTimeBase == null)
            sysTimeBase = new SystemTimeBase();
        return sysTimeBase;
    }
    
    private static SystemTimeBase sysTimeBase;
}
