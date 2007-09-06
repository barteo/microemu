/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2005 Andres Navarro
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contributor(s): 
 *   3GLab
 */

package javax.microedition.lcdui;

import java.util.Vector;

//TODO implement pointer events
public class Form extends Screen 
{
	Item items[] = new Item[4];
	int numOfItems = 0;
	int focusItemIndex;
	ItemStateListener itemStateListener = null;

	
	public Form(String title) 
	{
		super(title);
		focusItemIndex = -2;
	}

	
	public Form(String title, Item[] items) 
	{
		this(title);

		// TODO add this to MIDP1
		if (items != null) {
			this.items = new Item[items.length];
			System.arraycopy(items, 0, this.items, 0, items.length);
			numOfItems = this.items.length;
			for (int i = 0; i < numOfItems; i++) {
				verifyItem(this.items[i]);
			}
		}
	}

	
	public int append(Item item) 
	{
		verifyItem(item);

		if (numOfItems + 1 >= items.length) {
			Item newitems[] = new Item[numOfItems + 4];
			System.arraycopy(items, 0, newitems, 0, numOfItems);
			items = newitems;
		}
		items[numOfItems] = item;
		numOfItems++;
		
		repaint();

		return (numOfItems - 1);
	}

	
	public int append(Image img) 
	{
		return append(new ImageItem(null, img, ImageItem.LAYOUT_DEFAULT, null));
	}

	
	public int append(String str) 
	{
		if (str == null) {
			throw new NullPointerException();
		}

		return append(new StringItem(null, str));
	}

	
	public void delete(int itemNum) 
	{
		verifyItemNum(itemNum);

		items[itemNum].setOwner(null);
		System.arraycopy(items, itemNum + 1, items, itemNum, numOfItems - itemNum - 1);
		numOfItems--;
		
		repaint();
	}
	
	
	public void deleteAll()
	{
		for (int i = 0; i < numOfItems; i++) {
			items[i].setOwner(null);
		}
		numOfItems = 0;
		
		repaint();
	}

	
	public Item get(int itemNum) 
	{
		verifyItemNum(itemNum);

		return items[itemNum];
	}
	
	
	public int getHeight()
	{
		return super.getHeight();
	}
	
	
	public int getWidth()
	{
		return super.getWidth();
	}

	
	public void insert(int itemNum, Item item) 
	{
		verifyItemNum(itemNum);
		verifyItem(item);

		if (numOfItems + 1 == items.length) {
			Item newitems[] = new Item[numOfItems + 4];
			System.arraycopy(items, 0, newitems, 0, numOfItems);
			items = newitems;
		}
		System.arraycopy(
			items,
			itemNum,
			items,
			itemNum + 1,
			numOfItems - itemNum);
		items[itemNum] = item;
		items[itemNum].setOwner(this);
		numOfItems++;
		
		repaint();
	}

	
	public void set(int itemNum, Item item) 
	{
		verifyItemNum(itemNum);
		verifyItem(item);

		// TODO add this to MIDP1
		items[itemNum].setOwner(null);
		
		items[itemNum] = item;
		items[itemNum].setOwner(this);
		
		repaint();
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
    
    
	void fireItemStateListener(Item item) {
        if (itemStateListener != null) {
            itemStateListener.itemStateChanged(item);
        }
	}
	
	void fireItemStateListener()
    {
		if (focusItemIndex >= 0 && focusItemIndex < items.length)
			fireItemStateListener(items[focusItemIndex]);
    }

	
	void hideNotify() 
	{
		super.hideNotify();
		// TODO eliminate this to
		// allow focus restoring
		for (int i = 0; i < numOfItems; i++) {
			if (items[i].isFocusable() && items[i].hasFocus()) {
				items[i].setFocus(false);
				focusItemIndex = -2;
				break;
			}
		}
	}

	
	void keyPressed(int keyCode) 
	{
		if (focusItemIndex != -1) {
			if (Display.getGameAction(keyCode) == Canvas.FIRE) {
				items[focusItemIndex].select();
				// why do we call this here?
				// Andres Navarro
                fireItemStateListener();
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
		if (focusItemIndex < 0)
			return;
		int heightToItem = getHeightToItem(focusItemIndex);
		int heightAfterItem = heightToItem + items[focusItemIndex].getHeight();
		if (viewPortY > heightToItem) {
			viewPortY = heightToItem;
		} else if ((viewPortY + viewPortHeight) < heightAfterItem) {
			viewPortY = heightAfterItem - viewPortHeight;
		}
	}
	
	int traverse(int gameKeyCode, int top, int bottom) 
	{
		int height, testItemIndex, traverse, i;
		int topItemIndex, bottomItemIndex;

		if (numOfItems == 0) {
			return 0;
		}

		if (gameKeyCode == Canvas.UP) {
			topItemIndex = getTopVisibleIndex(top);
			if (focusItemIndex == -1) {
				testItemIndex = topItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse =
					items[testItemIndex].traverse(
						gameKeyCode,
						top - height,
						bottom - height,
						false);
			} else {
				testItemIndex = focusItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse =
					items[testItemIndex].traverse(
						gameKeyCode,
						top - height,
						bottom - height,
						true);
			}
			if (traverse != Item.OUTOFITEM) {
				if (focusItemIndex == -1
					&& items[testItemIndex].isFocusable()) {
					items[testItemIndex].setFocus(true);
					focusItemIndex = testItemIndex;
				}
				return traverse;
			} else {
				if (testItemIndex > 0) {
					// Czy istnieje obiekt focusable powyzej testItemIndex
					// widoczny na ekranie
					// jesli tak to zrob na nim traverse(false) i return
					// traverse
					for (i = testItemIndex - 1; i >= topItemIndex; i--) {
						if (items[i].isFocusable()) {
							if (focusItemIndex != -1) {
								items[focusItemIndex].setFocus(false);
							}
							items[i].setFocus(true);
							focusItemIndex = i;
							height = getHeightToItem(i);
							traverse =
								items[i].traverse(
									gameKeyCode,
									top - height,
									bottom - height,
									false);
							if (traverse == Item.OUTOFITEM) {
								return 0;
							} else {
								return traverse;
							}
						}
					}
					// Na najnizszym widocznym item zrob traverse(false)
					height = getHeightToItem(topItemIndex);
					traverse =
						items[topItemIndex].traverse(
							gameKeyCode,
							top - height,
							bottom - height,
							false);
					if (traverse == Item.OUTOFITEM) {
					} else {
						// Sprawdzenie czy znajduje sie powyzej na ekranie
						// focusable item
						// jesli tak zrob co trzeba
						bottomItemIndex = getTopVisibleIndex(bottom + traverse);
						if (focusItemIndex != -1
							&& focusItemIndex > bottomItemIndex) {
							items[focusItemIndex].setFocus(false);
							focusItemIndex = -1;
						}
						return traverse;
					}
				}
			}
		}
		if (gameKeyCode == Canvas.DOWN) {
			bottomItemIndex = getBottomVisibleIndex(bottom);
			if (focusItemIndex == -1) {
				testItemIndex = bottomItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse =
					items[testItemIndex].traverse(
						gameKeyCode,
						top - height,
						bottom - height,
						false);
			} else {
				testItemIndex = focusItemIndex;
				height = getHeightToItem(testItemIndex);
				traverse =
					items[testItemIndex].traverse(
						gameKeyCode,
						top - height,
						bottom - height,
						true);
			}
			if (traverse != Item.OUTOFITEM) {
				if (focusItemIndex == -1
					&& items[testItemIndex].isFocusable()) {
					items[testItemIndex].setFocus(true);
					focusItemIndex = testItemIndex;
				}
				return traverse;
			} else {
				if (testItemIndex < numOfItems - 1) {
					// Czy istnieje obiekt focusable ponizej testItemIndex
					// widoczny na ekranie
					// jesli tak to zrob na nim traverse(false) i return
					// traverse
					for (i = testItemIndex + 1; i <= bottomItemIndex; i++) {
						if (items[i].isFocusable()) {
							if (focusItemIndex != -1) {
								items[focusItemIndex].setFocus(false);
							}
							items[i].setFocus(true);
							focusItemIndex = i;
							height = getHeightToItem(i);
							traverse =
								items[i].traverse(
									gameKeyCode,
									top - height,
									bottom - height,
									false);
							if (traverse == Item.OUTOFITEM) {
								return 0;
							} else {
								return traverse;
							}
						}
					}
					// Na najnizszym widocznym item zrob traverse(false)
					height = getHeightToItem(bottomItemIndex);
					traverse =
						items[bottomItemIndex].traverse(
							gameKeyCode,
							top - height,
							bottom - height,
							false);
					if (traverse == Item.OUTOFITEM) {
					} else {
						// Sprawdzenie czy znajduje sie powyzej na ekranie
						// focusable item
						// jesli tak zrob co trzeba
						topItemIndex = getTopVisibleIndex(top + traverse);
						if (focusItemIndex != -1
							&& focusItemIndex < topItemIndex) {
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

	
	private int getTopVisibleIndex(int top) 
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

	
	private int getBottomVisibleIndex(int bottom) 
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

	
	private int getHeightToItem(int itemIndex) 
	{
		int height = 0;

		for (int i = 0; i < itemIndex; i++) {
			height += items[i].getHeight();
		}

		return height;
	}

	/**
	 * Verify that the item is non null and is not owned by this form or anyone
	 * else. If all is ok set the owner to this Form
	 * 
	 * @param item the item to be verified
	 * @throws IllegalStateException
	 * @throws NullPointerException
	 */
	private void verifyItem(Item item) 
	{
		// Check that we are being passed valid items
		if (item == null) {
			throw new NullPointerException("item is null");
		}
		if (item.getOwner() != null) {
			throw new IllegalStateException("item is already owned");
		}
		// All is ok make ourselves the owner
		item.setOwner(this);
	}

	/**
	 * Verify that the index passed in is valid for this form. ie within the
	 * range 0..size-1
	 * 
	 * @param itemNum the number of the item
	 * @throws IndexOutOfBoundsException
	 */
	private void verifyItemNum(int itemNum) 
	{
		if (itemNum < 0 || itemNum >= numOfItems) {
			throw new IndexOutOfBoundsException("item number is outside range of Form");
		}
	}

	Vector getCommands() {
		Vector formCommands = super.getCommands();
		if (focusItemIndex < 0)
			return formCommands;
		
		Item item = items[focusItemIndex];
		Vector itemCommands = item.commands;
		if (itemCommands.isEmpty())
			return formCommands;

		// if the focused item has commands we
		// need a new Vector with the special commands
		// for the items (see code at the end of class
		// Command and in CommandManager for more info)
		Vector allCommands = new Vector();
		for (int i = 0; i < formCommands.size(); i++)
			allCommands.add(formCommands.elementAt(i));
		
		for (int i = 0; i < itemCommands.size(); i++) {
			Command itemCommand = (Command)itemCommands.elementAt(i);
			itemCommand = itemCommand.getItemCommand(item);
			allCommands.add(itemCommand);
		}
		return allCommands;
	}
}
