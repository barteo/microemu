/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *
 *  @version $Id$
 */
package org.microemu.app.ui.swing;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import org.microemu.app.ui.swing.logconsole.LogTextArea;
import org.microemu.log.Logger;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;
import org.microemu.log.QueueAppender;
import org.microemu.log.StdOutAppender;

public class SwingLogConsoleDialog extends JFrame implements LoggerAppender {

	private static final long serialVersionUID = 1L;

	private static final boolean tests = true;
  	
	private LogTextArea logArea;
	
	private int testEventCounter = 0;
	
	public SwingLogConsoleDialog(Frame owner, QueueAppender logQueueAppender) {
		super("Log console");
		
		setIconImage(owner.getIconImage());
		
		JMenuBar menuBar = new JMenuBar();		
		JMenu menu = new JMenu("Log");
		
		JMenuItem menuClear = new JMenuItem("Clear");
		menuClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingLogConsoleDialog.this.logArea.setText("");				
			}
		});
		menu.add(menuClear);
		
		final JCheckBoxMenuItem menuStdOut = new JCheckBoxMenuItem("Write to std Out");
		menuStdOut.setState(StdOutAppender.enabled);
		menuStdOut.addActionListener(new ActionListener() {    
	  		public void actionPerformed(ActionEvent e) {
	  			StdOutAppender.enabled = menuStdOut.getState();
	  		}
	  	});
	  	menu.add(menuStdOut);
		
	  	
		menuBar.add(menu);		
		
		if (tests) {
			JMenu testMenu = new JMenu("Tests");
			JMenuItem testLog = new JMenuItem("Log 10 events");
			testLog.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < 10; i++) {
						log(testEventCounter++ + " " + new Date()+ "\n");
					}				
				}
			});
			testMenu.add(testLog);
			menuBar.add(testMenu);
		}
		
		setJMenuBar(menuBar);

		this.logArea = new LogTextArea(20, 40, 1000);
		JScrollPane scrollPane = new JScrollPane(this.logArea);
		scrollPane.setAutoscrolls(false);
		
		getContentPane().add(new JScrollPane(scrollPane));
		
		LoggingEvent event = null;
		while ((event = logQueueAppender.poll()) != null) {
			append(event);
		}
		
		Logger.removeAppender(logQueueAppender);
		Logger.addAppender(this);
	}
	
	public void log(String message) {
		logArea.append(message);
		//logArea.setCaretPosition(logArea.getText().length());
	}

	private String formatLocation(StackTraceElement ste) {
		if (ste == null) {
			return "";
		}
		return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";
	}
	
	public void append(LoggingEvent event) {
		StringBuffer bug = new StringBuffer(); 
		if (event.getLevel() == LoggingEvent.ERROR) {
			bug.append("Error:");
    	}
		bug.append(event.getMessage());
    	if (event.hasData()) {
    		bug.append(" [").append(event.getFormatedData()).append("]");
    	}
    	bug.append("\n\t  ").append(formatLocation(event.getLocation()));
    	if (event.getThrowable() != null) {
    		OutputStream out = new ByteArrayOutputStream();
    		PrintStream stream = new PrintStream(out);
    		event.getThrowable().printStackTrace(stream);
    		stream.flush();
    		bug.append(out.toString());
    	}
    	bug.append("\n");
    	log(bug.toString());
	}

}
