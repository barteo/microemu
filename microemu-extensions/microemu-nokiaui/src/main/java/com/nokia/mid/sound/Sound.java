/*
 *  Nokia API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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