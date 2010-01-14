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
