/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class ResizeDeviceDisplayDialog extends SwingDialogPanel {

    private static final long serialVersionUID = 1L;
    
    private class IntegerField extends JTextField {

        private static final long serialVersionUID = 1L;
        
        private int minValue;
        
        private int maxValue;

        public IntegerField(int cols, int minValue, int maxValue) {
            super(cols);
            
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        protected Document createDefaultModel() {
            return new IntegerDocument();
        }

        class IntegerDocument extends PlainDocument {

            private static final long serialVersionUID = 1L;

            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) {
                    return;
                }
                char[] test = str.toCharArray();
                for (int i = 0; i < test.length; i++) {
                    if (!Character.isDigit(test[i])) {
                        return;
                    }
                }
                String prevText = getText(0, getLength());
                super.insertString(offs, str, a);
                int testValue = Integer.parseInt(getText(0, getLength()));
                if (testValue < minValue | testValue > maxValue) {
                    replace(0, getLength(), prevText, a);
                }                
            }
        }
    };    
    
    private IntegerField widthField = new IntegerField(5, 1, 9999);
    
    private IntegerField heightField = new IntegerField(5, 1, 9999);

    public ResizeDeviceDisplayDialog() {
        add(new JLabel("Width:"));
        add(this.widthField);
        add(new JLabel("Height:"));
        add(this.heightField);
        JButton swapButton = new JButton("Swap");
        swapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tmp = widthField.getText();
                widthField.setText(heightField.getText());
                heightField.setText(tmp);
            }
        });
        add(swapButton);
    }

    public void setDeviceDisplaySize(int width, int height) {
        widthField.setText("" + width);
        heightField.setText("" + height);
    }
    
    public int getDeviceDisplayWidth() {
        return Integer.parseInt(widthField.getText());
    }
    
    public int getDeviceDisplayHeight() {
        return Integer.parseInt(heightField.getText());
    }

}
