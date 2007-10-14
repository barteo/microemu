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
 * A <code>ChoiceGroup</code> is a group of selectable elements intended to be
 * placed within a
 * {@link Form}. The group may be created with a mode that requires a
 * single choice to be made or that allows multiple choices. The
 * implementation is responsible for providing the graphical representation of
 * these modes and must provide visually different graphics for different
 * modes. For example, it might use &quot;radio buttons&quot; for the
 * single choice
 * mode and &quot;check boxes&quot; for the multiple choice mode.
 *
 * <p> <strong>Note:</strong> most of the essential methods have been
 * specified in the {@link Choice Choice} interface.</p>
 * @since MIDP 1.0
 */

public class ChoiceGroup extends Item implements Choice {

    /**
     * Creates a new, empty <code>ChoiceGroup</code>, specifying its
     * title and its type.
     * The type must be one of <code>EXCLUSIVE</code>,
     * <code>MULTIPLE</code>, or <code>POPUP</code>. The
     * <code>IMPLICIT</code>
     * choice type is not allowed within a <code>ChoiceGroup</code>.
     *
     * @param label the item's label (see {@link Item Item})
     * @param choiceType <code>EXCLUSIVE</code>, <code>MULTIPLE</code>,
     * or <code>POPUP</code>
     * @throws IllegalArgumentException if <code>choiceType</code> 
     *      is not one of
     * <code>EXCLUSIVE</code>, <code>MULTIPLE</code>, or <code>POPUP</code>
     * @see Choice#EXCLUSIVE
     * @see Choice#MULTIPLE
     * @see Choice#IMPLICIT
     * @see Choice#POPUP
     */
    public ChoiceGroup(String label, int choiceType) {
        this(label, choiceType, new String[] {}, null);
    }

    /**
     * Creates a new <code>ChoiceGroup</code>, specifying its title,
     * the type of the
     * <code>ChoiceGroup</code>, and an array of <code>Strings</code>
     * and <code>Images</code> to be used as its
     * initial contents.
     *
     * <p>The type must be one of <code>EXCLUSIVE</code>,
     * <code>MULTIPLE</code>, or <code>POPUP</code>.  The
     * <code>IMPLICIT</code>
     * type is not allowed for <code>ChoiceGroup</code>.</p>
     *
     * <p>The <code>stringElements</code> array must be non-null and
     * every array element
     * must also be non-null.  The length of the
     * <code>stringElements</code> array
     * determines the number of elements in the <code>ChoiceGroup</code>.  The
     * <code>imageElements</code> array
     * may be <code>null</code> to indicate that the
     * <code>ChoiceGroup</code> elements have no images.
     * If the
     * <code>imageElements</code> array is non-null, it must be the
     * same length as the
     * <code>stringElements</code> array.  Individual elements of the
     * <code>imageElements</code> array
     * may be <code>null</code> in order to indicate the absence of an
     * image for the
     * corresponding <code>ChoiceGroup</code> element.  Non-null elements
     * of the
     * <code>imageElements</code> array may refer to mutable or
     * immutable images.</p>
     *
     * @param label the item's label (see {@link Item Item})
     * @param choiceType <code>EXCLUSIVE</code>, <code>MULTIPLE</code>,
     * or <code>POPUP</code>
     * @param stringElements set of strings specifying the string parts of the
     * <code>ChoiceGroup</code> elements
     * @param imageElements set of images specifying the image parts of
     * the <code>ChoiceGroup</code> elements
     *
     * @throws NullPointerException if <code>stringElements</code>
     * is <code>null</code>
     * @throws NullPointerException if the <code>stringElements</code>
     * array contains
     * any <code>null</code> elements
     * @throws IllegalArgumentException if the <code>imageElements</code>
     * array is non-null
     * and has a different length from the <code>stringElements</code> array
     * @throws IllegalArgumentException if <code>choiceType</code> 
     *      is not one of
     * <code>EXCLUSIVE</code>, <code>MULTIPLE</code>, or <code>POPUP</code>
     *
     * @see Choice#EXCLUSIVE
     * @see Choice#MULTIPLE
     * @see Choice#IMPLICIT
     * @see Choice#POPUP
     */
    public ChoiceGroup(String label, int choiceType,
                       String[] stringElements, Image[] imageElements) {

        this(label, choiceType, stringElements, imageElements, false);
    }

