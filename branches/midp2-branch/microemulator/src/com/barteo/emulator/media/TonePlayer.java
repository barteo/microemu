/*
 *  MicroEmulator
 *  Copyright (C) 2003 Bartek Teodorczyk <barteo@barteo.net>
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

package com.barteo.emulator.media;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

import com.barteo.emulator.media.control.ToneControlImpl;


public class TonePlayer implements Player 
{
	private int state;
	
	
	public TonePlayer()
	{
		state = Player.UNREALIZED;
	}
	

	public void addPlayerListener(PlayerListener playerListener) 
	{
		throw new RuntimeException("TODO");
	}


	public void close() 
	{
		throw new RuntimeException("TODO");
	}


	public void deallocate() 
	{
		if (state == Player.UNREALIZED || state == Player.REALIZED) {
			return;
		}
		if (state == Player.CLOSED) {
			throw new IllegalStateException();
		}

		state = REALIZED;
		
//		throw new RuntimeException("TODO");
	}


	public String getContentType() 
	{
		throw new RuntimeException("TODO");
	}


	public long getDuration() 
	{
		throw new RuntimeException("TODO");
	}


	public long getMediaTime() 
	{
		throw new RuntimeException("TODO");
	}


	public int getState() 
	{
		return state;
	}


	public void prefetch() 
			throws MediaException 
	{
		if (state == Player.STARTED) {
			return;
		}
		if (state == Player.CLOSED) {
			throw new IllegalStateException();
		}
		if (state == Player.UNREALIZED) {
			realize();
		}
		
		state = Player.REALIZED;

//		throw new RuntimeException("TODO");
	}


	public void realize() 
			throws MediaException 
	{
		if (state == Player.REALIZED || state == Player.PREFETCHED || state == Player.STARTED) {
			return;
		}
		if (state == Player.CLOSED) {
			throw new IllegalStateException();
		}

		state = Player.REALIZED;

//		throw new RuntimeException("TODO");
	}


	public void removePlayerListener(PlayerListener playerListener) 
	{
		throw new RuntimeException("TODO");
	}


	public void setLoopCount(int count) 
	{
		throw new RuntimeException("TODO");
	}


	public long setMediaTime(long now) 
			throws MediaException 
	{
		throw new RuntimeException("TODO");
	}


	public void start() 
			throws MediaException 
	{
		if (state == Player.STARTED) {
			return;
		}
		if (state == Player.CLOSED) {
			throw new IllegalStateException();
		}
		if (state == Player.UNREALIZED || state == Player.REALIZED) {
			prefetch();
		}
		
		state = Player.STARTED;

//		throw new RuntimeException("TODO");
	}


	public void stop() 
			throws MediaException 
	{
		throw new RuntimeException("TODO");
	}


	public Control getControl(String controlType) 
	{
		if (controlType == null) {
			throw new IllegalArgumentException();
		}
		if (state == Player.UNREALIZED) {
			throw new IllegalStateException();
		}
		
		if (controlType.equals("javax.microedition.media.control.ToneControl")) {
			return new ToneControlImpl(this);
		}

		return null;
	}


	public Control[] getControls() 
	{
		throw new RuntimeException("TODO");
	}

}
