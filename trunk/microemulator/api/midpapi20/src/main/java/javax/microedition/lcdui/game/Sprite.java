/*
 *   
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

/**
 * A Sprite is a basic visual element that can be rendered with one of
 * several frames stored in an Image; different frames can be shown to 
 * animate the Sprite.  Several transforms such as flipping and rotation
 * can also be applied to a Sprite to further vary its appearance.  As with
 * all Layer subclasses, a Sprite's location can be changed and it can also
 * be made visible or invisible.
 * <P>
 * <h3>Sprite Frames</h3>
 * The raw frames used to render a Sprite are provided in a single Image
 * object, which may be mutable or immutable.  If more than one frame is used,
 * the Image is broken up into a series of equally-sized frames of a specified
 * width and height.  As shown in the figure below, the same set of frames may
 * be stored in several different arrangements depending on what is the most 
 * convenient for the game developer.  
 * <br>
 * <center><img src="doc-files/frames.gif" width=777 height=402 
 * ALT="Sprite Frames"></center>
 * <br>
 * <p>
 * Each frame is assigned a unique index number.  The frame located in the
 * upper-left corner of the Image is assigned an index of 0.  The remaining 
 * frames are then numbered consecutively in row-major order (indices are 
 * assigned across the first row, then the second row, and so on).  The method
 * {@link #getRawFrameCount()} returns the total number of raw frames.
 * <P>
 * <h3>Frame Sequence</h3>
 * A Sprite's frame sequence defines an ordered list of frames to be displayed.
 * The default frame sequence mirrors the list of available frames, so 
 * there is a direct mapping between the sequence index and the corresponding
 * frame index.  This also means that the length of the default frame sequence
 * is equal to the number of raw frames.  For example, if a Sprite has 4 
 * frames, its default frame sequence is {0, 1, 2, 3}.  
 * <center><img src="doc-files/defaultSequence.gif" width=182 height=269 
 * ALT="Default Frame Sequence"></center>
 * <P>
 * The developer must manually switch the current frame in the frame sequence.
 * This may be accomplished by calling {@link #setFrame}, 
 * {@link #prevFrame()}, or {@link #nextFrame()}.  Note that these methods 
 * always operate on the sequence index, they do not operate on frame indices;
 * however, if the default frame sequence is used, then the sequence indices
 * and the frame indices are interchangeable.
 * <P>
 * If desired, an arbitrary frame sequence may be defined for a Sprite.  
 * The frame sequence must contain at least one element, and each element must
 * reference a valid frame index.  By defining a new frame sequence, the 
 * developer can conveniently display the Sprite's frames in any order 
 * desired; frames may be repeated, omitted, shown in reverse order, etc.
 * <P>
 * For example, the diagram below shows how a special frame sequence might be
 * used to animate a mosquito.  The frame sequence is designed so that the 
 * mosquito flaps its wings three times and then pauses for a moment before 
 * the cycle is repeated.
 * <center><img src="doc-files/specialSequence.gif" width=346 height=510 
 * ALT="Special Frame Sequence"></center>
 * By calling {@link #nextFrame} each time the display is updated, the 
 * resulting animation would like this:
 * <br>
 * <center><img src="doc-files/sequenceDemo.gif" width=96 height=36></center>
 * <P>
 * <h3>Reference Pixel</h3>
 * Being a subclass of Layer, Sprite inherits various methods for setting and
 * retrieving its location such as {@link #setPosition setPosition(x,y)}, 
 * {@link #getX getX()}, and {@link #getY getY()}.  These methods all define
 * position in terms of the upper-left corner of the Sprite's visual bounds;
 * however, in some cases, it is more convenient to define the Sprite's position
 * in terms of an arbitrary pixel within its frame, especially if transforms
 * are applied to the Sprite.
 * <P>
 * Therefore, Sprite includes the concept of a <em>reference pixel</em>.    
 * The reference pixel is defined by specifying its location in the
 * Sprite's untransformed frame using 
 * {@link #defineReferencePixel defineReferencePixel(x,y)}.  
 * By default, the reference pixel is defined to be the pixel at (0,0) 
 * in the frame.  If desired, the reference pixel may be defined outside 
 * of the frame's bounds.    
 * <p>
 * In this example, the reference pixel is defined to be the pixel that
 * the monkey appears to be hanging from:
 * <p>
 * <center><img src="doc-files/refpixel.gif" width=304 height=199
 * ALT="Defining The Reference Pixel"></center>
 * <p>
 * {@link #getRefPixelX getRefPixelX()} and {@link #getRefPixelY getRefPixelY()}
 * can be used to query the location of the reference pixel in the painter's 
 * coordinate system.  The developer can also use 
 * {@link #setRefPixelPosition setRefPixelPosition(x,y)} to position the Sprite 
 * so that reference pixel appears at a specific location in the painter's
 * coordinate system.  These methods automatically account for any transforms
 * applied to the Sprite.
 * <p>
 * In this example, the reference pixel's position is set to a point at the end 
 * of a tree branch; the Sprite's location changes so that the reference pixel
 * appears at this point and the monkey appears to be hanging from the branch:
 * <p>
 * <center><img src="doc-files/setrefposition.gif" width=332 height=350
 * ALT="Setting The Reference Pixel Position"></center>
 * <p>
 * <a name="transforms"></a>
 * <h3>Sprite Transforms</h3>
 * Various transforms can be applied to a Sprite.  The available transforms
 * include rotations in multiples of 90 degrees, and mirrored (about
 * the vertical axis) versions of each of the rotations.  A Sprite's transform 
 * is set by calling {@link #setTransform setTransform(transform)}.
 * <p>
 * <center><img src="doc-files/transforms.gif" width=355 height=575 
 * ALT="Transforms"></center>
 * <br>
 * When a transform is applied, the Sprite is automatically repositioned 
 * such that the  reference pixel appears stationary in the painter's 
 * coordinate system.  Thus, the reference pixel effectively becomes the
 * center of the transform operation.  Since the reference pixel does not
 * move, the values returned by {@link #getRefPixelX()} and 
 * {@link #getRefPixelY()} remain the same; however, the values returned by
 * {@link #getX getX()} and {@link #getY getY()} may change to reflect the 
 * movement of the Sprite's upper-left corner.
 * <p>
 * Referring to the monkey example once again, the position of the 
 * reference pixel remains at (48, 22) when a 90 degree rotation
 * is applied, thereby making it appear as if the monkey is swinging
 * from the branch:
 * <p>
 * <center><img src="doc-files/transcenter.gif" width=333 height=350
 * ALT="Transform Center"></center>
 * <p>
 * <h3>Sprite Drawing</h3>
 * Sprites can be drawn at any time using the {@link #paint(Graphics)} method.  
 * The Sprite will be drawn on the Graphics object according to the current 
 * state information maintained by the Sprite (i.e. position, frame, 
 * visibility).  Erasing the Sprite is always the responsibility of code 
 * outside the Sprite class.<p>
 * <p>
 * Sprites can be implemented using whatever techniques a manufacturers 
 * wishes to use (e.g hardware acceleration may be used for all Sprites, for
 * certain sizes of Sprites, or not at all).
 * <p>
 * For some platforms, certain Sprite sizes may be more efficient than others;
 * manufacturers may choose to provide developers with information about
 * device-specific characteristics such as these.
 * <p>
 */

