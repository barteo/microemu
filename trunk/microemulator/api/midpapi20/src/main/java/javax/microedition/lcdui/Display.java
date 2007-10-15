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

package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;

/**
 * <code>Display</code> represents the manager of the display and
 * input devices of the
 * system. It includes methods for retrieving properties of the device and
 * for requesting that objects be displayed on the device. Other methods that
 * deal with device attributes are primarily used with {@link Canvas Canvas}
 * objects and are thus defined there instead of here. <p>
 *
 * There is exactly one instance of Display per {@link
 * javax.microedition.midlet.MIDlet MIDlet} and the application can get a
 * reference to that instance by calling the {@link
 * #getDisplay(javax.microedition.midlet.MIDlet) getDisplay()} method. The
 * application may call the <code>getDisplay()</code> method at any time
 * during course of
 * its execution. The <code>Display</code> object
 * returned by all calls to <code>getDisplay()</code> will remain the
 * same during this
 * time. <p>
 *
 * A typical application will perform the following actions in response to
 * calls to its <code>MIDlet</code> methods:
 * <UL>
 * <LI><STRONG>startApp</STRONG> - the application is moving from the
 * paused state to the active state.
 * Initialization of objects needed while the application is active should be
 * done.  The application may call
 * {@link #setCurrent(Displayable) setCurrent()} for the first screen if that
 * has not already been done. Note that <code>startApp()</code> can be
 * called several
 * times if <code>pauseApp()</code> has been called in between. This
 * means that one-time
 * initialization
 * should not take place here but instead should occur within the
 * <code>MIDlet's</code>
 * constructor.
 * </LI>
 * <LI><STRONG>pauseApp</STRONG> - the application may pause its threads.
 * Also, if it is
 * desirable to start with another screen when the application is re-activated,
 * the new screen should be set with <code>setCurrent()</code>.</LI>
 * <LI><STRONG>destroyApp</STRONG> - the application should free resources,
 * terminate threads, etc.
 * The behavior of method calls on user interface objects after
 * <code>destroyApp()</code> has returned is undefined. </li>
 * </UL>
 * <p>
 *
 * <P>The user interface objects that are shown on the display device are
 * contained within a {@link Displayable Displayable} object. At any time the
 * application may have at most one <code>Displayable</code> object
 * that it intends to be
 * shown on the display device and through which user interaction occurs.  This
 * <code>Displayable</code> is referred to as the <em>current</em>
 * <code>Displayable</code>. </p>
 *
 * <P>The <code>Display</code> class has a {@link
 * #setCurrent(Displayable) setCurrent()}
 * method for setting the current <code>Displayable</code> and a
 * {@link #getCurrent()
 * getCurrent()} method for retrieving the current
 * <code>Displayable</code>.  The
 * application has control over its current <code>Displayable</code>
 * and may call
 * <code>setCurrent()</code> at any time.  Typically, the application
 * will change the
 * current <code>Displayable</code> in response to some user action.
 * This is not always the
 * case, however.  Another thread may change the current
 * <code>Displayable</code> in
 * response to some other stimulus.  The current
 * <code>Displayable</code> will also be
 * changed when the timer for an {@link Alert Alert} elapses. </P>
 *
 * <p> The application's current <code>Displayable</code> may not
 * physically be drawn on the
 * screen, nor will user events (such as keystrokes) that occur necessarily be
 * directed to the current <code>Displayable</code>.  This may occur
 * because of the presence
 * of other <code>MIDlet</code> applications running simultaneously on
 * the same device. </p>
 *
 * <P>An application is said to be in the <em>foreground</em> if its current
 * <code>Displayable</code> is actually visible on the display device
 * and if user input
 * device events will be delivered to it. If the application is not in the
 * foreground, it lacks access to both the display and input devices, and it is
 * said to be in the <em>background</em>. The policy for allocation of these
 * devices to different <code>MIDlet</code> applications is outside
 * the scope of this
 * specification and is under the control of an external agent referred to as
 * the <em>application management software</em>. </p>
 *
 * <P>As mentioned above, the application still has a notion of its current
 * <code>Displayable</code> even if it is in the background. The
 * current <code>Displayable</code> is
 * significant, even for background applications, because the current
 * <code>Displayable</code> is always the one that will be shown the
 * next time the
 * application is brought into the foreground.  The application can determine
 * whether a <code>Displayable</code> is actually visible on the
 * display by calling {@link
 * Displayable#isShown isShown()}. In the case of <code>Canvas</code>,
 * the {@link
 * Canvas#showNotify() showNotify()} and {@link Canvas#hideNotify()
 * hideNotify()} methods are called when the <code>Canvas</code> is
 * made visible and is
 * hidden, respectively.</P>
 *
 * <P> Each <code>MIDlet</code> application has its own current
 * <code>Displayable</code>.  This means
 * that the {@link #getCurrent() getCurrent()} method returns the
 * <code>MIDlet's</code>
 * current <code>Displayable</code>, regardless of the
 * <code>MIDlet's</code> foreground/background
 * state.  For example, suppose a <code>MIDlet</code> running in the
 * foreground has current
 * <code>Displayable</code> <em>F</em>, and a <code>MIDlet</code>
 * running in the background has current
 * <code>Displayable</code> <em>B</em>.  When the foreground
 * <code>MIDlet</code> calls <code>getCurrent()</code>, it
 * will return <em>F</em>, and when the background <code>MIDlet</code>
 * calls <code>getCurrent()</code>, it
 * will return <em>B</em>.  Furthermore, if either <code>MIDlet</code>
 * changes its current
 * <code>Displayable</code> by calling <code>setCurrent()</code>, this
 * will not affect the any other
 * <code>MIDlet's</code> current <code>Displayable</code>. </p>
 *
 * <P>It is possible for <code>getCurrent()</code> to return
 * <code>null</code>. This may occur at startup
 * time, before the <code>MIDlet</code> application has called
 * <code>setCurrent()</code> on its first
 * screen.  The <code>getCurrent(</code>) method will never return a
 * reference to a
 * <code>Displayable</code> object that was not passed in a prior call
 * to <code>setCurrent()</code> call
 * by this <code>MIDlet</code>. </p>
 *
 * <a name="systemscreens"></a>
 * <h3>System Screens</h3>
 *
 * <P> Typically, the
 * current screen of the foreground <code>MIDlet</code> will be
 * visible on the display.
 * However, under certain circumstances, the system may create a screen that
 * temporarily obscures the application's current screen.  These screens are
 * referred to as <em>system screens.</em> This may occur if the system needs
 * to show a menu of commands or if the system requires the user to edit text
 * on a separate screen instead of within a text field inside a
 * <code>Form</code>.  Even
 * though the system screen obscures the application's screen, the notion of
 * the current screen does not change.  In particular, while a system screen is
 * visible, a call to <code>getCurrent()</code> will return the
 * application's current
 * screen, not the system screen.  The value returned by
 * <code>isShown()</code> is <code>false</code>
 * while the current <code>Displayable</code> is obscured by a system
 * screen. </p>
 *
 * <p> If system screen obscures a canvas, its
 * <code>hideNotify()</code> method is called.
 * When the system screen is removed, restoring the canvas, its
 * <code>showNotify()</code>
 * method and then its <code>paint()</code> method are called.  If the
 * system screen was used
 * by the user to issue a command, the <code>commandAction()</code>
 * method is called after
 * <code>showNotify()</code> is called. </p>
 *
 * <p>This class contains methods to retrieve the prevailing foreground and
 * background colors of the high-level user interface.  These methods are
 * useful for creating <CODE>CustomItem</CODE> objects that match the user
 * interface of other items and for creating user interfaces within
 * <CODE>Canvas</CODE> that match the user interface of the rest of the
 * system.  Implementations are not restricted to using foreground and
 * background colors in their user interfaces (for example, they might use
 * highlight and shadow colors for a beveling effect) but the colors returned
 * are those that match reasonably well with the implementation's color
 * scheme.  An application implementing a custom item should use the
 * background color to clear its region and then paint text and geometric
 * graphics (lines, arcs, rectangles) in the foreground color.</p>
 *
 * @since MIDP 1.0
 */

