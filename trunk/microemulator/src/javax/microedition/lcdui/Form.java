/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package javax.microedition.lcdui;


public class Form extends Screen
{

	Item items[] = new Item[4];
	int numOfItems = 0;
	int focusItemIndex;
  ItemStateListener itemStateListener;


  public Form(String title)
	{
		super(title);
		focusItemIndex = -2;
	}


  public Form(String title, Item[] items)
  {
    this(title);

    this.items = items;
    numOfItems = items.length;
    for (int i = 0; i < numOfItems; i++) {
      items[i].setOwner(this);
    }
  }


	public int append(Item item)
	{
    if (numOfItems + 1 == items.length) {
      Item newitems[] = new Item[numOfItems + 4];
      System.arraycopy(items, 0, newitems, 0, numOfItems);
      items = newitems;
    }
	  items[numOfItems] = item;
		items[numOfItems].setOwner(this);
    numOfItems++;

    return (numOfItems - 1);
	}


  public int append(Image img)
  {
    return append(new ImageItem(null, img, ImageItem.LAYOUT_DEFAULT, null));
  }


	public int append(String str)
	{
		return append(new StringItem(null, str));
	}


  public void delete(int itemNum)
  {
    // Not implemented
  }


  public Item get(int itemNum)
  {
    if (itemNum < 0 || itemNum >= numOfItems) {
      throw new IndexOutOfBoundsException();
    }
    return items[itemNum];
  }


  public void insert(int itemNum, Item item)
  {
    // Not implemented
  }


  public void set(int itemNum, Item item)
  {
    // Not implemented
  }


  public void setItemStateListener(ItemStateListener iListener)
  {
    itemStateListener = iListener;
  }


  public int size()
  {
    return numOfItems;
  }


  int paintContent(Graphics g)
  {
		int contentHeight = 0;
		int translateY;
		for (int i = 0; i < numOfItems; i++) {
			translateY = items[i].paint(g);
			g.translate(0, translateY);
			contentHeight += translateY;
		}
		g.translate(0, -contentHeight);

		return contentHeight;
  }


	int getHeight()
	{
		int height = 0;

		for (int i = 0; i < numOfItems; i++) {
			height += items[i].getHeight();
		}

		return height;
	}


	void hideNotify()
	{
		super.hideNotify();
	}


  void keyPressed(int keyCode)
  {
    if (focusItemIndex != -1) {
      if (Display.getGameAction(keyCode) == 8) {
        items[focusItemIndex].select();
      } else {
        items[focusItemIndex].keyPressed(keyCode);
      }
    }

    super.keyPressed(keyCode);
  }


	void showNotify()
	{
		super.showNotify();

		if (focusItemIndex == -2) {
			focusItemIndex = -1;

			for (int i = 0; i < numOfItems; i++) {
				if (items[i].isFocusable()) {
					items[i].setFocus(true);
					focusItemIndex = i;
					break;
				}
			}
		}
	}


	int traverse(int gameKeyCode, int top, int bottom)
	{
		int height, testItemIndex, traverse, i;
		int topItemIndex, bottomItemIndex;
    
    if (numOfItems == 0) {
      return 0;
    }

		if (gameKeyCode == 1) {
			topItemIndex = getTopVisibleIndex(top);
			if (focusItemIndex == -1) {
				testItemIndex = topItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse = items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
			} else {
				testItemIndex = focusItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse = items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, true);
			}
			if (traverse != Item.OUTOFITEM) {
				if (focusItemIndex == -1 && items[testItemIndex].isFocusable()) {
					items[testItemIndex].setFocus(true);
					focusItemIndex = testItemIndex;
				}
				return traverse;
			} else {
				if (testItemIndex > 0) {
					// Czy istnieje obiekt focusable powyzej testItemIndex widoczny na ekranie
					// jesli tak to zrob na nim traverse(false) i return traverse
					for (i = testItemIndex - 1; i >= topItemIndex; i--) {
						if (items[i].isFocusable()) {
							if (focusItemIndex != -1) {
								items[focusItemIndex].setFocus(false);
							}
							items[i].setFocus(true);
							focusItemIndex = i;
							height = getHeightToItem(i);
							traverse = items[i].traverse(gameKeyCode, top - height, bottom - height, false);
							if (traverse == Item.OUTOFITEM) {
								return 0;
							} else {
								return traverse;
							}
						}
					}
					// Na najnizszym widocznym item zrob traverse(false)
					height = getHeightToItem(topItemIndex);
					traverse = items[topItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
					if (traverse == Item.OUTOFITEM) {
					} else {
						// Sprawdzenie czy znajduje sie powyzej na ekranie focusable item
						// jesli tak zrob co trzeba
						bottomItemIndex = getTopVisibleIndex(bottom + traverse);
						if (focusItemIndex != -1 && focusItemIndex > bottomItemIndex) {
							items[focusItemIndex].setFocus(false);
							focusItemIndex = -1;
						}
						return traverse;
					}
				}
			}
		}
		if (gameKeyCode == 6) {
			bottomItemIndex = getBottomVisibleIndex(bottom);
			if (focusItemIndex == -1) {
				testItemIndex = bottomItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse = items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
			} else {
				testItemIndex = focusItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse = items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, true);
			}
			if (traverse != Item.OUTOFITEM) {
				if (focusItemIndex == -1 && items[testItemIndex].isFocusable()) {
					items[testItemIndex].setFocus(true);
					focusItemIndex = testItemIndex;
				}
				return traverse;
			} else {
				if (testItemIndex < numOfItems - 1) {
					// Czy istnieje obiekt focusable ponizej testItemIndex widoczny na ekranie
					// jesli tak to zrob na nim traverse(false) i return traverse
					for (i = testItemIndex + 1; i <= bottomItemIndex; i++) {
						if (items[i].isFocusable()) {
							if (focusItemIndex != -1) {
								items[focusItemIndex].setFocus(false);
							}
							items[i].setFocus(true);
							focusItemIndex = i;
							height = getHeightToItem(i);
							traverse = items[i].traverse(gameKeyCode, top - height, bottom - height, false);
							if (traverse == Item.OUTOFITEM) {
								return 0;
							} else {
								return traverse;
							}
						}
					}
					// Na najnizszym widocznym item zrob traverse(false)
					height = getHeightToItem(bottomItemIndex);
					traverse = items[bottomItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
					if (traverse == Item.OUTOFITEM) {
					} else {
						// Sprawdzenie czy znajduje sie powyzej na ekranie focusable item
						// jesli tak zrob co trzeba
						topItemIndex = getTopVisibleIndex(top + traverse);
						if (focusItemIndex != -1 && focusItemIndex < topItemIndex) {
							items[focusItemIndex].setFocus(false);
							focusItemIndex = -1;
						}
						return traverse;
					}
				}
			}
		}

		return 0;
	}


	int getTopVisibleIndex(int top)
	{
		int height = 0;

		for (int i = 0; i < numOfItems; i++) {
			height += items[i].getHeight();
			if (height >= top) {
				return i;
			}
		}

		return numOfItems - 1;
	}


	int getBottomVisibleIndex(int bottom)
	{
		int height = 0;

		for (int i = 0; i < numOfItems; i++) {
			height += items[i].getHeight();
			if (height > bottom) {
				return i;
			}
		}

		return numOfItems - 1;
	}


	int getHeightToItem(int itemIndex)
	{
		int height = 0;

		for (int i = 0; i < itemIndex; i++) {
			height += items[i].getHeight();
		}

		return height;
	}

}
