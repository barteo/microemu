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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

import org.microemu.CommandManager;
import org.microemu.EmulatorContext;
import org.microemu.app.util.IOUtils;
import org.microemu.device.impl.Color;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.FontManagerImpl;
import org.microemu.device.impl.Polygon;
import org.microemu.device.impl.PositionedImage;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.Shape;
import org.microemu.device.impl.SoftButton;


public class Device 
{
	private String name;
	
	private EmulatorContext context; 	

	private Image normalImage;
	private Image overImage;
	private Image pressedImage;
    
	private Vector buttons;
	private Vector softButtons;
	
	private boolean hasPointerEvents;
	private boolean hasPointerMotionEvents;
	// TODO not implemented yet
	private boolean hasRepeatEvents;

	private Properties systemProperties;
	private Properties systemPropertiesPreserve;
	
	public static final String DEFAULT_LOCATION = "org/microemu/device/default/device.xml";
	/**
	 * @deprecated
	 */
	private String descriptorLocation;
	
	private static Map specialInheritanceAttributeSet;

	public Device()
	{	    
		systemProperties = new Properties();
		systemPropertiesPreserve = new Properties();
        buttons = new Vector();
        softButtons = new Vector();
	}
	
	
	public static Device create(EmulatorContext context, ClassLoader classLoader, String descriptorLocation) throws IOException
	{
		XMLElement doc = loadDeviceDescriptor(classLoader, descriptorLocation);
		//saveDevice(doc);
		Device device = null;
        for (Enumeration e = doc.enumerateChildren(); e.hasMoreElements(); ) {
			XMLElement tmp = (XMLElement) e.nextElement();
			if (tmp.getName().equals("class-name")) {
				try {
					Class deviceClass = Class.forName(tmp.getContent(), true, classLoader);
					device = (Device) deviceClass.newInstance();
				} catch (ClassNotFoundException ex) {
					throw new IOException(ex.getMessage());
				} catch (InstantiationException ex) {
					throw new IOException(ex.getMessage());
				} catch (IllegalAccessException ex) {
					throw new IOException(ex.getMessage());
				}
				break;
			}
		}
        
        if (device == null) {
        	device = new Device();
        }
        device.context = context;
        
        device.loadConfig(classLoader, besourceBase(descriptorLocation), doc);
		
		return device;
	}
	
	
	public void init()
	{
        try {
        	initSystemProperties();
		} catch (SecurityException e) {
			System.out.println("Cannot load SystemProperties: " + e);
		}
	}

    /**
     * @deprecated use Device.create(EmulatorContext context, ClassLoader classLoader, String descriptorLocation);
     */
	public void init(EmulatorContext context)
    {     
        init(context, "/" + DEFAULT_LOCATION);
    }       

    
    /**
     * @deprecated use Device.create(EmulatorContext context, ClassLoader classLoader, String descriptorLocation);
     */
    public void init(EmulatorContext context, String descriptorLocation)
    {
    	this.context = context;
    	if (descriptorLocation.startsWith("/")) {
    		this.descriptorLocation = descriptorLocation.substring(1);
    	} else {
    		this.descriptorLocation = descriptorLocation;
    	}

        try {
        	String base = descriptorLocation.substring(0, descriptorLocation.lastIndexOf("/"));   
        	XMLElement doc = loadDeviceDescriptor(getClass().getClassLoader(), descriptorLocation);
            loadConfig(getClass().getClassLoader(), base, doc);
        } catch (IOException ex) {
            System.out.println("Cannot load config: " + ex);
        }
        try {
			initSystemProperties();
		} catch (SecurityException e) {
			System.out.println("Cannot load SystemProperties: " + e);
		}
    }
    
    /**
     * @deprecated
     */
    public String getDescriptorLocation() {
    	return descriptorLocation;
    }
    
    private void initSystemProperties() {
        for(Iterator i = systemProperties.entrySet().iterator(); i.hasNext(); ) {
        	Map.Entry e = (Map.Entry)i.next();
        	String orig = System.setProperty((String)e.getKey(), (String)e.getValue());
        	if (orig != null) {
        		systemPropertiesPreserve.put(e.getKey(), orig);
        	}
        }
    }
    
