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
