/*
 *  PC Media MIDP Java Library
 *  Copyright (C) 2006 Travis Berthelot
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

