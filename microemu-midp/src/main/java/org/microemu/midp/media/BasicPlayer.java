/*
 *  PC Media MIDP Java Library
 *  Copyright (C) 2006 Travis Berthelot
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

package org.microemu.midp.media;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

public abstract class BasicPlayer implements Player
{
   public static String CONTROL_TYPE = "ToneControl";
   
   private int state;

   private int loopCount;
   private TimeBase timeBase;

   private Vector listenersVector;

   public BasicPlayer()
   {
      this.setListenersVector(new Vector());
      this.setLoopCount(0);
      this.setState(Player.UNREALIZED);
   }

   public synchronized void addPlayerListener(PlayerListener playerListener)
   {
      this.getListenersVector().add(playerListener);
   }

   public void removePlayerListener(PlayerListener playerListener)
   {
      Enumeration enumeration = this.getListenersVector().elements();
      while(enumeration.hasMoreElements())
      {
         PlayerListener listener = (PlayerListener) enumeration.nextElement();
         if( listener == playerListener )
         {
            this.getListenersVector().remove(listener);
            break;
         }
      }
   }

   public int getState()
   {
      return this.state;
   }

   public synchronized void setState(int state)
   {
      this.state = state;
   }

   public long getDuration()
   {
      return 0;
   }
   
   public long getMediaTime()
   {
      return 0;
   }
   
   public TimeBase getTimeBase()
   {
      return this.timeBase;
   }
   
   public synchronized void setTimeBase(TimeBase timeBase)
   {
      this.timeBase = timeBase;
   }
   
   public void deallocate()
   {
   }
   
   public void prefetch() throws MediaException
   {
   }
   
   public void realize() throws MediaException
   {
   }
      
   public synchronized void setLoopCount(int count)
   {
      this.loopCount = count;
   }
   
   protected int getLoopCount()
   {
      return this.loopCount;
   }

   public synchronized long setMediaTime(long now) throws MediaException
   {
      return 0;
   }

   protected Vector getListenersVector()
   {
      return listenersVector;
   }

   protected synchronized void setListenersVector(Vector listenersVector)
   {
      this.listenersVector = listenersVector;
   }
   
   public synchronized void start() throws MediaException
   {
      this.setState(Player.STARTED);
   }
   
   public synchronized void stop() throws MediaException
   {
      this.setState(Player.PREFETCHED);
   }
}