public class Display {

    /**
     * Image type for <code>List</code> element image.
     *
     * <P>The value of <code>LIST_ELEMENT</code> is <code>1</code>.</P>
     *
     * @see #getBestImageWidth(int imageType)
     * @see #getBestImageHeight(int imageType)
     * @since MIDP 2.0
     */
    public static final int LIST_ELEMENT = 1;

    /**
     * Image type for <code>ChoiceGroup</code> element image.
     *
     * <P>The value of <code>CHOICE_GROUP_ELEMENT</code> is <code>2</code>.</P>
     *
     * @see #getBestImageWidth(int imageType)
     * @see #getBestImageHeight(int imageType)
     * @since MIDP 2.0
     */
    public static final int CHOICE_GROUP_ELEMENT = 2;

    /**
     * Image type for <code>Alert</code> image.
     *
     * <P>The value of <code>ALERT</code> is <code>3</code>.</P>
     *
     * @see #getBestImageWidth(int imageType)
     * @see #getBestImageHeight(int imageType)
     * @since MIDP 2.0
     */
    public static final int ALERT = 3;

    /**
     * A color specifier for use with <code>getColor</code>.
     * <code>COLOR_BACKGROUND</code> specifies the background color of
     * the screen.
     * The background color will always contrast with the foreground color.
     *
     * <p>
     * <code>COLOR_BACKGROUND</code> has the value <code>0</code>.
     *
     * @see #getColor
     * @since MIDP 2.0
     */
    public static final int COLOR_BACKGROUND = 0;

