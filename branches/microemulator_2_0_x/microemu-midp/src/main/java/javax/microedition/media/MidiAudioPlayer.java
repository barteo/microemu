/*
 *  MicroEmulator
 *  Copyright (C) 2006 John Blackmon
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.media.control.VolumeControl;
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

import org.microemu.MIDletBridge;

class MidiAudioPlayer implements Player, MetaEventListener 
{
	private int state;
	
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
    		state = UNREALIZED;
	    } 
	    catch( UnsatisfiedLinkError e ){ e.printStackTrace(); }
	    catch( IOException e ){ e.printStackTrace(); }
	    catch( MidiUnavailableException e ){ e.printStackTrace(); }
	    catch( InvalidMidiDataException e ){ e.printStackTrace(); }
		return false;
	}

	public void addPlayerListener(PlayerListener playerListener) 
	{
		if( vListeners == null )
			vListeners = new Vector();
		vListeners.add( playerListener );
	}

	public void close() 
	{
		if (state == CLOSED) {
			return;
		}
		
		MIDletBridge.removeMediaPlayer(this);
		if( sequencer != null ) {
			sequencer.close();
		}
		state = CLOSED;
	}

	public void deallocate() {
		// TODO handle UNREALIZED state
		state = REALIZED;
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
		return state;
	}

	public void prefetch() throws MediaException {
		// TODO Auto-generated method stub
		state = PREFETCHED;
	}

	public void realize() throws MediaException {
		// TODO Auto-generated method stub
		state = REALIZED;
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
		state = STARTED;
	}

	public void stop() throws MediaException {
		if( sequencer != null ) {
			sequencer.stop();
		}
		state = PREFETCHED;
	}

	public Control getControl(String controlType) {
		if (controlType.equals("VolumeControl")) {
			return new VolumeControl() {

				public int getLevel() {
					// TODO Auto-generated method stub
					return 0;
				}

				public boolean isMuted() {
					// TODO Auto-generated method stub
					return false;
				}

				public int setLevel(int level) {
					// TODO Auto-generated method stub
					return 0;
				}

				public void setMute(boolean mute) {
					// TODO Auto-generated method stub			
				}
				
			};
		} else {
			return null;
		}
	}

	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

    public void meta( MetaMessage event )
    {
        if (event.getType() == 47) //End of Track type
        {
            if (iLoopCount > 0) 
            {
                iLoopCount--;
            }
            if( iLoopCount > 0 || iLoopCount == -1)
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
