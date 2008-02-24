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


/**
 * An alert is a screen that shows data to the user and waits for a certain
 * period of time before proceeding to the next
 * <code>Displayable</code>. An alert can
 * contain a text string and an image.
 * The intended use of <code>Alert</code> is to inform the user about
 * errors and other
 * exceptional conditions.
 *
 * <P>The application can set the alert time to be infinity with
 * <code> setTimeout(Alert.FOREVER)</code>
 * in which case the <code>Alert</code> is considered to be <em>modal</em> and
 * the implementation provide a feature that allows the
 * user to &quot;dismiss&quot; the alert, whereupon the next
 * <code>Displayable</code>
 * is displayed as if the timeout had expired immediately.</P>
 *
 * <P>If an application specifies an alert to be of a
 * timed variety <em>and</em> gives it too much content such that it must
 * scroll,
 * then it automatically becomes a modal alert.</P>
 *
 * <P> An alert may have an <code>AlertType</code> associated with it
 * to provide an indication of the nature of the alert.
 * The implementation may use this type to play an
 * appropriate sound when the <code>Alert</code> is presented to the user.
 * See {@link AlertType#playSound(javax.microedition.lcdui.Display) 
 * AlertType.playSound()}.</P>
 *
 * <P>An alert may contain an optional <code>Image</code>.  The
 * <code>Image</code> may be mutable or
 * immutable.  If the <code>Image</code> is mutable, the effect is as
 * if a snapshot of its
 * contents is taken at the time the <code>Alert</code> is constructed
 * with this <code>Image</code> and
 * when <code>setImage</code> is called with an <code>Image</code>.
 * This snapshot is used whenever the contents of the
 * <code>Alert</code> are to be
 * displayed.  Even if the application subsequently draws into the
 * <code>Image</code>, the
 * snapshot is not modified until the next call to <code>setImage</code>.  The
 * snapshot is <em>not</em> updated when the <code>Alert</code>
 * becomes current or becomes
 * visible on the display.  (This is because the application does not have
 * control over exactly when <code>Displayables</code> appear and
 * disappear from the
 * display.)</P>
 *
 * <a name="indicator"></a>
 * <h3>Activity Indicators</h3>
 * 
 * <P>An alert may contain an optional {@link Gauge} object that is used as an 
 * activity or progress indicator.  By default, an <code>Alert</code>
 * has no activity
 * indicator; one may be set with the {@link #setIndicator} method.
 * The <code>Gauge</code>
 * object used for the activity indicator must conform to all of the following 
 * restrictions:</P>
 *
 * <ul>
 * <li>it must be non-interactive;</li>
 * <li>it must not be owned by another container (<code>Alert</code>
 * or <code>Form</code>);</li>
 * <li>it must not have any <code>Commands</code>;</li>
 * <li>it must not have an <code>ItemCommandListener</code>;</li>
 * <li>it must not have a label (that is, its label must be
 * <code>null</code>;</li>
 * <li>its preferred width and height must both be unlocked; and</li>
 * <li>its layout value must be <code>LAYOUT_DEFAULT</code>.</li>
 * </ul>
 *
 * <P>It is an error for the application to attempt to use a
 * <code>Gauge</code> object that
 * violates any of these restrictions.  In addition, when the
 * <code>Gauge</code> object is
 * being used as the indicator within an <code>Alert</code>, the
 * application is prevented
 * from modifying any of these pieces of the <code>Gauge's</code> state.</P>
 *
 * <a name="commands"></a>
 * <h3>Commands and Listeners</h3>
 *
 * <P>Like the other <code>Displayable</code> classes, an
 * <code>Alert</code> can accept <code>Commands</code>, which
 * can be delivered to a <code>CommandListener</code> set by the
 * application.  The <code>Alert</code>
 * class adds some special behavior for <code>Commands</code> and listeners.</P>
 *
 * <P>When it is created, an <code>Alert</code> implicitly has the
 * special <code>Command</code>
 * {@link #DISMISS_COMMAND} present on it.  If the application adds any 
 * other <code>Commands</code> to the <code>Alert</code>,
 * <code>DISMISS_COMMAND</code> is implicitly removed.  If the
 * application removes all other <code>Commands</code>,
 * <code>DISMISS_COMMAND</code> is implicitly
 * restored.  Attempts to add or remove <code>DISMISS_COMMAND</code>
 * explicitly are
 * ignored.  Thus, there is always at least one <code>Command</code>
 * present on an <code>Alert</code>.
 * </P>
 *
 * <P>If there are two or more <code>Commands</code> present on the
 * <code>Alert</code>, it is
 * automatically turned into a modal <code>Alert</code>, and the
 * timeout value is always
 * {@link #FOREVER}.  The <code>Alert</code> remains on the display
 * until a <code>Command</code> is
 * invoked.  If the Alert has one Command (whether it is DISMISS_COMMAND or it
 * is one provided by the application), the <code>Alert</code> may have
 * the timed behavior
 * as described above.  When a timeout occurs, the effect is the same as if
 * the user had invoked the <code>Command</code> explicitly.</P>
 *
 * <P>When it is created, an <code>Alert</code> implicitly has a
 * <code>CommandListener</code> called the
 * <em>default listener</em> associated with it.  This listener may be
 * replaced by an application-provided listener through use of the {@link
 * #setCommandListener} method.  If the application removes its listener by
 * passing <code>null</code> to the <code>setCommandListener</code> method,
 * the default listener is implicitly restored.</P>
 *
 * <P>The {@link Display#setCurrent(Alert, Displayable)} method and the {@link
 * Display#setCurrent(Displayable)} method (when called with an
 * <code>Alert</code>) define
 * special behavior for automatically advancing to another
 * <code>Displayable</code> after
 * the <code>Alert</code> is dismissed.  This special behavior occurs
 * only when the default
 * listener is present on the <code>Alert</code> at the time it is
 * dismissed or when a
 * command is invoked.  If the user invokes a <code>Command</code> and
 * the default listener
 * is present, the default listener ignores the <code>Command</code>
 * and implements the
 * automatic-advance behavior.</P>
 *
 * <P>If the application has set its own <code>CommandListener</code>, the
 * automatic-advance behavior is disabled.  The listener code is responsible
 * for advancing to another <code>Displayable</code>.  When the
 * application has provided a
 * listener, <code>Commands</code> are invoked normally by passing
 * them to the listener's
 * <code>commandAction</code> method.  The <code>Command</code> passed
 * will be one of the
 * <code>Commands</code> present on the <code>Alert</code>: either
 * <code>DISMISS_COMMAND</code> or one of the
 * application-provided <code>Commands</code>.</P>
 *
 * <P>The application can restore the default listener by passing
 * <code>null</code> to the <code>setCommandListener</code> method.</P>
 * 
 * <P>
 * <strong>Note:</strong> An application may set a {@link Ticker Ticker}
 * with {@link Displayable#setTicker Displayable.setTicker} on an
 * <code>Alert</code>, however it may not be displayed due to
 * implementation restrictions.
 * </P>
 *
 * @see AlertType
 * @since MIDP 1.0
 */

