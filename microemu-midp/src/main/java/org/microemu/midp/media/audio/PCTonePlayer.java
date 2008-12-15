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

import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.ToneControl;

import org.microemu.midp.media.BasicPlayer;
import org.microemu.midp.media.RunnableInterface;

public class PCTonePlayer extends BasicPlayer
   implements RunnableInterface
{
   
   private PCToneControl pcToneControl;
   
   private int sequenceIndex;
   
   private boolean running;
   
   private ToneInfo toneInfo;
   
   public PCTonePlayer()
   {
      try
      {
         this.pcToneControl = new PCToneControl();
         
         this.setTimeBase(new PCTimeBase());
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }
   
   public void close()
   {
   }
   
   public String getContentType()
   {
      return null;
   }
   
   public synchronized void start() throws MediaException
   {
      try
      {
         new Thread(this).start();
         super.start();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }
   
   public synchronized void stop() throws MediaException
   {
      try
      {
         this.setRunning(false);
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }
   
   public Control getControl(String controlType)
   {
      if(controlType.equals(CONTROL_TYPE))
      {
         return this.pcToneControl;
      }
      else
      {
         return null;
      }
   }
   
   public Control[] getControls()
   {
      return null;
   }
   
   public boolean isRunning()
   {
      return running;
   }
   
   public void setRunning(boolean running)
   {
      this.running = running;
   }
   
   private byte getNext()
   {
      return this.pcToneControl.sequence[this.sequenceIndex++];
   }
   
   public void playBlock() throws Exception
   {
      //Skip one byte
      byte currentByte = this.getNext();
      
      while(this.sequenceIndex < this.pcToneControl.sequence.length &&
         (currentByte = this.getNext()) != ToneControl.BLOCK_END)
      {
         if(currentByte == ToneControl.SILENCE)
         {
            Thread.sleep(toneInfo.getSleepDelay());
         }
         else
            if(currentByte == ToneControl.PLAY_BLOCK)
            {
            //Skip/ignore commandValue for play block
            this.getNext();
            }
            else
            {
            double noteDelta = (currentByte-69);
            double power = noteDelta/12.0;
            double d_frequency = 440 * Math.pow(2.0, power);
            
            //LogUtil.put(new Log("Note: " + currentByte + " Power: " + power, this, "playBlock"));
            
            toneInfo.setFrequency((int) d_frequency);
            
            //LogUtil.put(new Log(toneInfo.toString(), this, "playBlock"));
            
            Manager.playTone(
               toneInfo.getFrequency(),
               toneInfo.getLengthOfTime(),
               toneInfo.getVolume());
            
            Thread.sleep(toneInfo.getLengthOfTime() + 20);
            }
      }
      
      if(currentByte == ToneControl.BLOCK_END)
      {
         //Skip/ignore commandValue for block end
         this.getNext();
      }
      
   }
   
   public void run()
   {
      try
      {
         //LogUtil.put(new Log("Start", this, "run"));
         
         this.sequenceIndex = 0;
         
         this.toneInfo = new ToneInfo();
         
         this.setRunning(true);
         
         while(this.isRunning() &&
            this.sequenceIndex < this.pcToneControl.sequence.length)
         {
            byte currentControlCommand = this.getNext();
            if(currentControlCommand == ToneControl.VERSION)
            {
               int version = this.getNext();
               //do nothing I don't give a crap about no versioning
               //LogUtil.put(new Log("Version: " + version, this, "run"));
            }
            else
               if(currentControlCommand == ToneControl.SET_VOLUME)
               {
               toneInfo.setVolume(this.getNext());
               //LogUtil.put(new Log("Volume: " + toneInfo.getVolume(), this, "run"));
               }
               else
                  if(currentControlCommand == ToneControl.TEMPO)
                  {
               //TWB - add conversion from tempo to length of time
               //Beats Per Minute is 4 times tempo
               byte tempo = this.getNext();
               //LogUtil.put(new Log("Tempo: " + tempo, this, "run"));
               double resolutionDenominator = 64;
               double durationOfNote = 60 * 4 / (1/resolutionDenominator * tempo);
               toneInfo.setLengthOfTime((int) durationOfNote/16);
               //LogUtil.put(new Log("Length Of Time: " + toneInfo.getLengthOfTime(), this, "run"));
                  }
                  else
                     if(currentControlCommand == ToneControl.SILENCE)
                     {
               toneInfo.setSleepDelay(this.getNext());
               //LogUtil.put(new Log("Silence: " + toneInfo.getSleepDelay(), this, "run"));
               Thread.sleep(toneInfo.getSleepDelay());
                     }
                     else
                        if(currentControlCommand == ToneControl.PLAY_BLOCK ||
               currentControlCommand == ToneControl.BLOCK_START)
                        {
               //LogUtil.put(new Log("Start = -5/Play Block: " + currentControlCommand, this, "run"));
               this.playBlock();
                        }
                        else
                        {
               //TWB - Should not happen - But Shit Happens
//               LogUtil.put(new Log(
//                  "Unknown Command: " + currentControlCommand +
//                  " at index: " + this.sequenceIndex, this, "run"));
                        }
         }
         
         super.stop();
         //LogUtil.put(new Log("End", this, "run"));
      }
      catch (Exception e)
      {
//         LogUtil.put(new Log("Exception", this, "run", e));
      }
   }
}
