/*
 *  MicroEmulator
 *  Copyright (C) 2002-2005 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import org.microemu.CommandManager;
import org.microemu.EmulatorContext;
import org.microemu.device.impl.Color;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.PositionedImage;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;



public class Device 
{
	private String name;
	
	private EmulatorContext context; 	

	private Image normalImage;
	private Image overImage;
	private Image pressedImage;
    
	private Vector buttons;
	private Vector softButtons;
	
	
	public Device()
	{	    
		name = getClass().getName();
	}
	

    public void init(EmulatorContext context)
    {     
        init(context, "/org/microemu/device/device.xml");
    }       

    
    public void init(EmulatorContext context, String config)
    {
    	this.context = context;

        buttons = new Vector();
        softButtons = new Vector();

        try {
            loadConfig(config);
        } catch (IOException ex) {
            System.out.println("Cannot load config: " + ex);
        }
    }
    
    
    public String getName()
    {
    	return name;
    }
    
    
    public EmulatorContext getEmulatorContext() 
    {
  	  	return context;
    }


    public InputMethod getInputMethod()
    {
        return context.getDeviceInputMethod();
    }
    
    
    public FontManager getFontManager()
    {
		return context.getDeviceFontManager();
    }
    
    
    public DeviceDisplay getDeviceDisplay()
    {
        return context.getDeviceDisplay();
    }
    
    
    public Image getNormalImage()
    {
      return normalImage;
    }

    
    public Image getOverImage()
    {
      return overImage;
    }

    
    public Image getPressedImage()
    {
      return pressedImage;
    }  
    
    
    public Vector getSoftButtons()
    {
      return softButtons;
    }

    
    public Vector getButtons()
    {
      return buttons;
    }

    
    protected void loadConfig(String config)
		throws IOException
    {
    	String readLine;
    	StringBuffer xmlBuffer = new StringBuffer();
    	BufferedReader dis = new BufferedReader(
    			new InputStreamReader(getClass().getResourceAsStream(config)));
    	while ((readLine = dis.readLine()) != null) {
    		xmlBuffer.append(readLine);
    	}
    		
        XMLElement doc = new XMLElement();
        try {
          doc.parseString(xmlBuffer.toString());
        } catch (XMLParseException ex) {
        	throw new IOException(ex.toString());
        }
        
        String deviceName = doc.getStringAttribute("name");
        if (deviceName != null) {
        	name = deviceName;
        }

        DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) getDeviceDisplay();
        InputMethodImpl inputMethod = (InputMethodImpl) getInputMethod();
        inputMethod.setHasPointerEvents(false);
        inputMethod.setHasPointerMotionEvents(false);
        inputMethod.setHasRepeatEvents(false);
        for (Enumeration e = doc.enumerateChildren(); e.hasMoreElements(); ) {
          XMLElement tmp = (XMLElement) e.nextElement();
          if (tmp.getName().equals("img")) {
            try {
              if (tmp.getStringAttribute("name").equals("normal")) {
                normalImage = deviceDisplay.createSystemImage(tmp.getStringAttribute("src"));
              } else if (tmp.getStringAttribute("name").equals("over")) {
                overImage = deviceDisplay.createSystemImage(tmp.getStringAttribute("src"));
              } else if (tmp.getStringAttribute("name").equals("pressed")) {
                pressedImage = deviceDisplay.createSystemImage(tmp.getStringAttribute("src"));
              }
            } catch (IOException ex) {
              System.out.println("Cannot load " + tmp.getStringAttribute("src"));
              return;
            }
          } else if (tmp.getName().equals("display")) {
            for (Enumeration e_display = tmp.enumerateChildren(); e_display.hasMoreElements(); ) {
              XMLElement tmp_display = (XMLElement) e_display.nextElement();
              if (tmp_display.getName().equals("numcolors")) {
                deviceDisplay.setNumColors(Integer.parseInt(tmp_display.getContent()));
              } else if (tmp_display.getName().equals("iscolor")) {
                deviceDisplay.setIsColor(parseBoolean(tmp_display.getContent()));
              } else if (tmp_display.getName().equals("numalphalevels")) {
                  deviceDisplay.setNumAlphaLevels(Integer.parseInt(tmp_display.getContent()));
              } else if (tmp_display.getName().equals("background")) {
                deviceDisplay.setBackgroundColor(new Color(Integer.parseInt(tmp_display.getContent(), 16)));
              } else if (tmp_display.getName().equals("foreground")) {
                deviceDisplay.setForegroundColor(new Color(Integer.parseInt(tmp_display.getContent(), 16)));
              } else if (tmp_display.getName().equals("rectangle")) {
                deviceDisplay.setDisplayRectangle(getRectangle(tmp_display));
              } else if (tmp_display.getName().equals("paintable")) {
                deviceDisplay.setDisplayPaintable(getRectangle(tmp_display));
              }
            }
            for (Enumeration e_display = tmp.enumerateChildren(); e_display.hasMoreElements(); ) {
              XMLElement tmp_display = (XMLElement) e_display.nextElement();          
              if (tmp_display.getName().equals("img")) {
                if (tmp_display.getStringAttribute("name").equals("up")
                		|| tmp_display.getStringAttribute("name").equals("down")) {
                	SoftButton icon = deviceDisplay.createSoftButton(
                			tmp_display.getStringAttribute("name"),
                			getRectangle(getElement(tmp_display, "paintable")),
                			deviceDisplay.createSystemImage(tmp_display.getStringAttribute("src")),
                			deviceDisplay.createSystemImage(tmp_display.getStringAttribute("src")));
                	getSoftButtons().addElement(icon);
                } else if (tmp_display.getStringAttribute("name").equals("mode")) {
                  if (tmp_display.getStringAttribute("type").equals("123")) {
                    deviceDisplay.setMode123Image(new PositionedImage(
                    	deviceDisplay.createSystemImage(tmp_display.getStringAttribute("src")),
                        getRectangle(getElement(tmp_display, "paintable"))));
                  } else if (tmp_display.getStringAttribute("type").equals("abc")) {
                    deviceDisplay.setModeAbcLowerImage(new PositionedImage(
                    	deviceDisplay.createSystemImage(tmp_display.getStringAttribute("src")),
                        getRectangle(getElement(tmp_display, "paintable"))));
                  } else if (tmp_display.getStringAttribute("type").equals("ABC")) {
                    deviceDisplay.setModeAbcUpperImage(new PositionedImage(
                    	deviceDisplay.createSystemImage(tmp_display.getStringAttribute("src")),
                        getRectangle(getElement(tmp_display, "paintable"))));
                  }
                }
              } else if (tmp_display.getName().equals("icon")) {
            	  Image iconNormalImage = null;
            	  Image iconPressedImage = null;
            	  for (Enumeration e_icon = tmp_display.enumerateChildren(); e_icon.hasMoreElements(); ) {
            		  XMLElement tmp_icon = (XMLElement) e_icon.nextElement();
            		  if (tmp_icon.getName().equals("img")) {
            			  if (tmp_icon.getStringAttribute("name").equals("normal")) {
            				  iconNormalImage = deviceDisplay.createSystemImage(tmp_icon.getStringAttribute("src"));
            			  } else if (tmp_icon.getStringAttribute("name").equals("pressed")) {
            				  iconPressedImage = deviceDisplay.createSystemImage(tmp_icon.getStringAttribute("src"));
            			  }
            		  }
            	  }
            	  SoftButton icon = deviceDisplay.createSoftButton(
            			  tmp_display.getStringAttribute("name"), 
            			  getRectangle(getElement(tmp_display, "paintable")), 
            			  iconNormalImage, 
            			  iconPressedImage);
            	  if (icon.getName().equals("up")) {
            		  icon.setCommand(CommandManager.CMD_SCREEN_UP);
            	  } else if (icon.getName().equals("down")) {
            		  icon.setCommand(CommandManager.CMD_SCREEN_DOWN);
            	  }
            	  getSoftButtons().addElement(icon);
              }
            }
          } else if (tmp.getName().equals("input") || tmp.getName().equals("keyboard")) {
        	// "keyboard" is for backward compatibility  
            for (Enumeration e_keyboard = tmp.enumerateChildren(); e_keyboard.hasMoreElements(); ) {
              XMLElement tmp_keyboard = (XMLElement) e_keyboard.nextElement();
              if (tmp_keyboard.getName().equals("haspointerevents")) {
                inputMethod.setHasPointerEvents(parseBoolean(tmp_keyboard.getContent()));
              } else if (tmp_keyboard.getName().equals("haspointermotionevents")) {
                inputMethod.setHasPointerMotionEvents(parseBoolean(tmp_keyboard.getContent()));
              } else if (tmp_keyboard.getName().equals("hasrepeatevents")) {
                inputMethod.setHasRepeatEvents(parseBoolean(tmp_keyboard.getContent()));
              } else if (tmp_keyboard.getName().equals("button")) {
                Rectangle rectangle = null;
                Vector stringArray = new Vector();
                for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
                  XMLElement tmp_button = (XMLElement) e_button.nextElement();
                  if (tmp_button.getName().equals("chars")) {
                    for (Enumeration e_chars = tmp_button.enumerateChildren(); e_chars.hasMoreElements(); ) {
                      XMLElement tmp_chars = (XMLElement) e_chars.nextElement();
                      if (tmp_chars.getName().equals("char")) {                 
                        stringArray.addElement(tmp_chars.getContent());                    
                      }
                    }
                  } else if (tmp_button.getName().equals("rectangle")) {
                    rectangle = getRectangle(tmp_button);
                  }
                }
                char[] charArray = new char[stringArray.size()];
                for (int i = 0; i < stringArray.size(); i++) {
                  String str = (String) stringArray.elementAt(i);
                  if (str.length() > 0) {
                    charArray[i] = str.charAt(0);
                  } else {
                    charArray[i] = ' ';
                  }
                }
                getButtons().addElement(deviceDisplay.createButton(tmp_keyboard.getStringAttribute("name"), 
                    rectangle, tmp_keyboard.getStringAttribute("key"), charArray));
              } else if (tmp_keyboard.getName().equals("softbutton")) {
                Vector commands = new Vector();
                Rectangle rectangle = null, paintable = null;
                for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
                  XMLElement tmp_button = (XMLElement) e_button.nextElement();
                  if (tmp_button.getName().equals("rectangle")) {
                    rectangle = getRectangle(tmp_button);
                  } else if (tmp_button.getName().equals("paintable")) {
                    paintable = getRectangle(tmp_button);
                  } else if (tmp_button.getName().equals("command")) {
                    commands.addElement(tmp_button.getContent());
                  }
                }
                SoftButton button = deviceDisplay.createSoftButton(tmp_keyboard.getStringAttribute("name"),
                    rectangle, tmp_keyboard.getStringAttribute("key"), paintable, 
                    tmp_keyboard.getStringAttribute("alignment"), commands);
                getButtons().addElement(button);
                getSoftButtons().addElement(button);
              }
            }
          }
        }
    }
    
    
    private XMLElement getElement(XMLElement source, String name)
    {
        for (Enumeration e_content = source.enumerateChildren(); e_content.hasMoreElements();) {
            XMLElement tmp_content = (XMLElement) e_content.nextElement();
            if (tmp_content.getName().equals(name)) {
                return tmp_content;
            }
        }

        return null;
    }
    
    
    private Rectangle getRectangle(XMLElement source)
    {
        Rectangle rect = new Rectangle();

        for (Enumeration e_rectangle = source.enumerateChildren(); e_rectangle.hasMoreElements();) {
            XMLElement tmp_rectangle = (XMLElement) e_rectangle.nextElement();
            if (tmp_rectangle.getName().equals("x")) {
                rect.x = Integer.parseInt(tmp_rectangle.getContent());
            } else if (tmp_rectangle.getName().equals("y")) {
                rect.y = Integer.parseInt(tmp_rectangle.getContent());
            } else if (tmp_rectangle.getName().equals("width")) {
                rect.width = Integer.parseInt(tmp_rectangle.getContent());
            } else if (tmp_rectangle.getName().equals("height")) {
                rect.height = Integer.parseInt(tmp_rectangle.getContent());
            }
        }

        return rect;
    }     
    
    
    private boolean parseBoolean(String value)
    {
        if (value.toLowerCase().equals(new String("true").toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    


}