public class Alert extends Screen {

// *****************************************************
//  Public members
// *****************************************************

    /**
     * <code>FOREVER</code> indicates that an <code>Alert</code> is
     * kept visible until the user
     * dismisses it.  It is used as a value for the parameter to
     * {@link #setTimeout(int) setTimeout()}
     * to indicate that the alert is modal.  Instead of waiting for a
     * specified period of time, a modal <code>Alert</code> will wait
     * for the user to take
     * some explicit action, such as pressing a button, before proceeding to
     * the next <code>Displayable</code>.
     *
     * <P>Value <code>-2</code> is assigned to <code>FOREVER</code>.</P>
     */
    public final static int FOREVER = -2;

    /**
     * A <code>Command</code> delivered to a listener to indicate that
     * the <code>Alert</code> has been
     * dismissed.  This Command is implicitly present an on
     * <code>Alert</code> whenever
     * there are no other Commands present.  The field values of
     * <code>DISMISS_COMMAND</code> are as follows:
     *
     * <ul>
     * <li>label = &quot;&quot; (an empty string)</li>
     * <li>type = Command.OK</li>
     * <li>priority = 0</li>
     * </ul>
     *
     * <p>The label value visible to the application must be as specified 
     * above.  However, the implementation may display
     * <code>DISMISS_COMMAND</code> to the
     * user using an implementation-specific label.</p>
     *
     * <p>Attempting to add or remove <code>DISMISS_COMMAND</code>
     * from an <code>Alert</code> has no
     * effect.  However, <code>DISMISS_COMMAND</code> is treated as an
     * ordinary <code>Command</code> if
     * it is used with other <code>Displayable</code> types.</p>
     * @since MIDP 2.0
     *
     */
    public final static Command DISMISS_COMMAND =
        new Command("", Command.OK, 0);

// *****************************************************
//  Constructor(s)
// *****************************************************

