/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2005 Andres Navarro
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
 *
 *  Contributor(s):
 *    Shane Harper
 */
 
package javax.microedition.lcdui;

public class ChoiceGroup extends Item implements Choice
{
    int choiceType;

    private ChoiceItem items[] = new ChoiceItem[4];
    
    private int numOfItems = 0;
  
    private int fitPolicy;
    
    private int highlightedItemIndex = -1;  
    
    private List popupList;

	private static byte multiOff[] = {
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
        68, -82, 66, 96, -126 
    };

	private static byte multiOn[] = {
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
        -82, 66, 96, -126 
    };

	private static byte radioOff[] = {
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
	    0, 0, 73, 69, 78, 68, -82, 66, 96, -126 
    };

	private static byte radioOn[] = {
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
        -126 
    };

	private static final Image imgMultiOff = Image.createImage(multiOff, 0, multiOff.length);
	private static final Image imgMultiOn = Image.createImage(multiOn, 0, multiOn.length);
	private static final Image imgRadioOff = Image.createImage(radioOff, 0, radioOff.length);
	private static final Image imgRadioOn = Image.createImage(radioOn, 0, radioOn.length);


	public ChoiceGroup(String label, int choiceType) 
	{
		this(label, choiceType, true);
	}


	public ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements)
	{
		this(label, choiceType, stringElements, imageElements, true);
	}


	ChoiceGroup(String label, int choiceType, boolean validateChoiceType)
	{
		super(label);

		if (validateChoiceType) {
			if (choiceType != Choice.POPUP && choiceType != Choice.MULTIPLE && choiceType != Choice.EXCLUSIVE) {
				throw new IllegalArgumentException("Illegal choice type");
			}
		}
		this.choiceType = choiceType;
		if (choiceType == Choice.POPUP) {
			// POPUP has a hidden List to implement it's
			// behaviour
			popupList = new List(label, Choice.IMPLICIT);
			popupList.setCommandListener(new ImplicitListener());
		}
	}


	// XXX imageElements is ignored.
	ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements, boolean validateChoiceType)
	{
		this(label, choiceType, validateChoiceType);

		for (int i = 0; i < stringElements.length; i++) {
			if (imageElements == null) {
				append(stringElements[i], null);
			} else {
				append(stringElements[i], imageElements[i]);
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

    // Ensure that an item of an EXCLUSIVE list remains selected.
    if ((Choice.EXCLUSIVE == choiceType || Choice.POPUP == choiceType)
    		&& items[itemNum].isSelected()) {
      if (numOfItems > 1) {
        items[itemNum!=0 ? 0 : 1].setSelectedState(true);
      }
    }

    // Delete item.
    if (itemNum != numOfItems - 1) {
      System.arraycopy(items, itemNum+1, items, itemNum, numOfItems-itemNum-1);
    }
    numOfItems--;
    // clear the slot to allow garbage collection
    items[numOfItems] = null;

    // Ensure highlighted item remains highlighted (if it wasn't just deleted).
    if (highlightedItemIndex > itemNum) {
      --highlightedItemIndex;
    }

    // Ensure that an item remains highlighted.
    if (highlightedItemIndex >= numOfItems) {
      highlightedItemIndex = numOfItems-1;
    }
    
    if (choiceType == Choice.POPUP)
	  popupList.delete(itemNum);
    repaint();
  }
  
  public void deleteAll() {
	  // clear the array to allow garbage collection
	  for (int i = 0; i < numOfItems; i++)
		  items[i] = null;
	  numOfItems = 0;
	  highlightedItemIndex = -1;
	  if (choiceType == Choice.POPUP)
		  popupList.deleteAll();
	  repaint();
  }


  public int getFitPolicy() {
	  return fitPolicy;
  }
  
  public Font getFont(int itemNum) {
		if (itemNum < 0 || itemNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}
		return items[itemNum].getFont();
  }
  
  
  public Image getImage(int elementNum)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

    return items[elementNum].getImage();
  }


  /**
   * Queries the state of a ChoiceGroup and returns the state of all elements in
   * the boolean array selectedArray_return. NOTE: this is a result parameter.
   * It must be at least as long as the size of the ChoiceGroup as returned by
   * size(). If the array is longer, the extra elements are set to false.
   *
   * For ChoiceGroup objects of type MULTIPLE, any number of elements may be
   * selected and set to true in the result array. For ChoiceGroup objects of
   * type EXCLUSIVE, exactly one element will be selected, unless there are zero
   * elements in the ChoiceGroup.
   */
  public int getSelectedFlags(boolean[] selectedArray_return)
  {
		if (selectedArray_return == null) {
			throw new NullPointerException();
		}
		if (selectedArray_return.length < numOfItems) {
			throw new IllegalArgumentException();
		}

    // set selectedArray_return elements and count number of selected items
		int selectedItemsCount = 0;
		
			for (int i = 0; i < selectedArray_return.length; ++i) {
	      selectedArray_return[i] = (i<numOfItems) ? items[i].isSelected() : false;
	      if (selectedArray_return[i]) {
	        ++selectedItemsCount;
	      }
    }

    return selectedItemsCount;
  }

  /**
   *  Returns the index number of an element in the ChoiceGroup that is
   *  selected. For ChoiceGroup objects of type EXCLUSIVE there is at most one
   *  element selected, so this method is useful for determining the user's
   *  choice. Returns -1 if there are no elements in the ChoiceGroup.
   *
   *  For ChoiceGroup objects of type MULTIPLE, this always returns -1 because
   *  no single value can in general represent the state of such a ChoiceGroup.
   *  To get the complete state of a MULTIPLE Choice, see getSelectedFlags.
   */
  public int getSelectedIndex()
  {
    switch (choiceType) {
      case Choice.EXCLUSIVE:
      case Choice.POPUP:
        // XXX It'd be nice if the selected item index was stored, so it isn't
        //     necessary to search for it.
        for (int i = 0; i < numOfItems; ++i) {
          if (items[i].isSelected()) return i;
        }
        break;
      case Choice.IMPLICIT:
        return highlightedItemIndex;
    }
    return -1;
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
		if (stringPart == null) {
			throw new NullPointerException();
		}

		if (choiceType == Choice.POPUP) {
			  popupList.insert(elementNum, stringPart, imagePart);
		}

	if (numOfItems == items.length  /*no space left in item array*/) {
      ChoiceItem newItems[] = new ChoiceItem[numOfItems + 4];
      System.arraycopy(items, 0, newItems, 0, numOfItems);
      items = newItems;
    }

    System.arraycopy(items, elementNum, items, elementNum + 1,
                     numOfItems - elementNum);
  
	  items[elementNum] = new ChoiceItem(null, imagePart, stringPart);

    ++numOfItems;

    if (numOfItems == 1) {
      highlightedItemIndex = 0;
      if (Choice.EXCLUSIVE == choiceType ||
    		  Choice.POPUP == choiceType) {
        setSelectedIndex(0, true);
      }
    }
    
    repaint();
  }


  public boolean isSelected(int elementNum)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

    return items[elementNum].isSelected();
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
		items[elementNum].setImage(imagePart);

		if (choiceType == Choice.POPUP) {
			  popupList.set(elementNum, stringPart, imagePart);
		}
		
		repaint();
  }

  public void setFitPolicy(int policy) {
	  if (policy != Choice.TEXT_WRAP_DEFAULT &&
			  	policy != Choice.TEXT_WRAP_ON &&
			  	policy != Choice.TEXT_WRAP_OFF)
		  throw new IllegalArgumentException("Bad Policy");
	  fitPolicy = policy;
		if (choiceType == Choice.POPUP) {
			  popupList.setFitPolicy(policy);
		}
  }
  
  public void setFont(int itemNum, Font font) {
		if (itemNum < 0 || itemNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}
		items[itemNum].setFont(font);
		if (choiceType == Choice.POPUP) {
			  popupList.setFont(itemNum, font);
		}
  }


  public void setSelectedFlags(boolean[] selectedArray)
  {
		if (selectedArray == null) {
			throw new NullPointerException();
		}
		if (selectedArray.length < numOfItems) {
			throw new NullPointerException();
		}
		
		if (numOfItems == 0)
			return;

		if (choiceType == Choice.MULTIPLE) {
			for (int i = 0; i < numOfItems; i++) {
				setSelectedIndex(i, selectedArray[i]);
			}
		} else {
			int selectedItem = -1;
			for (int i = 0; i < numOfItems; i++) {
				if (selectedArray[i]) {
					setSelectedIndex(i, true);
					selectedItem = i;
					break;
				}
			}
			if (selectedItem == -1) {
				setSelectedIndex(0, true);
			}
			
			if (choiceType == Choice.POPUP) {
				popupList.setSelectedFlags(selectedArray);
			}
		}

  }


  public void setSelectedIndex(int elementNum, boolean selected)
  {
		if (elementNum < 0 || elementNum >= numOfItems) {
			throw new IndexOutOfBoundsException();
		}

        highlightedItemIndex = elementNum;
		if ((choiceType == Choice.EXCLUSIVE ||
				choiceType == Choice.POPUP) && selected) {
			for (int i = 0; i < numOfItems; i++) {
				items[i].setSelectedState(elementNum == i);
			}
			if (choiceType == Choice.POPUP) {
			        popupList.setSelectedIndex(elementNum, true);
		    }
			 repaint();
		} else if (choiceType == Choice.MULTIPLE) {
			items[elementNum].setSelectedState(selected);
			repaint();
		} else if (choiceType == Choice.IMPLICIT) {
		    if (selected) {
				items[elementNum].setSelectedState(selected);
				repaint();
		    }
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
		if (choiceType == Choice.POPUP) {
			if (highlightedItemIndex != -1) {
				height += items[highlightedItemIndex].getHeight();
			}
		} else {
			for (int i = 0; i < numOfItems; i++) {
				height += items[i].getHeight();
			}
		}

		return super.getHeight() + height;
	}
	
	/*
	 * Get item index from coordinates
	 */
	int getItemIndexAt(int x, int y)
	{
		x -= super.getHeight();
		int testY = 0;
		for (int i = 0; i < numOfItems; i++) {
			testY += items[i].getHeight();
			if (y < testY) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	int getHeightToItem(int itemIndex) 
	{
		int height = 0;

		for (int i = 0; i < itemIndex; i++) {
			height += items[i].getHeight();
		}

		return height;
	}


	int getItemHeight(int itemIndex)
	{
		return items[itemIndex].getHeight();
	}
	
	
  int paint(Graphics g)
  {
		super.paintContent(g);

		g.translate(0, super.getHeight());
		int translatedY = 0;
		
		if (choiceType == Choice.POPUP) {
			int index = getSelectedIndex();
			if (index != -1) {
				items[index].invertPaint(hasFocus());
				items[index].paint(g);
			}
			g.translate(0, -super.getHeight());
		} else {
			for (int i = 0; i < numOfItems; i++) {
				items[i].invertPaint(i == highlightedItemIndex && hasFocus());
				items[i].paint(g);
				g.translate(0, items[i].getHeight());
				translatedY += items[i].getHeight();
			}
			g.translate(0, -translatedY);
			g.translate(0, -super.getHeight());
		}

		return getHeight();
  }


  boolean select()
  {
    if (numOfItems == 0) {
      return false;
    }

    if (choiceType == Choice.POPUP) {
        getOwner().currentDisplay.setCurrent(popupList);
    } else {
	    // XXX What does the following statement do?
    	
    	// It is correct, in the case of multiple inverts the selected
    	// state, in exclusive selects the highligthed
    	// and in implicit it does nothing
    	// Andres Navarro
	    setSelectedIndex(highlightedItemIndex, !items[highlightedItemIndex].isSelected());
    }

    return true;
  }

  
  
  int traverse(int gameKeyCode, int top, int bottom, boolean action)
  {
	  
	if (this.choiceType == Choice.POPUP) {
		// POPUP has a totally different behaviour
	    if (gameKeyCode == Canvas.UP) {
			if (top > 0) {
				return -top;
			} else {
				return Item.OUTOFITEM;
			}
	    } else if (gameKeyCode == Canvas.DOWN) {
	    	if (!action) {
	    		int height = super.getHeight();
				int index = getSelectedIndex();
				if (index != -1) {
	    			height += items[index].getHeight();
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
	} else {
	    if (gameKeyCode == Canvas.UP) {
	      if (highlightedItemIndex > 0) {
					if (action) {
						highlightedItemIndex--;
					}
					int height = super.getHeight();
					for (int i = 0; i < highlightedItemIndex; i++) {
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
	    if (gameKeyCode == Canvas.DOWN) {
	      if ((!action && highlightedItemIndex < numOfItems)
	      		|| (action && highlightedItemIndex < (numOfItems - 1))) {
					if (action) {
						highlightedItemIndex++;
					}
					int height = super.getHeight();
					for (int i = 0; i <= highlightedItemIndex; i++) {
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
	}

		return 0;
  }

  void repaint() {
	  // the popup list should be repainted
	  // in the case it is being shown
	  if (choiceType == Choice.POPUP)
		  popupList.repaint();
	  super.repaint();
  }

  class ChoiceItem extends ImageStringItem
  {
    private boolean selected;
    private Font font;
    Image box;
           

    ChoiceItem(String label, Image image, String text)
    {
      super(label, image, text);
      setSelectedState(false);
      font = Font.getDefaultFont();
    }

    Font getFont() {
    	return font;
    }
    
    	public void setImage(Image img)
	{
                this.img = img;
		
                int width = 0;
                if (box != null) width+=box.getWidth();
                if (this.img != null) width+=img.getWidth();
                if (width > 0) width+=2;
                stringComponent.setWidthDecreaser(width);
	}

	int getHeight()
	{
                int height =  0;
                if (box != null) height = box.getHeight();                
		if (img != null && img.getHeight() > height) {
			height = img.getHeight();
                }
		if (stringComponent.getHeight() > height) {
                            height = stringComponent.getHeight();                
		}
                return height;
	}

  int paint(Graphics g)
  {
		if (stringComponent == null) {
			return 0;
		}

                int widthAddition = 0;
		if (box != null) {
			g.drawImage(box, 0, 0, Graphics.LEFT | Graphics.TOP);
			if (img != null) {
                            widthAddition = box.getWidth();
                            g.translate(box.getWidth(), 0);
                        }
                        else {
                            widthAddition = box.getWidth() + 2;
                            g.translate(box.getWidth() + 2, 0);
                        }
		}
                
                
		if (img != null) {
                        widthAddition += img.getWidth() + 2;
			g.drawImage(img, 0, 0, Graphics.LEFT | Graphics.TOP);
			g.translate(img.getWidth() + 2, 0);
		}

		int y = stringComponent.paint(g);

		if (widthAddition != 0) {
			g.translate(-widthAddition, 0);
		}

		return y;
  }
        
        
    
    boolean isSelected()
    {
      return selected;
    }

    void setFont(Font f) {
		if (f == null)
			throw new NullPointerException();
    	// only allow fonts of the same height
    	// for now (to simplify the layout)
    	if (f.getHeight() == font.getHeight())
    		font = f;
    }
    
    void setSelectedState(boolean state)
    {
      selected = state;
      
      if (choiceType != Choice.IMPLICIT && choiceType != Choice.POPUP) {
            box = (Choice.EXCLUSIVE  == choiceType ? 
						(state? imgRadioOn:imgRadioOff) : (state? imgMultiOn:imgMultiOff));
      }
    }
  }

  class ImplicitListener implements CommandListener {
		public void commandAction(Command c, Displayable d) {
			List list = (List) d;
			setSelectedIndex(list.getSelectedIndex(), true);
			try {
				getOwner().currentDisplay.setCurrent(getOwner());
				repaint();
			} catch (NullPointerException n) {
				// this happens if the item becomes an orphan
				// (ie not owned by a Form, shouldn't happen
				// if correct programming practices are used!!)
			}
		}
	}
}