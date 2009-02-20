/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
 *
 *  @version $Id$
 */
package org.microemu.iphone;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static joc.Static.YES;

import joc.Message;
import joc.Scope;
import joc.Selector;
import obc.NSObject;

public final class ThreadDispatcher {
	private static BlockingQueue<RunnableWrapper> queue=new LinkedBlockingQueue<RunnableWrapper>();
	private static Thread dispatchThread = new Thread() {
		@Override
		public void run() {
			Scope scope = new Scope();
			RunnableRunner runner=(RunnableRunner) new ThreadDispatcher.RunnableRunner().init();
			try {
				while (true) {
					RunnableWrapper wrapper = queue.take();
					synchronized (wrapper) {
						runner.runnable = wrapper.runnable;
//						System.out.println("will dispatch "+runner.runnable);
						runner.performSelectorOnMainThread$withObject$waitUntilDone$(new Selector("handle"), null, YES);
//						System.out.println("did dispatch "+runner.runnable);
						wrapper.notifyAll();
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				scope.close();
			}
		}
	};

	static {
		dispatchThread.setDaemon(true);
		dispatchThread.start();
	}

	public static final synchronized void dispatchOnMainThread(Runnable runnable, boolean waitOnFinish){
		RunnableWrapper wrapper=new ThreadDispatcher.RunnableWrapper(runnable);
		
		synchronized (wrapper) {
//			System.out.println("will queue "+wrapper.runnable);
//			try {
//				throw new IllegalArgumentException("DUMMY EXCEPTION");
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
			queue.add(wrapper);
			if(waitOnFinish){
				try {
					wrapper.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private static class RunnableWrapper{
		private Runnable runnable;

		private RunnableWrapper(Runnable runnable) {
			super();
			this.runnable = runnable;
		}
	}
	
	private static class RunnableRunner extends NSObject{
		private Runnable runnable;
		@Message
		public final void handle(){
			runnable.run();
		}
	}
}
