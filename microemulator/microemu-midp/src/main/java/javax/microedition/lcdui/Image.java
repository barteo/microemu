/*
 * MicroEmulator
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 *
 * Contributor(s):
 *   3GLab
 *   Andres Navarro
 */

package javax.microedition.lcdui;
import java.io.IOException;
import org.microemu.device.DeviceFactory;


public class Image
{

//    TODO there should be a public constructors
//    private Image() {
//        
//    }
    
	public static Image createImage(int width, int height)
	{
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		return DeviceFactory.getDevice().getDeviceDisplay().createImage(width, height, false, 0x00FFFFFF);
	}

	public static Image createImage(String name) throws IOException
	{
		return DeviceFactory.getDevice().getDeviceDisplay().createImage(name);
	}

	public static Image createImage(Image source)
	{
		return DeviceFactory.getDevice().getDeviceDisplay().createImage(source);
	}

	public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
	{
		return DeviceFactory.getDevice().getDeviceDisplay().createImage(imageData, imageOffset, imageLength);
	}

	public Graphics getGraphics()
	{
		throw new IllegalStateException("Image is immutable");
	}

        public int getHeight()
	{
		return 0;
	}

	public int getWidth()
	{
		return 0;
	}

	public boolean isMutable()
	{
		return false;
	}

	// Andres Navarro
	// MIDP2 Methods

        public void getRGB(int []argb, int offset, int scanlenght,
			int x, int y, int width, int height) {
		// Implemented in Immutable and Mutable image
	}

        public static Image createImage(java.io.InputStream stream) throws IOException {
            return DeviceFactory.getDevice().getDeviceDisplay().createImage(stream);
        }
        
        public static Image createImage(Image image, int x, int y, 
                int width, int height, int transform) {
            return DeviceFactory.getDevice().getDeviceDisplay().createImage(
                    image, x, y, width, height, transform);
        }
        
        public static Image createRGBImage(int[] rgb, int width, int height, 
                boolean processAlpha) {
            return DeviceFactory.getDevice().getDeviceDisplay().createRGBImage(rgb, 
                    width, height, processAlpha);
        }
	// Andres Navarro
}
