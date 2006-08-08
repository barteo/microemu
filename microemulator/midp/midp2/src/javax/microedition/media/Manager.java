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
import java.util.Vector;

public final class Manager
{

    public static final String TONE_DEVICE_LOCATOR = "device://tone";
    static Vector vMedia = null;
    
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
    	if( type.equals( "audio/x-wav" ) || type.equals( "audio/basic" ) || 
    		type.equals( "audio/mpeg" )  )
    	{
    	  	SampledAudioPlayer audPlayer = new SampledAudioPlayer();
        	audPlayer.open( stream, type );
        	
        	if( vMedia == null )
        		vMedia = new Vector();
        	vMedia.add( audPlayer );
            return audPlayer;
        }
    	else if( type.equals( "audio/midi" ) )
    	{
    	  	MidiAudioPlayer midiPlayer = new MidiAudioPlayer();
        	midiPlayer.open( stream, type );
        	
        	if( vMedia == null )
        		vMedia = new Vector();
        	vMedia.add( midiPlayer );
            return midiPlayer;
    	}
    	
    	return null;
    }
    
    
    public static void playTone(int note, int duration, int volume)
    		throws MediaException
	{
    	// TODO
	}

    static void mediaDone( Object objMedia )
	{
    	//remove the media from our list of media to cleanup
    	try 
    	{
	    	for( int index=0; vMedia != null && index<vMedia.size(); index++ )
	    	{
	    		if( objMedia == vMedia.elementAt( index ) )
	    			vMedia.removeElementAt( index );
	    	}
    	}
    	catch( ArrayIndexOutOfBoundsException e ) { return; };
	}
    
    static void cleanupMedia()
	{
    	try 
    	{
	    	while( vMedia != null && vMedia.size() > 0 )
	    	{
	    		Player play = (Player) vMedia.elementAt( 0 );
	    		play.close();
	    		vMedia.removeElementAt( 0 );
	    	}
    	}
    	catch( ArrayIndexOutOfBoundsException e ) { return; };
	}

}



