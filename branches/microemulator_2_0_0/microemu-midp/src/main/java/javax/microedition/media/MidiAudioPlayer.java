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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

class MidiAudioPlayer implements Player, MetaEventListener 
{
	private Sequence  sequence = null;             // The contents of a MIDI file
	private Sequencer sequencer = null;            // We play MIDI Sequences with a Sequencer
	private Vector    vListeners = null;           // All PlayerListeners for this audio
	private int       iLoopCount = 1;
	
	public boolean open( InputStream stream, String type ) 
	{
		try 
	    {
    		// First, get a Sequencer to play sequences of MIDI events
    		//That is, to send events to a Synthesizer at the right time.
    		sequencer = MidiSystem.getSequencer( ); // Used to play sequences
    		sequencer.open(); // Turn it on.
    		//Get a Synthesizer for the Sequencer to send notes to
    		Synthesizer synth = MidiSystem.getSynthesizer( );
    		synth.open( ); // acquire whatever resources it needs
    		//The Sequencer obtained above may be connected to a Synthesizer
    		//by default, or it may not. Therefore, we explicitly connect it.
    		Transmitter transmitter = sequencer.getTransmitter( );
    		Receiver receiver = synth.getReceiver( );
    		transmitter.setReceiver(receiver);
    		//Read the sequence from the file and tell the sequencer about it
    		sequence = MidiSystem.getSequence( stream );
    		sequencer.setSequence(sequence);
	    } 
	    catch( IOException e ){ e.printStackTrace(); }
	    catch( MidiUnavailableException e ){ e.printStackTrace(); }
	    catch( InvalidMidiDataException e ){ e.printStackTrace(); }
		return false;
	}

	protected void dispose() 
	{
        close();
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
		if( sequencer != null )
			sequencer.close();
	}

	public void deallocate() {
	}

	public String getContentType() {
		return "audio/midi";
	}

	public long getDuration() {
		return 0;
	}

	public long getMediaTime() 
	{
		if( sequencer != null )
			return sequencer.getMicrosecondPosition();
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

	public void setLoopCount(int count) 
	{
		iLoopCount = count;
	}

	public long setMediaTime(long now) throws MediaException {
		if( sequencer != null )
			sequencer.setMicrosecondPosition( now );
		return now;
	}

	public void start() throws MediaException {
		if( sequencer != null )
		{
			sequencer.addMetaEventListener( this );
			sequencer.start();
		}
	}

	public void stop() throws MediaException {
		if( sequencer != null )
			sequencer.stop();
	}

	public Control getControl(String controlType) {
		// TODO Auto-generated method stub
		return null;
	}

	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

    public void meta( MetaMessage event )
    {
        if (event.getType() == 47) //End of Track type
        {
        	iLoopCount--;
        	if( iLoopCount > 0 )
        	{
        		sequencer.setMicrosecondPosition( 0 );
        		try{ start(); } 
        		catch( MediaException e ) { e.printStackTrace(); }
        	}
        	else
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

}
