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

class PCToneHelper
{
   protected byte playBuffer[];
   private SourceDataLine sourceDataLine;
   private AudioInputStream audioInputStream;
   private AudioFormat audioFormat;
   
   public PCToneHelper(
      SourceDataLine sourceDataLine, 
      AudioInputStream audioInputStream,
      AudioFormat audioFormat,
      int size)
   {
      this.playBuffer = new byte[size];
      this.setSourceDataLine(sourceDataLine);
      this.setAudioInputStream(audioInputStream);
      this.setAudioFormat(audioFormat);
   }

   public SourceDataLine getSourceDataLine()
   {
      return sourceDataLine;
   }

   public void setSourceDataLine(SourceDataLine sourceDataLine)
   {
      this.sourceDataLine = sourceDataLine;
   }

   public AudioInputStream getAudioInputStream()
   {
      return audioInputStream;
   }

   public void setAudioInputStream(AudioInputStream audioInputStream)
   {
      this.audioInputStream = audioInputStream;
   }

   public AudioFormat getAudioFormat()
   {
      return audioFormat;
   }

   public void setAudioFormat(AudioFormat audioFormat)
   {
      this.audioFormat = audioFormat;
   }

}
