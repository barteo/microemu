/*
 *  Copyright (C) 2002 Travis Berthelot
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

package media;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public class OpenALManager
{
   private static boolean isInitialized = false;
   
   public OpenALManager()
   {
   }
   
   public static synchronized void init()
   {
      try
      {
         String[] imps = AL.getImplementations();
         if(imps.length > 0)
         {
            System.out.println("Available devices: ");
            for(int i=0; i<imps.length; i++)
            {
               System.out.println("  " + i + ": " + imps[i]);
            }
         }
      }
      catch (Exception e)
      {
         System.out.println("Unable to query available devices: " + e.getMessage());
      }
      
      try
      {
         AL.create();
         System.out.println("Created OpenAL using default device");
      }
      catch (Exception e)
      {
         System.out.print("Unable to create OpenAL.");
         System.out.println("  Please make sure that OpenAL is available on this system. Exception: " + e);
      }
      OpenALManager.isInitialized = true;
   }
   
   public static synchronized void shutdown()
   {
      if(AL.isCreated())
      {
         OpenALManager.isInitialized = false;
         AL.destroy();
         System.out.println("Destroyed OpenAL");
      }
      else
      {
         System.out.println("Could Not Destroy OpenAL: Not Already Created");
      }
   }
   
   public static synchronized void log()
   {
      int error = AL10.alGetError();
      if(error != AL10.AL_NO_ERROR)
      {
         System.out.println("OpenAL Error: " + AL10.alGetString(error));
      }
   }
   
   public static synchronized boolean isInitialized()
   {
      return OpenALManager.isInitialized;
   }
}

