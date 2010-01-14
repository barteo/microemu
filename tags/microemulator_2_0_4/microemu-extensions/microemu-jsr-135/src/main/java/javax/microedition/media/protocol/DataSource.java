/**
 *  MicroEmulator
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
package javax.microedition.media.protocol;

import java.io.IOException;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

public abstract class DataSource implements Controllable {

	private String sourceLocator;

	public DataSource(String locator) {
		sourceLocator = locator;
	}

	public String getLocator() {
		return sourceLocator;
	}

	public abstract String getContentType();

	public abstract void connect() throws IOException;

	public abstract void disconnect();

	public abstract void start() throws IOException;

	public abstract void stop() throws IOException;

	public abstract SourceStream[] getStreams();

	public abstract Control[] getControls();

	public abstract Control getControl(String s);

}
