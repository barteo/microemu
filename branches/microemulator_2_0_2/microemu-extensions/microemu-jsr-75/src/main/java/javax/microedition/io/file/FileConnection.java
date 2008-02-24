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
package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection {

	public abstract boolean isOpen();

	public abstract InputStream openInputStream() throws IOException;

	public abstract DataInputStream openDataInputStream() throws IOException;

	public abstract OutputStream openOutputStream() throws IOException;

	public abstract DataOutputStream openDataOutputStream() throws IOException;

	public abstract OutputStream openOutputStream(long byteOffset) throws IOException;

	public abstract long totalSize();

	public abstract long availableSize();

	public abstract long usedSize();

	public abstract long directorySize(boolean includeSubDirs) throws IOException;

	public abstract long fileSize() throws IOException;

	public abstract boolean canRead();

	public abstract boolean canWrite();

	public abstract boolean isHidden();

	public abstract void setReadable(boolean readable) throws IOException;

	public abstract void setWritable(boolean writable) throws IOException;

	public abstract void setHidden(boolean hidden) throws IOException;

	public abstract Enumeration list() throws IOException;

	public abstract Enumeration list(String filter, boolean includeHidden) throws IOException;

	public abstract void create() throws IOException;

	public abstract void mkdir() throws IOException;

	public abstract boolean exists();

	public abstract boolean isDirectory();

	public abstract void delete() throws IOException;

	public abstract void rename(String newName) throws IOException;

	public abstract void truncate(long byteOffset) throws IOException;

	public abstract void setFileConnection(String s) throws IOException;

	public abstract String getName();

	public abstract String getPath();

	public abstract String getURL();

	public abstract long lastModified();
}
