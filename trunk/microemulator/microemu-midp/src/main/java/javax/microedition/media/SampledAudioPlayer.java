/*
 *  MicroEmulator
 *  Copyright (C) 2006 John Blackmon
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class SampledAudioPlayer implements Player, LineListener 
{
	private AudioInputStream audioInputStream = null;
    private AudioInputStream decodedStream = null;
	private Clip clip = null;
	private Vector vListeners = null;           // All PlayerListeners for this audio
	private String strType = null;
	
	public boolean open( InputStream stream, String type ) 
	{
	    strType = type;
		try 
	    {
			audioInputStream = AudioSystem.getAudioInputStream( new BufferedInputStream( stream ) );
			AudioFormat format = audioInputStream.getFormat();
			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
			{
			    AudioFormat baseFormat = audioInputStream.getFormat();
			    AudioFormat decodedFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
										                     baseFormat.getSampleRate(),
										                     16,
										                     baseFormat.getChannels(),
										                     baseFormat.getChannels() * 2,
										                     baseFormat.getSampleRate(),
										                     false );
			    decodedStream = AudioSystem.getAudioInputStream( decodedFormat, audioInputStream );
			    int frameLength = (int) decodedStream.getFrameLength();
			    int frameSize = decodedFormat.getFrameSize();
			    DataLine.Info info = new DataLine.Info( Clip.class, decodedFormat,
			    										frameLength * frameSize);
			    clip = (Clip) AudioSystem.getLine( info );
		    	clip.open( decodedStream );
			}
	        else
	        {
				DataLine.Info info2 = new DataLine.Info( Clip.class, format, AudioSystem.NOT_SPECIFIED );
				clip = (Clip) AudioSystem.getLine( info2 );
		    	clip.open( audioInputStream );
	        }
	    } 
	    catch( UnsupportedAudioFileException e ){ e.printStackTrace(); return false; }
	    catch( IOException e ){ e.printStackTrace(); return false; }
	    catch( LineUnavailableException e ){ e.printStackTrace(); return false; }
		return true;
	}

	public void addPlayerListener(PlayerListener playerListener) 
	{
		if( vListeners == null )
			vListeners = new Vector();
		vListeners.add( playerListener );
	}

	public void close() 
	{
		Manager.mediaDone( this );
		if( clip != null )
		{
			clip.flush();
	    	clip.close();
		}
		
		try 
	    {
			if( decodedStream != null )
				decodedStream.close();
			if( audioInputStream != null )
				audioInputStream.close();
	    } 
	    catch( IOException e ) { e.printStackTrace(); }
	}

	public void deallocate() {
		if( clip != null )
			clip.flush();
	}

	public String getContentType() {
		return strType;
	}

	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getMediaTime() {
		if( clip != null )
			return clip.getMicrosecondPosition();
		return 0;
	}

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void prefetch() throws MediaException {
		// TODO Auto-generated method stub
	}

	public void realize() throws MediaException {
		// TODO Auto-generated method stub
	}

	public void removePlayerListener(PlayerListener playerListener) 
	{
	   if( vListeners == null )
		   return;
	   for( Iterator it = vListeners.iterator (); it.hasNext (); ) 
	   {
		    PlayerListener listener = (PlayerListener) it.next ();
		    if( listener == playerListener )
		    {
		    	vListeners.remove( listener );
		    	break;
	   		}
	   }
	}

	public void setLoopCount(int count) {
		if( clip != null )
			clip.loop( count );
	}

	public long setMediaTime(long now) throws MediaException {
		if( clip != null )
			clip.setMicrosecondPosition( now );
		return 0;
	}

	public void start() throws MediaException {
		if( clip != null )
		{
			clip.addLineListener( this );
			clip.start();
		}
	}

	public void stop() throws MediaException {
		if( clip != null )
			clip.stop();
	}

	public Control getControl(String controlType) {
		// TODO Auto-generated method stub
		return null;
	}

	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	public void update( LineEvent event ) 
	{
		if (event.getType().equals(LineEvent.Type.STOP))
		{
			close();
			if( vListeners != null )
			{
				for( Iterator it = vListeners.iterator (); it.hasNext (); ) 
				{
				    PlayerListener listener = (PlayerListener) it.next ();
				    listener.playerUpdate( this, PlayerListener.END_OF_MEDIA, null );
				}
			}
		}
	}
}
