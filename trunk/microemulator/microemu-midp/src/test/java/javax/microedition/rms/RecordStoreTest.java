/**
 * 
 */
package javax.microedition.rms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

/**
 * @author radoshi
 * 
 */
public final class RecordStoreTest extends TestCase {

	private static final class MockRecordStoreManager	implements
														RecordStoreManager {

		private final Hashtable stores = new Hashtable();

		private ExtendedRecordListener recordListener = null;
		
		public void init(MicroEmulator emulator) {			
		}
		
		public String getName() {
			return this.getClass().toString();
		}

		public void deleteRecordStore(String name)	throws RecordStoreNotFoundException,
													RecordStoreException {

			stores.remove(name);
			
			fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_DELETE, name);
		}

		public void deleteStores() {

			stores.clear();
		}

		public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {

			return 64 * 1024 * 1024;
		}

		public void init() {

		}

		public String[] listRecordStores() {

			String[] keys = new String[stores.size()];
			int index = 0;
			for (Enumeration names = stores.keys(); names.hasMoreElements();) {

				keys[index++] = (String) names.nextElement();
			}
			return keys;
		}

		public RecordStore openRecordStore(	String recordStoreName,
											boolean createIfNecessary) throws RecordStoreException {

			if (stores.contains(recordStoreName)) {
				RecordStoreImpl store = (RecordStoreImpl) stores.get(recordStoreName);
				fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_OPEN, recordStoreName);
				return store;
			}

			if (createIfNecessary) {

				RecordStoreImpl store = new RecordStoreImpl(this, recordStoreName);
				if (recordListener != null) {
					store.addRecordListener(recordListener);
				}
				stores.put(recordStoreName, store);
				fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_OPEN, recordStoreName);
				return store;
			}

			throw new RecordStoreNotFoundException("Unable to find store: "
													+ recordStoreName);
		}

		public void deleteRecord(RecordStoreImpl recordStoreImpl, int recordId) 
				throws RecordStoreNotOpenException, RecordStoreException 
		{
		}

		public void saveRecord(RecordStoreImpl recordStoreImpl, int recordId) 
				throws RecordStoreNotOpenException, RecordStoreException 
		{
		}

		public void setRecordListener(ExtendedRecordListener recordListener) {
			this.recordListener = recordListener;
		}

		public void fireRecordStoreListener(int type, String recordStoreName) {
			if (recordListener != null) {
				recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
			}
		}
	}

	private RecordStoreManager rsm;

	private RecordStoreImpl rs;

	protected void setUp() throws Exception {

		super.setUp();

		rsm = new MockRecordStoreManager();
		rs = new RecordStoreImpl(rsm, "test");
		rs.setOpen(true);
	}

	/**
	 * Test method for {@link javax.microedition.rms.RecordStore#getSize()}.
	 * 
	 * @throws RecordStoreException
	 * @throws RecordStoreFullException
	 * @throws RecordStoreNotOpenException
	 */
	public void testGetSize()	throws RecordStoreNotOpenException,
								RecordStoreFullException,
								RecordStoreException {

		rs.addRecord(new byte[100], 0, 100);
		rs.addRecord(new byte[100], 50, 50);

		assertEquals(150, rs.getSize());
	}

	private static int addRecord(RecordStore recordStore, long value)	throws IOException,
																		RecordStoreNotOpenException,
																		RecordStoreException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.writeLong(value);
		byte[] bytes = baos.toByteArray();

		return recordStore.addRecord(bytes, 0, bytes.length);
	}

	private static long extractFromByteArray(byte[] bytes) throws IOException {

		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

		return dis.readLong();
	}

	private RecordComparator recordComparator = new RecordComparator() {

		public int compare(byte[] first, byte[] second) {

			DataInputStream firstDis = new DataInputStream(new ByteArrayInputStream(first));
			DataInputStream secondDis = new DataInputStream(new ByteArrayInputStream(second));

			try {
				long firstLong = firstDis.readLong();
				long secondLong = secondDis.readLong();

				if (firstLong < secondLong) {
					return PRECEDES;
				} else if (firstLong > secondLong) {
					return FOLLOWS;
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			return EQUIVALENT;
		}
	};

	/**
	 * 
	 * 
	 */
	public void testRecordComparator() throws Exception {

		// TODO: this test illustrates that microemu implementation of
		// enumerate records is broken - ordering should be in
		// increasing numeric order, not decreasing

		addRecord(rs, 2);
		addRecord(rs, 5);
		addRecord(rs, 4);
		addRecord(rs, 99);
		addRecord(rs, 3);
		addRecord(rs, 1);

		RecordEnumeration re = rs.enumerateRecords(	null,
													recordComparator,
													false);
		assertEquals(1, extractFromByteArray(re.nextRecord()));
		assertEquals(2, extractFromByteArray(re.nextRecord()));
		assertEquals(3, extractFromByteArray(re.nextRecord()));
		assertEquals(4, extractFromByteArray(re.nextRecord()));
		assertEquals(5, extractFromByteArray(re.nextRecord()));
		assertEquals(99, extractFromByteArray(re.nextRecord()));

		rs.closeRecordStore();
	}

}
