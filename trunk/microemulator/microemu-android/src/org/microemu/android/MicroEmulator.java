/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */

package org.microemu.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.microedition.lcdui.Command;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.device.AndroidDevice;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidCommandUI;
import org.microemu.android.device.ui.AndroidDisplayableUI;
import org.microemu.android.util.AndroidLoggerAppender;
import org.microemu.android.util.AndroidRecordStoreManager;
import org.microemu.android.util.AndroidRepaintListener;
import org.microemu.app.Common;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CommandUI;
import org.microemu.log.Logger;
import org.microemu.util.JadProperties;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.Window;

public class MicroEmulator extends MicroEmulatorActivity {
	
	public static final String LOG_TAG = "MicroEmulator";
		
	public Common common;
	
	private MIDlet midlet;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        Logger.removeAllAppenders();
        Logger.setLocationEnabled(false);
        Logger.addAppender(new AndroidLoggerAppender());
        
        System.setOut(new PrintStream(new OutputStream() {
        	
        	StringBuffer line = new StringBuffer();

			@Override
			public void write(int oneByte) throws IOException {
				if (((char) oneByte) == '\n') {
					Logger.debug(line.toString());
					line.delete(0, line.length() - 1);
				} else {
					line.append((char) oneByte);
				}
			}
        	
        }));
        
        System.setErr(new PrintStream(new OutputStream() {
        	
        	StringBuffer line = new StringBuffer();

			@Override
			public void write(int oneByte) throws IOException {
				if (((char) oneByte) == '\n') {
					Logger.debug(line.toString());
					line.delete(0, line.length() - 1);
				} else {
					line.append((char) oneByte);
				}
			}
        	
        }));
        
        java.util.List params = new ArrayList();
        params.add("--usesystemclassloader");
        params.add("--quit");
        
        String midletClassName;
        String jadName = null;
		try {
			Class r = Class.forName(getComponentName().getPackageName() + ".R$string");
			Field[] fields = r.getFields();
			Class[] classes = r.getClasses();
	        midletClassName = getResources().getString(r.getField("class_name").getInt(null));
            try {
                jadName = getResources().getString(r.getField("jad_name").getInt(null));
            } catch (NoSuchFieldException e) {
            }

	        params.add(midletClassName);	       
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

        common = new Common(emulatorContext);
        common.setRecordStoreManager(new AndroidRecordStoreManager(this));
        common.setDevice(new AndroidDevice(emulatorContext, this));        
        common.initParams(params, null, AndroidDevice.class);
               
        System.setProperty("microedition.platform", "microemu-android");
        System.setProperty("microedition.configuration", "CLDC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");
        System.setProperty("microedition.locale", Locale.getDefault().toString());

        /* JSR-75 */
        Map properties = new HashMap();
        properties.put("fsRoot", "/");
        properties.put("fsSingle", "sdcard");
        common.registerImplementation("org.microemu.cldc.file.FileSystem", properties, false);
        MIDletSystemProperties.setPermission("javax.microedition.io.Connector.file.read", 1);
        MIDletSystemProperties.setPermission("javax.microedition.io.Connector.file.write", 1);
        System.setProperty("fileconn.dir.photos", "file:///sdcard/");

        if (jadName != null) {
            try {
    	        InputStream is = getAssets().open(jadName);
    	        common.jad = new JadProperties();
    	        common.jad.read(is);
            } catch (Exception e) {
            	Logger.error(e);
            }
        }
        
        initializeExtensions();
        
        common.getLauncher().setSuiteName(midletClassName);
        midlet = common.initMIDlet(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        if (contentView != null) {
            if (contentView instanceof AndroidRepaintListener) {
                ((AndroidRepaintListener) contentView).onPause();
            }
        }

        MIDletAccess ma = MIDletBridge.getMIDletAccess(midlet);
        if (ma != null) {
            ma.pauseApp();
            ma.getDisplayAccess().hideNotify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        new Thread(new Runnable() {

            public void run()
            {
                MIDletAccess ma = MIDletBridge.getMIDletAccess(midlet);
                if (ma != null) {
                    try {
                        ma.startApp();
                    } catch (MIDletStateChangeException e) {
                        e.printStackTrace();
                    }
                }

                if (contentView != null) {
                    if (contentView instanceof AndroidRepaintListener) {
                        ((AndroidRepaintListener) contentView).onResume();
                    }
                    post(new Runnable() {
                        public void run() {
                            contentView.invalidate();
                        }
                    });
                }
            }
            
        }).start();
    }
    
    protected void initializeExtensions() {
    }

    private boolean ignoreBackKeyUp = false;
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		final DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}
		AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
		if (ui == null) {
			return false;
		}		

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			List<AndroidCommandUI> commands = ui.getCommandsUI();
			
			CommandUI cmd = getFirstCommandOfType(commands, Command.BACK);
			if (cmd != null) {
				if (ui.getCommandListener() != null) {
					ignoreBackKeyUp = true;
					MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(cmd.getCommand(), da.getCurrent());
				}
				return true;
			}

			cmd = getFirstCommandOfType(commands, Command.EXIT);
			if (cmd != null) {
				if (ui.getCommandListener() != null) {
					ignoreBackKeyUp = true;
					MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(cmd.getCommand(), da.getCurrent());
				}
				return true;
			}
			
			cmd = getFirstCommandOfType(commands, Command.CANCEL);
			if (cmd != null) {
				if (ui.getCommandListener() != null) {
					ignoreBackKeyUp = true;
					MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(cmd.getCommand(), da.getCurrent());
				}
				return true;
			}
		}
					
