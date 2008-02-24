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
package org.microemu;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author vlads
 * 
 * See src/test/ssl/read-me.txt
 * 
 */
public class SSLContextSetup {
	
	private static boolean initialized = false;
	
	public static synchronized void setUp() {
    	if (initialized) {
    		return;
    	}
    	InputStream is = null;
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            is = SSLContextSetup.class.getResourceAsStream("/test-servers.keystore"); 
            if (is == null) {
            	new Error("keystore not found");
            }
            trustStore.load(is, "microemu2006".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");  
            trustManagerFactory.init(trustStore);  
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, secureRandom);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            initialized = true;
        } catch (Throwable e) {
            throw new Error(e);
        } finally {
        	if (is != null) {
        		try {
					is.close();
				} catch (IOException ignore) {
				}
        	}
        }
    }
}
