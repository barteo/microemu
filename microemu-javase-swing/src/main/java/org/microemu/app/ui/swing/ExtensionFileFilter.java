/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 */
 
package org.microemu.app.ui.swing;

import java.io.File;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;


public class ExtensionFileFilter extends FileFilter 
{
  
  String description;
  
  Hashtable extensions = new Hashtable(); 
  

  public ExtensionFileFilter(String description)
  {
    this.description = description;
  }
  
  
  public boolean accept(File file) 
  {
    if(file != null) {
	    if(file.isDirectory()) {
        return true;
	    }
      String ext = getExtension(file);
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
