/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.app.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Hashtable;


public class ExtensionFileFilter implements FilenameFilter 
{
  
	String description;
  
	Hashtable extensions = new Hashtable(); 
  

	public ExtensionFileFilter(String description)
	{
		this.description = description;
	}
  
  
	public boolean accept(File dir, String name) 
	{
		if(dir != null) {
			if(dir.isDirectory()) {
				return true;
			}
			String ext = getExtension(dir);
			if(ext != null && extensions.get(ext) != null) {
				return true;
			}
		}
    
		return false;
	}
  
  
	public void addExtension(String extension)
	{
		extensions.put(extension.toLowerCase(), this);
	}

  
	public String getDescription() 
	{
		return description;
	}

  
	String getExtension(File file) 
	{
		if (file != null) {
			String filename = file.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
  
		return null;
	}
  
}
