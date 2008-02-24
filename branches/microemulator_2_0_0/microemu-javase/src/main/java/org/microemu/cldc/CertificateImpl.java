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

import java.security.cert.X509Certificate;

import javax.microedition.pki.Certificate;

public class CertificateImpl implements Certificate {
	
	private X509Certificate cert;

	public CertificateImpl(X509Certificate cert) {
		this.cert = cert;
	}

	public String getIssuer() {
		return cert.getIssuerDN().getName();
	}

	public long getNotAfter() {
		return cert.getNotAfter().getTime();
	}

	public long getNotBefore() {
		return cert.getNotBefore().getTime();
	}

	public String getSerialNumber() {
		return cert.getSerialNumber().toString();
	}

	public String getSigAlgName() {
		return cert.getSigAlgName();
	}

	public String getSubject() {
		return cert.getSubjectDN().getName();
	}

	public String getType() {
		return cert.getType();
	}

	public String getVersion() {
		return Integer.toString(cert.getVersion());
	}

}
