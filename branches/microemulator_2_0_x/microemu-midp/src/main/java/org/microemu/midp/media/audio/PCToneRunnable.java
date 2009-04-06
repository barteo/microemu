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
