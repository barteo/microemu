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
 */
 
package javax.microedition.lcdui;

import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;


public class DateField extends Item
{

  public static final int DATE = 1;
  public static final int TIME = 2;
  public static final int DATE_TIME = 3;
  
  Date date;
  Date time;
  String label;
  int mode;

  ChoiceGroup dateTime;
  DateCanvas dateCanvas;
  TimeCanvas timeCanvas;
	static Command saveCommand = new Command("Save", Command.OK, 0);
	static Command backCommand = new Command("Back", Command.BACK, 0);
  CommandListener dateTimeListener = new CommandListener()
  {
    
    public void commandAction(Command c, Displayable d)
    {
      if (c == backCommand) {
        getOwner().currentDisplay.setCurrent(owner);
      } else if (c == saveCommand) {
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			to.setTimeInMillis(0L);
			
			if (d == dateCanvas) {
				from.setTimeInMillis(dateCanvas.getMillis());
	            to.set(Calendar.DAY_OF_MONTH, from.get(Calendar.DAY_OF_MONTH));
	            to.set(Calendar.MONTH, from.get(Calendar.MONTH));
	            to.set(Calendar.YEAR, from.get(Calendar.YEAR));
				date = to.getTime();
			} else {
				from.setTimeInMillis(timeCanvas.getMillis());
				to.set(Calendar.HOUR_OF_DAY, from.get(Calendar.HOUR_OF_DAY));
	            to.set(Calendar.MINUTE, from.get(Calendar.MINUTE));
				time = to.getTime();
			}


            updateDateTimeString();
			getOwner().currentDisplay.setCurrent(owner);
      }
    }
  };


  public DateField(String label, int mode)
  {
    this(label, mode, null);
  }


  public DateField(String label, int mode, TimeZone timeZone)
  {
	  // why not super(label)??
    super(null);
    
    this.label = label;
// TODO this is ignoring TimeZone!! 
    setInputMode(mode);
    
    dateCanvas = new DateCanvas();
    dateCanvas.addCommand(saveCommand);
    dateCanvas.addCommand(backCommand);
    dateCanvas.setCommandListener(dateTimeListener);

    timeCanvas = new TimeCanvas();
    timeCanvas.addCommand(saveCommand);
    timeCanvas.addCommand(backCommand);
    timeCanvas.setCommandListener(dateTimeListener);
}


  public Date getDate()
  {
    return date;
  }


  public void setDate(Date date)
  {
    this.date = date;
    // TODO change the Canvas!!
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

    // ChoiceGroup can't be IMPLICIT so we have to cheat
    // a little DON'T TRY THIS AT HOME!!
    //dateTime = new ChoiceGroup(label, Choice.IMPLICIT);
    dateTime = new ChoiceGroup(label, Choice.EXCLUSIVE);
    dateTime.choiceType = Choice.IMPLICIT;
    if ((mode & DATE) != 0) {
      dateTime.append("[date]", null);
    }
    if ((mode & TIME) != 0) {
      dateTime.append("[time]", null);
    }
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
          dateCanvas.setMillis(date.getTime());
      } else {
          dateCanvas.setMillis(System.currentTimeMillis());
      }
      getOwner().currentDisplay.setCurrent(dateCanvas);
    } else {
      if (time != null) {
          timeCanvas.setMillis(time.getTime());
      } else {
    	  Calendar cal = Calendar.getInstance();
    	  cal.set(Calendar.YEAR, 1970);
    	  cal.set(Calendar.MONTH, Calendar.JANUARY);
    	  cal.set(Calendar.DAY_OF_MONTH, 1);
    	  cal.set(Calendar.HOUR_OF_DAY, 12);
    	  cal.set(Calendar.MINUTE, 0);
    	  cal.set(Calendar.SECOND, 0);
          timeCanvas.setMillis(cal.getTimeInMillis());
      }
      getOwner().currentDisplay.setCurrent(timeCanvas);
    }
      
    return true;
  }
  
  
  int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		return dateTime.traverse(gameKeyCode, top, bottom, action);
	}
  
  private String formatDate() {
	  if (date == null)
		  return "[date]";
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(date);
	  
	  int day =  cal.get(Calendar.DAY_OF_MONTH);
	  int month =  cal.get(Calendar.MONTH) + 1;
	  int year =  cal.get(Calendar.YEAR);
	  
	  return Integer.toString(day) + "-" + month + "-" + year;
  }

  private String formatTime() {
	  if (time == null)
		  return "[time]";
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(time);
	  
	  int hours =  cal.get(Calendar.HOUR_OF_DAY);
	  int minutes =  cal.get(Calendar.MINUTE);
	  
	  return Integer.toString(hours) + ":" + (minutes < 10? "0" : "") + minutes;
  }

  void updateDateTimeString()
  {
    if ((mode & DATE) != 0) {
      dateTime.set(0, formatDate(), null);
    }
    if ((mode & TIME) != 0) {
        dateTime.set((((mode & DATE) != 0)? 1 : 0), formatTime(), null);
    }
  } 
  
}

