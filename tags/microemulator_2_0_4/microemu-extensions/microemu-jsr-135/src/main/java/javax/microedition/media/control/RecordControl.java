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
package javax.microedition.media.control;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface RecordControl extends Control {

	public abstract void setRecordStream(OutputStream outputstream);

	public abstract void setRecordLocation(String s) throws IOException, MediaException;

	public abstract String getContentType();

	public abstract void startRecord();

	public abstract void stopRecord();

	public abstract void commit() throws IOException;

	public abstract int setRecordSizeLimit(int i) throws MediaException;

	public abstract void reset() throws IOException;
}
