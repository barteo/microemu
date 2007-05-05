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
package com.siemens.mp.game;

import javax.microedition.lcdui.*;
import org.microemu.device.DeviceFactory;

/**
 *
 * @author  markus
 * @version
 */
public class TiledBackground extends GraphicObject{
    Image pixels[];
    byte map[];
    int widthInTiles;
    int heightInTiles;
    int posx;
    int posy;
    
    public TiledBackground(byte[] tilePixels, byte[] tileMask, byte[] map, int widthInTiles, int heightInTiles) {
        //        System.out.println("public TiledBackground(byte[] tilePixels, byte[] tileMask, byte[] map, int "+widthInTiles+", int "+heightInTiles+")");
        this(
        com.siemens.mp.ui.Image.createImageFromBitmap(tilePixels,8,tilePixels.length),
        com.siemens.mp.ui.Image.createImageFromBitmap(tileMask,8,tilePixels.length),
        map,
        widthInTiles,
        heightInTiles
        );
    }
    
    public TiledBackground(ExtendedImage tilePixels, ExtendedImage tileMask, byte[] map, int widthInTiles, int heightInTiles) {
        //System.out.println("public TiledBackground(ExtendedImage tilePixels, ExtendedImage tileMask, byte[] map, int widthInTiles, int heightInTiles)");
        this(tilePixels.getImage(), tileMask.getImage(), map, widthInTiles, heightInTiles);
    }
    
    public TiledBackground(Image tilePixels, Image tileMask, byte[] map, int widthInTiles, int heightInTiles) {
        System.out.println("public TiledBackground(Image tilePixels, Image tileMask, byte[] map, int "+widthInTiles+", int "+heightInTiles+")");
        
        this.map=map;
        this.heightInTiles=heightInTiles;
        this.widthInTiles=widthInTiles;
        
        pixels=new Image[tilePixels.getHeight()/8+3];
        pixels[0]=Image.createImage(8,8);
        pixels[1]=Image.createImage(8,8);
        pixels[2]=Image.createImage(8,8);
        pixels[2].getGraphics().fillRect(0,0,8,8);
        
        for (int i=0;i<this.pixels.length-3;i++) {
            Image img=Image.createImage(8,8);
            
            img.getGraphics().drawImage(tilePixels, 0, -i*8,0);
            pixels[i+3]=img;
        }
        
        /*for (int y=0;y<heightInTiles;y++) {
            for(int x=0;x<widthInTiles;x++) {
                System.out.print(" "+map[y*widthInTiles+x]);
            }
            System.out.println();
        }*/
        
    }
    
    
    
    public void setPositionInMap(int x, int y) {
        posx=x;
        posy=y;
    }
    
    protected void paint(Graphics g) {
        for (int y=posy/8;y<heightInTiles;y++) {
            for(int x=posx/8;x<widthInTiles;x++) {
                if (-posx+x*8>DeviceFactory.getDevice().getDeviceDisplay().getWidth()) break;
                g.drawImage(pixels[map[y*widthInTiles+x]], -posx+x*8,-posy+y*8,0);
            }
            if (-posy+y*8>DeviceFactory.getDevice().getDeviceDisplay().getHeight()) break;
        }
    }
}
