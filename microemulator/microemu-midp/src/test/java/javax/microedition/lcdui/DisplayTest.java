/**
 * MicroEmulator 
 * Copyright (C) 2007 Rushabh Doshi <radoshi@cs.stanford.edu> Pelago, Inc
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
 *   Andres Navarro
 *   
 *  @version $Id$
 */
package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;

import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.MutableImage;

/**
 * @author radoshi
 * 
 */
public class DisplayTest extends TestCase {

	private final static class MockDisplayable extends Displayable {

		volatile int prePaintCount = 0, postPaintCount = 0;

		/**
		 * 
		 */
		public MockDisplayable() {

			super("Mock");
		}

		final void paint(Graphics g) {

			prePaintCount++;

			synchronized (this) {

				try {
					this.wait();
				} catch (InterruptedException exception) {
					exception.printStackTrace();
				}
			}

			postPaintCount++;
		}

		final void reset() {

			this.prePaintCount = 0;
			this.postPaintCount = 0;
		}
	}

	private final static class MockRunner implements Runnable {

		volatile boolean hasRun = false;

		public final void run() {

			hasRun = true;
		}

		final void reset() {

			hasRun = false;
		}

	}

	private static final class MockDeviceDisplay implements DeviceDisplay {

		private int width = 100, height = 100;

		private Display display;

		/**
		 * @param display
		 */
		public MockDeviceDisplay(Display display) {

			assertNotNull(display);
			this.display = display;
		}

		public Image createImage(int width, int height) {

			return null;
		}

		public Image createImage(String name) throws IOException {

			return null;
		}

		public Image createImage(Image source) {

			return null;
		}

		public Image createImage(	byte[] imageData,
									int imageOffset,
									int imageLength) {

			return null;
		}

		public Image createImage(InputStream is) throws IOException {

			return null;
		}

		public Image createImage(	Image image,
									int x,
									int y,
									int width,
									int height,
									int transform) {

			return null;
		}

		public Image createRGBImage(int[] rgb,
									int width,
									int height,
									boolean processAlpha) {

			return null;
		}

		public MutableImage getDisplayImage() {

			return null;
		}

		public int getFullHeight() {

			return this.height;
		}

		public int getFullWidth() {

			return this.width;
		}

		public int getHeight() {

			return this.height;
		}

		public int getWidth() {

			return this.width;
		}

		public boolean isColor() {

			return false;
		}

		public boolean isFullScreenMode() {

			return false;
		}

		public int numAlphaLevels() {

			return 0;
		}

		public int numColors() {

			return 0;
		}

		public void repaint(int x, int y, int width, int height) {

			display.getCurrent().paint(null);
		}

		public void setScrollDown(boolean state) {

		}

		public void setScrollUp(boolean state) {

		}

	}

	private static final class MockFontManager implements FontManager {

		public int charWidth(Font f, char ch) {

			return 0;
		}

		public int charsWidth(Font f, char[] ch, int offset, int length) {

			return 0;
		}

		public int getBaselinePosition(Font f) {

			return 0;
		}

		public int getHeight(Font f) {

			return 0;
		}

		public void init() {

		}

		public int stringWidth(Font f, String str) {

			return 0;
		}

	}

	private static final class MockDevice implements Device {

		private Display display = new Display();

		private DeviceDisplay deviceDisplay = new MockDeviceDisplay(display);

		private FontManager fontMgr = new MockFontManager();

		private Vector softButtons = new Vector();

		public void destroy() {

		}

		public Vector getButtons() {

			return null;
		}

		public DeviceDisplay getDeviceDisplay() {

			return this.deviceDisplay;
		}

		public FontManager getFontManager() {

			return this.fontMgr;
		}

		public InputMethod getInputMethod() {

			return null;
		}

		public String getName() {

			return null;
		}

		public Image getNormalImage() {

			return null;
		}

		public Image getOverImage() {

			return null;
		}

		public Image getPressedImage() {

			return null;
		}

		public Vector getSoftButtons() {

			return this.softButtons;
		}

		public Map getSystemProperties() {

			return null;
		}

		public boolean hasPointerEvents() {

			return false;
		}

		public boolean hasPointerMotionEvents() {

			return false;
		}

		public boolean hasRepeatEvents() {

			return false;
		}

		public void init() {

		}

		public boolean vibrate(int duration) {

			return false;
		}

		/**
		 * @return the display
		 */
		public final Display getDisplay() {

			return display;
		}
	}

	private final MockDevice device = new MockDevice();

	private final Display display = device.getDisplay();

	protected void setUp() throws Exception {

		DeviceFactory.setDevice(this.device);
	}

	/**
	 * According to the spec, callSerially runnables should be called on the
	 * event thread after the paints have been serviced. Please see
	 * http://archives.java.sun.com/cgi-bin/wa?A2=ind0606&L=kvm-interest&D=0&P=2189
	 * for more details
	 * 
	 * @throws Exception
	 */
	public void testConcurrency() throws Exception {

		MockDisplayable disp = new MockDisplayable();

		display.setCurrent(disp);

		display.repaint(disp, 0, 0, 10, 10);

		assertPrePaintValue(1, disp, 1000);

		MockRunner runner = new MockRunner();
		display.callSerially(runner);

		Thread.sleep(1000);

		assertFalse(runner.hasRun);
		assertEquals(0, disp.postPaintCount);

		synchronized (disp) {

			disp.notify();
		}

		assertEventuallyRun(true, runner, 1000);
		assertEquals(1, disp.postPaintCount);
	}

	/**
	 * @param b
	 * @param runner
	 */
	private void assertEventuallyRun(	boolean value,
										MockRunner runner,
										int timeout) {

		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() < start + timeout) {

			if (value == runner.hasRun)
				return;

			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {

				fail("Interrupted");
			}
		}
		assertEquals(value, runner.hasRun);
	}

	/**
	 * @param conditional
	 * @param timeout
	 */
	private final void assertPrePaintValue(	int value,
											MockDisplayable disp,
											int timeout) {

		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() < start + timeout) {

			if (value == disp.prePaintCount)
				return;

			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {

				fail("Interrupted");
			}
		}
		assertEquals(value, disp.prePaintCount);
	}
}