public class Sprite extends Layer
{

    // ----- definitions for the various transformations possible -----

    /**
     * No transform is applied to the Sprite.  
     * This constant has a value of <code>0</code>.
     */
    public static final int TRANS_NONE = 0;
    
    /**
     * Causes the Sprite to appear rotated clockwise by 90 degrees.
     * This constant has a value of <code>5</code>.
     */
    public static final int TRANS_ROT90 = 5;

    /**
     * Causes the Sprite to appear rotated clockwise by 180 degrees.
     * This constant has a value of <code>3</code>.
     */
    public static final int TRANS_ROT180 = 3;

    /**
     * Causes the Sprite to appear rotated clockwise by 270 degrees.
     * This constant has a value of <code>6</code>.
     */
    public static final int TRANS_ROT270 = 6;

    /**
     * Causes the Sprite to appear reflected about its vertical
     * center.
     * This constant has a value of <code>2</code>.
     */
    public static final int TRANS_MIRROR = 2;

    /**
     * Causes the Sprite to appear reflected about its vertical
     * center and then rotated clockwise by 90 degrees.
     * This constant has a value of <code>7</code>.
     */
    public static final int TRANS_MIRROR_ROT90 = 7;

    /**
     * Causes the Sprite to appear reflected about its vertical
     * center and then rotated clockwise by 180 degrees.
     * This constant has a value of <code>1</code>.
     */
    public static final int TRANS_MIRROR_ROT180 = 1;

