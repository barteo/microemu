/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class OptionPane 
{
	
	public static final int ERROR_MESSAGE = 1;
	public static final int INFORMATION_MESSAGE = 2;
	
	
	private static WindowAdapter windowListener = new WindowAdapter()
	{
		public void windowClosing(WindowEvent ev) 
		{
			((Dialog) ev.getSource()).hide();
		}
	};
	

	public static String showInputDialog(Component parentComponent, String message)
	{
		final StringBuffer result = new StringBuffer();
		final Dialog dialog = new Dialog(getFrame(parentComponent), "Input");
		final TextField tfInput = new TextField(40);

		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		dialog.add(new Label(message), BorderLayout.WEST);
		dialog.add(tfInput, BorderLayout.CENTER);
		Panel panel = new Panel();
		Button btOk = new Button("OK");
		panel.add(btOk);
		Button btCancel = new Button("Cancel");
		panel.add(btCancel);
		dialog.add(panel, BorderLayout.SOUTH); 
		dialog.pack();
		dialog.addWindowListener(windowListener);
		
		btOk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ev) 
			{
				result.append(tfInput.getText());
				dialog.hide();
			}
		});
		
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) 
			{
				dialog.hide();
			}
		}); 

		dialog.show();
				
		if (result.length() > 0) {		
			return result.toString();
		}
		
		return null;
	}
	
	
	public static void showMessageDialog(Component parentComponent, String message, String title, int messageType)
	{	
		final Dialog dialog = new Dialog(getFrame(parentComponent), title);

		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		dialog.add(new Label(message), BorderLayout.CENTER);
		Panel panel = new Panel();
		Button btOk = new Button("OK");
		panel.add(btOk);
		dialog.add(panel, BorderLayout.SOUTH); 
		dialog.pack();
		dialog.addWindowListener(windowListener);
		
		btOk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ev) 
			{
				dialog.hide();
			}
		});		

		dialog.show();
	}
	
	
	private static Frame getFrame(Component parentComponent) 
	{
		Component tmp = parentComponent;
		while (true) {
			if (tmp instanceof Frame) {
				return (Frame) tmp;
			}
			tmp = tmp.getParent();
		}
	}
	
	
}
