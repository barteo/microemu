/*
 * @(#)DateField.java  30.08.2001
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

import java.util.Date;
import java.util.TimeZone;


public class DateField extends Item
{

  public static final int DATE = 1;
  public static final int TIME = 2;
  public static final int DATE_TIME = 3;
  
  int mode;

  ChoiceGroup dateTime;
  TextBox date;
  TextBox time;
	static Command saveCommand = new Command("Save", Command.OK, 0);
	static Command backCommand = new Command("Back", Command.BACK, 0);
  
  CommandListener dateTimeListener = new CommandListener()
  {
    
    public void commandAction(Command c, Displayable d)
    {
      if (c == saveCommand && d == date) {
        System.out.println("validate date");
        dateTime.set(0, date.getString(), null);
      } else if (c == saveCommand && d == time) {
        System.out.println("validate time");
        if ((mode & DATE) != 0) {
          dateTime.set(1, time.getString(), null);
        } else {
          dateTime.set(0, time.getString(), null);
        }
      }

      Display.getDisplay().setCurrent(owner);
    }
  };


  public DateField(String label, int mode)
  {
    this(label, mode, null);
  }


  public DateField(String label, int mode, TimeZone timeZone)
  {
    super(null);
    
    this.mode = mode;
    
    dateTime = new ChoiceGroup(label, Choice.IMPLICIT);
    
    if ((mode & DATE) != 0) {
      dateTime.append("[date]", null);
      date = new TextBox("Date [dd.mm.yyyy]", null, 10, TextField.NUMERIC);
      date.addCommand(saveCommand);
      date.addCommand(backCommand);
      date.setCommandListener(dateTimeListener);
    }
    if ((mode & TIME) != 0) {
      dateTime.append("[time]", null);
      time = new TextBox("Time [hh.mm]", null, 5, TextField.NUMERIC);
      time.addCommand(saveCommand);
      time.addCommand(backCommand);
      time.setCommandListener(dateTimeListener);
    }
  }


  public Date getDate()
  {
    return null;
  }


  public void setDate(Date date)
  {
  }


  public int getInputMode()
  {
    return -1;
  }


  public void setInputMode(int mode)
  {
  }


  public void setLabel(String label)
  {
  }


	boolean isFocusable()
	{
		return true;
	}

    
  int getHeight()
	{
		return super.getHeight() + dateTime.getHeight();
	}


  int paint(Graphics g)
  {
    super.paintContent(g);
    
    g.translate(0, super.getHeight());
		dateTime.paint(g);
		g.translate(0, -super.getHeight());

    
    return getHeight();
  }

  
	void setFocus(boolean state)
	{
    super.setFocus(state);

    dateTime.setFocus(state);
  }

  
  boolean select()
  {
    dateTime.select();

    if (dateTime.getSelectedIndex() == 0 && (mode & DATE) != 0) {
      Display.getDisplay().setCurrent(date);
    } else {
      Display.getDisplay().setCurrent(time);
    }
      
    return true;
  }
  
  
  int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		return dateTime.traverse(gameKeyCode, top, bottom, action);
	}
  
}