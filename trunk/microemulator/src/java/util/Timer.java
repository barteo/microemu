/*
 * @(#)Timer.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package java.util;


public class Timer
{
	
	TimerQueue timerQueue;
	TimerThread timerThread;
	
										 	
	public Timer()
	{
		timerQueue = new TimerQueue();
		timerThread = new TimerThread(timerQueue);
	}


	public void cancel()
	{
		timerThread.cancel();
	}


	public void schedule(TimerTask task, long delay)
	{
System.out.println("to be implemented...");		
	}
	
	
	public void schedule(TimerTask task, Date time)
	{
System.out.println("to be implemented...");		
	}
										 

	public void schedule(TimerTask task, long delay, long period)
	{
		task.nextExecutionTime = System.currentTimeMillis() + delay;
		task.period = period;
		
		timerQueue.add(task);
	}
										 

	public void schedule(TimerTask task, Date firstTime, long period)
	{
System.out.println("to be implemented...");		
	}										 										 	
	
	
	public void scheduleAtFixedRate(TimerTask task, long delay, long period)
	{
System.out.println("to be implemented...");		
	}
	
	
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period)	
	{
System.out.println("to be implemented...");		
	}
	
}


class TimerQueue
{

	TimerTask timerTasks[] = new TimerTask[4];
	int numOfTasks = 0;


	public void add(TimerTask task)
	{
		synchronized (this) {
	    if (numOfTasks + 1 == timerTasks.length) {
	      TimerTask newTimerTasks[] = new TimerTask[numOfTasks + 4];
	      System.arraycopy(timerTasks, 0, newTimerTasks, 0, numOfTasks);
	      timerTasks = newTimerTasks;
	    }
		  timerTasks[numOfTasks] = task;
	    numOfTasks++;		
		}
	}
	
	
	public TimerTask nextTask()
	{
		synchronized (this) {
			long smallestTime = Long.MAX_VALUE;
			for (int i = 0; i < numOfTasks; i++) {
				if (timerTasks[i].cancelled) {
					continue;
				}
				if (timerTasks[i].nextExecutionTime < Long.MAX_VALUE) {
					smallestTime = timerTasks[i].nextExecutionTime;
				}
			}
			if (smallestTime > System.currentTimeMillis()) {
				return null;
			}
			for (int i = 0; i < numOfTasks; i++) {
				if (timerTasks[i].nextExecutionTime == smallestTime) {
					return timerTasks[i];
				}
			}
		}
		
		return null;
	}
	
	
	public void reschedule(TimerTask task)
	{
		synchronized (this) {
			for (int i = 0; i < numOfTasks; i++) {
				if (timerTasks[i] == task) {
					timerTasks[i].nextExecutionTime = System.currentTimeMillis() + timerTasks[i].period;
					break;
				}
			}
		}
	}

}


class TimerThread extends Thread
{

	TimerQueue timerQueue;
	boolean cancel = false;


	TimerThread(TimerQueue timerQueue)
	{
		this.timerQueue = timerQueue;
		start();
	}
	
	
	void cancel()
	{
		cancel = true;
	}
	
	
	public void run()
	{
		TimerTask tmp_task;
	
		while(!cancel) {
			try {
				tmp_task = timerQueue.nextTask();
				if (tmp_task == null) {
					sleep(50);
				} else {
					tmp_task.run();
					timerQueue.reschedule(tmp_task);					
				}
			} catch (InterruptedException ex) {}
		}
	}

}
