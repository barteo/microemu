/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
 */
package com.siemens.mp.media.control;

import com.siemens.mp.media.Control;

public interface ToneControl
    extends Control
{
    public abstract void setSequence(byte abyte0[]);

    public static final byte VERSION = -2;
    public static final byte TEMPO = -3;
    public static final byte RESOLUTION = -4;
    public static final byte BLOCK_START = -5;
    public static final byte BLOCK_END = -6;
    public static final byte PLAY_BLOCK = -7;
    public static final byte SET_VOLUME = -8;
    public static final byte REPEAT = -9;
    public static final byte C4 = 60;
    public static final byte SILENCE = -1;
}
