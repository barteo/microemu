/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
