package org.microemu.device.impl;

public class Rectangle
{
    public int x;
    public int y;
    public int width;
    public int height;
    
    
    public Rectangle()
    {        
    }
    
    
    public Rectangle(Rectangle rect)
    {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }
    
}