    public void destroy() {
    	// Restore System Properties.
    	try {
			for (Iterator i = systemProperties.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				System.clearProperty((String) e.getKey());
			}
			for (Iterator i = systemPropertiesPreserve.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				System.setProperty((String) e.getKey(), (String) e.getValue());
			}
		} catch (SecurityException e) {
			System.out.println("Cannot restore SystemProperties: " + e);
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

    
    protected void loadConfig(ClassLoader classLoader, String base, XMLElement doc)
		throws IOException
    {
        String deviceName = doc.getStringAttribute("name");
        if (deviceName != null) {
        	name = deviceName;
        } else {
        	name = base;
        }

        hasPointerEvents = false;
        hasPointerMotionEvents = false;
        hasRepeatEvents = false;
        
        ((FontManagerImpl) getFontManager()).setAntialiasing(false);
        
        for (Enumeration e = doc.enumerateChildren(); e.hasMoreElements(); ) {
          XMLElement tmp = (XMLElement) e.nextElement();
          if (tmp.getName().equals("system-properties")) {
        	  parseSystemProperties(tmp);
          } else if (tmp.getName().equals("img")) {
            try {
              if (tmp.getStringAttribute("name").equals("normal")) {
                normalImage = loadImage(classLoader, base, tmp.getStringAttribute("src"));
              } else if (tmp.getStringAttribute("name").equals("over")) {
                overImage = loadImage(classLoader, base, tmp.getStringAttribute("src"));
              } else if (tmp.getStringAttribute("name").equals("pressed")) {
                pressedImage = loadImage(classLoader, base, tmp.getStringAttribute("src"));
              }
            } catch (IOException ex) {
              System.out.println("Cannot load " + tmp.getStringAttribute("src"));
              return;
            }
          } else if (tmp.getName().equals("display")) {
        	  parseDisplay(classLoader, base, tmp);
          } else if (tmp.getName().equals("fonts")) {
        	  parseFonts(classLoader, base, tmp);
          } else if (tmp.getName().equals("input") || tmp.getName().equals("keyboard")) {
        	// "keyboard" is for backward compatibility 
        	  parseInput(tmp);
          }
        }
    }
    
    
    private void parseDisplay(ClassLoader classLoader, String base, XMLElement tmp) throws IOException
    {
        DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) getDeviceDisplay();

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
            	// deprecated, moved to icon  
              	SoftButton icon = deviceDisplay.createSoftButton(
              			tmp_display.getStringAttribute("name"),
              			getRectangle(tmp_display.getChild("paintable")),
              			loadImage(classLoader, base, tmp_display.getStringAttribute("src")),
              			loadImage(classLoader, base, tmp_display.getStringAttribute("src")));
              	getSoftButtons().addElement(icon);              	
              } else if (tmp_display.getStringAttribute("name").equals("mode")) {
            	// deprecated, moved to status
                if (tmp_display.getStringAttribute("type").equals("123")) {
                  deviceDisplay.setMode123Image(new PositionedImage(
                	loadImage(classLoader, base, tmp_display.getStringAttribute("src")),
                      getRectangle(tmp_display.getChild("paintable"))));
                } else if (tmp_display.getStringAttribute("type").equals("abc")) {
                  deviceDisplay.setModeAbcLowerImage(new PositionedImage(
                	loadImage(classLoader, base, tmp_display.getStringAttribute("src")),
                      getRectangle(tmp_display.getChild("paintable"))));
                } else if (tmp_display.getStringAttribute("type").equals("ABC")) {
                  deviceDisplay.setModeAbcUpperImage(new PositionedImage(
                	loadImage(classLoader, base, tmp_display.getStringAttribute("src")),
                      getRectangle(tmp_display.getChild("paintable"))));
                }
              }
            } else if (tmp_display.getName().equals("icon")) {
          	  Image iconNormalImage = null;
          	  Image iconPressedImage = null;
          	  for (Enumeration e_icon = tmp_display.enumerateChildren(); e_icon.hasMoreElements(); ) {
          		  XMLElement tmp_icon = (XMLElement) e_icon.nextElement();
          		  if (tmp_icon.getName().equals("img")) {
          			  if (tmp_icon.getStringAttribute("name").equals("normal")) {
          				  iconNormalImage = loadImage(classLoader, base, tmp_icon.getStringAttribute("src"));
          			  } else if (tmp_icon.getStringAttribute("name").equals("pressed")) {
          				  iconPressedImage = loadImage(classLoader, base, tmp_icon.getStringAttribute("src"));
          			  }
          		  }
          	  }
          	  SoftButton icon = deviceDisplay.createSoftButton(
          			  tmp_display.getStringAttribute("name"), 
          			  getRectangle(tmp_display.getChild("paintable")), 
          			  iconNormalImage, 
          			  iconPressedImage);
          	  if (icon.getName().equals("up")) {
          		  icon.setCommand(CommandManager.CMD_SCREEN_UP);
          	  } else if (icon.getName().equals("down")) {
          		  icon.setCommand(CommandManager.CMD_SCREEN_DOWN);
          	  }
          	  getSoftButtons().addElement(icon);
            } else if (tmp_display.getName().equals("status")) {
				if (tmp_display.getStringAttribute("name").equals("input")) {
					Rectangle paintable = getRectangle(tmp_display.getChild("paintable"));
					for (Enumeration e_status = tmp_display.enumerateChildren(); e_status.hasMoreElements();) {
						XMLElement tmp_status = (XMLElement) e_status.nextElement();
						if (tmp_status.getName().equals("img")) {
							if (tmp_status.getStringAttribute("name").equals("123")) {
								deviceDisplay.setMode123Image(new PositionedImage(
										loadImage(classLoader, base, tmp_status.getStringAttribute("src")), paintable));
							} else if (tmp_status.getStringAttribute("name").equals("abc")) {
								deviceDisplay.setModeAbcLowerImage(new PositionedImage(
										loadImage(classLoader, base, tmp_status.getStringAttribute("src")), paintable));
							} else if (tmp_status.getStringAttribute("name").equals("ABC")) {
								deviceDisplay.setModeAbcUpperImage(new PositionedImage(
										loadImage(classLoader, base, tmp_status.getStringAttribute("src")), paintable));
							}
						}
					}
				}
			}
          }
    }
    
    
    private void parseFonts(ClassLoader classLoader, String base, XMLElement tmp) throws IOException
    {
        FontManagerImpl fontManager = (FontManagerImpl) getFontManager();
        
        String hint = tmp.getStringAttribute("hint");
        boolean antialiasing = false;
        if (hint != null && hint.equals("antialiasing")) {
        	antialiasing = true;
        }
    	fontManager.setAntialiasing(antialiasing);
    	
        for (Enumeration e_fonts = tmp.enumerateChildren(); e_fonts.hasMoreElements(); ) {
            XMLElement tmp_font = (XMLElement) e_fonts.nextElement();
            if (tmp_font.getName().equals("font")) {
            	String face = tmp_font.getStringAttribute("face").toLowerCase();
            	String style = tmp_font.getStringAttribute("style").toLowerCase();
            	String size = tmp_font.getStringAttribute("size").toLowerCase();
            	
            	if (face.startsWith("face_")) {
            		face = face.substring("face_".length());
            	}
            	if (style.startsWith("style_")) {
            		style = style.substring("style_".length());
            	}
            	if (size.startsWith("size_")) {
            		size = size.substring("size_".length());
            	}
            	
                for (Enumeration e_defs = tmp_font.enumerateChildren(); e_defs.hasMoreElements(); ) {
                	XMLElement tmp_def = (XMLElement) e_defs.nextElement();
                	if (tmp_def.getName().equals("system")) {
                		String defName = tmp_def.getStringAttribute("name");
                		String defStyle = tmp_def.getStringAttribute("style");
                		int defSize = Integer.parseInt(tmp_def.getStringAttribute("size"));
                		
                		fontManager.setFont(face, style, size,
                				fontManager.createSystemFont(defName, defStyle, defSize, antialiasing));
                	} else if (tmp_def.getName().equals("ttf")) {
                		String defSrc = tmp_def.getStringAttribute("src");
                		String defStyle = tmp_def.getStringAttribute("style");
                		int defSize = Integer.parseInt(tmp_def.getStringAttribute("size"));
                		
                		fontManager.setFont(face, style, size, 
                				fontManager.createTrueTypeFont(getResourceUrl(classLoader, base, defSrc), defStyle, defSize, antialiasing));
                	}
                }
            }
        }
    }
    
    
    private void parseInput(XMLElement tmp)
    {
        DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) getDeviceDisplay();

        for (Enumeration e_keyboard = tmp.enumerateChildren(); e_keyboard.hasMoreElements(); ) {
            XMLElement tmp_keyboard = (XMLElement) e_keyboard.nextElement();
            if (tmp_keyboard.getName().equals("haspointerevents")) {
              hasPointerEvents = parseBoolean(tmp_keyboard.getContent());
            } else if (tmp_keyboard.getName().equals("haspointermotionevents")) {
              hasPointerMotionEvents = parseBoolean(tmp_keyboard.getContent());
            } else if (tmp_keyboard.getName().equals("hasrepeatevents")) {
              hasRepeatEvents = parseBoolean(tmp_keyboard.getContent());
            } else if (tmp_keyboard.getName().equals("button")) {
              Shape shape = null;
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
                  shape = getRectangle(tmp_button);
                } else if (tmp_button.getName().equals("polygon")) {
                  shape = getPolygon(tmp_button);
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
              int keyCode = tmp_keyboard.getIntAttribute("keyCode", Integer.MIN_VALUE);
              getButtons().addElement(deviceDisplay.createButton(
            		  tmp_keyboard.getStringAttribute("name"),
            		  shape, 
            		  keyCode,
            		  tmp_keyboard.getStringAttribute("key"), 
            		  charArray));            	  
            } else if (tmp_keyboard.getName().equals("softbutton")) {
              Vector commands = new Vector();
              Shape shape = null; 
              Rectangle paintable = null;
              Font font = null;
              for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
                XMLElement tmp_button = (XMLElement) e_button.nextElement();
                if (tmp_button.getName().equals("rectangle")) {
                  shape = getRectangle(tmp_button);
                } else if (tmp_button.getName().equals("paintable")) {
                  paintable = getRectangle(tmp_button);
                } else if (tmp_button.getName().equals("command")) {
                  commands.addElement(tmp_button.getContent());
                } else if (tmp_button.getName().equals("font")) {
                  font = getFont(	
                		  tmp_button.getStringAttribute("face"),
                		  tmp_button.getStringAttribute("style"),
                		  tmp_button.getStringAttribute("size"));
                }
              }
              int keyCode = tmp_keyboard.getIntAttribute("keyCode", Integer.MIN_VALUE);
              SoftButton button = deviceDisplay.createSoftButton(
        			  tmp_keyboard.getStringAttribute("name"),
        			  shape,
        			  keyCode,
        			  tmp_keyboard.getStringAttribute("key"), 
        			  paintable, 
        			  tmp_keyboard.getStringAttribute("alignment"), 
        			  commands, 
        			  font);
              getButtons().addElement(button);
              getSoftButtons().addElement(button);
            }
          }    	
    }
    
    
    private void parseSystemProperties(XMLElement tmp)
    {
        for (Enumeration e_prop = tmp.enumerateChildren(); e_prop.hasMoreElements(); ) {
            XMLElement tmp_prop = (XMLElement) e_prop.nextElement();
            if (tmp_prop.getName().equals("system-property")) {
            	systemProperties.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
            }
        }
    }
    
    
    private static Font getFont(String face, String style, String size) {
    	int meFace = 0;
		if (face.equalsIgnoreCase("system")) {
			meFace |= Font.FACE_SYSTEM;
		} else if (face.equalsIgnoreCase("monospace")) {
			meFace |= Font.FACE_MONOSPACE;
		} else if (face.equalsIgnoreCase("proportional")) {
			meFace |= Font.FACE_PROPORTIONAL;
		}
		
		int meStyle = 0;
		String testStyle = style.toLowerCase();
		if (testStyle.indexOf("plain") != -1) {
			meStyle |= Font.STYLE_PLAIN;
		} 
		if (testStyle.indexOf("bold") != -1) {
			meStyle |= Font.STYLE_BOLD;
		} 
		if (testStyle.indexOf("italic") != -1) {
			meStyle |= Font.STYLE_ITALIC;
		} 
		if (testStyle.indexOf("underlined") != -1) {
			meStyle |= Font.STYLE_UNDERLINED;
		}
		
		int meSize = 0;
		if (size.equalsIgnoreCase("small")) {
			meSize |= Font.SIZE_SMALL;
		} else if (size.equalsIgnoreCase("medium")) {
			meSize |= Font.SIZE_MEDIUM;
		} else if (size.equalsIgnoreCase("large")) {
			meSize |= Font.SIZE_LARGE;
		}
		
		return Font.getFont(meFace, meStyle, meSize);
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
    
    
    private Polygon getPolygon(XMLElement source)
    {
    	Polygon poly = new Polygon();
    	
        for (Enumeration e_poly = source.enumerateChildren(); e_poly.hasMoreElements();) {
            XMLElement tmp_point = (XMLElement) e_poly.nextElement();
            if (tmp_point.getName().equals("point")) {
            	poly.addPoint(
            			Integer.parseInt(tmp_point.getStringAttribute("x")),
            			Integer.parseInt(tmp_point.getStringAttribute("y")));
            }
        }

        return poly;
    }
    
    
    private boolean parseBoolean(String value)
    {
        if (value.toLowerCase().equals(new String("true").toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }


	public boolean hasPointerEvents() {
		return hasPointerEvents;
	}


	public boolean hasPointerMotionEvents() {
		return hasPointerMotionEvents;
	}


	public boolean hasRepeatEvents() {
		return hasRepeatEvents;
	}
	
	private static void saveDevice(XMLElement doc) {
		File configFile = new File(".", "device-tmp.xml");
		FileWriter fw = null;
		try {
			fw = new FileWriter(configFile);
			doc.write(fw);
			fw.close();
		} catch (IOException ex) {
			System.out.println(ex);
		} finally {
			IOUtils.closeQuietly(fw);
		}
	}

	private static XMLElement loadDeviceDescriptor(ClassLoader classLoader, String descriptorLocation) throws IOException  {
		InputStream descriptor = classLoader.getResourceAsStream(descriptorLocation);
		if (descriptor == null) {
			throw new IOException("Cannot find descriptor at: " + descriptorLocation);
		}
		XMLElement doc;
		try {
			doc = loadXmlDocument(descriptor);
		} finally {
			IOUtils.closeQuietly(descriptor);
		}
		
		String parent = doc.getStringAttribute("extends");
		if (parent != null) {
			return inheritXML(loadDeviceDescriptor(classLoader, expandResourcePath(besourceBase(descriptorLocation), parent)), doc, "/");
		}
		return doc;
	}

	
	private static void inheritanceConstInit() {
		if (specialInheritanceAttributeSet == null) {
			specialInheritanceAttributeSet = new Hashtable();
			specialInheritanceAttributeSet.put("//FONTS/FONT", new String[]{"face", "style", "size"});
		}
	}
	
	/**
	 * Very simple xml inheritance for devices.
	 */
	static XMLElement inheritXML(XMLElement parent, XMLElement child, String parentName) {
		inheritanceConstInit();
		if (parent == null) {
			return child;
		}
		parent.setContent(child.getContent());
		for (Enumeration ena = child.enumerateAttributeNames(); ena.hasMoreElements();) {
			String key = (String) ena.nextElement();
			parent.setAttribute(key, child.getAttribute(key));
		}
		for (Enumeration enc = child.enumerateChildren(); enc.hasMoreElements();) {
			XMLElement c = (XMLElement) enc.nextElement();
			String fullName = (parentName + "/" + c.getName()).toUpperCase(Locale.ENGLISH);
			//System.out.println("processing [" + fullName + "]");
			int c_siblings = child.getChildCount(c.getName());
			int p_siblings = parent.getChildCount(c.getName());
			XMLElement p;
			if ((c_siblings > 1) || (p_siblings > 1)) {
				String [] equalAttributes = (String []) specialInheritanceAttributeSet.get(fullName); 
				if (equalAttributes != null) {
					p = parent.getChild(c.getName(), c.getStringAttributes(equalAttributes));
				} else {
					p = parent.getChild(c.getName(), c.getStringAttribute("name"));
				}
			} else {
				p = parent.getChild(c.getName());
			}
			// System.out.println("inheritXML " + c.getName());
			if (p == null) {
				parent.addChild(c);
			} else {
				inheritXML(p, c, fullName);
			}
		}
		return parent;
	}
	
	private static XMLElement loadXmlDocument(InputStream descriptor) throws IOException {
    	BufferedReader dis = new BufferedReader(new InputStreamReader(descriptor));
        XMLElement doc = new XMLElement();
        try {
          doc.parseFromReader(dis, 1);
        } catch (XMLParseException ex) {
        	throw new IOException(ex.toString());
        } finally {
        	dis.close();
        }
        return doc;
	}
	
	private static String besourceBase(String descriptorLocation) {
		return descriptorLocation.substring(0, descriptorLocation.lastIndexOf("/"));  
	}

	private static String expandResourcePath(String base, String src) throws IOException {
		String expandedSource;
		if (src.startsWith("/")) {
			expandedSource = src;
		} else {
			expandedSource = base + "/" + src;
		}
		if (expandedSource.startsWith("/")) {
			expandedSource = expandedSource.substring(1);
		}
		return expandedSource;
	}
	
	private URL getResourceUrl(ClassLoader classLoader, String base, String src) throws IOException {
		String expandedSource = expandResourcePath(base, src);
	
		URL result = classLoader.getResource(expandedSource);
		
		if (result == null) {
			throw new IOException("Cannot find resource: " + expandedSource);
		}
		
		return result; 
	}
	
	
	private Image loadImage(ClassLoader classLoader, String base, String src) throws IOException {
		URL url = getResourceUrl(classLoader, base, src);
		
		return ((DeviceDisplayImpl) getDeviceDisplay()).createSystemImage(url);
	}

}
