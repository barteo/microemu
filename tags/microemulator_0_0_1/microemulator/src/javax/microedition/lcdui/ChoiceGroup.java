/*
 * @(#)ChoiceGroup.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.microedition.lcdui;


public class ChoiceGroup extends Item implements Choice
{

	ImageStringItem items[] = new ImageStringItem[4];
	boolean selectedItems[] = new boolean[4];
  int numOfItems = 0;

  int choiceType;

  int selectedIndex;

	static byte multiOff[] = {
  		-119, 80, 78, 71, 13, 10, 26, 10, 0, 0,
      0, 13, 73, 72, 68, 82, 0, 0, 0, 10,
      0, 0, 0, 11, 2, 3, 0, 0, 0, 59,
      0, -12, -117, 0, 0, 0, 6, 80, 76, 84,
      69, -1, -1, -1, -69, -69, -69, -57, 75, -33,
      -8, 0, 0, 0, 30, 73, 68, 65, 84, 120,
      -38, 99, 96, 96, 96, 96, 12, 101, -8, -51,
      -32, -64, 32, -64, -60, -64, -64, -128, 11, 51,
      -122, 50, -4, 6, 0, 63, 116, 3, 1, 53,
      -108, 39, -26, 0, 0, 0, 0, 73, 69, 78,
      68, -82, 66, 96, -126 };

	static byte multiOn[] = {
	    -119, 80, 78, 71, 13, 10, 26, 10, 0, 0,
      0, 13, 73, 72, 68, 82, 0, 0, 0, 10,
      0, 0, 0, 11, 2, 3, 0, 0, 0, 59,
      0, -12, -117, 0, 0, 0, 12, 80, 76, 84,
      69, -1, -1, -1, -69, -69, -69, 106, 106, 106,
      2, 2, 2, 106, -103, 14, -47, 0, 0, 0,
      53, 73, 68, 65, 84, 120, -38, 99, 96, 96,
      124, -64, -16, -1, -77, 3, -45, 65, -111, 15,
      76, 12, 108, 12, 76, 12, -4, 12, 76, 12,
      18, 12, 76, -68, 127, 24, -104, 126, 45, 96,
      96, -7, -11, -109, -127, -23, -65, 3, 3, -29,
      127, -122, -113, 0, 5, 37, 12, -34, 1, -99,
      -83, 100, 0, 0, 0, 0, 73, 69, 78, 68,
      -82, 66, 96, -126 };

	static byte radioOff[] = {
	    -119, 80, 78, 71, 13, 10, 26, 10, 0, 0,
      0, 13, 73, 72, 68, 82, 0, 0, 0, 11,
      0, 0, 0, 11, 2, 3, 0, 0, 0, -44,
      -62, -97, -75, 0, 0, 0, 9, 80, 76, 84,
      69, -1, -1, -1, -69, -69, -69, 106, 106, 106,
      -44, 13, -1, -24, 0, 0, 0, 42, 73, 68,
      65, 84, 120, -38, 99, 96, 90, -59, -64, 32,
      -63, 48, -127, 65, -127, 65, -127, 41, -127, -31,
      5, 19, 3, 3, 3, 50, 102, 80, 96, 80,
      96, -6, -63, 80, -64, -64, -76, -118, 1, 0,
      113, 24, 5, 61, 73, -68, -100, 98, 0, 0,
      0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };

	static byte radioOn[] = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0,
      0, 13, 73, 72, 68, 82, 0, 0, 0, 11,
      0, 0, 0, 11, 2, 3, 0, 0, 0, -44,
      -62, -97, -75, 0, 0, 0, 12, 80, 76, 84,
      69, -1, -1, -1, -69, -69, -69, 106, 106, 106,
      2, 2, 2, 106, -103, 14, -47, 0, 0, 0,
      50, 73, 68, 65, 84, 120, -38, 5, -63, 65,
      13, 0, 32, 12, 4, -63, -19, -11, -117, 1,
      18, 68, -100, 10, 52, 19, 94, 72, 64, 17,
      101, -122, 44, -44, -29, 98, -52, 89, 77, -102,
      40, 2, 85, -95, -73, -63, -104, -63, 37, -117,
      15, -40, 119, 10, 41, 78, 26, -79, 59, 0,
      0, 0, 0, 73, 69, 78, 68, -82, 66, 96,
      -126 };

	private static final Image imgMultiOff = Image.createImage(multiOff, 0, multiOff.length);
	private static final Image imgMultiOn = Image.createImage(multiOn, 0, multiOn.length);
	private static final Image imgRadioOff = Image.createImage(radioOff, 0, radioOff.length);
	private static final Image imgRadioOn = Image.createImage(radioOn, 0, radioOn.length);


  public ChoiceGroup(String label, int choiceType)
  {
		this(label, choiceType, null, null);
  }


	public ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements)
	{
    super(label);
    this.choiceType = choiceType;
    selectedIndex = 0;

		if (stringElements != null) {
			for (int i = 0; i < stringElements.length; i++) {
				append(stringElements[i], null);
			}
			if (this.choiceType == Choice.EXCLUSIVE) {
				setSelectedIndex(0, true);
			}
		}
	}


	public int append(String stringPart, Image imagePart)
  {
		insert(numOfItems, stringPart, imagePart);

    return (numOfItems - 1);
  }


  public void delete(int itemNum)
  {
		if (itemNum < 0 || itemNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

    if(itemNum != numOfItems - 1) {
      System.arraycopy(items, itemNum + 2, items, itemNum + 1, numOfItems - itemNum - 1);
    }
    numOfItems--;
  }


  public Image getImage(int elementNum)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

    return null;
  }


  public int getSelectedFlags(boolean[] selectedArray_return)
  {
		if (selectedArray_return == null) {
			throw new NullPointerException();
		}
		if (selectedArray_return.length < numOfItems) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(selectedItems, 0, selectedArray_return, 0, numOfItems);

		int selectedItemsNum = 0;
		for (int i = 0; i < numOfItems; i++) {
			if (selectedItems[i] == true) {
				selectedItemsNum++;
			}
		}

    return selectedItemsNum;
  }


  public int getSelectedIndex()
  {
		return selectedIndex;
  }


  public String getString(int elementNum)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

    return items[elementNum].getText();
  }


  public void insert(int elementNum, String stringPart, Image imagePart)
  {
		if (elementNum < 0 || elementNum > numOfItems) {
			throw new IndexOutOfBoundsException();
		}
		if (imagePart != null && imagePart.isMutable()) {
			throw new IllegalArgumentException();
		}
		if (stringPart == null) {
			throw new NullPointerException();
		}

    if (numOfItems + 1 == items.length) {
      ImageStringItem newitems[] = new ImageStringItem[numOfItems + 4];
			boolean newselectedItems[] = new boolean[numOfItems + 4];
      System.arraycopy(items, 0, newitems, 0, elementNum);
      System.arraycopy(items, elementNum, newitems, elementNum + 1, numOfItems - elementNum);
      System.arraycopy(selectedItems, 0, newselectedItems, 0, elementNum);
      System.arraycopy(selectedItems, elementNum, newselectedItems, elementNum + 1, numOfItems - elementNum);
      items = newitems;
			selectedItems = newselectedItems;
    }
		if (choiceType == Choice.EXCLUSIVE) {
	    items[elementNum] = new ImageStringItem(null, imgRadioOff, stringPart);
		} else if (choiceType == Choice.MULTIPLE) {
	    items[elementNum] = new ImageStringItem(null, imgMultiOff, stringPart);
		} else if (choiceType == Choice.IMPLICIT) {
	    items[elementNum] = new ImageStringItem(null, null, stringPart);
		}
		selectedItems[elementNum] = false;
    numOfItems++;
  }


  public boolean isSelected(int elementNum)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

    return selectedItems[elementNum];
  }


  public void set(int elementNum, String stringPart, Image imagePart)
	{
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}
		if (imagePart != null && imagePart.isMutable()) {
			throw new IllegalArgumentException();
		}
		if (stringPart == null) {
			throw new NullPointerException();
		}

		items[elementNum].setText(stringPart);
  }


  public void setLabel(String label)
  {
    super.setLabel(label);
  }


  public void setSelectedFlags(boolean[] selectedArray)
  {
		if (selectedArray == null) {
			throw new NullPointerException();
		}
		if (selectedArray.length < numOfItems) {
			throw new NullPointerException();
		}

		if (choiceType == Choice.EXCLUSIVE) {
			boolean performed = false;
			for (int i = 0; i < numOfItems; i++) {
				if (selectedArray[i]) {
					setSelectedIndex(i, true);
					performed = true;
					break;
				}
			}
			if (!performed) {
				setSelectedIndex(0, true);
			}
		} else if (choiceType == Choice.MULTIPLE) {
      System.arraycopy(selectedArray, 0, selectedItems, 0, numOfItems);
		}
  }


  public void setSelectedIndex(int elementNum, boolean selected)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

		if (choiceType == Choice.EXCLUSIVE && selected) {
			for (int i = 0; i < numOfItems; i++) {
				selectedItems[i] = false;
				items[i].setImage(imgRadioOff);
			}
			selectedItems[elementNum] = selected;
			items[elementNum].setImage(imgRadioOn);
			repaint();
		} else if (choiceType == Choice.MULTIPLE) {
			selectedItems[elementNum] = selected;
			if (selected) {
				items[elementNum].setImage(imgMultiOn);
			} else {
				items[elementNum].setImage(imgMultiOff);
			}
			repaint();
		}
  }


  public int size()
  {
    return numOfItems;
  }


	boolean isFocusable()
	{
		return true;
	}


	int getHeight()
	{
		int height = 0;
		for (int i = 0; i < numOfItems; i++) {
      height += items[i].getHeight();
		}

		return super.getHeight() + height;
	}


  int paint(Graphics g)
  {
		super.paintContent(g);

		g.translate(0, super.getHeight());
		int translatedY = 0;
    StringItem tmp;
		for (int i = 0; i < numOfItems; i++) {
      if (i == selectedIndex && focus) {
        items[i].invertPaint(true);
      } else {
        items[i].invertPaint(false);
      }
			items[i].paint(g);
      g.translate(0, items[i].getHeight());
			translatedY += items[i].getHeight();
		}
		g.translate(0, -translatedY);
		g.translate(0, -super.getHeight());

		return getHeight();
  }


  boolean select()
  {
    if (numOfItems == 0) {
      return false;
    }

		if (selectedItems[selectedIndex] == false) {
			setSelectedIndex(selectedIndex, true);
		} else {
			setSelectedIndex(selectedIndex, false);
		}

    return true;
  }


  int traverse(int gameKeyCode, int top, int bottom, boolean action)
  {
    if (gameKeyCode == 1) {
      if (selectedIndex > 0) {
				if (action) {
        	selectedIndex--;
				}
				int height = super.getHeight();
				for (int i = 0; i < selectedIndex; i++) {
					height += items[i].getHeight();
				}
				if (height < top) {
					return height - top;
				} else {
					repaint();
				}
      } else {
				if (top > 0) {
					return -top;
				} else {
					return Item.OUTOFITEM;
				}
			}
    }
    if (gameKeyCode == 6) {
      if (selectedIndex < (numOfItems - 1)) {
				if (action) {
	        selectedIndex++;
				}
				int height = super.getHeight();
				for (int i = 0; i <= selectedIndex; i++) {
					height += items[i].getHeight();
				}
				if (height > bottom) {
					return height - bottom;
				} else {
			    repaint();
				}
      } else {
				return Item.OUTOFITEM;
			}
    }

		return 0;
  }

}