    /**
     * Constructs a new, empty <code>Alert</code> object with the
     * given title. If <code>null</code> is
     * passed, the <code>Alert</code> will have no title.  Calling
     * this constructor is
     * equivalent to calling
     *
     * <pre>
     *    <code>Alert(title, null, null, null)</code>
     * </pre>
     *
     * @param title the title string, or <code>null</code>
     *
     * @see #Alert(String, String, Image, AlertType)
     */
    public Alert(String title) {
        this(title, null, null, null);
    }

    /**
     * Constructs a new <code>Alert</code> object with the given title,
     * content
     * string and image, and alert type.
     * The layout of the contents is implementation dependent.
     * The timeout value of this new alert is the same value that is
     * returned by <code>getDefaultTimeout()</code>.
     * The <code>Image</code> provided may either be mutable or immutable.
     * The handling and behavior of specific <code>AlertTypes</code>
     * is described in
     * {@link AlertType}.  <code>null</code> is allowed as the value
     * of the <code>alertType</code>
     * parameter and indicates that the <code>Alert</code> is not to
     * have a specific alert
     * type.  <code>DISMISS_COMMAND</code> is the only
     * <code>Command</code> present on the new
     * <code>Alert</code>.  The <code>CommandListener</code>
     * associated with the new <code>Alert</code> is the
     * <em>default listener</em>.  Its behavior is described in more detail in 
     * the section <a href="#commands">Commands and Listeners</a>.
     *
     * @param title the title string, or <code>null</code> if there is no title
     * @param alertText the string contents, or <code>null</code> if there 
     * is no string
     * @param alertImage the image contents, or <code>null</code> if there 
     * is no image
     * @param alertType the type of the <code>Alert</code>, or
     * <code>null</code>
     * if the <code>Alert</code> has no
     * specific type
     */
    public Alert(String title, String alertText,
                 Image alertImage, AlertType alertType) {
        super(title);
        throw new RuntimeException("STUB");
    }

// *****************************************************
//  Public methods
// *****************************************************

