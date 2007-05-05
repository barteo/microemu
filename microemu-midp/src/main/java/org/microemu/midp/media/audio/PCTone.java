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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class PCTone
{
   //Max Time in Seconds
   private final static int MAX_TIME = 4;

   //8 16
   private final static int sampleSizeInBits = 16;

   //1 2
   private final static int channels = 1;

   private final static boolean signed = true;

   private final static boolean bigEndian = true;
   
   //8000,11025,16000,22050,44100
   private final static int sampleRate = 16000;
   
   private final static AudioFormat audioFormat =
      new AudioFormat(
      sampleRate,
      sampleSizeInBits,
      channels,
      signed,
      bigEndian);
   
   private final static DataLine.Info dataLineInfo =
      new DataLine.Info(
      SourceDataLine.class,
      audioFormat);
   
   private final byte audioData[] = new byte[sampleRate * MAX_TIME];
   
   public PCTone()
   {
   }
   
   public void play(double frequency, double duration, double volume)
   {
      try
      {
         InputStream byteArrayInputStream =
            new ByteArrayInputStream(audioData);
         
         AudioInputStream audioInputStream =
            new AudioInputStream(
            byteArrayInputStream,
            audioFormat,
            audioData.length/audioFormat.getFrameSize());

         SourceDataLine sourceDataLine =
            (SourceDataLine) AudioSystem.getLine(dataLineInfo);
         
//         final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
//         final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
         
         double amplify = volume/100;
         
         final double d_sampleRate = sampleRate;
         
         int size = (int) ((sampleRate * (duration/1000))/2);
         for(int index = 0; index < size; index++)
         {
            double currentTime = index/d_sampleRate;
            
            double sinValue =
               (Math.sin(2 * Math.PI * frequency * currentTime));
            
            //System.out.println("value: " + d_sampleRate * sinValue);
//            shortBuffer.put((short)(amplify * d_sampleRate * sinValue));
         }
         
         PCToneRunnable pcToneRunnable =
            PCToneRunnablePoolFactory.getInstance(
            sourceDataLine,
            audioInputStream,
            audioFormat,
            sampleRate * MAX_TIME);
         
         new Thread(pcToneRunnable).start();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
