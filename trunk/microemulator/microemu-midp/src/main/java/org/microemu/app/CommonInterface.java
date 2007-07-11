package org.microemu.app;

import org.microemu.MIDletAccess;
import org.microemu.MIDletContext;

public interface CommonInterface {

	MIDletContext startMidlet(Class midletClass, MIDletAccess previousMidletAccess);

}
