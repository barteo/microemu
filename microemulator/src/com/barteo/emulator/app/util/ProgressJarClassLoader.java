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


public class ProgressJarClassLoader extends URLClassLoader
{
  Hashtable entries = new Hashtable();

  Vector notLoadedRepositories = new Vector();
  
  
  public ProgressJarClassLoader()
  {
    super(new URL[0]);
  }
  
  
  public void addRepository(URL url)
  {
    super.addURL(url);
    notLoadedRepositories.add(url);
  }


  public Class findClass(String name) 
  {
    byte[] b = loadClassData(name);
    
    return defineClass(name, b, 0, b.length);
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
System.out.println("getResourceAsStream:" + newname + "+" + tmp);
      InputStream is = new ByteArrayInputStream(tmp);
System.out.println(is);      
      return is;
    }

    return getClass().getResourceAsStream(name);
  }
    
  
  private byte[] loadClassData(String name) 
  {
    loadRepositories();
    
    name = name.replace('.', '/') + ".class";
    byte[] result = (byte[]) entries.get(name);
    
System.out.println(name + "+" + result);

    return result;
  }
  
  
  private synchronized void loadRepositories()
  {
    for (Enumeration en = notLoadedRepositories.elements(); en.hasMoreElements(); ) {
      URL url = (URL) en.nextElement();
System.out.print(url);
      byte[] cache = new byte[1024];
    
      try {
        URLConnection conn = url.openConnection();
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
System.out.println("."+entry.getName());
            }
          } else {
System.out.println();
            break;
          }
        }
      } catch (IOException ex) {
        System.err.println(ex);
      }
      notLoadedRepositories.remove(url);
    }    
  }

}