    /**
     * A color specifier for use with <code>getColor</code>.
     * <code>COLOR_FOREGROUND</code> specifies the foreground color,
     * for text characters
     * and simple graphics on the screen.  Static text or user-editable
     * text should be drawn with the foreground color.  The foreground color
     * will always constrast with background color.
     *
     * <p> <code>COLOR_FOREGROUND</code> has the value <code>1</code>.
     *
     * @see #getColor
     * @since MIDP 2.0
     */
    public static final int COLOR_FOREGROUND = 1;

    /**
     * A color specifier for use with <code>getColor</code>.
     * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> identifies the color for the
     * focus, or focus highlight, when it is drawn as a
     * filled in rectangle. The highlighted
     * background will always constrast with the highlighted foreground.
     *
     * <p>
     * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> has the value <code>2</code>.
     *
     * @see #getColor
     * @since MIDP 2.0
     */
    public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;

    /**
     * A color specifier for use with <code>getColor</code>.
     * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> identifies the color for text
     * characters and simple graphics when they are highlighted.
     * Highlighted
     * foreground is the color to be used to draw the highlighted text
     * and graphics against the highlighted background.
     * The highlighted foreground will always constrast with
     * the highlighted background.
     *
     * <p>
     * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> has the value <code>3</code>.
     *
     * @see #getColor
     * @since MIDP 2.0
     */
    public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;

    /**
     * A color specifier for use with <code>getColor</code>.
     * <code>COLOR_BORDER</code> identifies the color for boxes and borders
     * when the object is to be drawn in a
     * non-highlighted state.  The border color is intended to be used with
     * the background color and will contrast with it.
     * The application should draw its borders using the stroke style returned
     * by <code>getBorderStyle()</code>.
     *
     * <p> <code>COLOR_BORDER</code> has the value <code>4</code>.
     *
     * @see #getColor
     * @since MIDP 2.0
     */
    public static final int COLOR_BORDER = 4;

