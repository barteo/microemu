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

import java.util.EmptyStackException;
import java.util.Stack;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public class PCToneRunnablePoolFactory
{
   private static Stack pcToneRunnableStack = new Stack();

   private PCToneRunnablePoolFactory()
   {
      
   }
   
   public static synchronized PCToneRunnable getInstance(
      SourceDataLine sourceDataLine,
      AudioInputStream audioInputStream,
      AudioFormat audioFormat,
      int size) throws Exception
   {

      try
      {
         PCToneRunnable pcToneRunnable = 
            (PCToneRunnable) pcToneRunnableStack.pop();

         pcToneRunnable.setSourceDataLine(sourceDataLine);
         pcToneRunnable.setAudioInputStream(audioInputStream);
         pcToneRunnable.setAudioFormat(audioFormat);
         
         return pcToneRunnable;
      }
      catch(EmptyStackException e)
      {
         //TWB - Using Exceptions for code control is usually bad.  Oh well...
         return new PCToneRunnable(
            sourceDataLine,
            audioInputStream,
            audioFormat,
            size);
      }
   }
   
   public static synchronized void push(PCToneRunnable pcToneRunnable)
   {
     pcToneRunnableStack.push(pcToneRunnable); 
   }

}
