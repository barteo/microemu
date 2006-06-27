/*
 *  Copyright (C) 2002 Travis Berthelot
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

package media;

import java.io.InputStream;
import java.nio.IntBuffer;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class PCWavPlayer implements Player
{
   private WaveData wavefile;
   private int repeat;
   private TimeBase timeBase;
   private IntBuffer buffers;
   private IntBuffer sources;
   
   public PCWavPlayer(InputStream inputStream)
   {
      this.buffers = BufferUtils.createIntBuffer(1);
      this.sources = BufferUtils.createIntBuffer(1);
      
      this.buffers.position(0).limit(1);
      AL10.alGenBuffers(this.buffers);
      OpenALManager.log();

      this.sources.position(0).limit(1);
      AL10.alGenSources(this.sources);
      OpenALManager.log();

      this.wavefile = WaveData.create(inputStream);
      
      AL10.alBufferData(buffers.get(0), wavefile.format, wavefile.data, wavefile.samplerate);
      wavefile.dispose();
      OpenALManager.log();

      AL10.alSourcei(sources.get(0), AL10.AL_BUFFER, buffers.get(0));
      OpenALManager.log();

      this.repeat = 0;
   }
   
   public TimeBase getTimeBase()
   {
      return this.timeBase;
   }
   
   public void setTimeBase(TimeBase timeBase)
   {
      this.timeBase = timeBase;
   }
   
   public void close()
   {
   }
   
   public void deallocate()
   {
      sources.position(0).limit(1);
      AL10.alDeleteSources(sources);
      OpenALManager.log();
      
      buffers.position(0).limit(1);
      AL10.alDeleteBuffers(buffers);
      OpenALManager.log();
   }
   
   public String getContentType()
   {
      return null;
   }
   
   public long getDuration()
   {
      return 0;
   }
   
   public long getMediaTime()
   {
      return 0;
   }
   
   public int getState()
   {
      return 0;
   }
   
   public void prefetch() throws MediaException
   {
   }

   public void realize() throws MediaException
   {
   }

   public void setLoopCount(int count)
   {
      this.repeat = count;
   }

   public long setMediaTime(long now) throws MediaException
   {
      return 0;
   }

   public synchronized void start() throws MediaException
   {
      try
      {
         if(MyManager.isInitialized())
         {
            AL10.alSourcePlay(sources.get(0));
            OpenALManager.log();
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }

   public void stop() throws MediaException
   {
      try
      {
         AL10.alSourceStop(sources.get(0));
         OpenALManager.log();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }

   public Control getControl(String controlType)
   {
      return null;
   }
   
   public Control[] getControls()
   {
      return null;
   }
   
   public void addPlayerListener(PlayerListener playerListener)
   {
   }
   
   public void removePlayerListener(PlayerListener playerListener)
   {
   }
}

