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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;

public class MyManager
{

   private MyManager()
   { 
   }
   
   public static void init() throws Exception
   {
      OpenALManager.init();
      sounds.Sounds.init();
   }
   
   public static Player createPlayer(String resource) throws Exception
   {
      int index = resource.indexOf(':');
      String resourceLocation = "." + resource.substring(index + 1);
      File file = new File(resourceLocation);
      FileInputStream inputStream = new FileInputStream(file);

      if(inputStream == null)
      {
         throw new Exception("Unable to obtain: " + resource);
      }

      return new PCWavPlayer(inputStream);
   }
   
   public static void shutdown()
   {
      OpenALManager.shutdown();
   }
   
   public static boolean isInitialized()
   {
      return OpenALManager.isInitialized();
   }

}

