/*
 *  @(#)KeyboardComponent.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.barteo.midp.lcdui;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

import javax.microedition.lcdui.Command;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.device.Device;

/**
 *  Description of the Class
 *
 *@author     barteo
 *@created    3 wrzesieñ 2001
 */
public class KeyboardComponent extends Panel {

    Button soft1, soft2;
    Button left, right, up, down, select;
    Button clear;

    Button one, two, three;
    Button four, five, six;
    Button seven, eight, nine;
    Button mark, zero, hash;

    KeyboardActionListener kb_listener = new KeyboardActionListener();


    /**
     *  Constructor for the KeyboardComponent object
     */
    public KeyboardComponent() {
        XYLayout xy = new XYLayout();
        setLayout(xy);

        soft1 = new Button("");
        soft1.addMouseListener(kb_listener);
        xy.addLayoutComponent(soft1, new XYConstraints(0, 0, 30, 15));
        add(soft1);

        soft2 = new Button("");
        soft2.addMouseListener(kb_listener);
        xy.addLayoutComponent(soft2, new XYConstraints(60, 0, 30, 15));
        add(soft2);

        left = new Button("l");
        left.addMouseListener(kb_listener);
        xy.addLayoutComponent(left, new XYConstraints(0, 45, 30, 15));
        add(left);

        right = new Button("r");
        right.addMouseListener(kb_listener);
        xy.addLayoutComponent(right, new XYConstraints(60, 45, 30, 15));
        add(right);

        up = new Button("u");
        up.addMouseListener(kb_listener);
        xy.addLayoutComponent(up, new XYConstraints(30, 30, 30, 15));
        add(up);

        down = new Button("d");
        down.addMouseListener(kb_listener);
        xy.addLayoutComponent(down, new XYConstraints(30, 60, 30, 15));
        add(down);

        select = new Button("sel");
        select.addMouseListener(kb_listener);
        xy.addLayoutComponent(select, new XYConstraints(30, 45, 30, 15));
        add(select);

        clear = new Button("clr");
        clear.addMouseListener(kb_listener);
        xy.addLayoutComponent(clear, new XYConstraints(30, 90, 30, 15));
        add(clear);

        one = new Button("1");
        one.addMouseListener(kb_listener);
        xy.addLayoutComponent(one, new XYConstraints(0, 120, 30, 15));
        add(one);

        two = new Button("2");
        two.addMouseListener(kb_listener);
        xy.addLayoutComponent(two, new XYConstraints(30, 120, 30, 15));
        add(two);

        three = new Button("3");
        three.addMouseListener(kb_listener);
        xy.addLayoutComponent(three, new XYConstraints(60, 120, 30, 15));
        add(three);

        four = new Button("4");
        four.addMouseListener(kb_listener);
        xy.addLayoutComponent(four, new XYConstraints(0, 135, 30, 15));
        add(four);

        five = new Button("5");
        five.addMouseListener(kb_listener);
        xy.addLayoutComponent(five, new XYConstraints(30, 135, 30, 15));
        add(five);

        six = new Button("6");
        six.addMouseListener(kb_listener);
        xy.addLayoutComponent(six, new XYConstraints(60, 135, 30, 15));
        add(six);

        seven = new Button("7");
        seven.addMouseListener(kb_listener);
        xy.addLayoutComponent(seven, new XYConstraints(0, 150, 30, 15));
        add(seven);

        eight = new Button("8");
        eight.addMouseListener(kb_listener);
        xy.addLayoutComponent(eight, new XYConstraints(30, 150, 30, 15));
        add(eight);

        nine = new Button("9");
        nine.addMouseListener(kb_listener);
        xy.addLayoutComponent(nine, new XYConstraints(60, 150, 30, 15));
        add(nine);

        mark = new Button("*");
        mark.addMouseListener(kb_listener);
        xy.addLayoutComponent(mark, new XYConstraints(0, 165, 30, 15));
        add(mark);

        zero = new Button("0");
        zero.addMouseListener(kb_listener);
        xy.addLayoutComponent(zero, new XYConstraints(30, 165, 30, 15));
        add(zero);

        hash = new Button("#");
        hash.addMouseListener(kb_listener);
        xy.addLayoutComponent(hash, new XYConstraints(60, 165, 30, 15));
        add(hash);
    }


    /**
     *  Description of the Class
     *
     *@author     barteo
     *@created    3 wrzesieñ 2001
     */
    class KeyboardActionListener extends MouseAdapter {

        /**
         *  Description of the Method
         *
         *@param  e  Description of Parameter
         */
        public void mousePressed(MouseEvent e) {
            int key = getKey(e);

            if (key != 0) {
                InputMethod.getInputMethod().keyPressed(key);
                return;
            }

            Button tmp = (Button) e.getSource();
            if (tmp == soft1 || tmp == soft2) {
                Command cmd;
                int i = 0;
                for (Enumeration s = Device.getSoftButtons().elements(); s.hasMoreElements(); ) {
                    cmd = ((SoftButton) s.nextElement()).getCommand();
                    if (cmd != null && i == 0 && tmp == soft1) {
                        CommandManager.getInstance().commandAction(cmd);
                    }
                    if (cmd != null && i == 1 && tmp == soft2) {
                        CommandManager.getInstance().commandAction(cmd);
                    }
                    i++;
                }
            }
        }


        /**
         *  Description of the Method
         *
         *@param  e  Description of Parameter
         */
        public void mouseReleased(MouseEvent e) {
            int key = getKey(e);

            if (key != 0) {
                InputMethod.getInputMethod().keyReleased(key);
                return;
            }
        }


        /**
         *  Gets the key attribute of the KeyboardActionListener object
         *
         *@param  e  Description of Parameter
         *@return    The key value
         */
        int getKey(MouseEvent e) {
            int key = 0;
            Button tmp = (Button) e.getSource();

            if (tmp == left) {
                key = KeyEvent.VK_LEFT;
            } else if (tmp == right) {
                key = KeyEvent.VK_RIGHT;
            } else if (tmp == up) {
                key = KeyEvent.VK_UP;
            } else if (tmp == down) {
                key = KeyEvent.VK_DOWN;
            } else if (tmp == select) {
                key = KeyEvent.VK_ENTER;
            } else if (tmp == clear) {
                key = KeyEvent.VK_BACK_SPACE;
            } else if (tmp == one) {
                key = KeyEvent.VK_1;
            } else if (tmp == two) {
                key = KeyEvent.VK_2;
            } else if (tmp == three) {
                key = KeyEvent.VK_3;
            } else if (tmp == four) {
                key = KeyEvent.VK_4;
            } else if (tmp == five) {
                key = KeyEvent.VK_5;
            } else if (tmp == six) {
                key = KeyEvent.VK_6;
            } else if (tmp == seven) {
                key = KeyEvent.VK_7;
            } else if (tmp == eight) {
                key = KeyEvent.VK_8;
            } else if (tmp == nine) {
                key = KeyEvent.VK_9;
            } else if (tmp == mark) {
                key = KeyEvent.VK_MULTIPLY;
            } else if (tmp == zero) {
                key = KeyEvent.VK_0;
            } else if (tmp == hash) {
                key = KeyEvent.VK_MODECHANGE;
            }

            return key;
        }

    }

}
