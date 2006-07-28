/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

import java.io.IOException;
import java.io.InputStream;

public final class Manager
{

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
    	if (stream == null) {
    		throw new IllegalArgumentException();
    	}
        if(type.indexOf("audio/midi") != -1) {
            return new MidiPlayer(stream);
        }
        
        throw new MediaException("Unsupported Format");
    }
    
    
    public static void playTone(int note, int duration, int volume)
            throws MediaException
    {
        // TODO
    }
    
}
