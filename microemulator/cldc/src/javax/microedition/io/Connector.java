/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.microemu.cldc.ClosedConnection;

import com.sun.cdc.io.ConnectionBaseInterface;


public class Connector
{

	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;
	
	
	public static Connection open(String name)
      throws IOException
  {
    return open(name, READ_WRITE, false);
  }
  
  
  public static Connection open(String name, int mode)
      throws IOException
  {
    return open(name, mode, false);
  }
  
  
    public static Connection open(String name, int mode, boolean timeouts) throws IOException
    {
        Class cl;
        try {
            cl = Class.forName("org.microemu.cldc." + name.substring(0, name.indexOf(':')) + ".Connection");
            ClosedConnection cn = (ClosedConnection) cl.newInstance();

            return cn.open(name);
        } catch (ClassNotFoundException ex) {
            try {
                cl = Class.forName("com.sun.cdc.io.j2me." + name.substring(0, name.indexOf(':')) + ".Protocol");
                ConnectionBaseInterface base = (ConnectionBaseInterface) cl.newInstance();                
            
                return base.openPrim(name.substring(name.indexOf(':') + 1), mode, timeouts);
            } catch (ClassNotFoundException ex1) {
                System.err.println(ex1);
                throw new ConnectionNotFoundException();
            } catch (InstantiationException ex1) {
                System.err.println(ex);
                throw new ConnectionNotFoundException();
            } catch (IllegalAccessException ex1) {
                System.err.println(ex);
                throw new ConnectionNotFoundException();
            }
        } catch (InstantiationException ex) {
            System.err.println(ex);
            throw new ConnectionNotFoundException();
        } catch (IllegalAccessException ex) {
            System.err.println(ex);
            throw new ConnectionNotFoundException();
        }
    }
  
  
  public static DataInputStream openDataInputStream(String name)
      throws IOException
  {
    InputConnection cn = (InputConnection) open(name);
    
    return cn.openDataInputStream();
  }
  
  
  public static DataOutputStream openDataOutputStream(String name)
      throws IOException
  {
    OutputConnection cn = (OutputConnection) open(name);
    
    return cn.openDataOutputStream();
  }
  
  
  public static InputStream openInputStream(String name)
      throws IOException
  {
    InputConnection cn = (InputConnection) open(name);
    
    return cn.openInputStream();
  }
  
    
	public static OutputStream openOutputStream(String name)
      throws IOException
  {
    OutputConnection cn = (OutputConnection) open(name);
    
    return cn.openOutputStream();
  }
  
}
