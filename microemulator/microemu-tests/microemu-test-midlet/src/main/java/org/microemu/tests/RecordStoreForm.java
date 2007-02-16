package org.microemu.tests;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class RecordStoreForm extends BaseTestsForm {

	static final Command loadCommand = new Command("Load", Command.ITEM, 1);
	
	static final Command storeCommand = new Command("Store", Command.ITEM, 2);
	
	static final Command deleteCommand = new Command("Delete", Command.ITEM, 3);
	
	static final String recordStoreName = "meTestRecordStore";
	
	TextField textFiled;
	
	StringItem stringItem;
	
	StringItem messageItem;
	
	public RecordStoreForm() {
		super("RecordStore");
		addCommand(loadCommand);
		addCommand(storeCommand);
		addCommand(deleteCommand);
		
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
	
	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == loadCommand) {
				load();
			} else if (c == storeCommand) {
				store();
			} else if (c == deleteCommand) {
				delete();
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