    /**
     * Causes the Sprite to appear reflected about its vertical
     * center and then rotated clockwise by 270 degrees.
     * This constant has a value of <code>4</code>.
     */
    public static final int TRANS_MIRROR_ROT270 = 4;

    // ----- Constructors -----

    /**
     * Creates a new non-animated Sprite using the provided Image.  
     * This constructor is functionally equivalent to calling
     * <code>new Sprite(image, image.getWidth(), image.getHeight())</code>
     * <p>
     * By default, the Sprite is visible and its upper-left 
     * corner is positioned at (0,0) in the painter's coordinate system.
     * <br>
     * @param image the <code>Image</code> to use as the single frame
     * for the </code>Sprite
     * @throws NullPointerException if <code>img</code> is <code>null</code>
     */
    public Sprite(Image image) {
        super(image.getWidth(), image.getHeight());
        throw new RuntimeException("STUB");
    }

    /**
     * Creates a new animated Sprite using frames contained in 
     * the provided Image.  The frames must be equally sized, with the 
     * dimensions specified by <code>frameWidth</code> and 
     * <code>frameHeight</code>.  They may be laid out in the image 
     * horizontally, vertically, or as a grid.  The width of the source 
     * image must be an integer multiple of the frame width, and the height
     * of the source image must be an integer multiple of the frame height.
     * The  values returned by {@link Layer#getWidth} and 
     * {@link Layer#getHeight} will reflect the frame width and frame height
     * subject to the Sprite's current transform.
     * <p>
     * Sprites have a default frame sequence corresponding to the raw frame
     * numbers, starting with frame 0.  The frame sequence may be modified
     * with {@link #setFrameSequence(int[])}.
     * <p>
     * By default, the Sprite is visible and its upper-left corner is 
     * positioned at (0,0) in the painter's coordinate system.
     * <p>
     * @param image the <code>Image</code> to use for <code>Sprite</code>
     * @param frameWidth the <code>width</code>, in pixels, of the
     * individual raw frames
     * @param frameHeight the <code>height</code>, in pixels, of the
     * individual raw frames
     * @throws NullPointerException if <code>img</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>frameHeight</code> or
     * <code>frameWidth</code> is less than <code>1</code>
     * @throws IllegalArgumentException if the <code>image</code>
     * width is not an integer multiple of the <code>frameWidth</code>
     * @throws IllegalArgumentException if the <code>image</code>
     * height is not an integer multiple of the <code>frameHeight</code>
     */
    public Sprite(Image image, int frameWidth, int frameHeight) {

        super(frameWidth, frameHeight);
        throw new RuntimeException("STUB");

    }

    /**
     * Creates a new Sprite from another Sprite.  <p>
     *
     * All instance attributes (raw frames, position, frame sequence, current
     * frame, reference point, collision rectangle, transform, and visibility) 
     * of the source Sprite are duplicated in the new Sprite.  
     *
     * @param s the <code>Sprite</code> to create a copy of
     * @throws NullPointerException if <code>s</code> is <code>null</code>
     *
     */
    public Sprite(Sprite s) {
        super(s != null ? s.getWidth() : 0, s != null ? s.getHeight() : 0);
        throw new RuntimeException("STUB");
    }


    // ----- public methods -----

