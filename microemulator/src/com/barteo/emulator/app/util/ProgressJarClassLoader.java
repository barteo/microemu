/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.app.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class ProgressJarClassLoader extends ClassLoader
{
  Hashtable entries = new Hashtable();

  Vector notLoadedRepositories = new Vector();
  
  ProgressListener listener;
  
  
  public ProgressJarClassLoader()
  {
    super();
  }
  

  public ProgressJarClassLoader(ClassLoader parent)
  {
    super(parent);
  }
  

  public void addRepository(URL url)
  {
    notLoadedRepositories.add(url);
  }


  public Class findClass(String name)
      throws ClassNotFoundException
  {
    Class result = findLoadedClass(name);
    if (result == null) {
      byte[] b = loadClassData(name);    
      result = defineClass(name, b, 0, b.length);
    }
    
    return result;
  }

  
  public InputStream getResourceAsStream(String name)
  {
    loadRepositories();
    
    String newname;
    if (name.startsWith("/")) {
      newname = name.substring(1);
    } else {
      newname = name;
    }
    byte[] tmp = (byte[]) entries.get(newname);
    if (tmp != null) {
      InputStream is = new ByteArrayInputStream(tmp);
      return is;
    }

    return getClass().getResourceAsStream(name);
  }
    
  
  private byte[] loadClassData(String name)
      throws ClassNotFoundException
  {
    loadRepositories();
    
    name = name.replace('.', '/') + ".class";
    byte[] result = (byte[]) entries.get(name);
    if (result == null) {
      throw new ClassNotFoundException(name);
    }
    
    return result;
  }
  
  
  public void setProgressListener(ProgressListener listener)
  {
    this.listener = listener;
  }
  
  
  private void fireProgressListener(ProgressEvent event)
  {
    if (listener != null) {
      listener.stateChanged(event);
    }
  }
  
  
  private synchronized void loadRepositories()
  {
    for (Enumeration en = notLoadedRepositories.elements(); en.hasMoreElements(); ) {
      URL repositoryEntry = (URL) en.nextElement();
      byte[] cache = new byte[1024];
      ProgressEvent event = new ProgressEvent();
      int progress = 0;
    
      try {
        URLConnection conn = repositoryEntry.openConnection();
        int size = conn.getContentLength();
        JarInputStream jis = new JarInputStream(conn.getInputStream());
        while (true) {
          JarEntry entry = jis.getNextJarEntry();          
          if (entry != null) {
            if (!entry.isDirectory()) {
              int b; 
              int i = 0;
              while ((b = jis.read()) != -1) {
                cache[i] = (byte) b;
                i++;
                if (i >= cache.length) {
                  byte newcache[] = new byte[cache.length + 1024];
                  System.arraycopy(cache, 0, newcache, 0, cache.length);
                  cache = newcache;
                }
              }
              byte[] tmp = new byte[i];
              System.arraycopy(cache, 0, tmp, 0, i);
              entries.put(entry.getName(), tmp);
              progress += entry.getCompressedSize();
              event.setCurrent(progress);
              event.setMax(size);
              fireProgressListener(event);
            }
          } else {
            break;
          }
        }
      } catch (IOException ex) {
        System.err.println(ex);
      }
      notLoadedRepositories.remove(repositoryEntry);
    }    
  }
  
}