    /**
     * A color specifier for use with <code>getColor</code>.
     * <code>COLOR_HIGHLIGHTED_BORDER</code>
     * identifies the color for boxes and borders when the object is to be
     * drawn in a highlighted state.  The highlighted border color is intended
     * to be used with the background color (not the highlighted background
     * color) and will contrast with it.  The application should draw its
     * borders using the stroke style returned <code>by getBorderStyle()</code>.
     *
     * <p> <code>COLOR_HIGHLIGHTED_BORDER</code> has the value <code>5</code>.
     *
     * @see #getColor
     * @since MIDP 2.0
     */
    public static final int COLOR_HIGHLIGHTED_BORDER = 5;

    
    Display(MIDlet midlet) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the <code>Display</code> object that is unique to this
     * <code>MIDlet</code>.
     * @param m <code>MIDlet</code> of the application
     * @return the display object that application can use for its user
     * interface
     *
     * @throws NullPointerException if <code>m</code> is <code>null</code>
     */
    public static Display getDisplay(MIDlet m) {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns one of the colors from the high level user interface
     * color scheme, in the form <code>0x00RRGGBB</code> based on the
     * <code>colorSpecifier</code> passed in.
     *
     * @param colorSpecifier the predefined color specifier;
     *  must be one of
     *  {@link #COLOR_BACKGROUND},
     *  {@link #COLOR_FOREGROUND},
     *  {@link #COLOR_HIGHLIGHTED_BACKGROUND},
     *  {@link #COLOR_HIGHLIGHTED_FOREGROUND},
     *  {@link #COLOR_BORDER}, or
     *  {@link #COLOR_HIGHLIGHTED_BORDER}
     * @return color in the form of <code>0x00RRGGBB</code>
     * @throws IllegalArgumentException if <code>colorSpecifier</code>
     * is not a valid color specifier
     * @since MIDP 2.0
     */
    public int getColor(int colorSpecifier) {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the stroke style used for border drawing
     * depending on the state of the component
     * (highlighted/non-highlighted). For example, on a monochrome
     * system, the border around a non-highlighted item might be
     * drawn with a <code>DOTTED</code> stroke style while the border around a
     * highlighted item might be drawn with a <code>SOLID</code> stroke style.
     *
     * @param highlighted <code>true</code> if the border style being
     * requested is for the
     * highlighted state, <code>false</code> if the border style being
     * requested is for the
     * non-highlighted state
     * @return {@link Graphics#DOTTED} or {@link Graphics#SOLID}
     * @since MIDP 2.0
     */
    public int getBorderStyle(boolean highlighted) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets information about color support of the device.
     * @return <code>true</code> if the display supports color,
     * <code>false</code> otherwise
     */
    public boolean isColor() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the number of colors (if <code>isColor()</code> is
     * <code>true</code>)
     * or graylevels (if <code>isColor()</code> is <code>false</code>)
     * that can be
     * represented on the device.<P>
     * Note that the number of colors for a black and white display is
     * <code>2</code>.
     * @return number of colors
     */
    public int numColors() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the number of alpha transparency levels supported by this
     * implementation.  The minimum legal return value is
     * <code>2</code>, which indicates
     * support for full transparency and full opacity and no blending.  Return
     * values greater than <code>2</code> indicate that alpha blending
     * is supported.  For
     * further information, see <a href="Image.html#alpha">Alpha
     * Processing</a>.
     *
     * @return number of alpha levels supported
     * @since MIDP 2.0
     */
    public int numAlphaLevels() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the current <code>Displayable</code> object for this
     * <code>MIDlet</code>.  The
     * <code>Displayable</code> object returned may not actually be
     * visible on the display
     * if the <code>MIDlet</code> is running in the background, or if
     * the <code>Displayable</code> is
     * obscured by a system screen.  The {@link Displayable#isShown()
     * Displayable.isShown()} method may be called to determine whether the
     * <code>Displayable</code> is actually visible on the display.
     *
     * <p> The value returned by <code>getCurrent()</code> may be
     * <code>null</code>. This
     * occurs after the application has been initialized but before the first
     * call to <code>setCurrent()</code>. </p>
     *
     * @return the <code>MIDlet's</code> current <code>Displayable</code> object
     * @see #setCurrent
     */
    public Displayable getCurrent() {
        throw new RuntimeException("STUB");
    }

    /**
     * Requests that a different <code>Displayable</code> object be
     * made visible on the
     * display.  The change will typically not take effect immediately.  It
     * may be delayed so that it occurs between event delivery method
     * calls, although it is not guaranteed to occur before the next event
     * delivery method is called.  The <code>setCurrent()</code> method returns
     * immediately, without waiting for the change to take place.  Because of
     * this delay, a call to <code>getCurrent()</code> shortly after a
     * call to <code>setCurrent()</code>
     * impossible to return the value passed to <code>setCurrent()</code>.
     *
     * <p> Calls to <code>setCurrent()</code> are not queued.  A
     * delayed request made by a
     * <code>setCurrent()</code> call may be superseded by a subsequent call to
     * <code>setCurrent()</code>.  For example, if screen
     * <code>S1</code> is current, then </p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *     d.setCurrent(S2);
     *     d.setCurrent(S3);     </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     *
     * <p> may eventually result in <code>S3</code> being made
     * current, bypassing <code>S2</code>
     * entirely. </p>
     *
     * <p> When a <code>MIDlet</code> application is first started,
     * there is no current
     * <code>Displayable</code> object.  It is the responsibility of
     * the application to
     * ensure that a <code>Displayable</code> is visible and can
     * interact with the user at
     * all times.  Therefore, the application should always call
     * <code>setCurrent()</code>
     * as part of its initialization. </p>
     *
     * <p> The application may pass <code>null</code> as the argument to
     * <code>setCurrent()</code>.  This does not have the effect of
     * setting the current
     * <code>Displayable</code> to <code>null</code>; instead, the
     * current <code>Displayable</code>
     * remains unchanged.  However, the application management software may
     * interpret this call as a request from the application that it is
     * requesting to be placed into the background.  Similarly, if the
     * application is in the background, passing a non-null
     * reference to <code>setCurrent()</code> may be interpreted by
     * the application
     * management software as a request that the application is
     * requesting to be
     * brought to the foreground.  The request should be considered to be made
     * even if the current <code>Displayable</code> is passed to the
     * <code>setCurrent()</code>.  For
     * example, the code </p>
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *   d.setCurrent(d.getCurrent());    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p> generally will have no effect other than requesting that the
     * application be brought to the foreground.  These are only requests,
     * and there is no requirement that the application management
     * software comply with these requests in a timely fashion if at all. </p>
     *
     * <p> If the <code>Displayable</code> passed to
     * <code>setCurrent()</code> is an {@link Alert
     * Alert}, the previously current <code>Displayable</code>, if
     * any, is restored after
     * the <code>Alert</code> has been dismissed.  If there is a
     * current <code>Displayable</code>, the
     * effect is as if <code>setCurrent(Alert, getCurrent())</code>
     * had been called.  Note
     * that this will result in an exception being thrown if the current
     * <code>Displayable</code> is already an alert.  If there is no
     * current <code>Displayable</code>
     * (which may occur at startup time) the implementation's previous state
     * will be restored after the <code>Alert</code> has been
     * dismissed.  The automatic
     * restoration of the previous <code>Displayable</code> or the
     * previous state occurs
     * only when the <code>Alert's</code> default listener is present
     * on the <code>Alert</code> when it
     * is dismissed.  See <a href="Alert.html#commands">Alert Commands and
     * Listeners</a> for details.</p>
     *
     * <p>To specify the
     * <code>Displayable</code> to be shown after an
     * <code>Alert</code> is dismissed, the application
     * should use the {@link #setCurrent(Alert,Displayable) setCurrent(Alert,
     * Displayable)} method.  If the application calls
     * <code>setCurrent()</code> while an
     * <code>Alert</code> is current, the <code>Alert</code> is
     * removed from the display and any timer
     * it may have set is cancelled. </p>
     *
     * <p> If the application calls <code>setCurrent()</code> while a
     * system screen is
     * active, the effect may be delayed until after the system screen is
     * dismissed.  The implementation may choose to interpret
     * <code>setCurrent()</code> in
     * such a situation as a request to cancel the effect of the system
     * screen, regardless of whether <code>setCurrent()</code> has
     * been delayed. </p>
     *
     * @param nextDisplayable the <code>Displayable</code> requested
     * to be made current;
     * <code>null</code> is allowed
     * @see #getCurrent
     */
    public void setCurrent(Displayable nextDisplayable) {
        throw new RuntimeException("STUB");
    }

    /**
     * Requests that this <code>Alert</code> be made current, and that
     * <code>nextDisplayable</code> be
     * made current
     * after the <code>Alert</code> is dismissed.  This call returns
     * immediately regardless
     * of the <code>Alert's</code> timeout value or whether it is a
     * modal alert.  The
     * <code>nextDisplayable</code> must not be an <code>Alert</code>,
     * and it must not be <code>null</code>.
     *
     * <p>The automatic advance to <code>nextDisplayable</code> occurs only
     * when the <code>Alert's</code> default listener is present on
     * the <code>Alert</code> when it
     * is dismissed.  See <a href="Alert.html#commands">Alert Commands and
     * Listeners</a> for details.</p>
     *
     * <p> In other respects, this method behaves identically to
     * {@link #setCurrent(Displayable) setCurrent(Displayable)}. </p>
     *
     * @param alert the alert to be shown
     * @param nextDisplayable the <code>Displayable</code> to be
     * shown after this alert is  dismissed
     *
     * @throws NullPointerException if alert or
     * <code>nextDisplayable</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>nextDisplayable</code>
     * is an <code>Alert</code>
     * @see Alert
     * @see #getCurrent
     */
    public void setCurrent(Alert alert, Displayable nextDisplayable) {
        throw new RuntimeException("STUB");
    }

    /**
     * Requests that the <code>Displayable</code> that contains this
     * <code>Item</code> be made current,
     * scrolls the <code>Displayable</code> so that this
     * <code>Item</code> is visible, and possibly
     * assigns the focus to this <code>Item</code>.  The containing
     * <code>Displayable</code> is first
     * made current as if {@link #setCurrent(Displayable)
     * setCurrent(Displayable)} had been called.  When the containing
     * <code>Displayable</code> becomes current, or if it is already
     * current, it is
     * scrolled if necessary so that the requested <code>Item</code>
     * is made visible.
     * Then, if the implementation supports the notion of input focus, and if
     * the <code>Item</code> accepts the input focus, the input focus
     * is assigned to the
     * <code>Item</code>.
     *
     * <p>This method always returns immediately, without waiting for the
     * switching of the <code>Displayable</code>, the scrolling, and
     * the assignment of
     * input focus to take place.</p>
     *
     * <p>It is an error for the <code>Item</code> not to be contained
     * within a container.
     * It is also an error if the <code>Item</code> is contained
     * within an <code>Alert</code>.</p>
     *
     * @param item the item that should be made visible
     * @throws IllegalStateException if the item is not owned by a container
     * @throws IllegalStateException if the item is owned by an
     * <code>Alert</code>
     * @throws NullPointerException if <code>item</code> is <code>null</code>
     * @since MIDP 2.0
     */
    public void setCurrentItem(Item item) {
        throw new RuntimeException("STUB");
    }

    /**
     * Causes the <code>Runnable</code> object <code>r</code> to have
     * its <code>run()</code> method
     * called later, serialized with the event stream, soon after completion of
     * the repaint cycle.  As noted in the
     * <a href="./package-summary.html#events">Event Handling</a>
     * section of the package summary,
     * the methods that deliver event notifications to the application
     * are all called serially. The call to <code>r.run()</code> will
     * be serialized along with
     * the event calls into the application. The <code>run()</code>
     * method will be called exactly once for each call to
     * <code>callSerially()</code>. Calls to <code>run()</code> will
     * occur in the order in which they were requested by calls to
     * <code>callSerially()</code>.
     *
     * <p> If the current <code>Displayable</code> is a <code>Canvas</code>
     * that has a repaint pending at the time of a call to
     * <code>callSerially()</code>, the <code>paint()</code> method of the
     * <code>Canvas</code> will be called and
     * will return, and a buffer switch will occur (if double buffering is in
     * effect), before the <code>run()</code> method of the
     * <code>Runnable</code> is called.
     * If the current <code>Displayable</code> contains one or more
     * <code>CustomItems</code> that have repaints pending at the time
     * of a call to <code>callSerially()</code>, the <code>paint()</code>
     * methods of the <code>CustomItems</code> will be called and will
     * return before the <code>run()</code> method of the
     * <code>Runnable</code> is called.
     * Calls to the
     * <code>run()</code> method will occur in a timely fashion, but
     * they are not guaranteed
     * to occur immediately after the repaint cycle finishes, or even before
     * the next event is delivered. </p>
     *
     * <p> The <code>callSerially()</code> method may be called from
     * any thread. The call to
     * the <code>run()</code> method will occur independently of the
     * call to <code>callSerially()</code>.
     * In particular, <code>callSerially()</code> will <em>never</em>
     * block waiting
     * for <code>r.run()</code>
     * to return. </p>
     *
     * <p> As with other callbacks, the call to <code>r.run()</code>
     * must return quickly. If
     * it is necessary to perform a long-running operation, it may be initiated
     * from within the <code>run()</code> method. The operation itself
     * should be performed
     * within another thread, allowing <code>run()</code> to return. </p>
     *
     * <p> The <code>callSerially()</code> facility may be used by
     * applications to run an
     * animation that is properly synchronized with the repaint cycle. A
     * typical application will set up a frame to be displayed and then call
     * <code>repaint()</code>.  The application must then wait until
     * the frame is actually
     * displayed, after which the setup for the next frame may occur.  The call
     * to <code>run()</code> notifies the application that the
     * previous frame has finished
     * painting.  The example below shows <code>callSerially()</code>
     * being used for this
     * purpose. </p>
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *     class Animation extends Canvas
     *         implements Runnable {
     *
     *     // paint the current frame
     *     void paint(Graphics g) { ... }
     *
     *        Display display; // the display for the application
     *
     *        void paint(Graphics g) { ... } // paint the current frame
     *
     *        void startAnimation() {
     *            // set up initial frame
     *            repaint();
     *            display.callSerially(this);
     *        }
     *
     *        // called after previous repaint is finished
     *        void run() {
     *            if ( &#47;* there are more frames *&#47; ) {
     *                // set up the next frame
     *                repaint();
     *                display.callSerially(this);
     *            }
     *        }
     *     }    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * @param r instance of interface <code>Runnable</code> to be called
     */
    public void callSerially(Runnable r) {
        throw new RuntimeException("STUB");
    }

    /**
     * Requests a flashing effect for the device's backlight.  The flashing
     * effect is intended to be used to attract the user's attention or as a
     * special effect for games.  Examples of flashing are cycling the
     * backlight on and off or from dim to bright repeatedly.
     * The return value indicates if the flashing of the backlight
     * can be controlled by the application.
     *
     * <p>The flashing effect occurs for the requested duration, or it is
     * switched off if the requested duration is zero.  This method returns
     * immediately; that is, it must not block the caller while the flashing
     * effect is running.</p>
     *
     * <p>Calls to this method are honored only if the
     * <code>Display</code> is in the
     * foreground.  This method MUST perform no action
     * and return <CODE>false</CODE> if the
     * <code>Display</code> is in the background.
     *
     * <p>The device MAY limit or override the duration. For devices
     * that do not include a controllable backlight, calls to this
     * method return <CODE>false</CODE>.
     *
     * @param duration the number of milliseconds the backlight should be
     * flashed, or zero if the flashing should be stopped
     *
     * @return <CODE>true</CODE> if the backlight can be controlled
     *           by the application and this display is in the foreground,
     *          <CODE>false</CODE> otherwise
     *
     * @throws IllegalArgumentException if <code>duration</code> is negative
     * @since MIDP 2.0
     */
    public boolean flashBacklight(int duration) {
        throw new RuntimeException("STUB");
    }

    /**
     * Requests operation of the device's vibrator.  The vibrator is
     * intended to be used to attract the user's attention or as a
     * special effect for games.  The return value indicates if the
     * vibrator can be controlled by the application.
     *
     * <p>This method switches on the vibrator for the requested
     * duration, or switches it off if the requested duration is zero.
     * If this method is called while the vibrator is still activated
     * from a previous call, the request is interpreted as setting a
     * new duration. It is not interpreted as adding additional time
     * to the original request. This method returns immediately; that
     * is, it must not block the caller while the vibrator is
     * running. </p>
     *
     * <p>Calls to this method are honored only if the
     * <code>Display</code> is in the foreground.  This method MUST
     * perform no action and return <CODE>false</CODE> if the
     * <code>Display</code> is in the background.</p>
     *
     * <p>The device MAY limit or override the duration.  For devices
     * that do not include a controllable vibrator, calls to this
     * method return <CODE>false</CODE>.</p>
     *
     * @param duration the number of milliseconds the vibrator should be run,
     * or zero if the vibrator should be turned off
     *
     * @return <CODE>true</CODE> if the vibrator can be controlled by the
     *           application and this display is in the foreground,
     *          <CODE>false</CODE> otherwise
     *
     * @throws IllegalArgumentException if <code>duration</code> is negative
     * @since MIDP 2.0
     */
    public boolean vibrate(int duration) {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the best image width for a given image type.
     * The image type must be one of
     * {@link #LIST_ELEMENT},
     * {@link #CHOICE_GROUP_ELEMENT}, or
     * {@link #ALERT}.
     *
     * @param imageType the image type
     * @return the best image width for the image type, may be zero if
     * there is no best size; must not be negative
     * @throws IllegalArgumentException if <code>imageType</code> is illegal
     * @since MIDP 2.0
     */
    public int getBestImageWidth(int imageType) {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the best image height for a given image type.
     * The image type must be one of
     * {@link #LIST_ELEMENT},
     * {@link #CHOICE_GROUP_ELEMENT}, or
     * {@link #ALERT}.
     *
     * @param imageType the image type
     * @return the best image height for the image type, may be zero if
     * there is no best size; must not be negative
     * @throws IllegalArgumentException if <code>imageType</code> is illegal
     * @since MIDP 2.0
     */
    public int getBestImageHeight(int imageType) {
        throw new RuntimeException("STUB");
    }
}
