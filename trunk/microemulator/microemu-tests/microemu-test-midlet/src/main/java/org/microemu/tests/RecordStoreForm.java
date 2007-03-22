/**
 *  MicroEmulator
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
package org.microemu.tests;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class RecordStoreForm extends BaseTestsForm {

	static final Command loadCommand = new Command("Load", Command.ITEM, 1);
	
	static final Command storeCommand = new Command("Store", Command.ITEM, 2);
	
	static final Command deleteCommand = new Command("Delete", Command.ITEM, 3);
	
	static final Command listCommand = new Command("List", Command.ITEM, 4);
	
	static final String recordStoreName = "meTestRecordStore";
	
	TextField textFiled;
	
	StringItem stringItem;
	
	StringItem messageItem;
	
	int savedRecordId = -1;
	
	public RecordStoreForm() {
		super("RecordStore");
		addCommand(loadCommand);
		addCommand(storeCommand);
		addCommand(deleteCommand);
		addCommand(listCommand);
		
		textFiled = new TextField("Enter data", "", 128, TextField.ANY);
		append(textFiled);
		
		stringItem = new StringItem("Loaded data", "n/a");
		append(stringItem);
		
		messageItem = new StringItem("Info:", "Use menu to load and store data");
		append(messageItem);
    }
	
	private void load() {
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(recordStoreName, false);
			String message;
			if (recordStore.getNumRecords() > 0) {
				int recordId = 1;
				System.out.println("getRecord " + recordId);
				byte[] data = recordStore.getRecord(recordId);
				message = recordId + " loaded";
				stringItem.setText(new String(data));
				
				if (savedRecordId != recordId) {
					messageItem.setText("recordId " + recordId + " is different " + savedRecordId);
				}
				
			} else {
				message = "recordStore empty";
				stringItem.setText("");
			}
			messageItem.setText(message);
		} catch (Throwable e) {
			System.out.println("error accessing RecordStore");
			e.printStackTrace();
			messageItem.setText(e.toString());
		} finally {
			closeQuietly(recordStore);
		}
	}
	
	private void store() {
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(recordStoreName, true);
			StringBuffer buf = new StringBuffer();
			buf.append("[").append(textFiled.getString()).append("]");
			byte[] data = buf.toString().getBytes();
			int recordId;
			String message;
			if (recordStore.getNumRecords() > 0) {
				recordId = 1;
				System.out.println("setRecord " + recordId);
				recordStore.setRecord(recordId, data, 0, data.length);
				message = recordId + " updated"; 
			} else {
				recordId = recordStore.addRecord(data, 0, data.length);
				message = recordId + " created";
			}
			savedRecordId = recordId; 
			messageItem.setText(message);
		} catch (Throwable e) {
			System.out.println("error accessing RecordStore");
			e.printStackTrace();
			messageItem.setText(e.toString());
		} finally {
			closeQuietly(recordStore);
		}
	}
	
	private void delete() {
		try {
			RecordStore.deleteRecordStore(recordStoreName);
			messageItem.setText("removed " + recordStoreName);
		} catch (Throwable e) {
			System.out.println("error accessing RecordStore");
			e.printStackTrace();
			messageItem.setText(e.toString());
		} 	
	}
	
	public class RecordStoreList extends List implements CommandListener {

		public RecordStoreList() {
			super("names of record stores", List.IMPLICIT);
			this.setCommandListener(this);
			addCommand(DisplayableUnderTests.backCommand);
		}

		/* (non-Javadoc)
		 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
		 */
		public void commandAction(Command c, Displayable d) {
			if (c == DisplayableUnderTests.backCommand) {
				Manager.midletInstance.setCurrentDisplayable(RecordStoreForm.this);
			}
		}
	}
	
	private void list() {
		try {
			String[] items = RecordStore.listRecordStores();
			messageItem.setText("listed " + items.length);
			List list = new RecordStoreList();
			for (int i = 0; i < items.length; i++) {
				list.append(items[i], null);				
			}
			Manager.midletInstance.setCurrentDisplayable(list);
		} catch (Throwable e) {
			System.out.println("error accessing RecordStore");
			e.printStackTrace();
			messageItem.setText(e.toString());
		} 
	}
	
	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == loadCommand) {
				load();
			} else if (c == storeCommand) {
				store();
			} else if (c == deleteCommand) {
				delete();
			} else if (c == listCommand) {
				list();
			}
		}
		super.commandAction(c, d);
	}
	
	public static void closeQuietly(RecordStore recordStore) {
		try {
			if (recordStore != null) {
				recordStore.closeRecordStore();
			}
		} catch (RecordStoreException ignore) {
			// ignore
		}
	}
}