    /**
     * Defines the reference pixel for this Sprite.  The pixel is
     * defined by its location relative to the upper-left corner of
     * the Sprite's un-transformed frame, and it may lay outside of
     * the frame's bounds.
     * <p>
     * When a transformation is applied, the reference pixel is
     * defined relative to the Sprite's initial upper-left corner
     * before transformation. This corner may no longer appear as the
     * upper-left corner in the painter's coordinate system under
     * current transformation.
     * <p>
     * By default, a Sprite's reference pixel is located at (0,0); that is,
     * the pixel in the upper-left corner of the raw frame.
     * <p>
     * Changing the reference pixel does not change the
     * Sprite's physical position in the painter's coordinate system;
     * that is, the values returned by {@link #getX getX()} and
     * {@link #getY getY()} will not change as a result of defining the
     * reference pixel.  However, subsequent calls to methods that
     * involve the reference pixel will be impacted by its new definition.
     *
     * @param inp_x the horizontal location of the reference pixel, relative
     * to the left edge of the un-transformed frame
     * @param inp_y the vertical location of the reference pixel, relative
     * to the top edge of the un-transformed frame
     * @see #setRefPixelPosition
     * @see #getRefPixelX
     * @see #getRefPixelY
     */
    public void defineReferencePixel(int inp_x, int inp_y) {
        throw new RuntimeException("STUB");
    }
    
    /**
     * Sets this Sprite's position such that its reference pixel is located
     * at (x,y) in the painter's coordinate system.
     * @param inp_x the horizontal location at which to place 
     * the reference pixel
     * @param inp_y the vertical location at which to place the reference pixel
     * @see #defineReferencePixel
     * @see #getRefPixelX
     * @see #getRefPixelY
     */         
    public void setRefPixelPosition(int inp_x, int inp_y) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the horizontal position of this Sprite's reference pixel
     * in the painter's coordinate system.  
     * @return the horizontal location of the reference pixel
     * @see #defineReferencePixel
     * @see #setRefPixelPosition
     * @see #getRefPixelY
     */         
    public int getRefPixelX() {
        throw new RuntimeException("STUB");
    }
        
    /**
     * Gets the vertical position of this Sprite's reference pixel
     * in the painter's coordinate system.
     * @return the vertical location of the reference pixel
     * @see #defineReferencePixel
     * @see #setRefPixelPosition
     * @see #getRefPixelX
     */         
    public int getRefPixelY() {
        throw new RuntimeException("STUB");
    }

    /**
     * Selects the current frame in the frame sequence.  <p>
     * The current frame is rendered when {@link #paint(Graphics)} is called.
     * <p>
     * The index provided refers to the desired entry in the frame sequence, 
     * not the index of the actual frame itself.
     * @param inp_sequenceIndex the index of of the desired entry in the frame 
     * sequence 
     * @throws IndexOutOfBoundsException if <code>frameIndex</code> is
     * less than<code>0</code>
     * @throws IndexOutOfBoundsException if <code>frameIndex</code> is
     * equal to or greater than the length of the current frame
     * sequence (or the number of raw frames for the default sequence)
     * @see #setFrameSequence(int[])
     * @see #getFrame
     */
    public void setFrame(int inp_sequenceIndex) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the current index in the frame sequence.  <p>
     * The index returned refers to the current entry in the frame sequence,
     * not the index of the actual frame that is displayed.
     *
     * @return the current index in the frame sequence 
     * @see #setFrameSequence(int[])
     * @see #setFrame
     */
    public final int getFrame() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the number of raw frames for this Sprite.  The value returned 
     * reflects the number of frames; it does not reflect the length of the 
     * Sprite's frame sequence.  However, these two values will be the same
     * if the default frame sequence is used.
     *
     * @return the number of raw frames for this Sprite
     * @see #getFrameSequenceLength
     */
    public int getRawFrameCount() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the number of elements in the frame sequence.  The value returned
     * reflects the length of the Sprite's frame sequence; it does not reflect
     * the number of raw frames.  However, these two values will be the same 
     * if the default frame sequence is used.
     *
     * @return the number of elements in this Sprite's frame sequence
     * @see #getRawFrameCount
     */
    public int getFrameSequenceLength() {
        throw new RuntimeException("STUB");
    }

