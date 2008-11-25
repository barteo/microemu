package org.microemu.iphone.device;

import joc.Pointer;
import obc.CGImage;

public interface IPhoneImage {

	public abstract Pointer<CGImage> getBitmap();

}