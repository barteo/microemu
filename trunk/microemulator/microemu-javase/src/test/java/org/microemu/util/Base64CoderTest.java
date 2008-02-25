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
package org.microemu.util;

import junit.framework.TestCase;

/**
 * @author vlads
 * @author <a href="mailto:russgold@acm.org">Russell Gold</a>
 * @author <a href="mailto:mtarruella@silacom.com">Marcos Tarruella</a> 
 *
 */
public class Base64CoderTest extends TestCase {


    public void testEncode() {
        assertEquals( "Result of encoding", "QWxhZGRpbjpvcGVuIHNlc2FtZQ==", Base64Coder.encode( "Aladdin:open sesame" ) );
        assertEquals( "Result of encoding", "QWRtaW46Zm9vYmFy",             Base64Coder.encode( "Admin:foobar" ) );
    }


    public void testDecode() {
        assertEquals( "Result of decoding", "Aladdin:open sesame", Base64Coder.decode( "QWxhZGRpbjpvcGVuIHNlc2FtZQ==" ) );
        assertEquals( "Result of decoding", "Admin:foobar",        Base64Coder.decode( "QWRtaW46Zm9vYmFy" ) );
    }

	private void verifyEncodeDecode(byte[] value) {
		char[] chars = Base64Coder.encode(value);
		byte[] decodedValue = Base64Coder.decode(chars);
		assertEquals("Data as expected length", value.length, decodedValue.length);
		for(int i = 0; i < value.length; i++ ) {
			assertEquals("Data as expected", value[i], decodedValue[i]);	
		}
	}
	
	public void testAllBytes() {
		final String message = "Wrong number...";
		int len = message.getBytes().length;
		byte[] data = new byte[len + 256];
		byte b = -127;
		for(int i = len; i < data.length; i++ ) {
			data[i] = b;
			b ++;
		}
		verifyEncodeDecode(data);
	}
}
