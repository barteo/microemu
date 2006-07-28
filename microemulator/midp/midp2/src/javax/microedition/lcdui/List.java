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
 *    3GLab
 */
 
package javax.microedition.lcdui;

//TODO implement pointer events
public class List extends Screen implements Choice
{

  public static final Command SELECT_COMMAND = new Command("", Command.SCREEN, 0);
  
  ChoiceGroup choiceGroup;
  
  private int initialPressedItem;
  
  
  public List(String title, int listType)
  {
  	super(title);
  	
  		if (listType != Choice.IMPLICIT &&
  				listType != Choice.MULTIPLE &&
  				listType != Choice.EXCLUSIVE)
  			throw new IllegalArgumentException("Illegal list type");

  		if (listType == Choice.IMPLICIT) {
  			// this is necessary because IMPLICIT is not
  			// a valid ChoiceGroup type
  			choiceGroup = new ChoiceGroup(null, Choice.EXCLUSIVE);
  			choiceGroup.choiceType = Choice.IMPLICIT;
  		} else {
  	  		choiceGroup = new ChoiceGroup(null, listType);
  		}
  			
		choiceGroup.setOwner(this);
		choiceGroup.setFocus(true);
		
		this.initialPressedItem = -1;
  }
  
  
	public List(String title, int listType, String[] stringElements, Image[] imageElements)
	{
    super(title);
    
	if (listType == Choice.IMPLICIT) {
		// this is necessary because IMPLICIT is not
		// a valid ChoiceGroup type,
		// exlusive is used because it has a default
		// selected value
		choiceGroup = new ChoiceGroup(null, Choice.EXCLUSIVE, stringElements, imageElements);
		choiceGroup.choiceType = Choice.IMPLICIT;        
        for (int i = 0; i < size(); i++) {
            set(i, getString(i), null);
        }
	} else {
		choiceGroup = new ChoiceGroup(null, listType, stringElements, imageElements);
	}
    	choiceGroup.setOwner(this);
		choiceGroup.setFocus(true);
		
		this.initialPressedItem = -1;
	}
						

  public int append(String stringPart, Image imagePart) 
  {
    return choiceGroup.append(stringPart, imagePart);
  }
    

  public void delete(int elementNum) 
  {
    choiceGroup.delete(elementNum);
  }
  
  public void deleteAll()
  {
	  choiceGroup.deleteAll();
  }
  
  public int getFitPolicy() 
  {
	  return choiceGroup.getFitPolicy();
  }
  
  public Font getFont(int elementNum)
  {
	  return choiceGroup.getFont(elementNum);
  }

  public Image getImage(int elementNum) 
  {
    return choiceGroup.getImage(elementNum);
  }
    

  public int getSelectedFlags(boolean[] selectedArray_return) 
  {
    return choiceGroup.getSelectedFlags(selectedArray_return);
  }
    

  public int getSelectedIndex() 
  {
    return choiceGroup.getSelectedIndex();
  }
    
  
  public String getString(int elementNum) 
  {
    return choiceGroup.getString(elementNum);
  }
    
  
  public void insert(int elementNum, String stringPart, Image imagePart) 
  {
    choiceGroup.insert(elementNum, stringPart, imagePart);
  }
    
  
  public boolean isSelected(int elementNum) 
  {
    return choiceGroup.isSelected(elementNum);
  }
  
  
  public void removeCommand(Command cmd)
  {
	  // TODO
	  throw new RuntimeException("not implemented");
  }
    
  
  public void set(int elementNum, String stringPart, Image imagePart) {
    choiceGroup.set(elementNum, stringPart, imagePart);    
  }
  
  public void setFitPolicy(int policy) 
  {
	  choiceGroup.setFitPolicy(policy);
  }
  
  public void setFont(int elementNum, Font font)
  {
	choiceGroup.setFont(elementNum, font);  
  }
  
  
  public void setSelectCommand(Command command)
  {
	  // TODO
	  throw new RuntimeException("not implemented");
  }
    
  
  public void setSelectedFlags(boolean[] selectedArray) 
  {
    choiceGroup.setSelectedFlags(selectedArray);    
  }
    
  
  public void setSelectedIndex(int elementNum, boolean selected) 
  {
    choiceGroup.setSelectedIndex(elementNum, selected);    
  }
  
  
  public void setTicker(Ticker ticker)
  {
	  // TODO
	  throw new RuntimeException("not implemented");
  }
  
  
  public void setTitle(String s)
  {
	  // TODO
	  throw new RuntimeException("not implemented");
  }
    
  
  void keyPressed(int keyCode)
  {
    if(Display.getGameAction(keyCode) == Canvas.FIRE 
        && choiceGroup.select() && super.getCommandListener() != null
        && choiceGroup.choiceType == Choice.IMPLICIT) {
      super.getCommandListener().commandAction(SELECT_COMMAND, this);
    } else {
      super.keyPressed(keyCode);
    }
  }
  
    
  	void pointerPressed(int x, int y) {
		// TODO currently only IMPLICIT type is implemented
  		Ticker ticker = getTicker();
  		if (ticker != null) {
  			y -= ticker.getHeight();
  		}
  		y -= title.getHeight();
  		y -= 1;
  		if (y >= 0 && y < viewPortHeight) {
	  		int pressedItem = choiceGroup.getItemIndexAt(x, y + viewPortY);
	  		if (pressedItem != -1) {
	  			setSelectedIndex(pressedItem, true);  	
	  			initialPressedItem = pressedItem;
	  		}
  		}
  	}


	void pointerReleased(int x, int y) {
		// TODO currently only IMPLICIT type is implemented
  		Ticker ticker = getTicker();
  		if (ticker != null) {
  			y -= ticker.getHeight();
  		}
  		y -= title.getHeight();
  		y -= 1;
  		if (y >= 0 && y < viewPortHeight) {
	  		int releasedItem = choiceGroup.getItemIndexAt(x, y + viewPortY);
	  		if (releasedItem != -1) {
	  			if (releasedItem == initialPressedItem
	  					&& super.getCommandListener() != null
	  					&& choiceGroup.choiceType == Choice.IMPLICIT) { 
	  				super.getCommandListener().commandAction(SELECT_COMMAND, this);
	  			}
	  		}
  		}
	}


int paintContent(Graphics g)
  {
    return choiceGroup.paint(g);
  }
  
  
  public int size() 
  {
    return choiceGroup.size();
  }

  
  int traverse(int gameKeyCode, int top, int bottom)
  {
		int traverse = choiceGroup.traverse(gameKeyCode, top, bottom, true);
		if (traverse == Item.OUTOFITEM) {
			return 0;
		} else {
	    return traverse;
		}
  }

}