    /**
     * Selects the next frame in the frame sequence.  <p>
     *
     * The frame sequence is considered to be circular, i.e. if 
     * {@link #nextFrame} is called when at the end of the sequence,
     * this method will advance to the first entry in the sequence.
     *
     * @see #setFrameSequence(int[])
     * @see #prevFrame
     */
    public void nextFrame() {
        throw new RuntimeException("STUB");
    }

    /**
     * Selects the previous frame in the frame sequence.  <p>
     *
     * The frame sequence is considered to be circular, i.e. if
     * {@link #prevFrame} is called when at the start of the sequence,
     * this method will advance to the last entry in the sequence.
     *
     * @see #setFrameSequence(int[])
     * @see #nextFrame
     */
    public void prevFrame() {
        throw new RuntimeException("STUB");
    }

    /**
     * Draws the Sprite.  
     * <P>
     * Draws current frame of Sprite using the provided Graphics object.
     * The Sprite's upper left corner is rendered at the Sprite's current
     * position relative to the origin of the Graphics object.  The current
     * position of the Sprite's upper-left corner can be retrieved by 
     * calling {@link #getX()} and {@link #getY()}.
     * <P>
     * Rendering is subject to the clip region of the Graphics object.
     * The Sprite will be drawn only if it is visible.
     * <p>
     * If the Sprite's Image is mutable, the Sprite is rendered using the
     * current contents of the Image.
     * 
     * @param g the graphics object to draw <code>Sprite</code> on
     * @throws NullPointerException if <code>g</code> is <code>null</code>
     *
     */
    public final void paint(Graphics g) {
        throw new RuntimeException("STUB");
    }

    /**
     * Set the frame sequence for this Sprite.  <p>
     *
     * All Sprites have a default sequence that displays the Sprites
     * frames in order.  This method allows for the creation of an
     * arbitrary sequence using the available frames.  The current
     * index in the frame sequence is reset to zero as a result of 
     * calling this method.
     * <p>
     * The contents of the sequence array are copied when this method
     * is called; thus, any changes made to the array after this method
     * returns have no effect on the Sprite's frame sequence.
     * <P>
     * Passing in <code>null</code> causes the Sprite to revert to the
     * default frame sequence.<p>
     *
     * @param sequence an array of integers, where each integer represents
     * a frame index
     *       
     * @throws ArrayIndexOutOfBoundsException if seq is non-null and any member
     *         of the array has a value less than <code>0</code> or
     *         greater than or equal to the
     *         number of frames as reported by {@link #getRawFrameCount()}
     * @throws IllegalArgumentException if the array has less than
     * <code>1</code> element
     * @see #nextFrame
     * @see #prevFrame
     * @see #setFrame
     * @see #getFrame
     *
     */
    public void setFrameSequence(int sequence[]) {
        throw new RuntimeException("STUB");
    }
    
    /**
     * Changes the Image containing the Sprite's frames.  
     * <p>
     * Replaces the current raw frames of the Sprite with a new set of raw
     * frames.  See the constructor {@link #Sprite(Image, int, int)} for
     * information on how the frames are created from the image.  The 
     * values returned by {@link Layer#getWidth} and {@link Layer#getHeight}
     * will reflect the new frame width and frame height subject to the 
     * Sprite's current transform.
     * <p>
     * Changing the image for the Sprite could change the number of raw 
     * frames.  If the new frame set has as many or more raw frames than the
     * previous frame set, then:
     * <ul>
     * <li>The current frame will be unchanged
     * <li>If a custom frame sequence has been defined (using 
     *     {@link #setFrameSequence(int[])}), it will remain unchanged.  If no
     *     custom frame sequence is defined (i.e. the default frame
     *     sequence
     *     is in use), the default frame sequence will be updated to
     *     be the default frame sequence for the new frame set.  In other
     *     words, the new default frame sequence will include all of the
     *     frames from the new raw frame set, as if this new image had been
     *     used in the constructor.
     * </ul>
     * <p>
     * If the new frame set has fewer frames than the previous frame set, 
     * then:
     * <ul>
     * <li>The current frame will be reset to entry 0
     * <li>Any custom frame sequence will be discarded and the frame sequence
     *     will revert to the default frame sequence for the new frame
     *     set.
     * </ul>
     * <p>
     * The reference point location is unchanged as a result of calling this 
     * method, both in terms of its defined location within the Sprite and its
     * position in the painter's coordinate system.  However, if the frame
     * size is changed and the Sprite has been transformed, the position of 
     * the Sprite's upper-left corner may change such that the reference 
     * point remains stationary.
     * <p>
     * If the Sprite's frame size is changed by this method, the collision 
     * rectangle is reset to its default value (i.e. it is set to the new 
     * bounds of the untransformed Sprite).
     * <p> 
     * @param img the <code>Image</code> to use for
     * <code>Sprite</code>
     * @param frameWidth the width in pixels of the individual raw frames
     * @param frameHeight the height in pixels of the individual raw frames
     * @throws NullPointerException if <code>img</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>frameHeight</code> or 
     * <code>frameWidth</code> is less than <code>1</code>
     * @throws IllegalArgumentException if the image width is not an integer
     * multiple of the <code>frameWidth</code>
     * @throws IllegalArgumentException if the image height is not an integer 
     * multiple of the <code>frameHeight</code>
     */
    public void setImage(Image img, int frameWidth, int frameHeight) {
        throw new RuntimeException("STUB");
    }