class DateCanvas extends Canvas {
	Calendar cal;

    private int month, day, year;
    private int selected;
    private long oneDay = 1000L * 3600 * 24;
	
	public DateCanvas() {
		cal = Calendar.getInstance();
	}

	public long getMillis() {
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTimeInMillis();
	}

	public void setMillis(long millis) {
		this.cal.setTimeInMillis(millis);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		repaint();
	}

	public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();

        g.setColor(0xffffff);
        g.fillRect(0, 0, w, h);
        
        Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, 
                Font.SIZE_MEDIUM);

        String dayStr = Integer.toString(day);
        if (day < 10)
            dayStr = "0" + dayStr;
        String monthStr = Integer.toString(month+1);
        if (month+1 < 10)
            monthStr = "0" + monthStr;
        String yearStr = Integer.toString(year);
        String delimiterStr = "/";
        
        int y = (h - font.getHeight()) >>> 1;

        int dayW = font.stringWidth(dayStr);
        int monthW = font.stringWidth(monthStr);
        int yearW = font.stringWidth(yearStr);
        int delimiterW = font.stringWidth(delimiterStr);

        int stringWidth = dayW + monthW + yearW + (delimiterW << 1);
        int offset = (w - stringWidth) >>> 1;
        int dOff = offset;
        int del1Off = dOff + dayW;
        int mOff = del1Off + delimiterW;
        int del2Off = mOff + monthW;
        int yOff = del2Off + delimiterW;
        
        g.setColor(0);
        g.setFont(font);
        // draw the delimiter 
        g.drawString(delimiterStr, del1Off, y, Graphics.LEFT | Graphics.TOP);
        g.drawString(delimiterStr, del2Off, y, Graphics.LEFT | Graphics.TOP);
        
        // draw the rectangles 
        
        // colors  
        int colorR, colorT;

        if (selected == 0) {
            colorR = 0x000000;
            colorT = 0xffffff;
        } else {
            colorR = 0xffffff;
            colorT = 0x000000;
        }
        
        g.setColor(colorR);
        g.fillRect(dOff, y, dayW, font.getHeight());
        g.setColor(colorT);
        g.drawString(dayStr, dOff, y, Graphics.LEFT | Graphics.TOP);

        if (selected == 1) {
            colorR = 0x000000;
            colorT = 0xffffff;
        } else {
            colorR = 0xffffff;
            colorT = 0x000000;
        }

        g.setColor(colorR);
        g.fillRect(mOff, y, monthW, font.getHeight());
        g.setColor(colorT);
        g.drawString(monthStr, mOff, y, Graphics.LEFT | Graphics.TOP);

        if (selected == 2) {
            colorR = 0x000000;
            colorT = 0xffffff;
        } else {
            colorR = 0xffffff;
            colorT = 0x000000;
        }

        g.setColor(colorR);
        g.fillRect(yOff, y, yearW, font.getHeight());
        g.setColor(colorT);
        g.drawString(yearStr, yOff, y, Graphics.LEFT | Graphics.TOP);
	}

	public synchronized void keyPressed(int keycode) {
        int k = getGameAction(keycode);
        
        if (k == Canvas.LEFT && selected > 0) {
            selected--;
            repaint();
        } else if (k == Canvas.RIGHT && selected < 2) {
            selected++;
            repaint();
        } else if (k == Canvas.UP) {
            Calendar cal = Calendar.getInstance();

            switch (selected) {
                case 0:  // day
                	cal.set(Calendar.YEAR, year);
                	cal.set(Calendar.MONTH, month);
                	cal.set(Calendar.DAY_OF_MONTH, day);
                	cal.set(Calendar.HOUR_OF_DAY, 1);
                	
                	cal.setTimeInMillis(cal.getTimeInMillis() + oneDay);
                	if(cal.get(Calendar.MONTH) == month)
                		day++;
                	else 
                		day = 1;
                	break;
                case 1: // month
                	if (month == Calendar.DECEMBER)
                		month = Calendar.JANUARY;
                	else 
                		month++;

                	cal.set(Calendar.YEAR, year);
                	cal.set(Calendar.MONTH, month);
                	cal.set(Calendar.DAY_OF_MONTH, 28);
                	cal.set(Calendar.HOUR_OF_DAY, 1);
                	
                	cal.setTimeInMillis(cal.getTimeInMillis() + oneDay*4);
                	int daysInMonth = 28+(4-cal.get(Calendar.DAY_OF_MONTH));
                	
                	if (day > daysInMonth)
                		day = daysInMonth;
                	break;
                case 2: // year
                	// arbitrary limit
                	if (year < 5000) {
                		year++;
                		
                		// here i simply use the fact that there
                		// were nor will be two lenient years in
                		// a row
	                	if (day == 29 && month == Calendar.FEBRUARY)
	                		day = 28;
                	}
                	break;
            }
            repaint();
        } else if (k == Canvas.DOWN) {
            Calendar cal = Calendar.getInstance();

            switch (selected) {
                case 0:  // day
                	if(day > 1)
                		day--;
                	else {
                    	cal.set(Calendar.YEAR, year);
                    	cal.set(Calendar.MONTH, month);
                    	cal.set(Calendar.DAY_OF_MONTH, 28);
                    	cal.set(Calendar.HOUR_OF_DAY, 1);
                    	
                    	cal.setTimeInMillis(cal.getTimeInMillis() + oneDay*4);
                    	int daysInMonth = 28+(4-cal.get(Calendar.DAY_OF_MONTH));
                    	day = daysInMonth;
                	}
                	break;
                case 1: // month
                	if (month == Calendar.JANUARY)
                		month = Calendar.DECEMBER;
                	else 
                		month--;

                	cal.set(Calendar.YEAR, year);
                	cal.set(Calendar.MONTH, month);
                	cal.set(Calendar.DAY_OF_MONTH, 28);
                	cal.set(Calendar.HOUR_OF_DAY, 1);
                	
                	cal.setTimeInMillis(cal.getTimeInMillis() + oneDay*4);
                	int daysInMonth = 28+(4-cal.get(Calendar.DAY_OF_MONTH));
                	
                	if (day > daysInMonth)
                		day = daysInMonth;
                	break;
                case 2: // year
                	// arbitrary limit
                	if (year > 1000) {
                		year--;
                		// here i simply use the fact that there
                		// were nor will be two lenient years in
                		// a row
	                	if (day == 29 && month == Calendar.FEBRUARY)
	                		day = 28;
                	}
                	break;
            }
            repaint();
        }
    }
}

