/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *  @version $Id$
 */
package org.microemu.midp.examples.simpledemo;

import java.util.Calendar;
import java.util.Date;

/**
 * @author vlads
 *
 */
public class Utils {
    
    public static String d00(int i) {
        if (i > 9) {
            return String.valueOf(i);
        } else {
            return "0" + String.valueOf(i);
        }
    }
    
    public static String when() {
        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        sb.append(d00(calendar.get(Calendar.HOUR_OF_DAY))).append(":");
        sb.append(d00(calendar.get(Calendar.MINUTE))).append(":");
        sb.append(d00(calendar.get(Calendar.SECOND)));
        return sb.toString();
    }
}