    /**
     * Special constructor used by List
     *
     * @param label the item's label (see {@link Item Item})
     * @param choiceType EXCLUSIVE or MULTIPLE
     * @param stringElements set of strings specifying the string parts of the
     * ChoiceGroup elements
     * @param imageElements set of images specifying the image parts of
     * the ChoiceGroup elements
     * @param implicitAllowed Flag to allow implicit selection
     *
     * @throws NullPointerException if stringElements is null
     * @throws NullPointerException if the stringElements array contains
     * any null elements
     * @throws IllegalArgumentException if the imageElements array is non-null
     * and has a different length from the stringElements array
     * @throws IllegalArgumentException if choiceType is neither
     * EXCLUSIVE nor MULTIPLE
     * @throws IllegalArgumentException if any image in the imageElements
     * array is mutable
     *
     * @see Choice#EXCLUSIVE
     * @see Choice#MULTIPLE
     * @see Choice#IMPLICIT
     */
    ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements, boolean implicitAllowed) {
        super(label);
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the number of elements in the <code>ChoiceGroup</code>.
     * 
     * @return the number of elements in the <code>ChoiceGroup</code>
     */
    public int size() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the <code>String</code> part of the element referenced by
     * <code>elementNum</code>.
     *
     * @param elementNum the index of the element to be queried
     * @return the string part of the element
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     * @see #getImage(int)
     */
    public String getString(int elementNum) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the <code>Image</code> part of the element referenced by
     * <code>elementNum</code>.
     *
     * @param elementNum the number of the element to be queried
     * @return the image part of the element, or null if there is no image
     * @throws IndexOutOfBoundsException if elementNum is invalid
     * @see #getString(int)
     */
    public Image getImage(int elementNum) {
        throw new RuntimeException("STUB");
    }

    /**
     * Appends an element to the <code>ChoiceGroup</code>.
     *
     * @param stringPart the string part of the element to be added
     * @param imagePart the image part of the element to be added, or
     * <code>null</code> if there is no image part
     * @return the assigned index of the element
     * @throws NullPointerException if <code>stringPart</code> is
     * <code>null</code>
     */
    public int append(String stringPart, Image imagePart) {
        throw new RuntimeException("STUB");
    }

    /**
     * Inserts an element into the <code>ChoiceGroup</code> just prior to
     * the element specified.
     *
     * @param elementNum the index of the element where insertion is to occur
     * @param stringPart the string part of the element to be inserted
     * @param imagePart the image part of the element to be inserted,
     * or <code>null</code> if there is no image part
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     * @throws NullPointerException if <code>stringPart</code>
     * is <code>null</code>
     */
    public void insert(int elementNum, String stringPart,
                       Image imagePart) {
        throw new RuntimeException("STUB");
    }

    /**
     * Deletes the element referenced by <code>elementNum</code>.
     *
     * @param elementNum the index of the element to be deleted
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     */
    public void delete(int elementNum) {
        throw new RuntimeException("STUB");
    }

    /**
     * Deletes all elements from this <code>ChoiceGroup</code>.
     */
    public void deleteAll() {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the <code>String</code> and <code>Image</code> parts of the
     * element referenced by <code>elementNum</code>,
     * replacing the previous contents of the element.
     *
     * @param elementNum the index of the element to be set
     * @param stringPart the string part of the new element
     * @param imagePart the image part of the element, or <code>null</code>
     * if there is no image part
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     * @throws NullPointerException if <code>stringPart</code> is
     * <code>null</code>
     */
    public void set(int elementNum, String stringPart, Image imagePart) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets a boolean value indicating whether this element is selected.
     *
     * @param elementNum the index of the element to be queried
     *
     * @return selection state of the element
     *
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     */
    public boolean isSelected(int elementNum) {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the index number of an element in the
     * <code>ChoiceGroup</code> that is
     * selected. For <code>ChoiceGroup</code> objects of type
     * <code>EXCLUSIVE</code> and <code>POPUP</code>
     * there is at most one element selected, so
     * this method is useful for determining the user's choice.
     * Returns <code>-1</code> if
     * there are no elements in the <code>ChoiceGroup</code>.
     *
     * <p>For <code>ChoiceGroup</code> objects of type
     * <code>MULTIPLE</code>, this always
     * returns <code>-1</code> because no
     * single value can in general represent the state of such a
     * <code>ChoiceGroup</code>.
     * To get the complete state of a <code>MULTIPLE</code>
     * <code>Choice</code>, see {@link
     * #getSelectedFlags getSelectedFlags}.</p>
     *
     * @return index of selected element, or <code>-1</code> if none
     * @see #setSelectedIndex
     */
    public int getSelectedIndex() {
        throw new RuntimeException("STUB");
    }

    /**
     * Queries the state of a <code>ChoiceGroup</code> and returns the state of
     * all elements in the
     * boolean array
     * <code>selectedArray_return</code>. <strong>Note:</strong> this
     * is a result parameter.
     * It must be at least as long as the size
     * of the <code>ChoiceGroup</code> as returned by <code>size()</code>.
     * If the array is longer, the extra
     * elements are set to <code>false</code>.
     *
     * <p>For <code>ChoiceGroup</code> objects of type
     * <code>MULTIPLE</code>, any
     * number of elements may be selected and set to true in the result
     * array.  For <code>ChoiceGroup</code> objects of type
     * <code>EXCLUSIVE</code> and <code>POPUP</code>
     * exactly one element will be selected, unless there are
     * zero elements in the <code>ChoiceGroup</code>. </p>
     *
     * @return the number of selected elements in the <code>ChoiceGroup</code>
     *
     * @param selectedArray_return array to contain the results
     * @throws IllegalArgumentException if <code>selectedArray_return</code>
     * is shorter than the size of the <code>ChoiceGroup</code>
     * @throws NullPointerException if <code>selectedArray_return</code>
     * is null
     * @see #setSelectedFlags
     */
    public int getSelectedFlags(boolean[] selectedArray_return) {
        throw new RuntimeException("STUB");
    }

    /**
     * For <code>ChoiceGroup</code> objects of type
     * <code>MULTIPLE</code>, this simply sets an
     * individual element's selected state.
     *
     * <P>For <code>ChoiceGroup</code> objects of type
     * <code>EXCLUSIVE</code> and <code>POPUP</code>, this can be used only to
     * select an element.  That is, the <code> selected </code> parameter must
     * be <code> true </code>. When an element is selected, the previously
     * selected element is deselected. If <code> selected </code> is <code>
     * false </code>, this call is ignored.</P>
     *
     * <p>For both list types, the <code>elementNum</code> parameter
     * must be within
     * the range
     * <code>[0..size()-1]</code>, inclusive. </p>
     *
     * @param elementNum the number of the element. Indexing of the
     * elements is zero-based
     * @param selected the new state of the element <code>true=selected</code>,
     * <code>false=not</code> selected
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     * @see #getSelectedIndex
     */
    public void setSelectedIndex(int elementNum, boolean selected) {
        throw new RuntimeException("STUB");
    }

    /**
     * Attempts to set the selected state of every element in the
     * <code>ChoiceGroup</code>. The array
     * must be at least as long as the size of the
     * <code>ChoiceGroup</code>. If the array is
     * longer, the additional values are ignored. <p>
     *
     * For <code>ChoiceGroup</code> objects of type
     * <code>MULTIPLE</code>, this sets the selected
     * state of every
     * element in the <code>Choice</code>. An arbitrary number of
     * elements may be selected.
     * <p>
     *
     * For <code>ChoiceGroup</code> objects of type
     * <code>EXCLUSIVE</code> and <code>POPUP</code>, exactly one array
     * element must have the value <code>true</code>. If no element is
     * <code>true</code>,
     * the first element
     * in the <code>Choice</code> will be selected. If two or more
     * elements are <code>true</code>, the
     * implementation will choose the first <code>true</code> element
     * and select it. <p>
     *
     * @param selectedArray an array in which the method collect the
     * selection status
     * @throws IllegalArgumentException if <code>selectedArray</code>
     * is shorter than the size of the <code>ChoiceGroup</code>
     * @throws NullPointerException if the <code>selectedArray</code>
     * is <code>null</code>
     * @see #getSelectedFlags
     */
    public void setSelectedFlags(boolean[] selectedArray) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the application's preferred policy for fitting
     * <code>Choice</code> element
     * contents to the available screen space. The set policy applies for all
     * elements of the <code>Choice</code> object.  Valid values are
     * {@link #TEXT_WRAP_DEFAULT}, {@link #TEXT_WRAP_ON},
     * and {@link #TEXT_WRAP_OFF}. Fit policy is a hint, and the
     * implementation may disregard the application's preferred policy.
     *
     * @param fitPolicy preferred content fit policy for choice elements
     * @throws IllegalArgumentException if <code>fitPolicy</code> is invalid
     * @see #getFitPolicy
     * @since MIDP 2.0
     */
    public void setFitPolicy(int fitPolicy) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the application's preferred policy for fitting
     * <code>Choice</code> element
     * contents to the available screen space.  The value returned is the
     * policy that had been set by the application, even if that value had
     * been disregarded by the implementation.
     *
     * @return one of {@link #TEXT_WRAP_DEFAULT}, {@link #TEXT_WRAP_ON}, or
     * {@link #TEXT_WRAP_OFF}
     * @see #setFitPolicy
     * @since MIDP 2.0
     */
    public int getFitPolicy() {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the application's preferred font for
     * rendering the specified element of this <code>Choice</code>.
     * An element's font is a hint, and the implementation may disregard
     * the application's preferred font.
     *
     * <p> The <code>elementNum</code> parameter must be within the range
     * <code>[0..size()-1]</code>, inclusive.</p>
     *
     * <p> The <code>font</code> parameter must be a valid <code>Font</code>
     * object or <code>null</code>. If the <code>font</code> parameter is
     * <code>null</code>, the implementation must use its default font
     * to render the element.</p>
     *
     * @param elementNum the index of the element, starting from zero
     * @param font the preferred font to use to render the element
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     * @see #getFont
     * @since MIDP 2.0
     */
    public void setFont(int elementNum, Font font) {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the application's preferred font for
     * rendering the specified element of this <code>Choice</code>. The
     * value returned is the font that had been set by the application,
     * even if that value had been disregarded by the implementation.
     * If no font had been set by the application, or if the application
     * explicitly set the font to <code>null</code>, the value is the default
     * font chosen by the implementation.
     *
     * <p> The <code>elementNum</code> parameter must be within the range
     * <code>[0..size()-1]</code>, inclusive.</p>
     *
     * @param elementNum the index of the element, starting from zero
     * @return the preferred font to use to render the element
     * @throws IndexOutOfBoundsException if <code>elementNum</code> is invalid
     * @see #setFont(int elementNum, Font font)
     * @since MIDP 2.0
     */
    public Font getFont(int elementNum) {
        throw new RuntimeException("STUB");
    }
}
