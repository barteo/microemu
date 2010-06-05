package org.microemu.android.device;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import android.graphics.Canvas;
import android.util.Log;

public class DebugDisplayGraphics extends AndroidDisplayGraphics
{

    @Override
    public void clipRect(int x, int y, int width, int height)
    {
        Log.d("DebugDisplayGraphics", "clipRect");
        super.clipRect(x, y, width, height);
    }

    @Override
    public void copyArea(int xSrc, int ySrc, int width, int height, int xDest, int yDest, int anchor)
    {
        Log.d("DebugDisplayGraphics", "copyArea");
        super.copyArea(xSrc, ySrc, width, height, xDest, yDest, anchor);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        Log.d("DebugDisplayGraphics", "drawArc");
        super.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawChar(char character, int x, int y, int anchor)
    {
        Log.d("DebugDisplayGraphics", "drawChar");
        super.drawChar(character, x, y, anchor);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y, int anchor)
    {
        Log.d("DebugDisplayGraphics", "drawChars");
        super.drawChars(data, offset, length, x, y, anchor);
    }

    @Override
    public void drawImage(Image img, int x, int y, int anchor)
    {
        Log.d("DebugDisplayGraphics" , "drawImage: " + x +"+"+ y +"+"+ anchor +" ("+ img.getWidth() +"+"+ img.getHeight() +")");
        super.drawImage(img, x, y, anchor);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        Log.d("DebugDisplayGraphics", "drawLine: " + x1 +"+"+ y1 +"+"+ x2 +"+"+ y2);
        super.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawRect(int x, int y, int width, int height)
    {
        Log.d("DebugDisplayGraphics", "drawRect");
        super.drawRect(x, y, width, height);
    }

    @Override
    public void drawRegion(Image src, int xSrc, int ySrc, int width, int height, int transform, int xDst, int yDst,
            int anchor)
    {
        Log.d("DebugDisplayGraphics", "drawRegion");
        super.drawRegion(src, xSrc, ySrc, width, height, transform, xDst, yDst, anchor);
    }

    @Override
    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height,
            boolean processAlpha)
    {
        Log.d("DebugDisplayGraphics", "drawRGB");
        super.drawRGB(rgbData, offset, scanlength, x, y, width, height, processAlpha);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        Log.d("DebugDisplayGraphics", "drawRoundRect");
        super.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawString(String str, int x, int y, int anchor)
    {
        Log.d("DebugDisplayGraphics", "drawString");
        super.drawString(str, x, y, anchor);
    }

    @Override
    public void drawSubstring(String str, int offset, int len, int x, int y, int anchor)
    {
        Log.d("DebugDisplayGraphics", "drawSubstring");
        super.drawSubstring(str, offset, len, x, y, anchor);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        Log.d("DebugDisplayGraphics", "fillArc");
        super.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillRect(int x, int y, int width, int height)
    {
        Log.d("DebugDisplayGraphics", "fillRect: " + x +"+"+ y +"+"+ width +"+"+ height);
        super.fillRect(x, y, width, height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        Log.d("DebugDisplayGraphics", "fillRoundRect");
        super.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3)
    {
        Log.d("DebugDisplayGraphics", "fillTriangle");
        super.fillTriangle(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public int getBlueComponent()
    {
        Log.d("DebugDisplayGraphics", "getBlueComponent");
        return super.getBlueComponent();
    }

    @Override
    public int getClipHeight()
    {
        Log.d("DebugDisplayGraphics", "getClipHeight");
        return super.getClipHeight();
    }

    @Override
    public int getClipWidth()
    {
        Log.d("DebugDisplayGraphics", "getClipWidth");
        return super.getClipWidth();
    }

    @Override
    public int getClipX()
    {
        Log.d("DebugDisplayGraphics", "getClipX");
        return super.getClipX();
    }

    @Override
    public int getClipY()
    {
        Log.d("DebugDisplayGraphics", "getClipY");
        return super.getClipY();
    }

    @Override
    public int getColor()
    {
        Log.d("DebugDisplayGraphics", "getColor:");
        return super.getColor();
    }

    @Override
    public int getDisplayColor(int color)
    {
        Log.d("DebugDisplayGraphics", "getDisplayColor");
        return super.getDisplayColor(color);
    }

    @Override
    public Font getFont()
    {
        Log.d("DebugDisplayGraphics", "getFont");
        return super.getFont();
    }

    @Override
    public int getGrayScale()
    {
        Log.d("DebugDisplayGraphics", "getGrayScale");
        return super.getGrayScale();
    }

    @Override
    public int getGreenComponent()
    {
        Log.d("DebugDisplayGraphics", "getGreenComponent");
        return super.getGreenComponent();
    }

    @Override
    public int getRedComponent()
    {
        Log.d("DebugDisplayGraphics", "getRedComponent");
        return super.getRedComponent();
    }

    @Override
    public int getStrokeStyle()
    {
        Log.d("DebugDisplayGraphics", "getStrokeStyle");
        return super.getStrokeStyle();
    }

    @Override
    public int getTranslateX()
    {
        Log.d("DebugDisplayGraphics", "getTranslateX");
        return super.getTranslateX();
    }

    @Override
    public int getTranslateY()
    {
        Log.d("DebugDisplayGraphics", "getTranslateY");
        return super.getTranslateY();
    }

    @Override
    public void setClip(int x, int y, int width, int height)
    {
        Log.d("DebugDisplayGraphics", "setClip: " + x +"+"+ y +"+"+ width +"+"+ height);
        super.setClip(x, y, width, height);
    }

    @Override
    public void setColor(int red, int green, int blue)
    {
        Log.d("DebugDisplayGraphics", "setColor: " + red +"+"+ green +"+"+ blue);
        super.setColor(red, green, blue);
    }

    @Override
    public void setColor(int RGB)
    {
        Log.d("DebugDisplayGraphics", "setColor: " + RGB);
        super.setColor(RGB);
    }

    @Override
    public void setFont(Font font)
    {
        Log.d("DebugDisplayGraphics", "setFont");
        super.setFont(font);
    }

    @Override
    public void setGrayScale(int grey)
    {
        Log.d("DebugDisplayGraphics", "setGrayScale");
        super.setGrayScale(grey);
    }

    @Override
    public void setStrokeStyle(int style)
    {
        Log.d("DebugDisplayGraphics", "setStrokeStyle");
        super.setStrokeStyle(style);
    }

    @Override
    public void translate(int x, int y)
    {
        Log.d("DebugDisplayGraphics", "translate");
        super.translate(x, y);
    }

}