    /**
     * Gets the default time for showing an <code>Alert</code>.  This
     * is either a
     * positive value, which indicates a time in milliseconds, or the special
     * value
     * {@link #FOREVER FOREVER},
     * which indicates that <code>Alerts</code> are modal by default.  The
     * value returned will vary across implementations and is presumably
     * tailored to be suitable for each.
     *
     * @return default timeout in milliseconds, or <code>FOREVER</code>
     */
    public int getDefaultTimeout() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the time this <code>Alert</code> will be shown.  This is
     * either a positive
     * value, which indicates a time in milliseconds, or the special value
     * <code>FOREVER</code>, which indicates that this
     * <code>Alert</code> is modal.  This value is not
     * necessarily the same value that might have been set by the
     * application
     * in a call to {@link #setTimeout}.  In particular, if the
     * <code>Alert</code> is made
     * modal because its contents is large enough to scroll, the value
     * returned by <code>getTimeout</code> will be <code>FOREVER</code>.
     *
     * @return timeout in milliseconds, or <code>FOREVER</code>
     * @see #setTimeout
     */
    public int getTimeout() {
        throw new RuntimeException("STUB");
    }

    /**
     * Set the time for which the <code>Alert</code> is to be shown.
     * This must either
     * be a positive time value in milliseconds, or the special value
     * <code>FOREVER</code>.
     *
     * @param time timeout in milliseconds, or <code>FOREVER</code>
     * @throws IllegalArgumentException if time is not positive and is
     * not <code>FOREVER</code>
     * @see #getTimeout
     */
    public void setTimeout(int time) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the type of the <code>Alert</code>.
     * @return a reference to an instance of <code>AlertType</code>,
     * or <code>null</code>
     * if the <code>Alert</code>
     * has no specific type
     * @see #setType
     */
    public AlertType getType() {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the type of the <code>Alert</code>.
     * The handling and behavior of specific <code>AlertTypes</code>
     * is described in
     * {@link AlertType}.
     * @param type an <code>AlertType</code>, or <code>null</code> if the
     * <code>Alert</code> has no
     * specific type
     * @see #getType
     */
    public void setType(AlertType type) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the text string used in the <code>Alert</code>.
     * @return the <code>Alert's</code> text string, or <code>null</code> 
     * if there is no text
     * @see #setString
     */
    public String getString() {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the text string used in the <code>Alert</code>.
     *
     * <p>If the <code>Alert</code> is visible on the display when its
     * contents are updated
     * through a call to <code>setString</code>, the display will be
     * updated with the new
     * contents as soon as it is feasible for the implementation to do so.
     * </p>
     *
     * @param str the <code>Alert's</code> text string, or <code>null</code>
     * if there is no text
     * @see #getString
     */
    public void setString(String str) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the <code>Image</code> used in the <code>Alert</code>.
     * @return the <code>Alert's</code> image, or <code>null</code> 
     * if there is no image
     * @see #setImage
     */
    public Image getImage() {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the <code>Image</code> used in the <code>Alert</code>.
     * The <code>Image</code> may be mutable or
     * immutable.  If <code>img</code> is <code>null</code>, specifies
     * that this <code>Alert</code> has no image.
     * If <code>img</code> is mutable, the effect is as if a snapshot is taken
     * of <code>img's</code> contents immediately prior to the call to
     * <code>setImage</code>.  This
     * snapshot is used whenever the contents of the
     * <code>Alert</code> are to be
     * displayed.  If <code>img</code> is already the
     * <code>Image</code> of this <code>Alert</code>, the effect
     * is as if a new snapshot of img's contents is taken.  Thus, after
     * painting into a mutable image contained by an <code>Alert</code>, the
     * application can call
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *    alert.setImage(alert.getImage());    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p>to refresh the <code>Alert's</code> snapshot of its
     * <code>Image</code>.</p>
     *
     * <p>If the <code>Alert</code> is visible on the display when its
     * contents are updated
     * through a call to <code>setImage</code>, the display will be
     * updated with the new
     * snapshot as soon as it is feasible for the implementation to do so.
     * </p>
     *
     * @param img the <code>Alert's</code> image, or <code>null</code>
     * if there is no image
     * @see #getImage
     */
    public void setImage(Image img) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets an activity indicator on this <code>Alert</code>.  The
     * activity indicator is a
     * {@link Gauge} object.  It must be in a restricted state in order for it
     * to be used as the activity indicator for an <code>Alert</code>.
     * The restrictions
     * are listed <a href="#indicator">above</a>.  If the
     * <code>Gauge</code> object
     * violates any of these restrictions,
     * <code>IllegalArgumentException</code> is thrown.
     *
     * <p>If <code>indicator</code> is <code>null</code>, this removes any
     * activity indicator present on this <code>Alert</code>.</p>
     *
     * @param indicator the activity indicator for this <code>Alert</code>,
     * or <code>null</code> if 
     * there is to be none
     *
     * @throws IllegalArgumentException if <code>indicator</code> does not
     * meet the restrictions for its use in an <code>Alert</code>
     * @see #getIndicator
     * @since MIDP 2.0
     */
    public void setIndicator(Gauge indicator) {

        throw new RuntimeException("STUB");
    }

    /**
     * Gets the activity indicator for this <code>Alert</code>.
     * 
     * @return a reference to this <code>Alert's</code> activity indicator,
     * or <code>null</code> if 
     * there is none
     * @see #setIndicator
     * @since MIDP 2.0
     */
    public Gauge getIndicator() {
        throw new RuntimeException("STUB");
    }

    /**
     * Similar to {@link Displayable#addCommand}, however when the
     * application first adds a command to an <code>Alert</code>,
     * {@link #DISMISS_COMMAND} is implicitly removed.  Calling this
     * method with <code>DISMISS_COMMAND</code> as the parameter has
     * no effect.
     *
     * @param cmd the command to be added
     *
     * @throws NullPointerException if cmd is <code>null</code>
     */
    public void addCommand(Command cmd) {
        throw new RuntimeException("STUB");
    }

    /**
     * Similar to {@link Displayable#removeCommand}, however when the
     * application removes the last command from an
     * <code>Alert</code>, {@link #DISMISS_COMMAND} is implicitly
     * added.  Calling this method with <code>DISMISS_COMMAND</code>
     * as the parameter has no effect.
     *
     * @param cmd the command to be removed
     */
    public void removeCommand(Command cmd) {
        throw new RuntimeException("STUB");
    }

    /**
     * The same as {@link Displayable#setCommandListener} but with the 
     * following additional semantics.  If the listener parameter is
     * <code>null</code>, the <em>default listener</em> is restored.
     * See <a href="#commands">Commands and Listeners</a> for the definition 
     * of the behavior of the default listener.
     *
     * @param l the new listener, or <code>null</code>
     */
    public void setCommandListener(CommandListener l) {
        throw new RuntimeException("STUB");
    }
}
