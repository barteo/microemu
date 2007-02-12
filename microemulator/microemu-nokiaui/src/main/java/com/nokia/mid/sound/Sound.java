/*
 *  Nokia API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
 
package com.nokia.mid.sound;


// Only empty methods, sound is currently not supported

public class Sound 
{
	public static final int SOUND_PLAYING = 0;
	public static final int SOUND_STOPPED = 1;
	public static final int SOUND_UNINITIALIZED = 3;
	public static final int FORMAT_TONE = 1;
	public static final int FORMAT_WAV = 5;
	
	
    public Sound(byte data[], int type) 
    {
    }
    
    
    public Sound(int freq, long duration) 
    {
    }
    
    
    public static int getConcurrentSoundCount(int type) 
    {
        return 0;
    }
    
    
    public int getGain() 
    {
        return 0;
    }
    
    
    public void setGain(int gain) 
    {
    }
    
    
    public int getState() 
    {
        return SOUND_STOPPED;
    }
    
    
    public static int[] getSupportedFormats() 
    {
        return new int[0];
    }
    
    
    public void init(byte data[], int type) 
    {    
    }
    
    
    public void init(int freq, long duration) 
    {
    }
    
    
    public synchronized void play(int loop) 
    {        
    }
    
    
    public void stop() 
    {
    }
    
     
    public void release() 
    {
    }
    
    
    public void resume() 
    {
    }
    
    
    public void setSoundListener(SoundListener listener) 
    {
    }
    
}