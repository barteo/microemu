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

package org.microemu.media.audio;

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
