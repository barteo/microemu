/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.app.classloader;

import java.util.List;
import java.util.Vector;

import org.microemu.app.ConfigurationException;

public class MIDletClassLoaderConfig {

    public static final int DELEGATION_STRICT = 0;

    public static final int DELEGATION_RELAXED = 1;

    public static final int DELEGATION_DELEGATING = 2;

    public static final int DELEGATION_SYSTEM = 3;

    private int delegationType;

    private boolean delegationSelected;

    List appclasses = new Vector();

    List appclasspath = new Vector();

    public MIDletClassLoaderConfig() {
        delegationSelected = false;
        delegationType = DELEGATION_STRICT;
    }

    public void setDelegationType(String delegationType) throws ConfigurationException {
        if ("strict".equalsIgnoreCase(delegationType)) {
            this.delegationType = DELEGATION_STRICT;
        } else if ("relaxed".equalsIgnoreCase(delegationType)) {
            this.delegationType = DELEGATION_RELAXED;
        } else if ("delegating".equalsIgnoreCase(delegationType)) {
            this.delegationType = DELEGATION_DELEGATING;
        } else if ("system".equalsIgnoreCase(delegationType)) {
            if ((appclasses.size() != 0) || (appclasspath.size() != 0)) {
                throw new ConfigurationException("Can't extend system CLASSPATH");
            }
            this.delegationType = DELEGATION_SYSTEM;
        } else {
            throw new ConfigurationException("Unknown delegationType [" + delegationType + "]");
        }
        delegationSelected = true;
    }

    public int getDelegationType(boolean forJad) {
        if ((!delegationSelected) && (!forJad)) {
            return DELEGATION_RELAXED;    
        } else {
            return delegationType;
        }
    }
    
    public boolean isClassLoaderDisabled() {
        return (this.delegationType == DELEGATION_SYSTEM);
    }

    public void addAppClassPath(String path) throws ConfigurationException {
        if (this.delegationType == DELEGATION_SYSTEM) {
            throw new ConfigurationException("Can't extend system CLASSPATH");
        }
        appclasspath.add(path);
    }

    public void addAppClass(String className) throws ConfigurationException {
        if (this.delegationType == DELEGATION_SYSTEM) {
            throw new ConfigurationException("Can't extend system CLASSPATH");
        }
        appclasses.add(className);
    }

}
