/*
 * @(#)Ticker.java  30.08.2001
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


public class Ticker
{

  static int PAINT_TIMEOUT = 500;

  String text;


	class TickerPaint implements Runnable
	{

		public void run()
		{
      while (true) {
        System.out.println("ticker move");
  			try {
  				Thread.sleep(PAINT_TIMEOUT);
  			} catch (InterruptedException ex) {}
      }
		}

	}


  public Ticker(String str)
  {
    if (str == null) {
      throw new NullPointerException();
    }
    text = str;
    TickerPaint tp = new TickerPaint();
		Thread t = new Thread(tp);
		t.start();
  }


  public String getString()
  {
    return text;
  }


  public void setString(String str)
  {
    if (str == null) {
      throw new NullPointerException();
    }
    text = str;
  }

}