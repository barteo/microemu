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


package org.microemu.midp.media.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.microemu.midp.media.RunnableInterface;

class PCToneRunnable extends PCToneHelper implements RunnableInterface
{

   public PCToneRunnable(
      SourceDataLine sourceDataLine,
      AudioInputStream audioInputStream,
      AudioFormat audioFormat,
      int size)
   {
      super(
         sourceDataLine,
         audioInputStream,
         audioFormat,
         size
         );
   }
   
   public void init() throws Exception
   {
      this.getSourceDataLine().open(this.getAudioFormat());
      this.getSourceDataLine().start();
   }
   
   public void close()
   {
      this.getSourceDataLine().drain();
      this.getSourceDataLine().stop();
      this.getSourceDataLine().close();
   }
   
   private boolean running;
   
   public synchronized boolean isRunning()
   {
      return running;
   }
   
   public synchronized void setRunning(boolean running)
   {
      this.running = running;
   }
   
   public void run()
   {
      try
      {
         this.setRunning(true);

         this.init();
         
         int cnt;
         
         while((cnt = this.getAudioInputStream().read(playBuffer, 0, playBuffer.length)) != -1)
         {
            if(cnt > 0)
            {
               this.getSourceDataLine().write(playBuffer, 0, cnt);
            }
         }
         
         this.close();
         
         this.setRunning(false);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