class TimeCanvas extends Canvas {
	private long millis;
	Calendar cal;
	private int minutes, hours;
	private int selected;
	
	public TimeCanvas() {
		cal = Calendar.getInstance();
	}

	public long getMillis() {
		this.cal.set(Calendar.HOUR_OF_DAY, hours);
		this.cal.set(Calendar.MINUTE, minutes);
		millis = cal.getTimeInMillis();
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
		this.cal.setTimeInMillis(millis);
		this.hours = cal.get(Calendar.HOUR_OF_DAY);
		this.minutes = cal.get(Calendar.MINUTE);
		repaint();
	}

    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();

        g.setColor(0xffffff);
        g.fillRect(0, 0, w, h);
        
        Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, 
                Font.SIZE_MEDIUM);

        String hoursStr = Integer.toString(hours);
        if (hours < 10)
            hoursStr = "0" + hoursStr;
        String minutesStr = Integer.toString(minutes);
        if (minutes < 10)
            minutesStr = "0" + minutesStr;
        String delimiterStr = " : ";
        
        int y = (h - font.getHeight()) >>> 1;

        int hoursW = font.stringWidth(hoursStr);
        int minutesW = font.stringWidth(minutesStr);
        int delimiterW = font.stringWidth(delimiterStr);

        int stringWidth = hoursW + minutesW + delimiterW;
        int offset = (w - stringWidth) >>> 1;
        int hOff = offset;
        int dOff = offset + hoursW;
        int mOff = dOff + delimiterW;
        
        g.setColor(0);
        g.setFont(font);
        // draw the delimiter 
        g.drawString(delimiterStr, dOff, y, Graphics.LEFT | Graphics.TOP);
        
        // draw the rectangles 
        
        // colors  
        int colorR, colorT;

        if (selected == 0) {
            colorR = 0x000000;
            colorT = 0xffffff;
        } else {
            colorR = 0xffffff;
            colorT = 0x000000;
        }
        
        g.setColor(colorR);
        g.fillRect(hOff, y, hoursW, font.getHeight());
        g.setColor(colorT);
        g.drawString(hoursStr, hOff, y, Graphics.LEFT | Graphics.TOP);

        if (selected == 1) {
            colorR = 0x000000;
            colorT = 0xffffff;
        } else {
            colorR = 0xffffff;
            colorT = 0x000000;
        }

        g.setColor(colorR);
        g.fillRect(mOff, y, minutesW, font.getHeight());
        g.setColor(colorT);
        g.drawString(minutesStr, mOff, y, Graphics.LEFT | Graphics.TOP);
    }

    public synchronized void keyPressed(int keycode) {
        int k = getGameAction(keycode);
        
        if (k == Canvas.LEFT && selected > 0) {
            selected--;
            repaint();
        } else if (k == Canvas.RIGHT && selected < 1) {
            selected++;
            repaint();
        } else if (k == Canvas.UP) {
            switch (selected) {
                case 0:  // hours
                    hours++;
                    if (hours > 23)
                        hours = 0;
                    break;
                case 1: // minutes
                    minutes++;
                    if (minutes > 59)
                        minutes = 0;
                    break;
            }
            repaint();
        } else if (k == Canvas.DOWN) {
            switch (selected) {
                case 0:  // hours
                    hours--;
                    if (hours < 0)
                        hours = 23;
                    break;
                case 1: // minutes
                    minutes--;
                    if (minutes < 0)
                        minutes = 59;
                    break;
            }
            repaint();
        }
    }
}