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
package com.siemens.mp.color_game;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class Sprite extends Layer {
    
    public static final int TRANS_NONE = 0;
    public static final int TRANS_ROT90 = 5;
    public static final int TRANS_ROT180 = 3;
    public static final int TRANS_ROT270 = 6;
    public static final int TRANS_MIRROR = 2;
    public static final int TRANS_MIRROR_ROT90 = 7;
    public static final int TRANS_MIRROR_ROT180 = 1;
    public static final int TRANS_MIRROR_ROT270 = 4;
    
    public Sprite(Image image) {
        super(image.getWidth(), image.getHeight());
        
        initializeFrames(image, image.getWidth(), image.getHeight(), false);
        
        // initialize collision rectangle
        initCollisionRectBounds();
    }
    
    public Sprite(Image image, int frameWidth, int frameHeight) {
        
        super(frameWidth, frameHeight);
        // if img is null img.getWidth() will throw NullPointerException
        if ((frameWidth < 1 || frameHeight < 1) ||
        ((image.getWidth() % frameWidth) != 0) ||
        ((image.getHeight() % frameHeight) != 0)) {
            throw new IllegalArgumentException();
        }
        
        // construct the array of images that
        // we use as "frames" for the sprite.
        // use default frame , sequence index = 0
        initializeFrames(image, frameWidth, frameHeight, false);
        
        // initialize collision rectangle
        initCollisionRectBounds();
        
    }
    
    public Sprite(Sprite s) {
        
        super(s != null ? s.getWidth() : 0,
        s != null ? s.getHeight() : 0);
        
        if (s == null) {
            throw new NullPointerException();
        }
        
        this.sourceImage = Image.createImage(s.sourceImage);
        
        this.numberFrames = s.numberFrames;
        
        this.frameCoordsX = new int[this.numberFrames];
        this.frameCoordsY = new int[this.numberFrames];
        
        System.arraycopy(s.frameCoordsX, 0,
        this.frameCoordsX, 0,
        s.getRawFrameCount());
        
        System.arraycopy(s.frameCoordsY, 0,
        this.frameCoordsY, 0,
        s.getRawFrameCount());
        
        this.x = s.getX();
        this.y = s.getY();
        
        // these fields are set by defining a reference point
        this.dRefX = s.dRefX;
        this.dRefY = s.dRefY;
        
        // these fields are set when defining a collision rectangle
        this.collisionRectX = s.collisionRectX;
        this.collisionRectY = s.collisionRectY;
        this.collisionRectWidth = s.collisionRectWidth;
        this.collisionRectHeight = s.collisionRectHeight;
        
        // these fields are set when creating a Sprite from an Image
        this.srcFrameWidth = s.srcFrameWidth;
        this.srcFrameHeight = s.srcFrameHeight;
        
       
        this.setVisible(s.isVisible());
        
        this.frameSequence = new int[s.getFrameSequenceLength()];
        this.setFrameSequence(s.frameSequence);
        this.setFrame(s.getFrame());
        
        this.setRefPixelPosition(s.getRefPixelX(), s.getRefPixelY());
        
    }
    
    public void defineReferencePixel(int x, int y) {
        dRefX = x;
        dRefY = y;
    }
    
    
    public void setRefPixelPosition(int x, int y) {
        
        // update this.x and this.y
        this.x = x - dRefX;
        this.y = y - dRefY;
        
    }
    
    
    public int getRefPixelX() {
        return this.x + dRefX;
    }
    
    
    public int getRefPixelY() {
        return this.y + dRefY;
    }
    
    public void setFrame(int sequenceIndex) {
        if (sequenceIndex < 0 || sequenceIndex >= frameSequence.length) {
            throw new IndexOutOfBoundsException();
        }
        this.sequenceIndex = sequenceIndex;
    }
    
    
    public final int getFrame() {
        return sequenceIndex;
    }
    
    
    public int getRawFrameCount() {
        return numberFrames;
    }
    
    
    public int getFrameSequenceLength() {
        return frameSequence.length;
    }
    
    
    public void nextFrame() {
        sequenceIndex = (sequenceIndex + 1) % frameSequence.length;
    }
    
    
    public void prevFrame() {
        if (sequenceIndex == 0) {
            sequenceIndex = frameSequence.length - 1;
        } else {
            sequenceIndex--;
        }
    }
    
    
    public final void paint(Graphics g) {
        if (g == null) {
            throw new NullPointerException();
        }
        
        if (visible) {
            
            int clipX = g.getClipX();
            int clipY = g.getClipY();
            int clipW = g.getClipWidth();
            int clipH =  g.getClipHeight();
            
            g.setClip(this.x, this.y, this.srcFrameWidth, this.srcFrameHeight);
            
            g.drawImage(sourceImage, this.x-frameCoordsX[frameSequence[sequenceIndex]],
            this.y-frameCoordsY[frameSequence[sequenceIndex]],Graphics.TOP | Graphics.LEFT);
            
            g.setClip(clipX, clipY, clipW, clipH);
        }
        
    }
    
    
    public void setFrameSequence(int sequence[]) {
        
        if (sequence == null) {
            // revert to the default sequence
            sequenceIndex = 0;
            customSequenceDefined = false;
            frameSequence = new int[numberFrames];
            // copy frames indices into frameSequence
            for (int i = 0; i < numberFrames; i++) {
                frameSequence[i] = i;
            }
            return;
        }
        
        if (sequence.length < 1) {
            throw new IllegalArgumentException();
        }
        
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] < 0 || sequence[i] >= numberFrames) {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
        customSequenceDefined = true;
        frameSequence = new int[sequence.length];
        System.arraycopy(sequence, 0, frameSequence, 0, sequence.length);
        sequenceIndex = 0;
    }
    
    
    public void setImage(Image img, int frameWidth, int frameHeight) {
        
        // if image is null image.getWidth() will throw NullPointerException
        if ((frameWidth < 1 || frameHeight < 1) ||
        ((img.getWidth() % frameWidth) != 0) ||
        ((img.getHeight() % frameHeight) != 0)) {
            throw new IllegalArgumentException();
        }
        
        int noOfFrames =
        (img.getWidth() / frameWidth)*(img.getHeight() / frameHeight);
        
        boolean maintainCurFrame = true;
        if (noOfFrames < numberFrames) {
            // use default frame , sequence index = 0
            maintainCurFrame = false;
            customSequenceDefined = false;
        }
        
        if (! ((srcFrameWidth == frameWidth) &&
        (srcFrameHeight == frameHeight))) {
            
            // computing is the location
            // of the reference pixel in the painter's coordinate system.
            // and then use this to find x and y position of the Sprite
            int oldX = this.x + dRefX;
            
            int oldY = this.y + dRefY;
            
            
            setWidthImpl(frameWidth);
            setHeightImpl(frameHeight);
            
            initializeFrames(img, frameWidth, frameHeight, maintainCurFrame);
            
            // initialize collision rectangle
            initCollisionRectBounds();
            
            // set the new x and y position of the Sprite
            this.x = oldX - dRefX;
            
            this.y = oldY - dRefY;

        } else {
            // just reinitialize the animation frames.
            initializeFrames(img, frameWidth, frameHeight, maintainCurFrame);
        }
        
    }
    
    
    public void defineCollisionRectangle(int x, int y, int width, int height) {
        
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        
        collisionRectX = x;
        collisionRectY = y;
        collisionRectWidth = width;
        collisionRectHeight = height;
    }
    
    //transfrom is ignored
    public void setTransform(int transform) {
    }
    
    //pixellevel nor supported
    public final boolean collidesWith(Sprite s, boolean pixelLevel) {
        
        // check if either of the Sprite's are not visible
        if (!(s.visible && this.visible)) {
            return false;
        }
        
        // these are package private
        // and can be accessed directly
        int otherLeft    = s.x + s.collisionRectX;
        int otherTop     = s.y + s.collisionRectY;
        int otherRight   = otherLeft + s.collisionRectWidth;
        int otherBottom  = otherTop  + s.collisionRectHeight;
        
        int left   = this.x + this.collisionRectX;
        int top    = this.y + this.collisionRectY;
        int right  = left + this.collisionRectWidth;
        int bottom = top  + this.collisionRectHeight;
        
        // check if the collision rectangles of the two sprites intersect
        if (intersectRect(otherLeft, otherTop, otherRight, otherBottom,
        left, top, right, bottom)) {
            
            
            return true;
        }
        return false;
        
    }
    
    //pixellevel not supported
    public final boolean collidesWith(TiledLayer t, boolean pixelLevel) {
        
        // check if either this Sprite or the TiledLayer is not visible
        if (!(t.visible && this.visible)) {
            return false;
        }
        
        // dimensions of tiledLayer, cell, and
        // this Sprite's collision rectangle
        
        // these are package private
        // and can be accessed directly
        int tLx1 = t.x;
        int tLy1 = t.y;
        int tLx2 = tLx1 + t.width;
        int tLy2 = tLy1 + t.height;
        
        int tW = t.cellWidth;
        int tH = t.cellHeight;
        
        int sx1 = this.x + this.collisionRectX;
        int sy1 = this.y + this.collisionRectY;
        int sx2 = sx1 + this.collisionRectWidth;
        int sy2 = sy1 + this.collisionRectHeight;
        
        // number of cells
        int tNumCols = t.columns;
        int tNumRows = t.rows;
        
        // temporary loop variables.
        int startCol; // = 0;
        int endCol;   // = 0;
        int startRow; // = 0;
        int endRow;   // = 0;
        
        if (!intersectRect(tLx1, tLy1, tLx2, tLy2, sx1, sy1, sx2, sy2)) {
            // if the collision rectangle of the sprite
            // does not intersect with the dimensions of the entire
            // tiled layer
            return false;
        }
        
        // so there is an intersection
        
        // note sx1 < sx2, tLx1 < tLx2, sx2 > tLx1  from intersectRect()
        // use <= for comparison as this saves us some
        // computation - the result will be 0
        startCol = (sx1 <= tLx1) ? 0 : (sx1 - tLx1)/tW;
        startRow = (sy1 <= tLy1) ? 0 : (sy1 - tLy1)/tH;
        // since tLx1 < sx2 < tLx2, the computation will yield
        // a result between 0 and tNumCols - 1
        // subtract by 1 because sx2,sy2 represent
        // the enclosing bounds of the sprite, not the
        // locations in the coordinate system.
        endCol = (sx2 < tLx2) ? ((sx2 - 1 - tLx1)/tW) : tNumCols - 1;
        endRow = (sy2 < tLy2) ? ((sy2 - 1 - tLy1)/tH) : tNumRows - 1;
        
        //if (!pixelLevel) {
        // check for intersection with a non-empty cell,
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (t.cellMatrix[row][col] != 0) {
                    return true;
                }
            }
        }
        // worst case! we scanned through entire
        // overlapping region and
        // all the cells are empty!
        return false;
        
        
    }
    
    //pixellevel not supported
    public final boolean collidesWith(Image image,
    int x, int y, boolean pixelLevel) {
        
        // check if this Sprite is not visible
        if (!(this.visible)) {
            return false;
        }
        
        // if image is null
        // image.getWidth() will throw NullPointerException
        int otherLeft    = x;
        int otherTop     = y;
        int otherRight   = x + image.getWidth();
        int otherBottom  = y + image.getHeight();
        
        int left   = this.x + this.collisionRectX;
        int top    = this.y + this.collisionRectY;
        int right  = left + this.collisionRectWidth;
        int bottom = top  + this.collisionRectHeight;
        
        // first check if the collision rectangles of the two sprites intersect
        if (intersectRect(otherLeft, otherTop, otherRight, otherBottom,
        left, top, right, bottom)) {
            
            
            return true;
        }
        return false;
        
    }
    
    
    //  ----- private -----
    
    private void initializeFrames(Image image, int fWidth,
    int fHeight, boolean maintainCurFrame) {
        
        int imageW = image.getWidth();
        int imageH = image.getHeight();
        
        int numHorizontalFrames = imageW / fWidth;
        int numVerticalFrames   = imageH / fHeight;
        
        sourceImage = image;
        
        srcFrameWidth = fWidth;
        srcFrameHeight = fHeight;
        
        numberFrames = numHorizontalFrames*numVerticalFrames;
        
        frameCoordsX = new int[numberFrames];
        frameCoordsY = new int[numberFrames];
        
        if (!maintainCurFrame) {
            sequenceIndex = 0;
        }
        
        if (!customSequenceDefined) {
            frameSequence = new int[numberFrames];
        }
        
        int currentFrame = 0;
        
        for (int yy = 0; yy < imageH; yy += fHeight) {
            for (int xx = 0; xx < imageW; xx += fWidth) {
                
                frameCoordsX[currentFrame] = xx;
                frameCoordsY[currentFrame] = yy;
                
                if (!customSequenceDefined) {
                    frameSequence[currentFrame] = currentFrame;
                }
                currentFrame++;
                
            }
        }
    }
    
    /**
     * initialize the collision rectangle
     */
    private void initCollisionRectBounds() {
        
        // reset x and y of collision rectangle
        collisionRectX = 0;
        collisionRectY = 0;
        
        // intialize the collision rectangle bounds to that of the sprite
        collisionRectWidth = this.width;
        collisionRectHeight = this.height;
        
    }
    
    
    private boolean intersectRect(int r1x1, int r1y1, int r1x2, int r1y2,
    int r2x1, int r2y1, int r2x2, int r2y2) {
        return !(r2x1 >= r1x2 || r2y1 >= r1y2 || r2x2 <= r1x1 || r2y2 <= r1y1);
    }
    
    
   
    private static final int INVERTED_AXES = 0x4;
    private static final int X_FLIP = 0x2;
    private static final int Y_FLIP = 0x1;
    private static final int ALPHA_BITMASK = 0xff000000;
    
    Image sourceImage;
    
    int numberFrames; // = 0;
    
    int[] frameCoordsX;
    int[] frameCoordsY;
    int srcFrameWidth;
    int srcFrameHeight;
    int[] frameSequence;
    private int sequenceIndex; // = 0
    private boolean customSequenceDefined; // = false;
    
    
    int dRefX; // =0
    
    int dRefY; // =0
    
    int collisionRectX; // =0
    
    int collisionRectY; // =0
    
    int collisionRectWidth;
    
    int collisionRectHeight;
    
    int t_currentTransformation;
    
 
    
}