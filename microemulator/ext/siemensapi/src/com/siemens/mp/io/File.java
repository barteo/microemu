/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
package com.siemens.mp.io;

import javax.microedition.midlet.*;

/**
 *
 * @author  markus
 * @version
 */
public class File {
    
    public File() {
    }
    
    public int close(int fileDescriptor) {
        System.out.println("public int close(int fileDescriptor)");
        return 0;
    }
    
    public static int copy(java.lang.String source, java.lang.String dest) {
        System.out.println("public static int copy(java.lang.String source, java.lang.String dest)");
        return 0;
    }
    
    public static int debugWrite(java.lang.String fileName, java.lang.String infoString) {
        System.out.println("public static int debugWrite(java.lang.String fileName, java.lang.String infoString)");
        return 0;
    }
    
    public static int delete(java.lang.String fileName) {
        System.out.println("public static int delete(java.lang.String fileName) ");
        return 0;
    }
    
    public static int exists(java.lang.String fileName) {
        System.out.println("public static int exists(java.lang.String fileName)");
        return 0;
    }
    
    public static boolean isDirectory(java.lang.String pathName) {
        return false;
    }
    
    public int length(int fileDescriptor) {
        System.out.println("public int length(int fileDescriptor)");
        return 0;
    }
    
    public static java.lang.String[] list(java.lang.String pathName) {
        System.out.println(" public static java.lang.String[] list(java.lang.String pathName)");
        return null;
    }
    
    public int open(java.lang.String fileName){
        System.out.println("public int open(java.lang.String "+fileName+")");
        return 0;
    }
    
    public int read(int fileDescriptor, byte[] buf, int offset, int numBytes) {
        System.out.println("public int read(int fileDescriptor, byte[] buf, int offset, int numBytes)");
        return 0;
    }
    
    public static int rename(java.lang.String source, java.lang.String dest) {
        System.out.println("public static int rename(java.lang.String source, java.lang.String dest)");
        return 0;
    }
    
    public int seek(int fileDescriptor, int seekpos) {
        System.out.println("public int seek(int fileDescriptor, int seekpos)");
        return 0;
    }
    
    public static int spaceAvailable() {
        System.out.println("public static int spaceAvailable()");
        return 0;
    }
    
    public static void truncate(int fileDescriptor, int size) {
        System.out.println("public static void truncate(int fileDescriptor, int size)");
    }
    
    public int write(int fileDescriptor, byte[] buf, int offset, int numBytes) {
        System.out.println("public int write(int fileDescriptor, byte[] buf, int offset, int numBytes)");
        return 0;
    }
}
