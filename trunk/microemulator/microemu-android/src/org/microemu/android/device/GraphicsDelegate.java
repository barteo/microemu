package org.microemu.android.device;

import javax.microedition.lcdui.Image;

import android.graphics.Bitmap;
import android.graphics.Rect;

public interface GraphicsDelegate
{

    void drawImage(Image img, int x, int y, int anchor);

    void drawLine(int x1, int y1, int x2, int y2);

    void drawRect(int x, int y, int width, int height);

    void drawRegionDelegate(Bitmap img, Rect srcRect, Rect dstRect);

    void drawSubstringDelegate(String str, int offset, int len, int newx, int newy, int anchor);

    void fillRect(int x, int y, int width, int height);

    void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3);

    void setClip(int x, int y, int width, int height);

    void translate(int x, int y);

}
