/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

import junit.framework.TestCase;

/**
 * @author vlads
 *
 */
public class FileConnectionTest extends TestCase {

	static {
		CreateTestFileSystem.init();
	}

	public void testRoot() throws Exception {
		FileConnection fconn = (FileConnection) Connector.open("file:///test/");
	
		assertTrue("should exists", fconn.exists());
		
		assertTrue("is directory", fconn.isDirectory());
		
		assertEquals("", fconn.getName());
		
		assertEquals("/test/", fconn.getPath());
		
		assertEquals("file:///test/", fconn.getURL());
		
		fconn.close();
	}
	
	public void testDirectory() throws Exception {
		FileConnection fconn = (FileConnection) Connector.open("file:///test/dir1/");
	
		assertTrue("should exists", fconn.exists());
		
		assertTrue("is directory", fconn.isDirectory());
		
		assertEquals("dir1/", fconn.getName());
		
		assertEquals("/test/", fconn.getPath());
		
		assertEquals("file:///test/dir1/", fconn.getURL());
		
		fconn.close();
	}
	
	public void testFile() throws Exception {
		FileConnection fconn = (FileConnection) Connector.open("file:///test/dir1/f1.txt");
	
		// If no exception is thrown, then the URI is valid, but the file may or may not exist.
		assertTrue("should exists", fconn.exists());
		
		assertFalse("not directory", fconn.isDirectory());
		
		assertEquals("f1.txt", fconn.getName());
		
		assertEquals("/test/dir1/", fconn.getPath());
		
		assertEquals("file:///test/dir1/f1.txt", fconn.getURL());
		
		fconn.close();
	}
	
	public void testFileWrite() throws Exception {
		final String fileName = "file:///test/dir1/write.txt";
		FileConnection fconn = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		final String testData = "TestData";
		try {
			fconn = (FileConnection) Connector.open(fileName);
			dos = fconn.openDataOutputStream();
			dos.writeChars(testData);
			dos.close();
			dos = null;
			
			dis = fconn.openDataInputStream();
			StringBuffer data = new StringBuffer();
			for(int i = 0; i < testData.length(); i ++) {
				data.append(dis.readChar()); 
			}
			assertEquals(testData, data.toString());
			dis.close();
		} finally {
			if (dos != null) {
				dos.close();
			}
			if (dis != null) {
				dis.close();
			}
			if (fconn != null) {
				fconn.close();
			}
		}
	}

	public void testFileTruncate() throws Exception {
		final String fileName = "file:///test/dir1/truncate.txt";
		FileConnection fconn = null;
		InputStream is = null;
		OutputStream os = null;
		final String testDataBase = "Test";
		final String testData = testDataBase + "Data";
		try {
			fconn = (FileConnection) Connector.open(fileName);
			
			os = fconn.openOutputStream();
			os.write(testData.getBytes());
			os.close();
			os = null;
			
			fconn.truncate(testDataBase.length());
			
			is = fconn.openInputStream();
			StringBuffer data = new StringBuffer();
			for(int i = 0; i < testDataBase.length(); i ++) {
				data.append((char)is.read()); 
			}
			assertEquals(testDataBase, data.toString());
			
			assertEquals("available", 0, is.available());
			
			try {
				int eof = is.read();
				if (eof != -1) {
					fail("End of File expected");
				}
			} catch (IOException eof) {
			}
			is.close();
			
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
			if (fconn != null) {
				fconn.close();
			}
		}
	}
	
	public void testFileAppend() throws Exception {
		final String fileName = "file:///test/dir1/append.txt";
		FileConnection fconn = null;
		InputStream is = null;
		OutputStream os = null;
		final String testDataBase = "Test";
		final String testData = testDataBase + "Data";
		final String testDataAppend = "Append";
		final String testDataAppended = testDataBase +  testDataAppend;
		try {
			fconn = (FileConnection) Connector.open(fileName);
			
			os = fconn.openOutputStream();
			os.write(testData.getBytes());
			os.close();
			os = null;
			
			os = fconn.openOutputStream(testDataBase.length());
			os.write(testDataAppend.getBytes());
			os.close();
			os = null;
			
			is = fconn.openInputStream();
			StringBuffer data = new StringBuffer();
			for(int i = 0; i < testDataAppended.length(); i ++) {
				data.append((char)is.read()); 
			}
			assertEquals(testDataAppended, data.toString());
			is.close();
			
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
			if (fconn != null) {
				fconn.close();
			}
		}
	}
}