    /**
     * Defines the Sprite's bounding rectangle that is used for collision 
     * detection purposes.  This rectangle is specified relative to the 
     * un-transformed Sprite's upper-left corner and defines the area that is
     * checked for collision detection.  For pixel-level detection, only those
     * pixels within the collision rectangle are checked. 
     *
     * By default, a Sprite's collision rectangle is located at 0,0 as has the
     * same dimensions as the Sprite.  The collision rectangle may be 
     * specified to be larger or smaller than the default rectangle; if made 
     * larger, the pixels outside the bounds of the Sprite are considered to be
     * transparent for pixel-level collision detection.
     *
     * @param inp_x the horizontal location of the collision 
     * rectangle relative to the untransformed Sprite's left edge
     * @param inp_y the vertical location of the collision rectangle relative to
     * the untransformed Sprite's top edge
     * @param width the width of the collision rectangle
     * @param height the height of the collision rectangle
     * @throws IllegalArgumentException if the specified
     * <code>width</code> or <code>height</code> is
     * less than <code>0</code>
     */
    public void defineCollisionRectangle(int inp_x, int inp_y, 
                                         int width, int height) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the transform for this Sprite.  Transforms can be 
     * applied to a Sprite to change its rendered appearance.  Transforms 
     * are applied to the original Sprite image; they are not cumulative, 
     * nor can they be combined.  By default, a Sprite's transform is 
     * {@link #TRANS_NONE}.
     * <P>
     * Since some transforms involve rotations of 90 or 270 degrees, their
     * use may result in the overall width and height of the Sprite 
     * being swapped.  As a result, the values returned by 
     * {@link Layer#getWidth} and {@link Layer#getHeight} may change.
     * <p>
     * The collision rectangle is also modified by the transform so that
     * it remains static relative to the pixel data of the Sprite.  
     * Similarly, the defined reference pixel is unchanged by this method,
     * but its visual location within the Sprite may change as a result.
     * <P>
     * This method repositions the Sprite so that the location of 
     * the reference pixel in the painter's coordinate system does not change
     * as a result of changing the transform.  Thus, the reference pixel 
     * effectively becomes the centerpoint for the transform.  Consequently,
     * the values returned by {@link #getRefPixelX} and {@link #getRefPixelY} 
     * will be the same both before and after the transform is applied, but 
     * the values returned by {@link #getX getX()} and {@link #getY getY()}
     * may change.  
     * <p>
     * @param transform the desired transform for this <code>Sprite</code>
     * @throws IllegalArgumentException if the requested
     * <code>transform</code> is invalid
     * @see #TRANS_NONE
     * @see #TRANS_ROT90
     * @see #TRANS_ROT180
     * @see #TRANS_ROT270
     * @see #TRANS_MIRROR
     * @see #TRANS_MIRROR_ROT90
     * @see #TRANS_MIRROR_ROT180
     * @see #TRANS_MIRROR_ROT270
     *
     */
    public void setTransform(int transform) {
        throw new RuntimeException("STUB");
    }

    /**
     * Checks for a collision between this Sprite and the specified Sprite.
     * <P>
     * If pixel-level detection is used, a collision is detected only if
     * opaque pixels collide.  That is, an opaque pixel in the first
     * Sprite would have to collide with an opaque  pixel in the second
     * Sprite for a collision to be detected.  Only those pixels within
     * the Sprites' respective collision rectangles are checked.
     * <P>
     * If pixel-level detection is not used, this method simply
     * checks if the Sprites' collision rectangles intersect.
     * <P>
     * Any transforms applied to the Sprites are automatically accounted for.
     * <P>
     * Both Sprites must be visible in order for a collision to be
     * detected.
     * <P>
     * @param s the <code>Sprite</code> to test for collision with
     * @param pixelLevel <code>true</code> to test for collision on a
     * pixel-by-pixel basis, <code>false</code> to test using simple
     * bounds checking.
     * @return <code>true</code> if the two Sprites have collided, otherwise
     * <code>false</code>
     * @throws NullPointerException if Sprite <code>s</code> is 
     * <code>null</code>
     */
    public final boolean collidesWith(Sprite s, boolean pixelLevel) 
    {
        throw new RuntimeException("STUB");
    }

    /**
     * Checks for a collision between this Sprite and the specified
     * TiledLayer.  If pixel-level detection is used, a collision is
     * detected only if opaque pixels collide.  That is, an opaque pixel in
     * the Sprite would have to collide with an opaque pixel in TiledLayer
     * for a collision to be detected.  Only those pixels within the Sprite's
     * collision rectangle are checked.
     * <P>
     * If pixel-level detection is not used, this method simply checks if the
     * Sprite's collision rectangle intersects with a non-empty cell in the
     * TiledLayer.
     * <P>
     * Any transform applied to the Sprite is automatically accounted for.
     * <P>
     * The Sprite and the TiledLayer must both be visible in order for
     * a collision to be detected.
     * <P>
     * @param t the <code>TiledLayer</code> to test for collision with
     * @param pixelLevel <code>true</code> to test for collision on a
     * pixel-by-pixel basis, <code>false</code> to test using simple bounds
     * checking against non-empty cells.
     * @return <code>true</code> if this <code>Sprite</code> has
     * collided with the <code>TiledLayer</code>, otherwise
     * <code>false</code>
     * @throws NullPointerException if <code>t</code> is <code>null</code>
     */
    public final boolean collidesWith(TiledLayer t, boolean pixelLevel) {
        throw new RuntimeException("STUB");
    }

    /**
     * Checks for a collision between this Sprite and the specified Image
     * with its upper left corner at the specified location.  If pixel-level
     * detection is used, a collision is detected only if opaque pixels
     * collide.  That is, an opaque pixel in the Sprite would have to collide
     * with an opaque  pixel in Image for a collision to be detected.  Only
     * those pixels within the Sprite's collision rectangle are checked.
     * <P>
     * If pixel-level detection is not used, this method simply checks if the
     * Sprite's collision rectangle intersects with the Image's bounds.
     * <P>
     * Any transform applied to the Sprite is automatically accounted for.
     * <P>
     * The Sprite must be visible in order for a collision to be
     * detected.
     * <P>
     * @param image the <code>Image</code> to test for collision
     * @param inp_x the horizontal location of the <code>Image</code>'s
     * upper left corner
     * @param inp_y the vertical location of the <code>Image</code>'s
     * upper left corner
     * @param pixelLevel <code>true</code> to test for collision on a
     * pixel-by-pixel basis, <code>false</code> to test using simple
     * bounds checking
     * @return <code>true</code> if this <code>Sprite</code> has
     * collided with the <code>Image</code>, otherwise
     * <code>false</code>
     * @throws NullPointerException if <code>image</code> is
     * <code>null</code>
     */
    public final boolean collidesWith(Image image, int inp_x, 
                                      int inp_y, boolean pixelLevel) {
        throw new RuntimeException("STUB");
    }
}

