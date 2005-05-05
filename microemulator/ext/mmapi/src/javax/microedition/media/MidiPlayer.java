/*
 *  MicroEmulator
 *  Copyright (C) 2005 daniel(at)angrymachine.com.ar
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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MidiPlayer implements Player, MetaEventListener
{
    Sequencer sequencer;
    int state = Player.UNREALIZED;
    protected Vector listeners = new Vector();
    
    MidiPlayer(InputStream is)
    {
        Sequence seq;
        try
        {
            seq = MidiSystem.getSequence(is);
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(seq);
            sequencer.addMetaEventListener(this);
        } 
        catch (InvalidMidiDataException e)
        {
        } 
        catch (IOException e)
        {
        } 
        catch (MidiUnavailableException e)
        {
        }
    }

    public void realize() throws MediaException
    {
        state = Player.REALIZED;
    }

    public void prefetch() throws MediaException
    {
        state = Player.PREFETCHED;
    }

    public void start() throws MediaException
    {
        if(sequencer != null)
        {
            state = Player.STARTED;
            sequencer.start();
        }
    }
    
    public void meta(MetaMessage event)
    {
        if (event.getType() == 47) // end of track
        {
            try
            {
                stop();
                for(int i=0;i != listeners.size();++i)
                {
                    PlayerListener pl = (PlayerListener) listeners.elementAt(i);
                    pl.playerUpdate(this, PlayerListener.STOPPED,null);
                }
                
            } 
            catch (MediaException e)
            {
            }
        }        
    }

    public void stop() throws MediaException
    {
        if(state == Player.STARTED)
        {
            state = Player.PREFETCHED;
        }
        
        sequencer.stop();
    }


    public void deallocate()
    {
        sequencer.close();
        state = Player.UNREALIZED;
    }

    public void close()
    {
        deallocate();
    }


    public long setMediaTime(long l) throws MediaException
    {
        //TODO
        return 0;
    }


    public long getMediaTime()
    {
        //TODO
        return 0;
    }


    public int getState()
    {
        return state;
    }


    public long getDuration()
    {
        //TODO
        return 0;
    }


    public String getContentType()
    {
        return "audio/mid";
    }


    public void setLoopCount(int i)
    {
        //TODO
    }

    public void addPlayerListener(PlayerListener playerlistener)
    {
        if(!listeners.contains(playerlistener))
        {
            listeners.add(playerlistener);
        }
    }


    public void removePlayerListener(PlayerListener playerlistener)
    {
        listeners.remove(playerlistener);
    }


    public void setTimeBase(TimeBase timebase) throws MediaException
    {
    }


    public TimeBase getTimeBase()
    {
        return null;
    }

}
