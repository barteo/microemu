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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;


public class Alert extends Screen
{

	public static final int FOREVER = -2;

	StringItem alertText;
	AlertType type;
	static final Command OK = new Command("OK", Command.OK, 0);
	int time;


	CommandListener alertListener = new CommandListener()
	{

		public void commandAction(Command cmd, Displayable d)
		{
			currentDisplay.clearAlert();
		}

	};


	public Alert(String title)
	{
		this(title, null, null, null);
	}


	public Alert(String title, String alertText, Image alertImage, AlertType alertType)
	{
		super(title);
		setTimeout(getDefaultTimeout());
		this.alertText = new StringItem(null, alertText);
		setType(alertType);
		super.setCommandListener(alertListener);
	}


	public void addCommand(Command cmd)
	{
		throw new IllegalStateException("Alert does not accept commands");
	}


	public int getDefaultTimeout()
	{
		return Alert.FOREVER;
	}


	public String getString()
	{
		return alertText.getText();
	}


	public int getTimeout()
	{
		return time;
	}


  public AlertType getType()
  {
    return type;
  }
  
  
  public void setType(AlertType type)
	{
		this.type = type;
	}


	public void setCommandListener(CommandListener l)
	{
		throw new IllegalStateException("Alert does not accept listeners");
	}


	public void setImage(Image img)
	{
    if (img.isMutable()) {
      throw new IllegalArgumentException("Image cannot be mutable");
    }
	}


	public void setString(String str)
	{
		alertText.setText(str);
		repaint();
	}


	public void setTimeout(int time)
	{
    if (time != FOREVER && time <= 0) {
      throw new IllegalArgumentException();
    }
		this.time = time;
    super.removeCommand(OK);
 		if (getTimeout() == Alert.FOREVER) {
			super.addCommand(OK);
		}

	}


	int getHeight()
	{
		return alertText.getHeight();
	}


	int paintContent(Graphics g)
	{
		return alertText.paint(g);
	}


	void showNotify()
	{
		super.showNotify();

    viewPortY = 0;
	}


	int traverse(int gameKeyCode, int top, int bottom)
	{
		Font f = Font.getDefaultFont();

		if (gameKeyCode == 1 && top != 0) {
			return -f.getHeight();
		}
		if (gameKeyCode == 6 && bottom < getHeight()) {
			return f.getHeight();
		}

		return 0;
	}

}
