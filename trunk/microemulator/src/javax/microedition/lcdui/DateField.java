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

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateField extends Item
{

  public static final int DATE = 1;
  public static final int TIME = 2;
  public static final int DATE_TIME = 3;
  
  Date date;
  String label;
  int mode;

  ChoiceGroup dateTime;
  TextBox dateBox;
  TextBox timeBox;
  
  static SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy");
  static SimpleDateFormat timeFormat = new SimpleDateFormat("H.mm");
  
  Alert alert = new Alert("Parse Error");
  
	static Command saveCommand = new Command("Save", Command.OK, 0);
	static Command backCommand = new Command("Back", Command.BACK, 0);
  
  CommandListener dateTimeListener = new CommandListener()
  {
    
    public void commandAction(Command c, Displayable d)
    {
      if (c == backCommand) {
        Display.getDisplay().setCurrent(owner);
      } else if (c == saveCommand && d == dateBox) {
        try {
          Date tmp = dateFormat.parse(dateBox.getString());
          if (date == null) {
            date = tmp;
          } else {
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(tmp);
            to.setTime(date);
            to.set(Calendar.DAY_OF_MONTH, from.get(Calendar.DAY_OF_MONTH));
            to.set(Calendar.MONTH, from.get(Calendar.MONTH));
            to.set(Calendar.YEAR, from.get(Calendar.YEAR));
            date = to.getTime();
          }
          updateDateTimeString();
          Display.getDisplay().setCurrent(owner);
        } catch (ParseException ex) {
          alert.setString("Date: " + ex.getMessage());
          Display.getDisplay().setCurrent(alert);
        }
      } else if (c == saveCommand && d == timeBox) {
        try {
          Date tmp = timeFormat.parse(timeBox.getString());
          if (date == null) {
            date = tmp;
          } else {
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(tmp);
            to.setTime(date);
            to.set(Calendar.HOUR_OF_DAY, from.get(Calendar.HOUR_OF_DAY));
            to.set(Calendar.MINUTE, from.get(Calendar.MINUTE));
            date = to.getTime();
          }
          updateDateTimeString();
          Display.getDisplay().setCurrent(owner);
        } catch (ParseException ex) {
          alert.setString("Time: " + ex.getMessage());
          Display.getDisplay().setCurrent(alert);
        }
      }
    }
  };


  public DateField(String label, int mode)
  {
    this(label, mode, null);
  }


  public DateField(String label, int mode, TimeZone timeZone)
  {
    super(null);
    
    this.label = label;

    setInputMode(mode);
    
    dateBox = new TextBox("Date [dd.mm.yyyy]", null, 10, TextField.NUMERIC);
    dateBox.addCommand(saveCommand);
    dateBox.addCommand(backCommand);
    dateBox.setCommandListener(dateTimeListener);
    timeBox = new TextBox("Time [hh.mm]", null, 5, TextField.NUMERIC);
    timeBox.addCommand(saveCommand);
    timeBox.addCommand(backCommand);
    timeBox.setCommandListener(dateTimeListener);    
  }


  public Date getDate()
  {
    return date;
  }


  public void setDate(Date date)
  {
    this.date = date;
    updateDateTimeString();
  }


  public int getInputMode()
  {
    return mode;
  }


  public void setInputMode(int mode)
  {
    if (mode < 1 || mode > 3) {
      throw new IllegalArgumentException();
    }
    
    this.mode = mode;

    dateTime = new ChoiceGroup(label, Choice.IMPLICIT);
    if ((mode & DATE) != 0) {
      dateTime.append("[date]", null);
    }
    if ((mode & TIME) != 0) {
      dateTime.append("[time]", null);
    }
  }


  public void setLabel(String label)
  {
    this.label = label;
    
    dateTime.setLabel(label);
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
      if (date != null) {
        dateBox.setString(dateFormat.format(date));
      } else {
        dateBox.setString("");
      }
      Display.getDisplay().setCurrent(dateBox);
    } else {
      if (date != null) {
        timeBox.setString(timeFormat.format(date));
      } else {
        timeBox.setString("");
      }
      Display.getDisplay().setCurrent(timeBox);
    }
      
    return true;
  }
  
  
  int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		return dateTime.traverse(gameKeyCode, top, bottom, action);
	}
  

  void updateDateTimeString()
  {
    if ((mode & DATE) != 0) {
      dateTime.set(0, dateFormat.format(date), null);
    }
    if ((mode & TIME) != 0) {
      if ((mode & DATE) != 0) {
        dateTime.set(1, timeFormat.format(date), null);
      } else {
        dateTime.set(0, timeFormat.format(date), null);
      }
    }
  } 
  
}