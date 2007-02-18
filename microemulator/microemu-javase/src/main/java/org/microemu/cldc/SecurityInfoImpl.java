/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.cldc;

import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import org.microemu.log.Logger;

public class SecurityInfoImpl implements SecurityInfo {

	private String cipherSuite;
	private String protocolName;
	private Certificate certificate;

	public SecurityInfoImpl(String cipherSuite, String protocolName, Certificate certificate) {
		this.cipherSuite = cipherSuite;
		this.protocolName = protocolName;
		this.certificate = certificate;
	}

	public String getCipherSuite() {
		return cipherSuite;
	}

	public String getProtocolName() {
		if (protocolName.startsWith("TLS")) {
			return "TLS";
		} else if (protocolName.startsWith("SSL")) {
			return "SSL";
		} else {
			// TODO Auto-generated method stub
			try {
				throw new RuntimeException();
			} catch (RuntimeException ex) {
				Logger.error(ex);
				throw ex;
			}
		}
	}

	public String getProtocolVersion() {
		if (protocolName.startsWith("TLS")) {
			return "3.1";
		} else if (getProtocolName().equals("SSL")) {
			return "3.0";
		} else {
			// TODO Auto-generated method stub
			try {
				throw new RuntimeException();
			} catch (RuntimeException ex) {
				Logger.error(ex);
				throw ex;
			}
		}
	}

	public Certificate getServerCertificate() {
		return certificate;
	}

}