		if (ui instanceof AndroidCanvasUI) {
            if (ignoreKey(keyCode)) {
                return false;    
            }

			Device device = DeviceFactory.getDevice();
			((AndroidInputMethod) device.getInputMethod()).buttonPressed(event);

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && ignoreBackKeyUp) {
			ignoreBackKeyUp = false;
			return true;
		}
		
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		final DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}
		AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
		if (ui == null) {
			return false;
		}		

		if (ui instanceof AndroidCanvasUI) {
			if (ignoreKey(keyCode)) {
	            return false;    
	        }
	
			Device device = DeviceFactory.getDevice();
			((AndroidInputMethod) device.getInputMethod()).buttonReleased(event);
	
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}
	
	private CommandUI getFirstCommandOfType(List<AndroidCommandUI> commands, int commandType) {
		for (int i = 0; i < commands.size(); i++) {
			CommandUI cmd = commands.get(i);
			if (cmd.getCommand().getCommandType() == commandType) {
				return cmd;
			}
		}	
		
		return null;
	}
	
    private boolean ignoreKey(int keyCode) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_UP:
        case KeyEvent.KEYCODE_HEADSETHOOK: 
            return true;
        default:
            return false;
        }    
    }
	
    private final static KeyEvent KEY_RIGHT_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
    
    private final static KeyEvent KEY_RIGHT_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
	
    private final static KeyEvent KEY_LEFT_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
    	
    private final static KeyEvent KEY_LEFT_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT);

    private final static KeyEvent KEY_DOWN_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
    
    private final static KeyEvent KEY_DOWN_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN);
    	
    private final static KeyEvent KEY_UP_DOWN_EVENT = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
    	
    private final static KeyEvent KEY_UP_UP_EVENT = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP);

    private final static float TRACKBALL_THRESHOLD = 0.4f; 
	
	private float accumulatedTrackballX = 0;
	
	private float accumulatedTrackballY = 0;
	
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return false;
			}
			final DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return false;
			}
			AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
			if (ui instanceof AndroidCanvasUI) {
				float x = event.getX();
				float y = event.getY();
				if ((x > 0 && accumulatedTrackballX < 0) || (x < 0 && accumulatedTrackballX > 0)) {
					accumulatedTrackballX = 0;
				}
				if ((y > 0 && accumulatedTrackballY < 0) || (y < 0 && accumulatedTrackballY > 0)) {
					accumulatedTrackballY = 0;
				}
				if (accumulatedTrackballX + x > TRACKBALL_THRESHOLD) {
					accumulatedTrackballX -= TRACKBALL_THRESHOLD;
					KEY_RIGHT_DOWN_EVENT.dispatch(this);
					KEY_RIGHT_UP_EVENT.dispatch(this);
				} else if (accumulatedTrackballX + x < -TRACKBALL_THRESHOLD) {
					accumulatedTrackballX += TRACKBALL_THRESHOLD;
					KEY_LEFT_DOWN_EVENT.dispatch(this);
					KEY_LEFT_UP_EVENT.dispatch(this);
				}
				if (accumulatedTrackballY + y > TRACKBALL_THRESHOLD) {
					accumulatedTrackballY -= TRACKBALL_THRESHOLD;
					KEY_DOWN_DOWN_EVENT.dispatch(this);
					KEY_DOWN_UP_EVENT.dispatch(this);
				} else if (accumulatedTrackballY + y < -TRACKBALL_THRESHOLD) {
					accumulatedTrackballY += TRACKBALL_THRESHOLD;
					KEY_UP_DOWN_EVENT.dispatch(this);
					KEY_UP_UP_EVENT.dispatch(this);
				}
				accumulatedTrackballX += x;
				accumulatedTrackballY += y;
				
				return true;
			}
		}
		
		return super.onTrackballEvent(event);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		final DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}
		AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
		if (ui == null) {
			return false;
		}		
		
		menu.clear();	
		boolean result = false;
		List<AndroidCommandUI> commands = ui.getCommandsUI();
		for (int i = 0; i < commands.size(); i++) {
			result = true;
			AndroidCommandUI cmd = commands.get(i);
			if (cmd.getCommand().getCommandType() != Command.BACK && cmd.getCommand().getCommandType() != Command.EXIT) {
				SubMenu item = menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getCommand().getLabel());
				item.setIcon(cmd.getDrawable());
			}
		}

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		final DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}
		AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
		if (ui == null) {
			return false;
		}

		int commandIndex = item.getItemId() - Menu.FIRST;
		List<AndroidCommandUI> commands = ui.getCommandsUI();
		CommandUI c = commands.get(commandIndex);

		if (c != null) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(c.getCommand(), da.getCurrent());
			return true;
		}

		return false;
	}

}